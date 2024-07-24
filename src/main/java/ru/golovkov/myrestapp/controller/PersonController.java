package ru.golovkov.myrestapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.golovkov.myrestapp.exc.BadRequestException;
import ru.golovkov.myrestapp.exc.ExceptionDetails;
import ru.golovkov.myrestapp.exc.UnauthorizedException;
import ru.golovkov.myrestapp.model.dto.response.PersonResponseDto;
import ru.golovkov.myrestapp.service.PersonService;

import java.util.List;

@RestController
@RequestMapping("${app.base-url}")
@SecurityRequirement(name = "Authorization")
@AllArgsConstructor
public class PersonController {

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
    public List<PersonResponseDto> getPersonList(@RequestParam(name = "nameToSearch", required = false, defaultValue = "") String name,
                                                 @RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                                 @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize
    ) {
        return personService.getAllByNameContaining(name, pageNumber, pageSize);
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
}
