package ru.golovkov.myrestapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.golovkov.myrestapp.exception.ExceptionDetails;
import ru.golovkov.myrestapp.exception.entity.MessageNotFoundException;
import ru.golovkov.myrestapp.exception.entity.MessageOperationForbiddenException;
import ru.golovkov.myrestapp.exception.httpcommon.UnauthorizedException;
import ru.golovkov.myrestapp.model.dto.response.MessageResponseDto;
import ru.golovkov.myrestapp.security.PersonDetails;
import ru.golovkov.myrestapp.service.MessageService;

import java.util.List;

@RestController
@RequestMapping("${app.url.messages}")
@SecurityRequirement(name = "Authorization")
@AllArgsConstructor
public class MessageSearchController {

    private final MessageService messageService;

    @Operation(summary = "Получение сообщения по ID")
    @ApiResponse(
            responseCode = "200",
            description = "Успешно найденное сообщение",
            content = {@Content(
                    schema = @Schema(implementation = MessageResponseDto.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE
            )}
    )
    @ApiResponse(
            responseCode = "403",
            description = "Запрещено искать сообщения других пользователей",
            content = {@Content(
                    schema = @Schema(implementation = ExceptionDetails.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE
            )}
    )
    @GetMapping("/{id}")
    public MessageResponseDto getMessageById(@PathVariable Long id,
                                             @AuthenticationPrincipal PersonDetails personDetails) {
        Long principalId = personDetails.getPerson().getId();
        MessageResponseDto messageResponseDto = messageService.getById(id);
        throwExceptionIfPrincipalIsNotSender(messageResponseDto, principalId);
        return messageResponseDto;
    }

    @Operation(summary = "Получение списка сообщений от другого пользователя")
    @ApiResponse(
            responseCode = "200",
            description = "Успешно полученный список сообщений от другого пользователя",
            content = {@Content(
                    array = @ArraySchema(schema = @Schema(implementation = MessageResponseDto.class)),
                    mediaType = MediaType.APPLICATION_JSON_VALUE
            )}
    )
    @GetMapping("/from/{senderId}")
    public List<MessageResponseDto> getMessageListFromSenderById(@PathVariable Long senderId,
                                                                 @AuthenticationPrincipal PersonDetails personDetails,
                                                                 @ParameterObject @PageableDefault Pageable pageable) {
        Long principalId = personDetails.getPerson().getId();
        return messageService.getListFromSenderById(principalId, senderId, pageable);
    }

    @Operation(summary = "Получение сообщений от другого пользователя по содержанию")
    @ApiResponse(
            responseCode = "200",
            description = "Успешно полученные сообщения от другого пользователя по содержанию",
            content = {@Content(
                    array = @ArraySchema(schema = @Schema(implementation = MessageResponseDto.class)),
                    mediaType = MediaType.APPLICATION_JSON_VALUE
            )}
    )
    @GetMapping("/search-from/{senderId}")
    public List<MessageResponseDto> getMessageListFromSenderByIdAndContent(@PathVariable Long senderId,
                                                                           @RequestParam String content,
                                                                           @AuthenticationPrincipal PersonDetails personDetails,
                                                                           @ParameterObject @PageableDefault Pageable pageable) {
        Long principalId = personDetails.getPerson().getId();
        return messageService.getListFromSenderByIdAndContent(principalId, senderId, content, pageable);
    }

    @Operation(summary = "Получение сообщений диалога с другим пользователем по содержанию")
    @ApiResponse(
            responseCode = "200",
            description = "Успешно полученные сообщения диалога с другим пользователем по содержанию",
            content = {@Content(
                    array = @ArraySchema(schema = @Schema(implementation = MessageResponseDto.class)),
                    mediaType = MediaType.APPLICATION_JSON_VALUE
            )}
    )
    @GetMapping("/search-with/{senderId}")
    public List<MessageResponseDto> getMessageListWithSenderByIdsAndContent(@PathVariable Long senderId,
                                                                            @RequestParam String content,
                                                                            @AuthenticationPrincipal PersonDetails personDetails,
                                                                            @ParameterObject @PageableDefault Pageable pageable) {
        Long principalId = personDetails.getPerson().getId();
        return messageService.getListWithSenderByIdsAndContent(principalId, senderId, content, pageable);
    }

    private void throwExceptionIfPrincipalIsNotSender(MessageResponseDto messageResponseDto, Long principalId) {
        if (!messageResponseDto.getSenderId().equals(principalId)) {
            throw new MessageOperationForbiddenException(
                    MessageOperationForbiddenException.OperationType.SEARCHING,
                    principalId,
                    messageResponseDto.getSenderId()
            );
        }
    }

    @ApiResponse(
            responseCode = "404",
            description = "Ни одного сообщения не найдено",
            content = {@Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionDetails.class)
            )}
    )
    @ExceptionHandler(MessageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDetails handleMessageNotFoundException(MessageNotFoundException e) {
        return new ExceptionDetails(e.toString());
    }

    @ApiResponse(
            responseCode = "401",
            description = "Пользователь не авторизован",
            content = {@Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionDetails.class)
            )}
    )
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionDetails handleUnauthorizedException(UnauthorizedException e) {
        return new ExceptionDetails(e.toString());
    }
}
