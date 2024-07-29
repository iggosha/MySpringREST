package ru.golovkov.myrestapp.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageRequestDto {

    @NotBlank
    private String content;

    private Long senderId;

    private Long receiverId;

    public MessageRequestDto(String content, Long senderId) {
        this.content = content;
        this.senderId = senderId;
    }
}
