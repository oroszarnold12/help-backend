package com.bbte.styoudent.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class Note {
    private String title;
    private String body;
    private Map<String, String> data;
}
