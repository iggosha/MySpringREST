package ru.golovkov.myrestapp.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PersonRequestDto {


    @NotBlank
    private String name;
    @NotNull
    private Integer age;
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
