package com.example.service;

import com.example.dao.ContactDao;
import com.example.model.Contact;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ContactService {

    private final JdbcTemplate jdbcTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ContactService.class);

    private final ContactDao contactDao;

    public ContactService(JdbcTemplate jdbcTemplate, ContactDao contactDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.contactDao = contactDao;
    }

    public void uploadContacts(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Файл не должен быть пустым");
        }

        try (InputStreamReader reader = new InputStreamReader(file.getInputStream())) {
            CsvToBean<Contact> csvToBean = new CsvToBeanBuilder<Contact>(reader)
                    .withType(Contact.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withSeparator(',')
                    .build();

            List<Contact> contacts = csvToBean.parse();

            // Validate contacts
            validateContacts(contacts);

            logger.info("Uploading {} contacts from file: {}", contacts.size(), file.getOriginalFilename());
            contactDao.batchInsert(contacts);
        } catch (Exception e) {
            logger.error("Error uploading contacts: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось загрузить контакты: " + e.getMessage(), e);
        }
    }

    private void validateContacts(List<Contact> contacts) {
        boolean hasEmptyFirstName = contacts.stream()
                .anyMatch(contact -> contact.getFirstName() == null || contact.getFirstName().trim().isEmpty());

        if (hasEmptyFirstName) {
            throw new IllegalArgumentException("Поле 'first_name' не может быть пустым");
        }

        // Additional validations can be added here
    }

    public void deleteAll() {
        logger.info("Deleting all contacts.");
        contactDao.deleteAll();
    }

    public void deleteById(Long id) {
        if (!contactDao.existsById(id)) {
            throw new NoSuchElementException("Контакт с ID " + id + " не найден.");
        }
        contactDao.deleteById(id);
    }

    public void update(Contact contact) {
        logger.info("Updating contact: {}", contact);
        contactDao.update(contact);
    }
}