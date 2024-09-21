package com.help.api.controller.conversation;

import com.help.api.assembler.ConversationAssembler;
import com.help.api.exception.BadRequestException;
import com.help.api.exception.InternalServerException;
import com.help.api.util.ConversationUtil;
import com.help.dto.incoming.conversation.ConversationCreationDto;
import com.help.dto.outgoing.conversation.ConversationDto;
import com.help.model.person.Person;
import com.help.model.conversation.Conversation;
import com.help.security.util.AuthUtil;
import com.help.service.conversation.ConversationParticipationService;
import com.help.service.person.PersonService;
import com.help.service.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/conversations/{conversationId}/participants")
public class ConversationParticipationController {
    private final PersonService personService;
    private final ConversationUtil conversationUtil;
    private final ConversationAssembler conversationAssembler;
    private final ConversationParticipationService conversationParticipationService;

    public ConversationParticipationController(PersonService personService, ConversationUtil conversationUtil,
                                               ConversationAssembler conversationAssembler,
                                               ConversationParticipationService conversationParticipationService) {
        this.personService = personService;
        this.conversationUtil = conversationUtil;
        this.conversationAssembler = conversationAssembler;
        this.conversationParticipationService = conversationParticipationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ConversationDto> addParticipants(
            @PathVariable("conversationId") Long conversationId,
            @RequestBody @Valid ConversationCreationDto conversationCreationDto
    ) {
        log.debug("POST /conversations/{}/participants, {}", conversationId, conversationCreationDto);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        Conversation conversation = conversationUtil.getConversationIfCreator(conversationId, person);

        if (conversation.getName() == null) {
            throw new BadRequestException("Can't add people to simple conversation!");
        }

        List<Person> participants = conversationUtil.getPersonsFromEmails(conversationCreationDto.getEmails(), person);
        participants = participants.stream().filter(participant ->
                !conversationUtil.checkIfParticipates(conversationId, participant.getId()))
                .collect(Collectors.toList());

        conversationUtil.createParticipations(conversation, participants);

        return ResponseEntity.ok(
                conversationAssembler.modelToDto(conversation)
        );
    }

    @DeleteMapping("{participantId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteParticipant(
            @PathVariable("conversationId") Long conversationId,
            @PathVariable("participantId") Long participantId
    ) {
        log.debug("DELETE /conversations/{}/participants/{}", conversationId, participantId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        Conversation conversation = conversationUtil.getConversationIfCreator(conversationId, person);

        if (conversation.getName() == null) {
            throw new BadRequestException("Can't kick people from simple conversation!");
        }

        if (participantId.equals(person.getId())) {
            throw new BadRequestException("You can not kick yourself!");
        }

        try {
            conversationParticipationService.deleteParticipationByParticipantId(participantId);

            return ResponseEntity.noContent().build();
        } catch (ServiceException serviceException) {
            throw new InternalServerException("Could not delete conversation participation", serviceException);
        }
    }
}
