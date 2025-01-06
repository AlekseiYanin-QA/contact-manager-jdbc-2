package com.example.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class Contact {

    @CsvBindByName(column = "ID")
    private Long id;

    @CsvBindByName(column = "Имя")
    @NotBlank(message = "Поле 'Имя' не может быть пустым")
    private String firstName;

    @CsvBindByName(column = "Фамилия")
    private String lastName;

    @CsvBindByName(column = "Номер телефона")
    private String phoneNumber;

    @CsvBindByName(column = "Email")
    @Email(message = "Некорректный формат email")
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}