package com.bbte.styoudent.api.util;

import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.ForbiddenException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.api.exception.NotFoundException;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.model.conversation.Conversation;
import com.bbte.styoudent.model.conversation.ConversationMessage;
import com.bbte.styoudent.service.*;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

@Component
public class ConversationUtil {
    private final ConversationParticipationService conversationParticipationService;
    private final PersonService personService;
    private final ConversationService conversationService;
    private final ConversationMessageService conversationMessageService;

    public ConversationUtil(ConversationParticipationService conversationParticipationService,
                            PersonService personService, ConversationService conversationService,
                            ConversationMessageService conversationMessageService) {
        this.conversationParticipationService = conversationParticipationService;
        this.personService = personService;
        this.conversationService = conversationService;
        this.conversationMessageService = conversationMessageService;
    }

    public boolean checkIfParticipates(Long conversationId, Long personId) {
        try {
            return conversationParticipationService.checkIfExistsByConversationIdAndPersonId(conversationId, personId);
        } catch (DataAccessException dataAccessException) {
            throw new ServiceException("Could not check conversation participation", dataAccessException);
        }
    }

    public List<Person> getPersonsFromEmails(String[] emails, Person person) {
        List<Person> persons = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        LinkedHashSet<String> emailsSet =
                new LinkedHashSet<>(Arrays.asList(emails));
        emailsSet.add(person.getEmail());
        String[] distinctEmails = emailsSet.toArray(new String[0]);

        for (String email : distinctEmails) {
            try {
                persons.add(personService.getPersonByEmail(email));
            } catch (ServiceException serviceException) {
                failed.add(email);
            }
        }

        if (failed.size() > 0) {
            throw new BadRequestException(
                    "Persons with emails: " + String.join(", ", failed) + " does not exists!"
            );
        }

        return persons;
    }

    public void createParticipations(Conversation conversation, List<Person> persons) {
        List<String> failed = new ArrayList<>();

        persons.forEach(person -> {
            try {
                conversationParticipationService.createParticipation(conversation, person);
            } catch (ServiceException serviceException) {
                failed.add(person.getFirstName() + " " + person.getLastName());
            }
        });

        if (failed.size() > 0) {
            throw new InternalServerException(
                    "Could not add persons: " + String.join(", ", failed) + "!"
            );
        }
    }

    public Conversation getConversationIfCreator(Long conversationId, Person person) {
        Conversation conversation;

        try {
            conversation = conversationService.getById(conversationId);

            if (!conversation.getCreator().equals(person)) {
                throw new ForbiddenException("Access denied!");
            }
        } catch (ServiceException serviceException) {
            throw new NotFoundException(
                    "Conversation with id: " + conversationId + " does not exists!", serviceException
            );
        }

        return conversation;
    }

    public Conversation getConversationIfParticipates(Long conversationId, Person person) {
        Conversation conversation;

        try {
            conversation = conversationService.getById(conversationId);
        } catch (ServiceException serviceException) {
            throw new NotFoundException(
                    "Conversation with id: " + conversationId + " does not exists!", serviceException
            );
        }

        if (!checkIfParticipates(conversationId, person.getId())) {
            throw new ForbiddenException("Access denied!");
        }

        return conversation;
    }

    public ConversationMessage getConversationMessageIfExists(Long conversationId, Long messageId) {
        try {
            return conversationMessageService.getByConversationIdAndId(conversationId, messageId);
        } catch (ServiceException serviceException) {
            throw new NotFoundException(
                    "Conversation with id: " + conversationId + " has no message with id: " + messageId + "!"
            );
        }
    }
}
