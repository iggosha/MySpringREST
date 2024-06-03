package ru.golovkov.myrestapp.model.dto.request;

import lombok.Data;

@Data
public class PersonRequestDto {

    private String name;

    private Integer age;

    private String email;
}
