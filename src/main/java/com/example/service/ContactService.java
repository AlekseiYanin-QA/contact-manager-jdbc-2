package com.example.service;

import com.example.dao.ContactDao;
import com.example.model.Contact;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.*;

@Service
public class ContactService {

    private static final Logger logger = LoggerFactory.getLogger(ContactService.class);
    private final ContactDao contactDao;

    public ContactService(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    public UploadResult uploadContacts(MultipartFile file) throws IllegalArgumentException {
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

            return processContacts(contacts);
        } catch (Exception e) {
            logger.error("Ошибка при загрузке контактов: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось загрузить контакты: " + e.getMessage(), e);
        }
    }

    private UploadResult processContacts(List<Contact> contacts) {
        Set<String> uniqueIdentifiers = new HashSet<>();
        List<Contact> distinctContacts = new ArrayList<>();
        int duplicateCount = 0;

        for (Contact contact : contacts) {
            if (isUnique(contact)) {
                distinctContacts.add(contact);
                uniqueIdentifiers.add(contact.getPhoneNumber()); // или contact.getEmail();
            } else {
                duplicateCount++;
            }
        }

        // Вставка уникальных контактов в базу данных
        contactDao.batchInsert(distinctContacts);
        logger.info("Успешно загружено {} уникальных контактов.", distinctContacts.size());

        return new UploadResult(distinctContacts.size(), duplicateCount);
    }

    private boolean isUnique(Contact contact) {
        boolean isUnique = true;

        // Проверка на уникальность по номеру телефона
        if (contactDao.existsByPhoneNumber(contact.getPhoneNumber())) {
            isUnique = false;
            logger.warn("Контакт с номером телефона {} уже существует. Пропуск.", contact.getPhoneNumber());
        }

        // Проверка на уникальность по email
        if (contactDao.existsByEmail(contact.getEmail())) {
            isUnique = false;
            logger.warn("Контакт с email {} уже существует. Пропуск.", contact.getEmail());
        }

        return isUnique;
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
        if (!contactDao.existsById(contact.getId())) {
            throw new NoSuchElementException("Контакт с ID " + contact.getId() + " не найден.");
        }

        logger.info("Updating contact: {}", contact);
        contactDao.update(contact);
    }
}
