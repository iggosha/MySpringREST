package ru.golovkov.myrestapp.exc;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PersonNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDetails handle404(PersonNotFoundException e) {
        return new ExceptionDetails(e.toString());
    }

    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionDetails handle401(HttpClientErrorException.Unauthorized e) {
        return new ExceptionDetails(e.toString());
    }

//    @ExceptionHandler({DataIntegrityViolationException.class, IllegalArgumentException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ExceptionDetails handle400(RuntimeException e) {
//        return new ExceptionDetails(e.toString());
//    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDetails handleExc(Exception e) {
        return new ExceptionDetails(e.toString());
    }
}
