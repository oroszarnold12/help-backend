package com.help.api.assembler;

import com.help.dto.incoming.person.PersonSignUpDto;
import com.help.dto.incoming.person.PersonUpdateDto;
import com.help.dto.outgoing.person.PersonDto;
import com.help.model.person.Person;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PersonAssembler {
    private final ModelMapper modelMapper;

    public PersonAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public PersonDto modelToDto(Person person) {
        return modelMapper.map(person, PersonDto.class);
    }

    public Person signUpDtoToModel(PersonSignUpDto personSignUpDto) {
        return modelMapper.map(personSignUpDto, Person.class);
    }

    public Person updateDtoToModel(PersonUpdateDto personUpdateDto) {
        return modelMapper.map(personUpdateDto, Person.class);
    }
}
