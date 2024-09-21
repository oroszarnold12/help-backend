package com.help.dto.outgoing.course;

import com.help.dto.outgoing.person.PersonDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseFileDto {
    private Long id;
    private String fileName;
    private Long size;
    private LocalDateTime creationDate;
    private PersonDto uploader;
}
