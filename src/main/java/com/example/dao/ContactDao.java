package com.example.dao;

import com.example.model.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Repository
public class ContactDao {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ContactDao.class);

    public ContactDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public List<Contact> findAll() {
        String sql = "SELECT * FROM contacts";
        return namedParameterJdbcTemplate.query(sql, new ContactRowMapper());
    }

    public void deleteAll() {
        try {
            namedParameterJdbcTemplate.update("DELETE FROM contacts", Map.of());
            logger.info("All contacts deleted successfully.");
        } catch (DataAccessException e) {
            logger.error("Error deleting all contacts: {}", e.getMessage());
            throw e; // Пробрасываем исключение дальше
        }
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM contacts WHERE id = :id";
        try {
            int rowsAffected = namedParameterJdbcTemplate.update(sql, Map.of("id", id));
            if (rowsAffected == 0) {
                logger.warn("No contact found with ID {}", id);
                throw new NoSuchElementException("Контакт с ID " + id + " не найден.");
            }
            logger.info("Contact with ID {} deleted successfully.", id);
        } catch (DataAccessException e) {
            logger.error("Error deleting contact with ID {}: {}", id, e.getMessage());
            throw e; // Пробрасываем исключение дальше
        }
    }

    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM contacts WHERE id = :id";
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, Map.of("id", id), Integer.class);
        return count != null && count > 0;
    }

    public Contact findById(Long id) {
        String sql = "SELECT * FROM contacts WHERE id = :id";
        List<Contact> contacts = namedParameterJdbcTemplate.query(sql, Map.of("id", id), new ContactRowMapper());
        if (contacts.isEmpty()) {
            throw new NoSuchElementException("Контакт с ID " + id + " не найден.");
        }
        return contacts.getFirst();
    }

    public void update(Contact contact) {
        String sql = "UPDATE contacts SET first_name = :firstName, last_name = :lastName, phone_number = :phoneNumber, email = :email WHERE id = :id";
        try {
            namedParameterJdbcTemplate.update(sql, Map.of(
                    "firstName", contact.getFirstName(),
                    "lastName", contact.getLastName(),
                    "phoneNumber", contact.getPhoneNumber(),
                    "email", contact.getEmail(),
                    "id", contact.getId()
            ));
            logger.info("Contact with ID {} updated successfully.", contact.getId());
        } catch (DataAccessException e) {
            logger.error("Error updating contact with ID {}: {}", contact.getId(), e.getMessage());
            throw e; // Пробрасываем исключение дальше
        }
    }

    public void batchInsert(List<Contact> contacts) {
        String sql = "INSERT INTO contacts (first_name, last_name, phone_number, email) VALUES (:firstName, :lastName, :phoneNumber, :email)";

        try {
            for (Contact contact : contacts) {
                namedParameterJdbcTemplate.update(sql, Map.of(
                        "firstName", contact.getFirstName(),
                        "lastName", contact.getLastName(),
                        "phoneNumber", contact.getPhoneNumber(),
                        "email", contact.getEmail()
                ));
            }
            logger.info("{} contacts inserted successfully.", contacts.size());
        } catch (DataAccessException e) {
            logger.error("Error inserting contacts: {}", e.getMessage());
            throw e;
        }
    }
}