package com.example.dao;

import com.example.model.Contact;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class ContactDao {
    private final JdbcTemplate jdbcTemplate;

    public ContactDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void batchInsert(List<Contact> contacts) {
        String sql = "INSERT INTO contacts (first_name, last_name, phone_number, email) VALUES (?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, contacts, contacts.size(), (ps, contact) -> {
            ps.setString(1, contact.getFirstName());
            ps.setString(2, contact.getLastName());
            ps.setString(3, contact.getPhoneNumber());
            ps.setString(4, contact.getEmail());
        });
    }
}