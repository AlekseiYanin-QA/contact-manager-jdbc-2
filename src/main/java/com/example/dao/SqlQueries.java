package com.example.dao;

public enum SqlQueries {
    SELECT_ALL_CONTACTS("SELECT * FROM contacts"),
    DELETE_ALL_CONTACTS("DELETE FROM contacts"),
    DELETE_CONTACT_BY_ID("DELETE FROM contacts WHERE id = :id"),
    SELECT_COUNT_BY_ID("SELECT COUNT(*) FROM contacts WHERE id = :id"),
    SELECT_CONTACT_BY_ID("SELECT * FROM contacts WHERE id = :id"),
    UPDATE_CONTACT("UPDATE contacts SET first_name = :firstName, last_name = :lastName, phone_number = :phoneNumber, email = :email WHERE id = :id"),
    SELECT_COUNT_BY_PHONE_NUMBER("SELECT COUNT(*) FROM contacts WHERE phone_number = :phoneNumber"),
    SELECT_COUNT_BY_EMAIL("SELECT COUNT(*) FROM contacts WHERE email = :email"),
    INSERT_CONTACT("INSERT INTO contacts (first_name, last_name, phone_number, email) VALUES (:firstName, :lastName, :phoneNumber, :email)");

    private final String query;

    SqlQueries(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
