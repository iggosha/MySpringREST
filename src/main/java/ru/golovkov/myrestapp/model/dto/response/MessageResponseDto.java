package ru.golovkov.myrestapp.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageResponseDto {

    private Long id;

    private String content;

    private Long senderId;
    @JsonFormat(pattern = "HH:mm dd.MM.YYYY")
    private LocalDateTime sentAt;
}
