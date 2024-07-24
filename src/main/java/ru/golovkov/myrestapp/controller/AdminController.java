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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.golovkov.myrestapp.exc.BadRequestException;
import ru.golovkov.myrestapp.exc.ExceptionDetails;
import ru.golovkov.myrestapp.exc.ForbiddenException;
import ru.golovkov.myrestapp.exc.UnauthorizedException;
import ru.golovkov.myrestapp.model.dto.request.PersonRequestDto;
import ru.golovkov.myrestapp.service.PersonService;

import java.util.Map;

@RestController
@RequestMapping("${app.base-url}/admin")
@SecurityRequirement(name = "Authorization")
@AllArgsConstructor
public class AdminController {

    private final PersonService personService;

    @Operation(summary = "Удаление пользователя по имени")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Роль пользователя с указанным ID изменена на ADMIN",
                    content = {@Content(
                            examples = {
                                    @ExampleObject(name = "Сообщение об удалении", value = """
                                            {
                                              "changed": "role of user with id 1 to admin'"
                                            }""",
                                            description = "Сообщение об удалении пользователя с указанием его имени")
                            },
                            schema = @Schema(type = "object"),
                            mediaType = MediaType.APPLICATION_JSON_VALUE
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
    @PutMapping("/upgrade-role")
    public Map<String, String> upgradeRole(@RequestParam(name = "rawPasswordOfPerson") String rawPasswordOfPerson,
                                           @RequestParam(name = "id") Long id) {
        personService.upgradeRole(rawPasswordOfPerson, id);
        return Map.of("changed", STR."role of user with id \{id} to admin");
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
                    responseCode = "404",
                    description = "Не найден пользователь с таким именем",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionDetails.class)
                    )}
            )
    })
    @DeleteMapping("")
    public Map<String, String> deletePersonByName(@RequestParam(name = "nameToSearch") String name) {
        personService.deleteByName(name);
        return Map.of("deleted", STR."user with name '\{name}'");
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
                    responseCode = "404",
                    description = "Не найден пользователь с таким ID",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionDetails.class)
                    )}
            )
    })
    @PutMapping("/{id}")
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
                    responseCode = "404",
                    description = "Не найден пользователь с таким именем",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionDetails.class)
                    )}
            )
    })
    @PutMapping("")
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
                    responseCode = "404",
                    description = "Не найден пользователь с таким ID",
                    content = {@Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionDetails.class)
                    )}
            )
    })
    @DeleteMapping("/{id}")
    public Map<String, String> deletePersonById(@PathVariable("id") Long id) {
        personService.deleteById(id);
        return Map.of("deleted", STR."user with id \{id}");
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

    @ApiResponse(
            responseCode = "403",
            description = "Пользователь имеет недостаточно прав",
            content = {@Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionDetails.class)
            )}
    )
    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionDetails handleForbiddenException(ForbiddenException e) {
        return new ExceptionDetails(e.toString());
    }
}
