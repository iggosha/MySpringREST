package ru.golovkov.myrestapp.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.golovkov.myrestapp.exception.entity.MessageNotFoundException;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class MessageServiceImplExceptionTest {

    private static Message mockMessage;
    private static MessageRequestDto mockMessageRequestDto;
    private static MessageResponseDto mockMessageResponseDto;
    private static List<Message> mockMessageList;
    private static List<MessageResponseDto> mockMessageResponseDtoList;
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
    }

    @Test
    void create_EntityExists_ThrowsDataIntegrityViolationException() {
        when(messageMapper.requestDtoToEntity(any())).thenReturn(mockMessage);
        when(messageRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DataIntegrityViolationException.class,
                () -> messageService.create(mockMessageRequestDto));

        verify(messageMapper).requestDtoToEntity(mockMessageRequestDto);
        verify(messageRepository).save(mockMessage);
        verify(messageMapper, never()).entityToResponseDto(any());
    }

    @Test
    void getById_EntityDoesntExist_ThrowsPersonNotFoundException() {
        when(messageRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(messageMapper.entityToResponseDto(any())).thenReturn(mockMessageResponseDto);

        assertThrows(MessageNotFoundException.class, () -> messageService.getById(id));

        verify(messageRepository).findById(senderId);
        verify(messageMapper, never()).entityToResponseDto(mockMessage);
    }

    @Test
    void getAll_EntitiesDontExist_ThrowsPersonNotFoundException() {
        when(messageRepository.findAll()).thenReturn(List.of());
        when(messageMapper.entityListToResponseDtoList(anyList())).thenReturn(List.of());

        assertThrows(MessageNotFoundException.class, () -> messageService.getAll());

        verify(messageRepository).findAll();
        verify(messageMapper, never()).entityListToResponseDtoList(mockMessageList);
    }

    @Test
    void getListFromSenderByIdAndContent_EntitiesDontExist_ThrowsPersonNotFoundException() {
        when(messageRepository
                .findAllByReceiver_IdAndSender_IdAndContentContainingIgnoreCase(
                        anyLong(),
                        anyLong(),
                        anyString(),
                        any()))
                .thenReturn(Page.empty());
        when(messageMapper.entityListToResponseDtoList(anyList())).thenReturn(mockMessageResponseDtoList);

        assertThrows(MessageNotFoundException.class,
                () -> messageService.getListFromSenderByIdAndContent(receiverId, senderId, content, pageRequest));

        verify(messageRepository).findAllByReceiver_IdAndSender_IdAndContentContainingIgnoreCase(receiverId, senderId, content, pageRequest);
        verify(messageMapper, never()).entityListToResponseDtoList(mockMessageList);
    }

    @Test
    void getListWithSenderByIdsAndContent() {
        when(messageRepository
                .findAllWithSenderByIdsAndContent(
                        anyLong(),
                        anyLong(),
                        anyString(),
                        any()))
                .thenReturn(Page.empty());
        when(messageMapper.entityListToResponseDtoList(anyList())).thenReturn(mockMessageResponseDtoList);

        assertThrows(MessageNotFoundException.class,
                () -> messageService.getListWithSenderByIdsAndContent(receiverId, senderId, content, pageRequest));

        verify(messageRepository).findAllWithSenderByIdsAndContent(receiverId, senderId, content, pageRequest);
        verify(messageMapper, never()).entityListToResponseDtoList(mockMessageList);
    }

    @Test
    void getListFromSenderById() {
        when(messageRepository
                .findAllByReceiver_IdAndSender_Id(
                        anyLong(),
                        anyLong(),
                        any()))
                .thenReturn(Page.empty());
        when(messageMapper.entityListToResponseDtoList(anyList())).thenReturn(mockMessageResponseDtoList);

        assertThrows(MessageNotFoundException.class,
                () -> messageService.getListFromSenderById(receiverId, senderId, pageRequest));

        verify(messageRepository).findAllByReceiver_IdAndSender_Id(receiverId, senderId, pageRequest);
        verify(messageMapper, never()).entityListToResponseDtoList(mockMessageList);
    }

    @Test
    void getListWithSenderByIds() {
        when(messageRepository
                .findAllWithSenderByIds(
                        anyLong(),
                        anyLong(),
                        any()))
                .thenReturn(Page.empty());
        when(messageMapper.entityListToResponseDtoList(anyList())).thenReturn(mockMessageResponseDtoList);

        assertThrows(MessageNotFoundException.class,
                () -> messageService.getListWithSenderByIds(receiverId, senderId, pageRequest));

        verify(messageRepository).findAllWithSenderByIds(receiverId, senderId, pageRequest);
        verify(messageMapper, never()).entityListToResponseDtoList(mockMessageList);
    }

    @Test
    void updateById() {
        when(messageRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(messageRepository.save(any())).thenReturn(mockMessage);
        when(messageMapper.entityToResponseDto(any())).thenReturn(mockMessageResponseDto);

        assertThrows(MessageNotFoundException.class, () -> messageService.updateById(mockMessageRequestDto, id));

        verify(messageRepository).findById(id);
        verify(messageMapper, never()).updateEntityFromRequestDto(mockMessage, mockMessageRequestDto);
        verify(messageRepository, never()).save(mockMessage);
        verify(messageMapper, never()).entityToResponseDto(mockMessage);
    }

    @Test
    void deleteById() {
        when(messageRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(MessageNotFoundException.class, () -> messageService.deleteById(id));

        verify(messageRepository).existsById(id);
        verify(messageRepository, never()).deleteById(id);
    }
}