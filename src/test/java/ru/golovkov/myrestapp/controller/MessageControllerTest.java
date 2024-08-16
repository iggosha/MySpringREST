package ru.golovkov.myrestapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.golovkov.myrestapp.model.dto.request.MessageRequestDto;
import ru.golovkov.myrestapp.model.dto.response.MessageResponseDto;
import ru.golovkov.myrestapp.model.entity.Message;
import ru.golovkov.myrestapp.model.entity.Person;
import ru.golovkov.myrestapp.security.JwtUtil;
import ru.golovkov.myrestapp.security.PersonDetails;
import ru.golovkov.myrestapp.service.MessageService;
import ru.golovkov.myrestapp.service.PersonDetailsService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
@AutoConfigureMockMvc(addFilters = false)
class MessageControllerTest {

    private static long id1;
    private static String content1;
    private static Person sender;
    private static Person receiver;
    private static MessageResponseDto messageResponseDto1;
    private static List<MessageResponseDto> messageResponseDtoList;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MessageService messageService;
    @MockBean
    private PersonDetailsService personDetailsService;
    @MockBean
    private JwtUtil jwtUtil;
    @InjectMocks
    private MessageController messageController;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        id1 = 1L;
        long id2 = 2L;
        content1 = "content1";
        String content2 = "content2";

        sender = new Person();
        sender.setId(id1);
        sender.setName("name");
        sender.setPassword("password");
        sender.setAge(52);
        sender.setEmail("email");
        sender.setRegistrationDate(LocalDate.now());
        sender.setReceivedMessages(List.of());
        sender.setSentMessages(List.of());

        receiver = new Person();
        receiver.setId(id2);

        MessageRequestDto messageRequestDto1 = new MessageRequestDto(content1, id1, id2);
        MessageRequestDto messageRequestDto2 = new MessageRequestDto(content2, id2, id1);

        Message message1 = new Message();
        message1.setId(id1);
        message1.setContent(messageRequestDto1.getContent());
        message1.setSender(sender);
        message1.setReceiver(receiver);
        message1.setSentAt(LocalDateTime.now());

        Message message2 = new Message();
        message2.setId(id2);
        message2.setContent(messageRequestDto2.getContent());
        message2.setSender(receiver);
        message2.setReceiver(sender);
        message2.setSentAt(LocalDateTime.now());

        messageResponseDto1 = new MessageResponseDto();
        messageResponseDto1.setId(id1);
        messageResponseDto1.setContent(message1.getContent());
        messageResponseDto1.setSenderId(message1.getSender().getId());
        messageResponseDto1.setSentAt(message1.getSentAt());

        MessageResponseDto messageResponseDto2 = new MessageResponseDto();
        messageResponseDto2.setId(id2);
        messageResponseDto2.setContent(message2.getContent());
        messageResponseDto2.setSenderId(message2.getSender().getId());
        messageResponseDto2.setSentAt(message2.getSentAt());

        messageResponseDtoList = List.of(messageResponseDto1, messageResponseDto2);

        PersonDetails personDetails = new PersonDetails(sender);

        Authentication authentication = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return personDetails.getAuthorities();
            }

            @Override
            public Object getCredentials() {
                return personDetails.getPassword();
            }

            @Override
            public Object getDetails() {
                return personDetails;
            }

            @Override
            public Object getPrincipal() {
                return personDetails;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return personDetails.getUsername();
            }
        };

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @WithMockUser
    void postMessage() throws Exception {
        when(messageService.create(any(MessageRequestDto.class))).thenReturn(messageResponseDto1);

        mockMvc.perform(post("/api/v1/messages/{receiverId}", receiver.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content1)
                )
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(messageResponseDto1)));
    }

    @Test
    @WithMockUser
    void getMessageListWithSenderByIds() throws Exception {
        when(messageService.getListWithSenderByIds(anyLong(),
                anyLong(),
                any(Pageable.class))
        ).thenReturn(messageResponseDtoList);

        mockMvc.perform(get("/api/v1/messages/with/{senderId}", sender.getId())
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "sentAt,desc")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(messageResponseDtoList)));
    }

    @Test
    @WithMockUser
    void putMessageById() throws Exception {
        when(messageService.updateById(any(MessageRequestDto.class), anyLong())).thenReturn(messageResponseDto1);

        mockMvc.perform(put("/api/v1/messages/{id}", id1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content1)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(messageResponseDto1)));
    }

    @Test
    @WithMockUser
    void deleteMessageById() throws Exception {
        when(messageService.getById(anyLong())).thenReturn(messageResponseDto1);

        mockMvc.perform(delete("/api/v1/messages/{id}", id1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(messageResponseDto1)));
    }
}
