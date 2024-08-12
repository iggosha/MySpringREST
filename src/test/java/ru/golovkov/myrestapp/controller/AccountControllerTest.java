package ru.golovkov.myrestapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.golovkov.myrestapp.exception.entity.WrongPasswordException;
import ru.golovkov.myrestapp.model.dto.request.PersonAuthRequestDto;
import ru.golovkov.myrestapp.model.dto.request.PersonRequestDto;
import ru.golovkov.myrestapp.model.dto.response.JwtResponseDto;
import ru.golovkov.myrestapp.model.dto.response.PersonResponseDto;
import ru.golovkov.myrestapp.model.entity.Person;
import ru.golovkov.myrestapp.security.JwtUtil;
import ru.golovkov.myrestapp.security.PersonDetails;
import ru.golovkov.myrestapp.service.PersonDetailsService;
import ru.golovkov.myrestapp.service.PersonService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    private static String jwtToken;
    private static PersonAuthRequestDto personAuthRequestDto;
    private static PersonRequestDto personRequestDto;
    private static PersonResponseDto personResponseDto;
    private static Person person;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PersonService personService;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private PersonDetailsService personDetailsService;
    @MockBean
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private AccountController accountController;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        String name = "name";
        String password = "password";
        jwtToken = "token";
        String email = "email@mail.com";
        long id = 1L;
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

        personAuthRequestDto = new PersonAuthRequestDto();
        personAuthRequestDto.setName(name);
        personAuthRequestDto.setPassword(password);

        personRequestDto = new PersonRequestDto();
        personRequestDto.setName(name);
        personRequestDto.setPassword(password);
        personRequestDto.setAge(age);
        personRequestDto.setEmail(email);

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
    @WithMockUser
    void getCurrentUser() {
        mockMvc.perform(get("/api/people/current-user")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(person)));
    }

    @SneakyThrows
    @Test
    void postLogin() {
        when(jwtUtil.generateToken(any(String.class))).thenReturn(jwtToken);

        JwtResponseDto jwtResponseDto = new JwtResponseDto(jwtToken);
        mockMvc.perform(post("/api/people/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personAuthRequestDto))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(jwtResponseDto)));
    }

    @Test
    void postLoginThrowsWrongPasswordException() throws Exception {
        when(authenticationManager.authenticate(any())).thenThrow(WrongPasswordException.class);
        mockMvc.perform(post("/api/people/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personAuthRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void postRegistration() {
        when(personService.create(any(PersonRequestDto.class))).thenReturn(personResponseDto);
        mockMvc.perform(post("/api/people/public/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personRequestDto))
                )
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(personResponseDto)));
    }

    @SneakyThrows
    @Test
    @WithMockUser(roles = "BASE")
    void updateCurrentUser() {
        when(personService.updateById(any(PersonRequestDto.class), anyLong())).thenReturn(personResponseDto);
        when(jwtUtil.generateToken(any(String.class))).thenReturn(jwtToken);
        JwtResponseDto jwtResponseDto = new JwtResponseDto(jwtToken);

        mockMvc.perform(put("/api/people/current-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personRequestDto))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(jwtResponseDto)));
    }
}
