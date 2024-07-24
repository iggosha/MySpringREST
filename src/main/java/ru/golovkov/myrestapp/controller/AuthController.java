package ru.golovkov.myrestapp.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.golovkov.myrestapp.exc.BadRequestException;
import ru.golovkov.myrestapp.exc.ExceptionDetails;
import ru.golovkov.myrestapp.model.dto.request.PersonAuthRequestDto;
import ru.golovkov.myrestapp.model.dto.request.PersonRequestDto;
import ru.golovkov.myrestapp.security.JwtUtil;
import ru.golovkov.myrestapp.security.PersonDetails;
import ru.golovkov.myrestapp.service.PersonService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${app.base-url}")
@AllArgsConstructor
public class AuthController {

    private final PersonService personService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @SecurityRequirement(name = "Authorization")
    @GetMapping("/public/hello")
    @Operation(summary = "Приветствие пользователя")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Приветствие, либо вывод данных авторизованного пользователя",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(name = "Приветствие", value = """
                                            {
                                              "hello": "user"
                                            }""",
                                            description = "Приветствие, если пользователь не авторизован"),
                                    @ExampleObject(name = "Вывод данных пользователя", value = """
                                            {
                                              "role": "[ROLE_ROLE]",
                                              "encrypted password": "$encrypted$password",
                                              "name": "name"
                                            }
                                            """,
                                            description = "Данные пользователя, если он авторизован")
                            },
                            schema = @Schema(type = "object")
                    )}
            )
    })
    public Map<String, String> getHello(Authentication authentication) {
        if (authentication != null) {
            PersonDetails principal = (PersonDetails) authentication.getPrincipal();
            Map<String, String> helloMap = new HashMap<>();
            helloMap.put("name", principal.getUsername());
            helloMap.put("role", principal.getAuthorities().toString());
            helloMap.put("encrypted password", authentication.getCredentials().toString());
            return helloMap;
        }
        return Map.of("hello", "user");
    }

    @PostMapping(value = "/public/login")
    @Operation(summary = "Вход и получение JWT")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Выполнен вход, выдан JWT",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {@ExampleObject(
                                    name = "JWT",
                                    value = """
                                            {
                                              "jwt": "json.web.token"
                                            }""",
                                    description = "Успешно сгенерированный JWT")
                            },
                            schema = @Schema(type = "object")
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
    public Map<String, String> postLogin(@Valid @ParameterObject PersonAuthRequestDto personAuthRequestDto) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                personAuthRequestDto.getName(),
                personAuthRequestDto.getPassword()
        );
        verifyCredentials(authToken);
        return Map.of("jwt", jwtUtil.generateToken(personAuthRequestDto.getName()));
    }


    @PostMapping("/public/registration")
    @Operation(summary = "Регистрация")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Выполнена регистрация",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {@ExampleObject(
                                    name = "Созданный пользователь",
                                    value = """
                                            {
                                              "Created user": "PersonRequestDto(name=name, age=1, email=email@email.com, password=1)"
                                            }""",
                                    description = "Данные созданного пользователя")
                            },
                            schema = @Schema(type = "object")
                    )}
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    @Validated
    public Map<String, String> postRegistration(@Valid @ParameterObject PersonRequestDto personRequestDto) {
        personService.create(personRequestDto);
        return Map.of("Created user", personRequestDto.toString());
    }

    private void verifyCredentials(UsernamePasswordAuthenticationToken authToken) {
        try {
            authenticationManager.authenticate(authToken);
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Incorrect credentials");
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
