package com.example.controller;

import com.example.model.Contact;
import com.example.service.ContactService;
import com.example.service.UploadResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/contacts")
public class ContactController {
    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadContacts(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            logger.warn("Attempted to upload an empty file.");
            return ResponseEntity.badRequest().body("Файл не должен быть пустым");
        }
        try {
            // Получаем результат загрузки из сервиса
            UploadResult result = contactService.uploadContacts(file);
            logger.info("Contacts uploaded successfully from file: {}", file.getOriginalFilename());

            return ResponseEntity.ok("Контакты успешно загружены! Уникальных контактов: " + result.getUniqueCount() +
                    ", Пропущено дубликатов: " + result.getDuplicateCount());
        } catch (Exception e) {
            logger.error("Failed to upload contacts: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Не удалось загрузить контакты: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<String> deleteAll() {
        contactService.deleteAll();
        logger.info("All contacts deleted.");
        return ResponseEntity.ok("Все контакты удалены.");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        try {
            contactService.deleteById(id);
            return ResponseEntity.ok("Контакт с ID " + id + " удален.");
        } catch (NoSuchElementException e) {
            logger.error("Контакт с ID {} не найден: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Контакт с ID " + id + " не найден.");
        } catch (DataAccessException e) {
            logger.error("Ошибка доступа к данным при удалении контакта с ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Не удалось удалить контакт с ID " + id);
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при удалении контакта с ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Не удалось удалить контакт с ID " + id);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateContact(@PathVariable Long id, @RequestBody Contact contact) {
        contact.setId(id);

        try {
            contactService.update(contact);
            logger.info("Contact with ID {} updated.", id);
            return ResponseEntity.ok("Контакт с ID " + id + " изменен.");
        } catch (Exception e) {
            logger.error("Failed to update contact with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(404).body("Контакт с ID " + id + " не найден.");
        }
    }
}