package ru.golovkov.myrestapp.exception.entity;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MessageNotFoundException extends RuntimeException {

    public MessageNotFoundException(String message) {
        super(message);
    }

    public MessageNotFoundException() {
        super("No message was found");
    }

    public MessageNotFoundException(Long id) {
        super(STR."No message with id \{id} was found");
    }

}
