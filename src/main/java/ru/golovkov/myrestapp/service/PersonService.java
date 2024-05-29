package ru.golovkov.myrestapp.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.golovkov.myrestapp.model.Person;
import ru.golovkov.myrestapp.repo.PersonRepo;

import java.util.List;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class PersonService {

    private final PersonRepo personRepo;

    public List<Person> findAll() {
        return personRepo.findAll();
    }

    public Person findOne(Long id) {
        return personRepo
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found user with id" + id));
    }
}
