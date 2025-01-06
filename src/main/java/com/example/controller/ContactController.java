package com.example.controller;

import com.example.service.ContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/contacts")
public class ContactController {
    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadContacts(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Файл не должен быть пустым");
        }

        try {
            contactService.uploadContacts(file);
            return ResponseEntity.ok("Контакты успешно загружены!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Не удалось загрузить контакты: " + e.getMessage());
        }
    }
}