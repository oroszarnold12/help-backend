package com.bbte.styoudent.api.controller.conversation;

import com.bbte.styoudent.api.assembler.ConversationAssembler;
import com.bbte.styoudent.api.exception.ForbiddenException;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.api.util.ConversationUtil;
import com.bbte.styoudent.dto.incoming.conversation.ConversationMessageCreationDto;
import com.bbte.styoudent.dto.outgoing.conversation.ConversationMessageDto;
import com.bbte.styoudent.model.person.Person;
import com.bbte.styoudent.model.conversation.Conversation;
import com.bbte.styoudent.model.conversation.ConversationMessage;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.conversation.ConversationMessageService;
import com.bbte.styoudent.service.person.PersonService;
import com.bbte.styoudent.service.ServiceException;
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
