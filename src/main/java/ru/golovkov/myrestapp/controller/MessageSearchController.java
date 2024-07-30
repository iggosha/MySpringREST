package ru.golovkov.myrestapp.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.golovkov.myrestapp.exception.httpcommon.ForbiddenException;
import ru.golovkov.myrestapp.model.dto.response.MessageResponseDto;
import ru.golovkov.myrestapp.security.PersonDetails;
import ru.golovkov.myrestapp.service.MessageService;

import java.util.List;

@RestController
@RequestMapping("${app.messages-url}")
@SecurityRequirement(name = "Authorization")
@AllArgsConstructor
public class MessageSearchController {

    private final MessageService messageService;

    @GetMapping("/{id}")
    public MessageResponseDto getMessageById(@PathVariable Long id,
                                             @AuthenticationPrincipal PersonDetails personDetails) {
        Long principalId = personDetails.getPerson().getId();
        MessageResponseDto messageResponseDto = messageService.getById(id);
        if (!messageResponseDto.getSenderId().equals(principalId)) {
            throw new ForbiddenException(STR.
                    "Another sender's messages aren't available for searching by ID! Current user's ID: \{
                            principalId}, sender's ID: \{messageResponseDto.getSenderId()}");
        }
        return messageResponseDto;
    }

    @GetMapping("/from/{senderId}")
    public List<MessageResponseDto> getMessageListFromSenderById(@PathVariable Long senderId,
                                                                 @AuthenticationPrincipal PersonDetails personDetails,
                                                                 @ParameterObject @PageableDefault Pageable pageable) {
        Long principalId = personDetails.getPerson().getId();
        return messageService.getListFromSenderById(principalId, senderId, pageable);
    }

    @GetMapping("/search-from/{senderId}")
    public List<MessageResponseDto> getMessageListFromSenderByIdAndContent(@PathVariable Long senderId,
                                                                           @RequestParam String content,
                                                                           @AuthenticationPrincipal PersonDetails personDetails,
                                                                           @ParameterObject @PageableDefault Pageable pageable) {
        Long principalId = personDetails.getPerson().getId();
        return messageService.getListFromSenderByIdAndContent(principalId, senderId, content, pageable);
    }

    @GetMapping("/search-with/{senderId}")
    public List<MessageResponseDto> getMessageListWithSenderByIdsAndContent(@PathVariable Long senderId,
                                                                            @RequestParam String content,
                                                                            @AuthenticationPrincipal PersonDetails personDetails,
                                                                            @ParameterObject @PageableDefault Pageable pageable) {
        Long principalId = personDetails.getPerson().getId();
        return messageService.getListWithSenderByIdsAndContent(principalId, senderId, content, pageable);
    }
}
