package ru.golovkov.myrestapp.exception;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.golovkov.myrestapp.exception.entity.PersonNotFoundException;
import ru.golovkov.myrestapp.exception.entity.WrongPasswordException;
import ru.golovkov.myrestapp.exception.httpcommon.BadRequestException;
import ru.golovkov.myrestapp.exception.httpcommon.ForbiddenException;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PersonNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDetails handlePersonNotFoundException(PersonNotFoundException e) {
        return new ExceptionDetails(e.toString());
    }

    @ExceptionHandler({BadRequestException.class,
            IllegalArgumentException.class,
            DataIntegrityViolationException.class,
            WrongPasswordException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDetails handleBadRequestException(RuntimeException e) {
        return new ExceptionDetails(e.toString());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDetails handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ExceptionDetails(e.toString());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionDetails handleForbiddenException(ForbiddenException e) {
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
