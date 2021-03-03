package com.bbte.styoudent.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/server-status")
public class ServerStatusController {
    @CrossOrigin
    @GetMapping("/ping")
    ResponseEntity<?> ping() {
        return ResponseEntity.ok().build();
    }
}
