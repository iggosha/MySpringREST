package ru.golovkov.myrestapp.exc;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WrongPasswordException extends RuntimeException {

    public WrongPasswordException() {
        super("Password is incorrect");
    }

    public WrongPasswordException(String message) {
        super(message);
    }
}