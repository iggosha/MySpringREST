package ru.golovkov.myrestapp.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import ru.golovkov.myrestapp.mapper.MessageMapper;
import ru.golovkov.myrestapp.model.dto.request.MessageRequestDto;
import ru.golovkov.myrestapp.model.dto.response.MessageResponseDto;
import ru.golovkov.myrestapp.model.entity.Message;
import ru.golovkov.myrestapp.model.entity.Person;
import ru.golovkov.myrestapp.repository.MessageRepository;
import ru.golovkov.myrestapp.service.MessageService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class MessageServiceImplTest {

    private static Message mockMessage;
    private static MessageRequestDto mockMessageRequestDto;
    private static MessageResponseDto mockMessageResponseDto;
    private static List<Message> mockMessageList;
    private static List<MessageResponseDto> mockMessageResponseDtoList;
    private static Page<Message> mockMessagePage;
    private static Long id;
    private static Long senderId;
    private static Long receiverId;
    private static String content;
    private static PageRequest pageRequest;
    @Autowired
    private MessageService messageService;
    @MockBean
    private MessageMapper messageMapper;
    @MockBean
    private MessageRepository messageRepository;

    @BeforeEach
    void setUp() {
        id = 1L;
        senderId = 1L;
        receiverId = 2L;
        Person sender = new Person();
        sender.setId(senderId);
        Person receiver = new Person();
        receiver.setId(receiverId);

        content = "hello";
        pageRequest = PageRequest.of(1, 10, Sort.by(Sort.Direction.ASC, "sentAt"));

        mockMessageRequestDto = new MessageRequestDto(content, senderId);

        mockMessage = new Message();
        mockMessage.setId(id);
        mockMessage.setContent(mockMessageRequestDto.getContent());
        mockMessage.setSender(sender);
        mockMessage.setReceiver(receiver);
        mockMessage.setSentAt(LocalDateTime.now());

        mockMessageResponseDto = new MessageResponseDto();
        mockMessageResponseDto.setId(mockMessage.getId());
        mockMessageResponseDto.setContent(mockMessage.getContent());
        mockMessageResponseDto.setSenderId(mockMessage.getSender().getId());
        mockMessageResponseDto.setSentAt(mockMessage.getSentAt());

        mockMessageList = new ArrayList<>(List.of(mockMessage));
        mockMessageResponseDtoList = new ArrayList<>(List.of(mockMessageResponseDto));
        mockMessagePage = new PageImpl<>(mockMessageList);
    }

    @Test
    void create() {
        when(messageMapper.requestDtoToEntity(mockMessageRequestDto)).thenReturn(mockMessage);
        when(messageRepository.save(any(Message.class))).thenReturn(mockMessage);
        when(messageMapper.entityToResponseDto(mockMessage)).thenReturn(mockMessageResponseDto);

        MessageResponseDto messageResponseDto = messageService.create(mockMessageRequestDto);

        assertEquals(mockMessageResponseDto, messageResponseDto);
        verify(messageMapper).requestDtoToEntity(mockMessageRequestDto);
        verify(messageRepository).save(mockMessage);
        verify(messageMapper).entityToResponseDto(mockMessage);
    }

    @Test
    void getById() {
        when(messageRepository.findById(anyLong())).thenReturn(Optional.of(mockMessage));
        when(messageMapper.entityToResponseDto(mockMessage)).thenReturn(mockMessageResponseDto);

        MessageResponseDto messageResponseDto = messageService.getById(id);

        assertEquals(mockMessageResponseDto, messageResponseDto);
        verify(messageRepository).findById(senderId);
        verify(messageMapper).entityToResponseDto(mockMessage);
    }

    @Test
    void getAll() {
        when(messageRepository.findAll()).thenReturn(mockMessageList);
        when(messageMapper.entityListToResponseDtoList(mockMessageList)).thenReturn(mockMessageResponseDtoList);

        List<MessageResponseDto> messageResponseDtoList = messageService.getAll();

        assertEquals(mockMessageResponseDtoList, messageResponseDtoList);
        verify(messageRepository).findAll();
        verify(messageMapper).entityListToResponseDtoList(mockMessageList);
    }

    @Test
    void getListFromSenderByIdAndContent() {
        when(messageRepository
                .findAllByReceiver_IdAndSender_IdAndContentContainingIgnoreCase(
                        anyLong(),
                        anyLong(),
                        anyString(),
                        any(Pageable.class)))
                .thenReturn(mockMessagePage);
        when(messageMapper.entityListToResponseDtoList(mockMessageList)).thenReturn(mockMessageResponseDtoList);

        List<MessageResponseDto> messageResponseDtoList =
                messageService.getListFromSenderByIdAndContent(receiverId, senderId, content, pageRequest);

        assertEquals(mockMessageResponseDtoList, messageResponseDtoList);
        verify(messageRepository).findAllByReceiver_IdAndSender_IdAndContentContainingIgnoreCase(receiverId, senderId, content, pageRequest);
        verify(messageMapper).entityListToResponseDtoList(mockMessageList);
    }

    @Test
    void getListWithSenderByIdsAndContent() {
        when(messageRepository
                .findAllWithSenderByIdsAndContent(
                        anyLong(),
                        anyLong(),
                        anyString(),
                        any(Pageable.class)))
                .thenReturn(mockMessagePage);
        when(messageMapper.entityListToResponseDtoList(mockMessageList)).thenReturn(mockMessageResponseDtoList);

        List<MessageResponseDto> messageResponseDtoList =
                messageService.getListWithSenderByIdsAndContent(receiverId, senderId, content, pageRequest);

        assertEquals(mockMessageResponseDtoList, messageResponseDtoList);
        verify(messageRepository).findAllWithSenderByIdsAndContent(receiverId, senderId, content, pageRequest);
        verify(messageMapper).entityListToResponseDtoList(mockMessageList);
    }

    @Test
    void getListFromSenderById() {
        when(messageRepository
                .findAllByReceiver_IdAndSender_Id(
                        anyLong(),
                        anyLong(),
                        any(Pageable.class)))
                .thenReturn(mockMessagePage);
        when(messageMapper.entityListToResponseDtoList(mockMessageList)).thenReturn(mockMessageResponseDtoList);

        List<MessageResponseDto> messageResponseDtoList =
                messageService.getListFromSenderById(receiverId, senderId, pageRequest);

        assertEquals(mockMessageResponseDtoList, messageResponseDtoList);
        verify(messageRepository).findAllByReceiver_IdAndSender_Id(receiverId, senderId, pageRequest);
        verify(messageMapper).entityListToResponseDtoList(mockMessageList);
    }

    @Test
    void getListWithSenderByIds() {
        when(messageRepository
                .findAllWithSenderByIds(
                        anyLong(),
                        anyLong(),
                        any(Pageable.class)))
                .thenReturn(mockMessagePage);
        when(messageMapper.entityListToResponseDtoList(mockMessageList)).thenReturn(mockMessageResponseDtoList);

        List<MessageResponseDto> messageResponseDtoList =
                messageService.getListWithSenderByIds(receiverId, senderId, pageRequest);

        assertEquals(mockMessageResponseDtoList, messageResponseDtoList);
        verify(messageRepository).findAllWithSenderByIds(receiverId, senderId, pageRequest);
        verify(messageMapper).entityListToResponseDtoList(mockMessageList);
    }

    @Test
    void updateById() {
        when(messageRepository.findById(anyLong())).thenReturn(Optional.of(mockMessage));
        when(messageRepository.save(any(Message.class))).thenReturn(mockMessage);
        when(messageMapper.entityToResponseDto(mockMessage)).thenReturn(mockMessageResponseDto);

        MessageResponseDto messageResponseDto = messageService.updateById(mockMessageRequestDto, id);

        assertEquals(mockMessageResponseDto, messageResponseDto);
        verify(messageRepository).findById(id);
        verify(messageRepository).save(mockMessage);
        verify(messageMapper).updateEntityFromRequestDto(mockMessage, mockMessageRequestDto);
    }

    @Test
    void deleteById() {
        when(messageRepository.existsById(anyLong())).thenReturn(true);

        messageService.deleteById(id);

        verify(messageRepository).deleteById(id);
    }
}