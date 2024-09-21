package com.help.api.controller.course;

import com.help.api.assembler.InvitationAssembler;
import com.help.api.exception.BadRequestException;
import com.help.api.exception.InternalServerException;
import com.help.api.util.InvitationUtil;
import com.help.api.util.ParticipationUtil;
import com.help.dto.incoming.course.InvitationCreationDto;
import com.help.dto.outgoing.ApiResponseMessage;
import com.help.model.course.Course;
import com.help.model.course.Invitation;
import com.help.model.person.Person;
import com.help.security.util.AuthUtil;
import com.help.service.course.InvitationService;
import com.help.service.person.ParticipationService;
import com.help.service.person.PersonService;
import com.help.service.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/invitations")
public class InvitationController {
    private final InvitationService invitationService;
    private final PersonService personService;
    private final ParticipationService participationService;
    private final InvitationAssembler invitationAssembler;
    private final ParticipationUtil participationUtil;
    private final InvitationUtil invitationUtil;

    public InvitationController(InvitationService invitationService, PersonService personService,
                                ParticipationService participationService, InvitationAssembler invitationAssembler,
                                ParticipationUtil participationUtil, InvitationUtil invitationUtil) {
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
            @RequestBody @Valid InvitationCreationDto invitationCreationDto,
            @RequestParam boolean inviteByEmails, @RequestParam boolean inviteByPersonGroups) {
        log.debug("POST /invitations {}", invitationCreationDto);

        Person user = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        Course course = invitationUtil.getCourse(invitationCreationDto.getCourseId());

        participationUtil.checkIfParticipates(course.getId(), user);

        if (inviteByEmails && inviteByPersonGroups) {
            throw new BadRequestException("Can't invite by two methods at the same time!");
        }

        List<Person> persons = invitationUtil.getPersons(inviteByEmails, inviteByPersonGroups,
                invitationCreationDto.getEmails(), invitationCreationDto.getPersonGroups());

        persons = persons.stream().filter(person -> !participationService.checkIfParticipates(course.getId(), person)
                && !invitationService.checkIfExistsByPersonIdAndCourseId(person.getId(), course.getId()))
                .collect(Collectors.toList());

        try {
            persons.forEach(person -> invitationUtil.createSingleNotificationForInvitation(
                    invitationService.createInvitation(course, person)
            ));

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

        invitationUtil.checkIfExists(id, person);

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
