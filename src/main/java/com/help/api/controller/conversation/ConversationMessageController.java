package com.help.api.controller.conversation;

import com.help.api.assembler.ConversationAssembler;
import com.help.api.exception.ForbiddenException;
import com.help.api.exception.InternalServerException;
import com.help.api.util.ConversationUtil;
import com.help.dto.incoming.conversation.ConversationMessageCreationDto;
import com.help.dto.outgoing.conversation.ConversationMessageDto;
import com.help.model.person.Person;
import com.help.model.conversation.Conversation;
import com.help.model.conversation.ConversationMessage;
import com.help.security.util.AuthUtil;
import com.help.service.conversation.ConversationMessageService;
import com.help.service.person.PersonService;
import com.help.service.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/conversations/{conversationId}/messages")
public class ConversationMessageController {
    private final PersonService personService;
    private final ConversationUtil conversationUtil;
    private final ConversationMessageService conversationMessageService;
    private final ConversationAssembler conversationAssembler;

    public ConversationMessageController(PersonService personService, ConversationUtil conversationUtil,
                                         ConversationMessageService conversationMessageService,
                                         ConversationAssembler conversationAssembler) {
        this.personService = personService;
        this.conversationUtil = conversationUtil;
        this.conversationMessageService = conversationMessageService;
        this.conversationAssembler = conversationAssembler;
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ConversationMessageDto> saveConversationMessage(
            @PathVariable("conversationId") Long conversationId,
            @RequestBody @Valid ConversationMessageCreationDto conversationMessageCreationDto
    ) {
        log.debug("POST /conversations/{}/messages, {}", conversationId, conversationMessageCreationDto);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        Conversation conversation = conversationUtil.getConversationIfParticipates(conversationId, person);

        ConversationMessage conversationMessage = new ConversationMessage();
        conversationMessage.setContent(conversationMessageCreationDto.getContent());
        conversationMessage.setConversation(conversation);
        conversationMessage.setCreationDate(LocalDateTime.now());
        conversationMessage.setCreator(person);
        conversationMessage.setDeleted(false);

        try {
            conversationMessage = conversationMessageService.save(conversationMessage);

            conversationUtil.createMultipleNotificationsOfMessageCreation(conversationMessage);

            return ResponseEntity.ok(conversationAssembler.messageModelToDto(conversationMessage));
        } catch (ServiceException serviceException) {
            throw new InternalServerException("Could not save conversation message!", serviceException);
        }
    }

    @PutMapping("{messageId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ConversationMessageDto> updateConversationMessage(
            @PathVariable("conversationId") Long conversationId,
            @PathVariable("messageId") Long messageId,
            @RequestBody @Valid ConversationMessageCreationDto conversationMessageCreationDto
    ) {
        log.debug("PUT /conversations/{}/messages/{}, {}",
                conversationId, messageId, conversationMessageCreationDto);

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        ConversationMessage conversationMessage =
                conversationUtil.getConversationMessageIfExists(conversationId, messageId);

        if (!conversationMessage.getCreator().equals(person)
                || !conversationUtil.checkIfParticipates(conversationId, person.getId())) {
            throw new ForbiddenException("Access denied!");
        }

        conversationMessage.setContent("Message removed!");
        conversationMessage.setDeleted(true);

        try {
            return ResponseEntity.ok(
                    conversationAssembler.messageModelToDto(
                            conversationMessageService.save(conversationMessage)
                    )
            );
        } catch (ServiceException serviceException) {
            throw new InternalServerException("Could not update conversation message!", serviceException);
        }
    }
}
