package com.example.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class Contact {
    @CsvBindByName(column = "Имя") // Убедитесь, что названия соответствуют заголовкам в CSV
    private String firstName;

    @CsvBindByName(column = "Фамилия")
    private String lastName;

    @CsvBindByName(column = "Номер телефона")
    private String phoneNumber;

    @CsvBindByName(column = "Email")
    private String email;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}