package ru.golovkov.myrestapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.golovkov.myrestapp.model.dto.response.PersonResponseDto;
import ru.golovkov.myrestapp.model.entity.Person;
import ru.golovkov.myrestapp.security.JwtUtil;
import ru.golovkov.myrestapp.service.PersonDetailsService;
import ru.golovkov.myrestapp.service.PersonService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonSearchController.class)
@AutoConfigureMockMvc(addFilters = false)
class PersonSearchControllerTest {

    private static PersonResponseDto personResponseDto;
    private static List<PersonResponseDto> personResponseDtoList;

    private static Person person;
    private static long id;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PersonDetailsService personDetailsService;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private PersonService personService;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        String name = "name";
        String password = "password";
        String email = "email@mail.com";
        id = 1L;
        int age = 52;

        personResponseDto = new PersonResponseDto();
        personResponseDto.setName(name);
        personResponseDto.setPassword(password);
        personResponseDto.setAge(age);
        personResponseDto.setEmail(email);

        personResponseDtoList = List.of(personResponseDto);
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void getPersonList() {
        when(personService.getAllByNameContaining(anyString(), any(Pageable.class))).thenReturn(personResponseDtoList);
        mockMvc.perform(get("/api/v1/people")
                        .param("nameToSearch", "name")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "name,asc")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(personResponseDtoList)));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void getPersonById() {
        when(personService.getById(anyLong())).thenReturn(personResponseDto);
        mockMvc.perform(get("/api/v1/people/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(personResponseDto)));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void getPersonByName() {
        when(personService.getByName(anyString())).thenReturn(personResponseDto);
        mockMvc.perform(get("/api/v1/people/search")
                        .param("nameToSearch", "name")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(personResponseDto)));
    }
}