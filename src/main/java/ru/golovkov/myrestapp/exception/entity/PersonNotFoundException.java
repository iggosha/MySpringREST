package ru.golovkov.myrestapp.exception.entity;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PersonNotFoundException extends RuntimeException {

    public PersonNotFoundException(String message) {
        super(message);
    }

    public PersonNotFoundException(Long id) {
        super(STR."No person with id \{id} was found");
    }

    public PersonNotFoundException() {
        super("No person was found");
    }
}
