package ru.golovkov.myrestapp.exception;

import lombok.Data;

import java.util.Date;

@Data
public class ExceptionDetails {

    private String timestamp;

    private String message;

    public ExceptionDetails(String message) {
        this.timestamp = new Date().toString();
        this.message = message;
    }
}
