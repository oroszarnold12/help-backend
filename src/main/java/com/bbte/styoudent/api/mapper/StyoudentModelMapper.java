package com.bbte.styoudent.api.mapper;


import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StyoudentModelMapper {
    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }
}
