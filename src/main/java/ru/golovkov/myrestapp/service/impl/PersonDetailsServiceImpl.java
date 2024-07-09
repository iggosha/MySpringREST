package ru.golovkov.myrestapp.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.golovkov.myrestapp.exc.PersonNotFoundException;
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person = personRepository
                .findByName(username)
                .orElseThrow(() -> new PersonNotFoundException("User with name '" + username + "' not found"));
        return new PersonDetails(person);
    }
}
