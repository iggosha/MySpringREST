package ru.golovkov.myrestapp.exception.entity;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class MessageOperationForbiddenException extends RuntimeException {

    public MessageOperationForbiddenException(OperationType operationType, Long principalId, Long senderId) {
        super(STR."Another sender's messages aren't available for \{
                operationType}! Current user's ID: \{
                principalId}, sender's ID: \{
                senderId}");
    }

    public enum OperationType {
        SENDING, SEARCHING, EDITING, DELETING
    }
}