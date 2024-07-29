package ru.golovkov.myrestapp.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.golovkov.myrestapp.exception.httpcommon.BadRequestException;
import ru.golovkov.myrestapp.exception.entity.PersonNotFoundException;
import ru.golovkov.myrestapp.exception.entity.WrongPasswordException;
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
@AllArgsConstructor
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
        checkIfPersonListNotEmpty(personList);
        return personMapper.entityListToResponseDtoList(personList);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PersonResponseDto> getAllByNameContaining(String name, int pageNumber, int pageSize) {
        PageRequest pageRequest;
        List<Person> personList;
        if (name == null || name.isBlank()) {
            pageRequest = PageRequest.of(pageNumber, pageSize);
            personList = personRepository.findAll(pageRequest).getContent();
        } else {
            pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "name"));
            personList = personRepository
                    .findAllByNameContainingIgnoreCase(pageRequest, name).getContent();
        }
        checkIfPersonListNotEmpty(personList);
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
        checkIfPersonExistsById(id);
        personRepository.deleteById(id);
    }

    @Override
    public void deleteByName(String name) {
        checkIfPersonExistsByName(name);
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

    private void checkIfPersonExistsById(Long id) {
        if (!personRepository.existsById(id)) {
            throw new PersonNotFoundException(STR."No person with id \{id} was found");
        }
    }

    private void checkIfPersonExistsByName(String name) {
        if (!personRepository.existsByName(name)) {
            throw new PersonNotFoundException(STR."No person with name '\{name}' was found");
        }
    }

    private void checkIfPersonListNotEmpty(List<Person> personList) {
        if (personList.isEmpty()) {
            throw new PersonNotFoundException("No person was found");
        }
    }

    private Person getPersonById(Long id) {
        return personRepository
                .findById(id)
                .orElseThrow(() -> new PersonNotFoundException(STR."No person with id \{id} was found"));
    }

    private Person getPersonByName(String name) {
        return personRepository
                .findByName(name)
                .orElseThrow(() -> new PersonNotFoundException(STR."No person with name '\{name}' was found"));
    }
}
