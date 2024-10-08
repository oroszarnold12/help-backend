package com.help.api.controller.conversation;

import com.help.api.assembler.ConversationAssembler;
import com.help.api.exception.BadRequestException;
import com.help.api.exception.ForbiddenException;
import com.help.api.exception.InternalServerException;
import com.help.api.exception.NotFoundException;
import com.help.api.util.ConversationUtil;
import com.help.dto.incoming.conversation.ConversationCreationDto;
import com.help.dto.outgoing.conversation.ConversationDto;
import com.help.dto.outgoing.conversation.ThinConversationDto;
import com.help.model.person.Person;
import com.help.model.conversation.Conversation;
import com.help.security.util.AuthUtil;
import com.help.service.conversation.ConversationService;
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
@RequestMapping("/conversations")
public class ConversationController {
    private final PersonService personService;
    private final ConversationService conversationService;
    private final ConversationAssembler conversationAssembler;
    private final ConversationUtil conversationUtil;

    public ConversationController(PersonService personService, ConversationService conversationService,
                                  ConversationAssembler conversationAssembler, ConversationUtil conversationUtil) {
        this.personService = personService;
        this.conversationService = conversationService;
        this.conversationAssembler = conversationAssembler;
        this.conversationUtil = conversationUtil;
    }

    @GetMapping
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<List<ThinConversationDto>> getConversations() {
        log.debug("GET /conversations");

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            return ResponseEntity.ok(
                    conversationService.getByPersonId(person.getId())
                            .stream().map(conversationAssembler::modelToThinDto)
                            .collect(Collectors.toList())
            );
        } catch (ServiceException se) {
            throw new InternalServerException("Could not GET conversations!", se);
        }
    }

    @GetMapping("{conversationId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ConversationDto> getConversation(
            @PathVariable("conversationId") Long conversationId) {
        log.debug("GET /conversations/{}", conversationId);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            if (!conversationUtil.checkIfParticipates(conversationId, person.getId())) {
                throw new ForbiddenException("Access denied!");
            }

            Conversation conversation = conversationService.getById(conversationId);

            return ResponseEntity.ok(
                    conversationAssembler.modelToDto(conversation)
            );
        } catch (ServiceException se) {
            throw new NotFoundException("Conversation with id: " + conversationId + " does not exists!", se);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ConversationDto> saveConversation(
            @RequestBody @Valid ConversationCreationDto conversationCreationDto
    ) {
        log.debug("POST /conversations/{}", conversationCreationDto);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        List<Person> participants = conversationUtil.getPersonsFromEmails(conversationCreationDto.getEmails(), person);

        if (participants.size() > 2
                && (conversationCreationDto.getName() == null || conversationCreationDto.getName().isBlank())) {
            throw new BadRequestException("Group conversation should have a name!");
        }

        try {
            Conversation conversation = new Conversation();
            conversation.setCreator(person);
            if (participants.size() > 2) {
                conversation.setName(conversationCreationDto.getName());
            }

            conversation = conversationService.save(conversation);

            conversationUtil.createParticipations(conversation, participants);

            return ResponseEntity.ok(
                    conversationAssembler.modelToDto(conversation)
            );
        } catch (ServiceException se) {
            throw new InternalServerException("Conversation creation failed!", se);
        }
    }
}
