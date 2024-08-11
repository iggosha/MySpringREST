package ru.golovkov.myrestapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.golovkov.myrestapp.exception.ExceptionDetails;
import ru.golovkov.myrestapp.exception.entity.PersonNotFoundException;
import ru.golovkov.myrestapp.exception.httpcommon.BadRequestException;
import ru.golovkov.myrestapp.exception.httpcommon.ForbiddenException;
import ru.golovkov.myrestapp.exception.httpcommon.UnauthorizedException;
import ru.golovkov.myrestapp.model.dto.request.PersonRequestDto;
import ru.golovkov.myrestapp.model.dto.response.PersonResponseDto;
import ru.golovkov.myrestapp.service.PersonService;

@RestController
@RequestMapping("${app.people-url}/admin")
@SecurityRequirement(name = "Authorization")
@AllArgsConstructor
public class PersonAdminController {

    private final PersonService personService;

    @Operation(summary = "Удаление пользователя по имени")
    @ApiResponse(
            responseCode = "200",
            description = "Данные пользователя с успешно изменённой ролью",
            content = {@Content(
                    schema = @Schema(implementation = PersonResponseDto.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE
            )}
    )
    @PutMapping("/upgrade-role")
    public PersonResponseDto upgradeRole(@RequestParam(name = "rawPasswordOfPerson") String rawPasswordOfPerson,
                                         @RequestParam(name = "id") Long id) {
        return personService.upgradeRole(rawPasswordOfPerson, id);
    }


    @SneakyThrows
    @Operation(summary = "Удаление пользователя по имени")
    @ApiResponse(
            responseCode = "200",
            description = "Успешно удалённый пользователь",
            content = {@Content(
                    schema = @Schema(implementation = PersonResponseDto.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE
            )}
    )
    @DeleteMapping("")
    public PersonResponseDto deletePersonByName(@RequestParam(name = "nameToSearch") String name) {
        PersonResponseDto personResponseDto = personService.getByName(name);
        personService.deleteByName(name);
        return personResponseDto;
    }


    @Operation(summary = "Изменение данных пользователя по ID")
    @ApiResponse(
            responseCode = "200",
            description = "Успешно изменённый пользователь",
            content = {@Content(
                    schema = @Schema(implementation = PersonResponseDto.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE
            )}
    )
    @PutMapping("/{id}")
    public PersonResponseDto updatePersonById(@ParameterObject PersonRequestDto personRequestDto,
                                              @PathVariable Long id) {
        return personService.updateById(personRequestDto, id);
    }

    @Operation(summary = "Изменение данных пользователя по имени")
    @ApiResponse(
            responseCode = "200",
            description = "Успешно изменённый пользователь",
            content = {@Content(
                    schema = @Schema(implementation = PersonResponseDto.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE
            )}
    )
    @PutMapping("")
    public PersonResponseDto updatePersonByName(@ParameterObject PersonRequestDto personRequestDto,
                                                @RequestParam("nameToSearch") String name) {
        return personService.updateByName(personRequestDto, name);
    }

    @SneakyThrows
    @Operation(summary = "Удаление пользователя по ID")
    @ApiResponse(
            responseCode = "200",
            description = "Успешно удаленный пользователь",
            content = {@Content(
                    schema = @Schema(implementation = PersonResponseDto.class),
                    mediaType = MediaType.APPLICATION_JSON_VALUE
            )}
    )
    @DeleteMapping("/{id}")
    public PersonResponseDto deletePersonById(@PathVariable Long id) {
        PersonResponseDto personResponseDto = personService.getById(id);
        personService.deleteById(id);
        return personResponseDto;
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
