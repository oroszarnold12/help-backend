package com.help.api.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HelpModelMapper {
    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }
}
