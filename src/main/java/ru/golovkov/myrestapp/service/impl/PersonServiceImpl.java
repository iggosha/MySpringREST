package ru.golovkov.myrestapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.golovkov.myrestapp.exception.entity.PersonNotFoundException;
import ru.golovkov.myrestapp.exception.entity.WrongPasswordException;
import ru.golovkov.myrestapp.exception.httpcommon.BadRequestException;
import ru.golovkov.myrestapp.mapper.PersonMapper;
import ru.golovkov.myrestapp.model.dto.request.PersonRequestDto;
import ru.golovkov.myrestapp.model.dto.response.PersonResponseDto;
import ru.golovkov.myrestapp.model.entity.Person;
import ru.golovkov.myrestapp.model.entity.UserRole;
import ru.golovkov.myrestapp.repository.PersonRepository;
import ru.golovkov.myrestapp.service.PersonService;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    private final PersonMapper personMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    public PersonResponseDto create(PersonRequestDto personRequestDto) {
        if (personRequestDto.getPassword() == null ||
                personRequestDto.getAge() == null ||
                personRequestDto.getEmail() == null ||
                personRequestDto.getName() == null ||
                personRequestDto.getPassword().isBlank() ||
                personRequestDto.getEmail().isBlank() ||
                personRequestDto.getName().isBlank()
        ) {
            throw new BadRequestException("All fields must be filled");
        }
        Person person = personMapper.requestDtoToEntity(personRequestDto);
        person.setRegistrationDate(LocalDate.now());
        person.setRole(UserRole.ROLE_BASE);
        person.setPassword(passwordEncoder.encode(personRequestDto.getPassword()));
        person = personRepository.save(person);
        return personMapper.entityToResponseDto(person);
    }

    @Transactional(readOnly = true)
    @Override
    public PersonResponseDto getById(Long id) {
        return personMapper.entityToResponseDto(getPersonById(id));
    }

    @Transactional(readOnly = true)
    @Override
    public PersonResponseDto getByName(String name) {
        return personMapper.entityToResponseDto(getPersonByName(name));
    }

    @Transactional(readOnly = true)
    @Override
    public List<PersonResponseDto> getAll() {
        List<Person> personList = personRepository.findAll();
        throwExceptionIfPersonListIsEmpty(personList);
        return personMapper.entityListToResponseDtoList(personList);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PersonResponseDto> getAllByNameContaining(String name, Pageable pageable) {
        List<Person> personList;
        if (name == null || name.isBlank()) {
            personList = personRepository.findAll(pageable).getContent();
        } else {
            personList = personRepository
                    .findAllByNameContainingIgnoreCase(pageable, name).getContent();
        }
        throwExceptionIfPersonListIsEmpty(personList);
        return personMapper.entityListToResponseDtoList(personList);
    }

    @Override
    public PersonResponseDto updateById(PersonRequestDto personRequestDto, Long id) {
        Person person = getPersonById(id);
        personMapper.updateEntityFromRequestDto(person, personRequestDto);
        if (personRequestDto.getPassword() != null && !personRequestDto.getPassword().isBlank()) {
            person.setPassword(passwordEncoder.encode(personRequestDto.getPassword()));
        }
        person = personRepository.save(person);
        return personMapper.entityToResponseDto(person);
    }

    @Override
    public PersonResponseDto updateByName(PersonRequestDto personRequestDto, String name) {
        Person person = getPersonByName(name);
        personMapper.updateEntityFromRequestDto(person, personRequestDto);
        if (personRequestDto.getPassword() != null && !personRequestDto.getPassword().isBlank()) {
            person.setPassword(passwordEncoder.encode(personRequestDto.getPassword()));
        }
        person = personRepository.save(person);
        return personMapper.entityToResponseDto(person);
    }

    @Override
    public void deleteById(Long id) {
        throwExceptionIfNoPersonExistsById(id);
        personRepository.deleteById(id);
    }

    @Override
    public void deleteByName(String name) {
        throwExceptionIfNoPersonExistsByName(name);
        personRepository.deleteByName(name);
    }

    @Override
    public PersonResponseDto upgradeRole(String rawPassword, Long id) {
        Person person = getPersonById(id);
        if (passwordEncoder.matches(rawPassword, person.getPassword())) {
            person.setRole(UserRole.ROLE_ADMIN);
        } else {
            throw new WrongPasswordException();
        }
        person = personRepository.save(person);
        return personMapper.entityToResponseDto(person);
    }

    private void throwExceptionIfNoPersonExistsById(Long id) {
        if (!personRepository.existsById(id)) {
            throw new PersonNotFoundException(id);
        }
    }

    private void throwExceptionIfNoPersonExistsByName(String name) {
        if (!personRepository.existsByName(name)) {
            throw new PersonNotFoundException(STR."No person with name '\{name}' was found");
        }
    }

    private void throwExceptionIfPersonListIsEmpty(List<Person> personList) {
        if (personList.isEmpty()) {
            throw new PersonNotFoundException();
        }
    }

    private Person getPersonById(Long id) {
        return personRepository
                .findById(id)
                .orElseThrow(() -> new PersonNotFoundException(id));
    }

    private Person getPersonByName(String name) {
        return personRepository
                .findByName(name)
                .orElseThrow(() -> new PersonNotFoundException(STR."No person with name '\{name}' was found"));
    }
}
