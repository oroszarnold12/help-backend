package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.PersonDto;
import com.bbte.styoudent.dto.ThinPersonDto;
import com.bbte.styoudent.dto.incoming.PersonSignUpDto;
import com.bbte.styoudent.dto.incoming.PersonUpdateDto;
import com.bbte.styoudent.model.Person;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PersonAssembler {
    private final ModelMapper modelMapper;

    public PersonAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Person dtoToModel(PersonDto personDto) {
        return modelMapper.map(personDto, Person.class);
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

    public Person thinDtoToModel(ThinPersonDto thinPersonDto) {
        return modelMapper.map(thinPersonDto, Person.class);
    }

    public ThinPersonDto modelToThinDto(Person person) {
        return modelMapper.map(person, ThinPersonDto.class);
    }
}
