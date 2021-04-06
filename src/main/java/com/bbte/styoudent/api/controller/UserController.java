package com.bbte.styoudent.api.controller;

import com.bbte.styoudent.api.assembler.PersonAssembler;
import com.bbte.styoudent.api.exception.InternalServerException;
import com.bbte.styoudent.dto.incoming.PersonPasswordDto;
import com.bbte.styoudent.dto.outgoing.PersonDto;
import com.bbte.styoudent.model.Person;
import com.bbte.styoudent.model.FileObject;
import com.bbte.styoudent.security.util.AuthUtil;
import com.bbte.styoudent.service.PersonService;
import com.bbte.styoudent.service.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    private final PersonService personService;
    private final PersonAssembler personAssembler;

    public UserController(PersonService personService, PersonAssembler personAssembler) {
        this.personService = personService;
        this.personAssembler = personAssembler;
    }

    @GetMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('STUDENT')")
    public PersonDto getCurrentUser() {
        log.debug("GET /user");

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        return personAssembler.modelToDto(person);
    }

    @PutMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('STUDENT')")
    public PersonDto changePassword(@RequestBody @Valid PersonPasswordDto personPasswordDto) {
        log.debug("PUT /user");

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            return personAssembler.modelToDto(
                    personService.changePassword(person, personPasswordDto.getPassword())
            );
        } catch (ServiceException se) {
            throw new InternalServerException("Could not change password!", se);
        }
    }

    @Transactional
    @PostMapping("/image")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('STUDENT')")
    public PersonDto uploadImage(@RequestParam("image") @NotNull MultipartFile image) {
        log.debug("POST /user/image");

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            FileObject fileObject = new FileObject();
            fileObject.setBytes(image.getBytes());
            person.setImage(fileObject);

            return personAssembler.modelToDto(
                    personService.savePerson(person)
            );
        } catch (IOException | ServiceException exception) {
            throw new InternalServerException("Could not store image!", exception);
        }
    }

    @Transactional
    @GetMapping(value = "/image")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('STUDENT')")
    public Resource downloadImage() {
        log.debug("GET /user/image");

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        return (person.getImage() == null || person.getImage().getBytes() == null) ?
                null :
                new ByteArrayResource(
                        person.getImage().getBytes()
                );
    }

    @Transactional
    @GetMapping(value = "{userId}/image")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('STUDENT')")
    public Resource downloadImage(@PathVariable("userId") Long userId) {
        log.debug("GET /user/{}/image", userId);

        Person person = personService.getPersonById(userId);

        return (person.getImage() == null || person.getImage().getBytes() == null) ?
                null :
                new ByteArrayResource(
                        person.getImage().getBytes()
                );
    }

    @Transactional
    @DeleteMapping(value = "/image")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('STUDENT')")
    public void removeImage() {
        log.debug("DELETE /user/image");

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());
        if (person.getImage() != null) {
            person.getImage().setBytes(null);
        }

        try {
            personService.savePerson(person);
        } catch (ServiceException se) {
            throw new InternalServerException("Could not remove image!", se);
        }
    }
}
