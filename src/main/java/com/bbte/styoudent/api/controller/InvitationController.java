package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.InvitationAssembler;
import com.bbte.styoudent.api.exception.BadRequestException;
import com.bbte.styoudent.api.exception.ForbiddenException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.api.util.InvitationUtil;
import com.bbte.styoudent.api.util.ParticipationUtil;
import com.bbte.styoudent.dto.incoming.InvitationCreationDto;
import com.bbte.styoudent.dto.outgoing.ApiResponseMessage;
import com.bbte.styoudent.model.Course;
import com.bbte.styoudent.model.Invitation;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/invitations")
public class InvitationController {
    private final CourseService courseService;
    private final InvitationService invitationService;
    private final PersonService personService;
    private final ParticipationService participationService;
    private final InvitationAssembler invitationAssembler;
    private final ParticipationUtil participationUtil;
    private final InvitationUtil invitationUtil;

    public InvitationController(CourseService courseService, InvitationService invitationService,
                                PersonService personService, ParticipationService participationService,
                                InvitationAssembler invitationAssembler, ParticipationUtil participationUtil,
                                InvitationUtil invitationUtil) {
        this.courseService = courseService;
        this.invitationService = invitationService;
        this.personService = personService;
        this.participationService = participationService;
        this.invitationAssembler = invitationAssembler;
        this.participationUtil = participationUtil;
        this.invitationUtil = invitationUtil;
    }

    @GetMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('STUDENT')")
    public ResponseEntity<Map<String, List<?>>> getInvitations() {
        log.debug("GET /invitations");

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            return ResponseEntity.ok(
                    Collections.singletonMap("invitations", invitationService.getAllByPerson(person)
                            .stream().map(invitationAssembler::modelToDto)
                            .collect(Collectors.toList())));
        } catch (ServiceException se) {
            throw new InternalServerException("Could not GET invitations!", se);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponseMessage> createInvitations(
            @RequestBody @Valid InvitationCreationDto invitationCreationDto) {
        log.debug("POST /invitations {}", invitationCreationDto);

        Person user = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        Course course;

        try {
            course = courseService.getById(invitationCreationDto.getCourseId());
        } catch (ServiceException se) {
            throw new BadRequestException(
                    "Course with id: " + invitationCreationDto.getCourseId() + " doesn't exists!", se
            );
        }

        participationUtil.checkIfParticipates(course.getId(), user);

        List<Person> persons = new ArrayList<>();

        try {
            for (String email : invitationCreationDto.getEmails()) {
                persons.add(personService.getPersonByEmail(email));
            }
        } catch (ServiceException se) {
            throw new BadRequestException(
                    "Could not GET persons with emails: " + Arrays.toString(invitationCreationDto.getEmails()) + "!", se
            );
        }

        persons = persons.stream().filter(person -> !participationService.checkIfParticipates(course.getId(), person) &&
                !invitationService.checkIfExistsByPersonIdAndCourseId(person.getId(), course.getId()))
                .collect(Collectors.toList());

        try {
            persons.forEach((person -> invitationUtil.createSingleNotificationForInvitation(
                    invitationService.createInvitation(course, person)
            )));

            return ResponseEntity.ok().body(new ApiResponseMessage("Invitations created successfully!"));
        } catch (ServiceException se) {
            throw new InternalServerException("Could not POST invitations!", se);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponseMessage> deleteInvitation(@PathVariable(name = "id") Long id,
                                                               @RequestParam boolean accept) {
        log.debug("DELETE /invitations/{}", id);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            if (!invitationService.checkIfExistsByIdAndPerson(id, person)) {
                throw new ForbiddenException("Access denied");
            }
        } catch (ServiceException se) {
            throw new InternalServerException("Could not check if invitation exists!", se);
        }

        if (accept) {
            Invitation invitation = invitationService.getInvitationById(id).orElseThrow(() ->
                    new BadRequestException("Could not GET invitation with id: " + id));

            try {
                participationService.createInitialParticipation(invitation.getCourse(), invitation.getPerson());
            } catch (ServiceException se) {
                throw new InternalServerException("Could not create participation!", se);
            }
        }

        try {
            invitationService.deleteInvitation(id);

            return ResponseEntity.ok().body(new ApiResponseMessage("Invitations with id: " + id + " accepted!"));
        } catch (ServiceException se) {
            throw new BadRequestException("Could not DELETE invitation with id: " + id + "!", se);
        }

    }
}
