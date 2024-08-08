package ru.golovkov.myrestapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.golovkov.myrestapp.exception.ExceptionDetails;
import ru.golovkov.myrestapp.exception.entity.PersonNotFoundException;
import ru.golovkov.myrestapp.exception.httpcommon.BadRequestException;
import ru.golovkov.myrestapp.exception.httpcommon.UnauthorizedException;
import ru.golovkov.myrestapp.model.dto.response.PersonResponseDto;
import ru.golovkov.myrestapp.service.PersonService;

import java.util.List;

@RestController
@RequestMapping("${app.people-url}")
@SecurityRequirement(name = "Authorization")
@AllArgsConstructor
public class PersonSearchController {

    private final PersonService personService;

    @Operation(summary = "Получение списка пользователей с возможностью поиска по имени")
    @ApiResponse(
            responseCode = "200",
            description = "Список пользователей",
            content = {@Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = List.class)
            )}
    )
    @GetMapping("")
    public List<PersonResponseDto> getPersonList(@RequestParam(name = "nameToSearch", required = false, defaultValue = "") String name,
                                                 @ParameterObject @PageableDefault(direction = Sort.Direction.ASC, sort = "name") Pageable pageable
    ) {
        return personService.getAllByNameContaining(name, pageable);
    }

    @Operation(summary = "Получение пользователя по ID")
    @ApiResponse(
            responseCode = "200",
            description = "Пользователь найден",
            content = {@Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PersonResponseDto.class)
            )}
    )
    @GetMapping("/{id}")
    public PersonResponseDto getPersonById(@PathVariable Long id) {
        return personService.getById(id);
    }

    @Operation(summary = "Получение пользователя по имени")
    @ApiResponse(
            responseCode = "200",
            description = "Пользователь с запрашиваемым именем найден",
            content = {@Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PersonResponseDto.class)
            )}
    )
    @GetMapping("/search")
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

    @ApiResponse(
            responseCode = "404",
            description = "Ни одного пользователя не найдено",
            content = {@Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExceptionDetails.class)
            )}
    )
    @ExceptionHandler(PersonNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDetails handlePersonNotFoundException(PersonNotFoundException e) {
        return new ExceptionDetails(e.toString());
    }
}
