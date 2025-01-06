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

    public ContactDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public List<Contact> findAll() {
        return namedParameterJdbcTemplate.query(SqlQueries.SELECT_ALL_CONTACTS.getQuery(), new ContactRowMapper());
    }

    public void deleteAll() {
        try {
            namedParameterJdbcTemplate.update(SqlQueries.DELETE_ALL_CONTACTS.getQuery(), Map.of());
            logger.info("All contacts deleted successfully.");
        } catch (DataAccessException e) {
            logger.error("Error deleting all contacts: {}", e.getMessage());
            throw new DataAccessException("Не удалось удалить все контакты", e) {};
        }
    }

    public void deleteById(Long id) {
        try {
            int rowsAffected = namedParameterJdbcTemplate.update(SqlQueries.DELETE_CONTACT_BY_ID.getQuery(), Map.of("id", id));
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
        Integer count = namedParameterJdbcTemplate.queryForObject(SqlQueries.SELECT_COUNT_BY_ID.getQuery(), Map.of("id", id), Integer.class);
        return count != null && count > 0;
    }

    public Optional<Contact> findById(Long id) {
        List<Contact> contacts = namedParameterJdbcTemplate.query(SqlQueries.SELECT_CONTACT_BY_ID.getQuery(), Map.of("id", id), new ContactRowMapper());
        if (contacts.isEmpty()) {
            logger.warn("Контакт с ID {} не найден.", id);
            return Optional.empty();
        }
        return Optional.of(contacts.getFirst());
    }

    public void update(Contact contact) {
        try {
            int rowsAffected = namedParameterJdbcTemplate.update(SqlQueries.UPDATE_CONTACT.getQuery(), Map.of(
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
        Integer count = namedParameterJdbcTemplate.queryForObject(SqlQueries.SELECT_COUNT_BY_PHONE_NUMBER.getQuery(), Map.of("phoneNumber", phoneNumber), Integer.class);
        return count != null && count > 0;
    }

    public boolean existsByEmail(String email) {
        Integer count = namedParameterJdbcTemplate.queryForObject(SqlQueries.SELECT_COUNT_BY_EMAIL.getQuery(), Map.of("email", email), Integer.class);
        return count != null && count > 0;
    }

    public void batchInsert(List<Contact> contacts) {
        try {
            for (Contact contact : contacts) {
                namedParameterJdbcTemplate.update(SqlQueries.INSERT_CONTACT.getQuery(), Map.of(
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
