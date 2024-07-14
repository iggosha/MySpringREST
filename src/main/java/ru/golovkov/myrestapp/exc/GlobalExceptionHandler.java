package ru.golovkov.myrestapp.exc;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PersonNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDetails handlePersonNotFoundException(PersonNotFoundException e) {
        return new ExceptionDetails(e.toString());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDetails handleBadRequestException(BadRequestException e) {
        return new ExceptionDetails(e.toString());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDetails handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return new ExceptionDetails(e.toString());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionDetails> handleRuntimeException(RuntimeException e) {
        if (e.getCause() instanceof PersonNotFoundException personNotFoundException) {
            return new ResponseEntity<>(new ExceptionDetails(personNotFoundException.toString()), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(new ExceptionDetails(e.toString()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
