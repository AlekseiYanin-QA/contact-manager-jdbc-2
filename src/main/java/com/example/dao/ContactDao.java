package com.example.dao;

import com.example.model.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
public class ContactDao {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ContactDao.class);

    // SQL-запросы как константы
    private static final String SELECT_ALL_CONTACTS = "SELECT * FROM contacts";
    private static final String DELETE_ALL_CONTACTS = "DELETE FROM contacts";
    private static final String DELETE_CONTACT_BY_ID = "DELETE FROM contacts WHERE id = :id";
    private static final String SELECT_COUNT_BY_ID = "SELECT COUNT(*) FROM contacts WHERE id = :id";
    private static final String SELECT_CONTACT_BY_ID = "SELECT * FROM contacts WHERE id = :id";
    private static final String UPDATE_CONTACT = "UPDATE contacts SET first_name = :firstName, last_name = :lastName, phone_number = :phoneNumber, email = :email WHERE id = :id";
    private static final String SELECT_COUNT_BY_PHONE_NUMBER = "SELECT COUNT(*) FROM contacts WHERE phone_number = :phoneNumber";
    private static final String SELECT_COUNT_BY_EMAIL = "SELECT COUNT(*) FROM contacts WHERE email = :email";
    private static final String INSERT_CONTACT = "INSERT INTO contacts (first_name, last_name, phone_number, email) VALUES (:firstName, :lastName, :phoneNumber, :email)";

    public ContactDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public List<Contact> findAll() {
        return namedParameterJdbcTemplate.query(SELECT_ALL_CONTACTS, new ContactRowMapper());
    }

    public void deleteAll() {
        try {
            namedParameterJdbcTemplate.update(DELETE_ALL_CONTACTS, Map.of());
            logger.info("All contacts deleted successfully.");
        } catch (DataAccessException e) {
            logger.error("Error deleting all contacts: {}", e.getMessage());
            throw new DataAccessException("Не удалось удалить все контакты", e) {};
        }
    }

    public void deleteById(Long id) {
        try {
            int rowsAffected = namedParameterJdbcTemplate.update(DELETE_CONTACT_BY_ID, Map.of("id", id));
            if (rowsAffected == 0) {
                logger.warn("No contact found with ID {}", id);
                throw new NoSuchElementException("Контакт с ID " + id + " не найден.");
            }
            logger.info("Contact with ID {} deleted successfully.", id);
        } catch (DataAccessException e) {
            logger.error("Error deleting contact with ID {}: {}", id, e.getMessage());
            throw new DataAccessException("Не удалось удалить контакт с ID " + id, e) {};
        }
    }

    public boolean existsById(Long id) {
        Integer count = namedParameterJdbcTemplate.queryForObject(SELECT_COUNT_BY_ID, Map.of("id", id), Integer.class);
        return count != null && count > 0;
    }

    public Optional<Contact> findById(Long id) {
        List<Contact> contacts = namedParameterJdbcTemplate.query(SELECT_CONTACT_BY_ID, Map.of("id", id), new ContactRowMapper());
        if (contacts.isEmpty()) {
            logger.warn("Контакт с ID {} не найден.", id);
            return Optional.empty();
        }
        return Optional.of(contacts.getFirst());
    }

    public void update(Contact contact) {
        try {
            int rowsAffected = namedParameterJdbcTemplate.update(UPDATE_CONTACT, Map.of(
                    "firstName", contact.getFirstName(),
                    "lastName", contact.getLastName(),
                    "phoneNumber", contact.getPhoneNumber(),
                    "email", contact.getEmail(),
                    "id", contact.getId()
            ));
            if (rowsAffected == 0) {
                logger.warn("Не удалось обновить контакт с ID {}: контакт не найден.", contact.getId());
                throw new NoSuchElementException("Контакт с ID " + contact.getId() + " не найден.");
            }
            logger.info("Contact with ID {} updated successfully.", contact.getId());
        } catch (DataAccessException e) {
            logger.error("Error updating contact with ID {}: {}", contact.getId(), e.getMessage());
            throw new DataAccessException("Не удалось обновить контакт с ID " + contact.getId(), e) {};
        }
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        Integer count = namedParameterJdbcTemplate.queryForObject(SELECT_COUNT_BY_PHONE_NUMBER, Map.of("phoneNumber", phoneNumber), Integer.class);
        return count != null && count > 0;
    }

    public boolean existsByEmail(String email) {
        Integer count = namedParameterJdbcTemplate.queryForObject(SELECT_COUNT_BY_EMAIL, Map.of("email", email), Integer.class);
        return count != null && count > 0;
    }

    public void batchInsert(List<Contact> contacts) {
        try {
            for (Contact contact : contacts) {
                namedParameterJdbcTemplate.update(INSERT_CONTACT, Map.of(
                        "firstName", contact.getFirstName(),
                        "lastName", contact.getLastName(),
                        "phoneNumber", contact.getPhoneNumber(),
                        "email", contact.getEmail()
                ));
            }
            logger.info("{} contacts inserted successfully.", contacts.size());
        } catch (DataAccessException e) {
            logger.error("Error inserting contacts: {}", e.getMessage());
            throw new DataAccessException("Не удалось вставить контакты", e) {};
        }
    }
}
