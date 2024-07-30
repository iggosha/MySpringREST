package ru.golovkov.myrestapp.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.golovkov.myrestapp.exception.httpcommon.ForbiddenException;
import ru.golovkov.myrestapp.model.dto.request.MessageRequestDto;
import ru.golovkov.myrestapp.model.dto.response.MessageResponseDto;
import ru.golovkov.myrestapp.security.PersonDetails;
import ru.golovkov.myrestapp.service.MessageService;

import java.util.List;

@RestController
@RequestMapping("${app.messages-url}")
@SecurityRequirement(name = "Authorization")
@AllArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/{receiverId}")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDto postMessage(@PathVariable Long receiverId,
                                          @RequestBody String content,
                                          @AuthenticationPrincipal PersonDetails personDetails) {
        Long principalId = personDetails.getPerson().getId();
        MessageRequestDto messageRequestDto = new MessageRequestDto(content, principalId, receiverId);
        return messageService.create(messageRequestDto);
    }


    @GetMapping("/with/{senderId}")
    public List<MessageResponseDto> getMessageListWithSenderByIds(@PathVariable Long senderId,
                                                                  @AuthenticationPrincipal PersonDetails personDetails,
    @ParameterObject @PageableDefault(sort = "sentAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long principalId = personDetails.getPerson().getId();
        return messageService.getListWithSenderByIds(principalId, senderId, pageable).reversed();
    }

    @PutMapping("/{id}")
    public MessageResponseDto putMessageById(@RequestBody String content,
                                             @AuthenticationPrincipal PersonDetails personDetails,
                                             @PathVariable Long id) {
        Long principalId = personDetails.getPerson().getId();
        MessageRequestDto messageRequestDto = new MessageRequestDto(content, principalId);
        return messageService.updateById(messageRequestDto, id);
    }

    @DeleteMapping("/{id}")
    public MessageResponseDto deleteMessageById(@PathVariable Long id,
                                                @AuthenticationPrincipal PersonDetails personDetails) {
        Long principalId = personDetails.getPerson().getId();
        MessageResponseDto messageResponseDto = messageService.getById(id);
        if (!messageResponseDto.getSenderId().equals(principalId)) {
            throw new ForbiddenException(STR
                    ."Another sender's messages aren't available for deleting! Current user's ID: \{
                    principalId}, sender's ID: \{messageResponseDto.getSenderId()})}");
        }
        messageService.deleteById(id);
        return messageResponseDto;
    }
}
