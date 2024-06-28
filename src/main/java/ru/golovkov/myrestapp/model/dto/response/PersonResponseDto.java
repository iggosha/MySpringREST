package ru.golovkov.myrestapp.model.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PersonResponseDto {

    private Long id;

    private String name;

    private Integer age;

    private String email;

    private String password;

    private String role;

    private LocalDate registrationDate;

}
