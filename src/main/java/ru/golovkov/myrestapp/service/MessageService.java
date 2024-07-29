package ru.golovkov.myrestapp.service;

import org.springframework.data.domain.Pageable;
import ru.golovkov.myrestapp.model.dto.request.MessageRequestDto;
import ru.golovkov.myrestapp.model.dto.response.MessageResponseDto;

import java.util.List;

public interface MessageService extends CrudService<MessageRequestDto, MessageResponseDto> {

    List<MessageResponseDto> getListFromSenderByIdAndContent(Long receiverId, Long senderId, String content, Pageable pageable);

    List<MessageResponseDto> getListWithSenderByIdsAndContent(Long receiverId, Long senderId, String content, Pageable pageable);

    List<MessageResponseDto> getListFromSenderById(Long receiverId, Long senderId, Pageable pageable);

    List<MessageResponseDto> getListWithSenderByIds(Long receiverId, Long senderId, Pageable pageable);
}
