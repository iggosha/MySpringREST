package ru.golovkov.myrestapp.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.golovkov.myrestapp.exception.ExceptionDetails;
import ru.golovkov.myrestapp.exception.entity.WrongPasswordException;
import ru.golovkov.myrestapp.exception.httpcommon.BadRequestException;
import ru.golovkov.myrestapp.model.dto.request.PersonAuthRequestDto;
import ru.golovkov.myrestapp.model.dto.request.PersonRequestDto;
import ru.golovkov.myrestapp.model.dto.response.JwtResponseDto;
import ru.golovkov.myrestapp.model.dto.response.PersonResponseDto;
import ru.golovkov.myrestapp.model.entity.Person;
import ru.golovkov.myrestapp.security.JwtUtil;
import ru.golovkov.myrestapp.security.PersonDetails;
import ru.golovkov.myrestapp.service.PersonService;

@RestController
@RequestMapping("${app.people-url}")
@AllArgsConstructor
public class AuthController {

    private final PersonService personService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @SneakyThrows
    @SecurityRequirement(name = "Authorization")
    @GetMapping("/current-user")
    @Operation(summary = "Получение данных текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Авторизованный пользователь",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Person.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не авторизован",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionDetails.class)
                    )}
            )
    })
    public Person getHello(@AuthenticationPrincipal PersonDetails personDetails) {
        return personDetails.getPerson();
    }

    @PostMapping(value = "/public/login")
    @Operation(summary = "Вход и получение JWT")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Выполнен вход, выдан JWT",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = JwtResponseDto.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найден пользователь с такими данными",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionDetails.class)
                    )}
            )
    })
    public JwtResponseDto postLogin(@Valid @ParameterObject PersonAuthRequestDto personAuthRequestDto) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                personAuthRequestDto.getName(),
                personAuthRequestDto.getPassword()
        );
        verifyCredentials(authToken);
        return new JwtResponseDto(jwtUtil.generateToken(personAuthRequestDto.getName()));
    }


    @PostMapping("/public/registration")
    @Operation(summary = "Регистрация")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Зарегистрированный пользователь",
                    content = {@Content(
                            schema = @Schema(implementation = PersonResponseDto.class)
                    )}
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    @Validated
    public PersonResponseDto postRegistration(@Valid @ParameterObject PersonRequestDto personRequestDto) {
        return personService.create(personRequestDto);
    }

    private void verifyCredentials(UsernamePasswordAuthenticationToken authToken) {
        try {
            authenticationManager.authenticate(authToken);
        } catch (BadCredentialsException e) {
            throw new WrongPasswordException("Incorrect credentials");
        }
    }

    @ApiResponse(
            responseCode = "400",
            description = "Некорректные данные запроса",
            content = {@Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionDetails.class)
            )}
    )
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDetails handleBadRequestException(BadRequestException e) {
        return new ExceptionDetails(e.toString());
    }
}
