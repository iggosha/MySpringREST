package ru.golovkov.myrestapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.golovkov.myrestapp.exc.BadRequestException;
import ru.golovkov.myrestapp.exc.ExceptionDetails;
import ru.golovkov.myrestapp.exc.UnauthorizedException;
import ru.golovkov.myrestapp.model.dto.request.PersonRequestDto;
import ru.golovkov.myrestapp.model.dto.response.PersonResponseDto;
import ru.golovkov.myrestapp.service.PersonService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${app.base-url}")
@SecurityRequirement(name = "Authorization")
@AllArgsConstructor
public class PersonController {

    //http://localhost:8888/swagger-ui/index.html#/person-controller

    private final PersonService personService;

    @Operation(summary = "Получение списка пользователей")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список пользователей",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = List.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ни одного пользователя не найдено",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = List.class)
                    )}
            )
    })
    @GetMapping("")
    public ResponseEntity<List<PersonResponseDto>> getPersonList(@RequestParam(name = "nameToSearch", required = false) String name) {
        List<PersonResponseDto> personResponseDtos;
        if (name != null && !name.isBlank()) {
            personResponseDtos = personService.getAllByNameContaining(name);
        } else {
            personResponseDtos = personService.getAll();
        }
        if (personResponseDtos.isEmpty()) {
            return new ResponseEntity<>(personResponseDtos, HttpStatusCode.valueOf(404));
        }
        return new ResponseEntity<>(personResponseDtos, HttpStatusCode.valueOf(200));
    }

    @Operation(summary = "Получение пользователя по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь найден",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PersonResponseDto.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найден пользователь с таким ID",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionDetails.class)
                    )}
            )
    })
    @GetMapping("/{id}")
    public PersonResponseDto getPersonById(@PathVariable("id") Long id) {
        return personService.getById(id);
    }

    @Operation(summary = "Получение пользователя по имени")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь с запрашиваемым именем найден",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PersonResponseDto.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найден пользователь с таким именем",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionDetails.class)
                    )}
            )
    })
    @GetMapping("/one")
    public PersonResponseDto getPersonByName(@RequestParam(name = "nameToSearch") String name) {
        return personService.getByName(name);
    }

    @Operation(summary = "Изменение данных пользователя по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь изменён",
                    content = {@Content(
                            examples = {
                                    @ExampleObject(name = "Сообщение об изменении", value = """
                                            {
                                              "changed": "user with id 1"
                                            }""",
                                            description = "Сообщение об изменении пользователя с указанием его ID")
                            },
                            schema = @Schema(type = "object"),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )}
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Пользователь не имеет требуемых прав",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionDetails.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найден пользователь с таким ID",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionDetails.class)
                    )}
            )
    })
    @PutMapping("/admin/{id}")
    public Map<String, String> updatePersonById(@ParameterObject PersonRequestDto personRequestDto,
                                                @PathVariable("id") Long id) {
        personService.updateById(personRequestDto, id);
        return Map.of("changed", STR."user with id \{id}");
    }

    @Operation(summary = "Изменение данных пользователя по имени")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь с запрашиваемым именем изменён",
                    content = {@Content(
                            examples = {
                                    @ExampleObject(name = "Сообщение об изменении", value = """
                                            {
                                              "changed": "user with name 'name'"
                                            }""",
                                            description = "Сообщение об изменении пользователя с указанием его имени")
                            },
                            schema = @Schema(type = "object"),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )}
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Пользователь не имеет требуемых прав",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionDetails.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найден пользователь с таким именем",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionDetails.class)
                    )}
            )
    })
    @PutMapping("/admin")
    public Map<String, String> updatePersonByName(@ParameterObject PersonRequestDto personRequestDto,
                                                  @RequestParam("nameToSearch") String name) {
        personService.updateByName(personRequestDto, name);
        return Map.of("changed", STR."user with name '\{name}'");
    }

    @Operation(summary = "Удаление пользователя по ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь удалён",
                    content = {@Content(
                            examples = {
                                    @ExampleObject(name = "Сообщение об удалении", value = """
                                            {
                                              "deleted": "user with id 1"
                                            }""",
                                            description = "Сообщение об удалении пользователя с указанием его ID")
                            },
                            schema = @Schema(type = "object"),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )}
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Пользователь не имеет требуемых прав",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionDetails.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найден пользователь с таким ID",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionDetails.class)
                    )}
            )
    })
    @DeleteMapping("/admin/{id}")
    public Map<String, String> deletePersonById(@PathVariable("id") Long id) {
        personService.deleteById(id);
        return Map.of("deleted", STR."user with id \{id}");
    }

    @Operation(summary = "Удаление пользователя по имени")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь с запрашиваемым именем удалён",
                    content = {@Content(
                            examples = {
                                    @ExampleObject(name = "Сообщение об удалении", value = """
                                            {
                                              "deleted": "user with name 'name'"
                                            }""",
                                            description = "Сообщение об удалении пользователя с указанием его имени")
                            },
                            schema = @Schema(type = "object"),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )}
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Пользователь не имеет требуемых прав",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionDetails.class)
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Не найден пользователь с таким именем",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionDetails.class)
                    )}
            )
    })
    @DeleteMapping("/admin")
    public Map<String, String> deletePersonByName(@RequestParam(name = "nameToSearch") String name) {
        personService.deleteByName(name);
        return Map.of("deleted", STR."user with name '\{name}'");
    }

    @ApiResponse(
            responseCode = "400",
            description = "Некорректные данные авторизации",
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

    @ApiResponse(
            responseCode = "401",
            description = "Пользователь не авторизован",
            content = {@Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionDetails.class)
            )}
    )
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionDetails handleUnauthorizedException(UnauthorizedException e) {
        return new ExceptionDetails(e.toString());
    }
}
