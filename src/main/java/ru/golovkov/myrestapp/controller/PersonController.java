package ru.golovkov.myrestapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.golovkov.myrestapp.model.dto.request.PersonRequestDto;
import ru.golovkov.myrestapp.model.dto.response.PersonResponseDto;
import ru.golovkov.myrestapp.service.PersonService;

import java.util.List;

@RestController
@RequestMapping("${app.base-url}")
@AllArgsConstructor
public class PersonController {

    //http://localhost:8888/swagger-ui/index.html#/person-controller

    private final PersonService personService;
    private final ObjectMapper objectMapper;


    @GetMapping("")
    public List<PersonResponseDto> getPeople() {
        return personService.getAll();
    }

    @SneakyThrows
    @GetMapping("/public/hello")
    public String getHello(Authentication authentication) {
        if (authentication != null) {
            return objectMapper.writeValueAsString(authentication.getPrincipal());
        }
        return "Hello";
    }

    @PostMapping("/public/login")
    public void postLogin(@ParameterObject String username, @ParameterObject String password) {
        // 4 Swagger
    }

    @PostMapping("/logout")
    public void postLogout() {
        // 4 Swagger
    }

    @GetMapping("/{id}")
    public PersonResponseDto getPerson(@PathVariable("id") Long id) {
        return personService.getById(id);
    }

    @PostMapping("/public/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public void postPerson(@ParameterObject PersonRequestDto personRequestDto) {
        personService.create(personRequestDto);
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
