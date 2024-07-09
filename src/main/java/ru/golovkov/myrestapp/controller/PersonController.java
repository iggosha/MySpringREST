package ru.golovkov.myrestapp.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;
import ru.golovkov.myrestapp.model.dto.request.PersonRequestDto;
import ru.golovkov.myrestapp.model.dto.response.PersonResponseDto;
import ru.golovkov.myrestapp.service.PersonService;

import java.util.List;

@RestController
@RequestMapping("${app.base-url}")
@SecurityRequirement(name = "Authorization")
@AllArgsConstructor
public class PersonController {

    //http://localhost:8888/swagger-ui/index.html#/person-controller

    private final PersonService personService;

    @GetMapping("")
    public List<PersonResponseDto> getPeople() {
        return personService.getAll();
    }

    @GetMapping("/{id}")
    public PersonResponseDto getPerson(@PathVariable("id") Long id) {
        return personService.getById(id);
    }

    @PutMapping("/admin/{id}")
    public void updatePerson(@ParameterObject PersonRequestDto personRequestDto,
                             @PathVariable("id") Long id) {
        personService.updateById(personRequestDto, id);
    }

    @DeleteMapping("/admin/{id}")
    public void deletePerson(@PathVariable("id") Long id) {
        personService.deleteById(id);
    }
}
