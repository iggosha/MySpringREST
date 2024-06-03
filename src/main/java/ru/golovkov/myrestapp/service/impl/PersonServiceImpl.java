package ru.golovkov.myrestapp.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.golovkov.myrestapp.exc.PersonNotFoundException;
import ru.golovkov.myrestapp.mapper.PersonMapper;
import ru.golovkov.myrestapp.model.dto.request.PersonRequestDto;
import ru.golovkov.myrestapp.model.dto.response.PersonResponseDto;
import ru.golovkov.myrestapp.model.entity.Person;
import ru.golovkov.myrestapp.repository.PersonRepository;
import ru.golovkov.myrestapp.service.PersonService;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    private final PersonMapper personMapper;

    @Override
    public void create(PersonRequestDto personRequestDto) {
        Person person = personMapper.toEntity(personRequestDto);
        person.setRegistrationDate(LocalDate.now());
        personRepository.save(person);
    }

    @Transactional(readOnly = true)
    @Override
    public PersonResponseDto getById(Long id) {
        return personMapper.toResponseDto(getPersonById(id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<PersonResponseDto> getAll() {
        return personRepository.findAll().stream().map(personMapper::toResponseDto).toList();
    }

    @Override
    public void updateById(PersonRequestDto personRequestDto, Long id) {
        Person personToUpdate = getPersonById(id);
        personMapper.updateEntityFromRequestDto(personToUpdate, personRequestDto);
        personRepository.save(personToUpdate);
    }

    @Override
    public void deleteById(Long id) {
        personRepository.deleteById(id);
    }

    private Person getPersonById(Long id) {
        return personRepository
                .findById(id)
                .orElseThrow(() -> new PersonNotFoundException(STR."Person with id \{id} not found"));
    }
}
