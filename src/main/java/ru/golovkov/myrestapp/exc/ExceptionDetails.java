package ru.golovkov.myrestapp.exc;

import lombok.Data;

import java.util.Date;

@Data
public class ExceptionDetails {

    private Date timestamp;

    private String message;

    public ExceptionDetails(String message) {
        this.timestamp = new Date();
        this.message = message;
    }
}
