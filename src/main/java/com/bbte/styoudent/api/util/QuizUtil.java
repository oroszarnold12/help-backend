package com.bbte.styoudent.api.util;

import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.api.exception.NotFoundException;
import com.bbte.styoudent.model.*;
import com.bbte.styoudent.service.QuizGradeService;
import com.bbte.styoudent.service.QuizService;
import com.bbte.styoudent.service.QuizSubmissionService;
import com.bbte.styoudent.service.ServiceException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class QuizUtil {
    private final QuizService quizService;
    private final QuizGradeService quizGradeService;
    private final QuizSubmissionService quizSubmissionService;

    public QuizUtil(QuizService quizService, QuizGradeService quizGradeService,
                    QuizSubmissionService quizSubmissionService) {
        this.quizService = quizService;
        this.quizGradeService = quizGradeService;
        this.quizSubmissionService = quizSubmissionService;
    }

    public void checkIfHasThisQuiz(Long courseId, Long quizId) {
        try {
            if (!quizService.checkIfExistsByCourseIdAndId(courseId, quizId)) {
                throw new BadRequestException(
                        "Course with id: " + courseId + " has no quiz with id: " + quizId + "!"
                );
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not check quiz!", se);
        }
    }

    private boolean checkIfGraded(Long quizId, Person submitter) {
        try {
            return quizGradeService.checkIfExistsByQuizIdAndSubmitter(quizId, submitter);
        } catch (ServiceException se) {
            throw new InternalServerException("Could not check quiz grade!", se);
        }
    }

    public void gradeQuiz(Quiz quiz, QuizSubmission quizSubmission, Person submitter) {
        AtomicReference<Double> grade = new AtomicReference<>((double) 0);

        quiz.getQuestions().forEach(question -> {
            long numberOfCorrectAnswers = question.getAnswers().stream().filter(Answer::getCorrect).count();
            double valueOfAnswer = question.getPoints() / numberOfCorrectAnswers;

            AtomicInteger numberOfHits = new AtomicInteger();
            question.getAnswers().forEach(answer -> {
                if (answer.getCorrect()) {
                    if (checkIfAnswerIsPicked(quizSubmission, answer.getId())) {
                        numberOfHits.getAndIncrement();
                    }
                } else {
                    if (checkIfAnswerIsPicked(quizSubmission, answer.getId())) {
                        numberOfHits.getAndDecrement();
                    }
                }
            });

            grade.set(grade.get() + (numberOfHits.get() * valueOfAnswer > 0 ? numberOfHits.get() * valueOfAnswer : 0));
        });

        QuizGrade quizGrade;

        if (checkIfGraded(quiz.getId(), submitter)) {
            quizGrade = quizGradeService.getByQuizIdAndBySubmitter(quiz.getId(), submitter).get(0);
            quizGrade.setGrade(grade.get());
        } else {
            quizGrade = new QuizGrade();
            quizGrade.setGrade(grade.get());
            quizGrade.setQuiz(quiz);
            quizGrade.setSubmitter(submitter);
        }

        try {
            quizGradeService.save(quizGrade);
        } catch (ServiceException se) {
            throw new InternalServerException("Could not grade quiz submission!", se);
        }
    }

    public void checkIfAlreadySubmitted(Long quizId, Long submitterId) {
        try {
            if (quizSubmissionService.checkIfExistsByQuizIdAndSubmitterId(quizId, submitterId)) {
                throw new BadRequestException("You have already submitted this quiz!");
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not check quiz submission!", se);
        }
    }

    public Answer getAnswer(Quiz quiz, Long answerId) {
        List<Answer> answers = new ArrayList<>();
        quiz.getQuestions().forEach(question -> answers.addAll(question.getAnswers()));

        return answers.stream().filter(answer -> answer.getId().equals(answerId)).findFirst().orElseThrow(() ->
                new BadRequestException(
                        "Quiz with id: " + quiz.getId() + " has no answer with id: " + answerId
                ));
    }

    private boolean checkIfAnswerIsPicked(QuizSubmission quizSubmission, Long answerId) {
        AtomicReference<Boolean> picked = new AtomicReference<>();
        quizSubmission.getAnswerSubmissions().forEach(answerSubmission -> {
            if (answerSubmission.getAnswer().getId().equals(answerId)) {
                picked.set(answerSubmission.getPicked());
            }
        });

        return picked.get();
    }

    public void checkIfPublished(Long courseId, Long quizId) {
        try {
            if (!quizService.getByCourseIdAndId(courseId, quizId).getPublished()) {
                throw new NotFoundException(
                        "Course with id: " + courseId + " has no quiz with id: " + quizId + "!"
                );
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not check quiz!", se);
        }
    }
}
