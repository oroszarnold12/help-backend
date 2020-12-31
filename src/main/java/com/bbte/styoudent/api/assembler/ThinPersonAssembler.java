package com.bbte.styoudent.api.assembler;

import com.bbte.styoudent.dto.ThinPersonDto;
import com.bbte.styoudent.model.Person;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ThinPersonAssembler {
    private final ModelMapper modelMapper;

    public ThinPersonAssembler(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Person dtoToModel(ThinPersonDto thinPersonDto) {
        return modelMapper.map(thinPersonDto, Person.class);
    }

    public ThinPersonDto modelToDto(Person person) {
        return modelMapper.map(person, ThinPersonDto.class);
    }
}

