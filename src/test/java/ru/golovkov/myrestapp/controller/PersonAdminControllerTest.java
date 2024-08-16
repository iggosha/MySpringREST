package ru.golovkov.myrestapp.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.golovkov.myrestapp.model.dto.request.PersonRequestDto;
import ru.golovkov.myrestapp.model.dto.response.PersonResponseDto;
import ru.golovkov.myrestapp.model.entity.Person;
import ru.golovkov.myrestapp.security.JwtUtil;
import ru.golovkov.myrestapp.security.PersonDetails;
import ru.golovkov.myrestapp.service.PersonDetailsService;
import ru.golovkov.myrestapp.service.PersonService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class PersonAdminControllerTest {

    private static PersonRequestDto personRequestDto;
    private static PersonResponseDto personResponseDto;
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

        person = new Person();
        person.setId(id);
        person.setName(name);
        person.setPassword(password);
        person.setAge(age);
        person.setEmail(email);
        person.setRegistrationDate(LocalDate.now());
        person.setReceivedMessages(List.of());
        person.setSentMessages(List.of());

        personRequestDto = new PersonRequestDto();
        personRequestDto.setName(name);
        personRequestDto.setPassword(password);
        personRequestDto.setAge(age);
        personRequestDto.setEmail(email);

        personResponseDto = new PersonResponseDto();
        personResponseDto.setName(name);
        personResponseDto.setPassword(password);
        personResponseDto.setAge(age);
        personResponseDto.setEmail(email);


        PersonDetails personDetails = new PersonDetails(person);

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

    @SneakyThrows
    @Test
    @WithMockUser(roles = "ADMIN")
    void upgradeRole() {
        when(personService.upgradeRole(anyString(), anyLong())).thenReturn(personResponseDto);
        mockMvc.perform(put("/api/v1/people/admin/upgrade-role")
                        .param("rawPasswordOfPerson", "password")
                        .param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(personResponseDto)));
    }

    @SneakyThrows
    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePersonByName() {
        when(personService.getByName(anyString())).thenReturn(personResponseDto);
        doNothing().when(personService).deleteByName(anyString());
        mockMvc.perform(delete("/api/v1/people/admin")
                        .param("nameToSearch", "name")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(personResponseDto)));
    }

    @SneakyThrows
    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePersonById() {
        when(personService.updateById(any(PersonRequestDto.class), anyLong())).thenReturn(personResponseDto);
        mockMvc.perform(put("/api/v1/people/admin/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personRequestDto))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(personResponseDto)));
    }

    @SneakyThrows
    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePersonByName() {
        when(personService.updateByName(any(PersonRequestDto.class), anyString())).thenReturn(personResponseDto);
        mockMvc.perform(put("/api/v1/people/admin")
                        .param("nameToSearch", "name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personRequestDto))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(personResponseDto)));
    }

    @SneakyThrows
    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePersonById() {
        when(personService.getById(anyLong())).thenReturn(personResponseDto);
        doNothing().when(personService).deleteById(anyLong());
        mockMvc.perform(delete("/api/v1/people/admin/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(personResponseDto)));
    }
}
