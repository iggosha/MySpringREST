package ru.golovkov.myrestapp.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.golovkov.myrestapp.exception.entity.PersonNotFoundException;
import ru.golovkov.myrestapp.model.entity.Person;
import ru.golovkov.myrestapp.repository.PersonRepository;
import ru.golovkov.myrestapp.security.PersonDetails;
import ru.golovkov.myrestapp.service.PersonDetailsService;

@Service
@AllArgsConstructor
public class PersonDetailsServiceImpl implements PersonDetailsService {

    private final PersonRepository personRepository;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String name) {
        Person person = personRepository
                .findByName(name)
                .orElseThrow(() -> new PersonNotFoundException(STR."No person with name '\{name}' was found"));
        return new PersonDetails(person);
    }
}
