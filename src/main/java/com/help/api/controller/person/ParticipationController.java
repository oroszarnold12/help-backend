package com.help.api.controller.person;

import com.help.api.assembler.ParticipationAssembler;
import com.help.api.exception.BadRequestException;
import com.help.api.exception.InternalServerException;
import com.help.dto.incoming.person.ParticipationCreationDto;
import com.help.dto.outgoing.person.ParticipationDto;
import com.help.model.person.Participation;
import com.help.model.person.Person;
import com.help.security.util.AuthUtil;
import com.help.service.person.ParticipationService;
import com.help.service.person.PersonService;
import com.help.service.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/participations")
public class ParticipationController {
    private final PersonService personService;
    private final ParticipationService participationService;
    private final ParticipationAssembler participationAssembler;

    public ParticipationController(PersonService personService, ParticipationService participationService,
                                   ParticipationAssembler participationAssembler) {
        this.personService = personService;
        this.participationService = participationService;
        this.participationAssembler = participationAssembler;
    }

    @GetMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('STUDENT')")
    public List<ParticipationDto> getParticipations() {
        log.debug("GET /participations");

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            return participationService.getAllByPerson(person)
                    .stream().map(participationAssembler::modelToDto).collect(Collectors.toList());
        } catch (ServiceException se) {
            throw new InternalServerException("Could not GET participations!", se);
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('STUDENT')")
    public List<ParticipationDto> updateParticipations(
            @RequestBody @Valid @NotEmpty List<ParticipationCreationDto> participations
    ) {
        log.debug("PUT /participations");

        Person person = personService.getPersonByEmail(AuthUtil.getCurrentUsername());

        try {
            participations.forEach((participation) -> {
                Participation oldParticipation = participationService.getByCourseIdAndPerson(
                        participation.getCourseId(), person
                );

                oldParticipation.setShowOnDashboard(participation.getShowOnDashboard());
                participationService.save(oldParticipation);
            });

            return participationService.getAllByPerson(person)
                    .stream().map(participationAssembler::modelToDto).collect(Collectors.toList());
        } catch (ServiceException se) {
            throw new BadRequestException("Could not PUT participations!", se);
        }
    }
}
