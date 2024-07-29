package ru.golovkov.myrestapp.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.golovkov.myrestapp.exception.entity.MessageNotFoundException;
import ru.golovkov.myrestapp.exception.httpcommon.ForbiddenException;
import ru.golovkov.myrestapp.mapper.MessageMapper;
import ru.golovkov.myrestapp.model.dto.request.MessageRequestDto;
import ru.golovkov.myrestapp.model.dto.response.MessageResponseDto;
import ru.golovkov.myrestapp.model.entity.Message;
import ru.golovkov.myrestapp.repository.MessageRepository;
import ru.golovkov.myrestapp.service.MessageService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    @Override
    public MessageResponseDto create(MessageRequestDto requestDto) {
        Message message = messageMapper.requestDtoToEntity(requestDto);
        message.setSentAt(LocalDateTime.now());
        message = messageRepository.save(message);
        return messageMapper.entityToResponseDto(message);
    }

    @Override
    public MessageResponseDto getById(Long id) {
        return messageMapper.entityToResponseDto(getMessageById(id));
    }

    @Override
    public List<MessageResponseDto> getAll() {
        return messageMapper.entityListToResponseDtoList(messageRepository.findAll());
    }

    @Transactional(readOnly = true)
    @Override
    public List<MessageResponseDto> getListFromSenderByIdAndContent(Long receiverId, Long senderId, String content, Pageable pageable) {
        List<Message> messageList = messageRepository
                .findAllByReceiver_IdAndSender_IdAndContentContainingIgnoreCase(receiverId, senderId, content, pageable)
                .toList();
        return messageMapper.entityListToResponseDtoList(messageList);
    }

    @Transactional(readOnly = true)
    @Override
    public List<MessageResponseDto> getListWithSenderByIdsAndContent(Long receiverId, Long senderId, String content, Pageable pageable) {
        List<Message> messageList = messageRepository
                .getPageWithSenderByIdsAndContent(receiverId, senderId, content, pageable)
                .stream().toList();
        return messageMapper.entityListToResponseDtoList(messageList);
    }

    @Transactional(readOnly = true)
    @Override
    public List<MessageResponseDto> getListFromSenderById(Long receiverId, Long senderId, Pageable pageable) {
        List<Message> messageList = messageRepository
                .findAllByReceiver_IdAndSender_Id(receiverId, senderId, pageable)
                .toList();
        return messageMapper.entityListToResponseDtoList(messageList);
    }

    @Transactional(readOnly = true)
    @Override
    public List<MessageResponseDto> getListWithSenderByIds(Long receiverId, Long senderId, Pageable pageable) {
        List<Message> messageList = messageRepository
                .getPageWithSenderByIds(receiverId, senderId, pageable)
                .toList();
        return messageMapper.entityListToResponseDtoList(messageList);
    }

    @Override
    public MessageResponseDto updateById(MessageRequestDto requestDto, Long messageId) {
        Message message = getMessageById(messageId);
        if (!message.getSender().getId().equals(requestDto.getSenderId())) {
            throw new ForbiddenException(STR."Another sender's messages aren't available for editing! Current user's ID: \{message.getSender().getId()}, sender's ID: \{requestDto.getSenderId()}");
        }
        messageMapper.updateEntityFromRequestDto(message, requestDto);
        message = messageRepository.save(message);
        return messageMapper.entityToResponseDto(message);
    }

    @Override
    public void deleteById(Long id) {
        checkIfMessageExistsById(id);
        messageRepository.deleteById(id);
    }

    private Message getMessageById(Long id) {
        return messageRepository
                .findById(id)
                .orElseThrow(() -> new MessageNotFoundException(STR."No message with id \{id} was found"));
    }

    private void checkIfMessageExistsById(Long id) {
        if (!messageRepository.existsById(id)) {
            throw new MessageNotFoundException(STR."No message with id \{id} was found");
        }
    }
}
