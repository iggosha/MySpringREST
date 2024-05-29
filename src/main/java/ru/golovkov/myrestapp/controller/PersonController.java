package ru.golovkov.myrestapp.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.golovkov.myrestapp.model.Person;
import ru.golovkov.myrestapp.service.PersonService;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping("/people")
    public List<Person> getPeople() {
        return personService.findAll();
    }

    @GetMapping("/people/{id}")
    public Person getPerson(@PathVariable("id") Long id) {
        return personService.findOne(id);
    }
}
