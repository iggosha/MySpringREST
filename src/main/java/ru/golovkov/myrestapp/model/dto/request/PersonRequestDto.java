package ru.golovkov.myrestapp.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PersonRequestDto {

    @Size(min = 1, message = "Name must be at least 1 character long")
    private String name;
    @Positive(message = "Age must be positive")
    private Integer age;
    @Email(message = "Email must be valid")
    private String email;
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
}
