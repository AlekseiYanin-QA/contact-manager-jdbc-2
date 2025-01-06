package com.example.service;

import com.example.dao.ContactDao;
import com.example.model.Contact;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.List;

@Service
public class ContactService {
    private final ContactDao contactDao;

    public ContactService(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    public void uploadContacts(MultipartFile file) throws Exception {
        try (InputStreamReader reader = new InputStreamReader(file.getInputStream())) {
            CsvToBean<Contact> csvToBean = new CsvToBeanBuilder<Contact>(reader)
                    .withType(Contact.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withSeparator(',')
                    .build();

            List<Contact> contacts = csvToBean.parse();

            boolean hasEmptyFirstName = contacts.stream()
                    .anyMatch(contact -> contact.getFirstName() == null || contact.getFirstName().trim().isEmpty());

            if (hasEmptyFirstName) {
                throw new IllegalArgumentException("Поле 'first_name' не может быть пустым");
            }

            contactDao.batchInsert(contacts);
        }
    }
}
