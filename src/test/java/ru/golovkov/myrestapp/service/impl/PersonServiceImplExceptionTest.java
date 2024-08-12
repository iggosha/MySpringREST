package ru.golovkov.myrestapp.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.golovkov.myrestapp.exception.entity.PersonNotFoundException;
import ru.golovkov.myrestapp.exception.entity.WrongPasswordException;
import ru.golovkov.myrestapp.mapper.PersonMapper;
import ru.golovkov.myrestapp.model.dto.request.PersonRequestDto;
import ru.golovkov.myrestapp.model.dto.response.PersonResponseDto;
import ru.golovkov.myrestapp.model.entity.Person;
import ru.golovkov.myrestapp.model.entity.UserRole;
import ru.golovkov.myrestapp.repository.PersonRepository;
import ru.golovkov.myrestapp.service.PersonService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class PersonServiceImplExceptionTest {

    private static Person mockPerson;
    private static PersonRequestDto mockPersonRequestDto;
    private static PersonResponseDto mockPersonResponseDto;
    private static List<Person> mockPersonList;
    private static long id;
    private static String name;
    private static PageRequest pageRequest;
    @Autowired
    private PersonService personService;
    @MockBean
    private PersonMapper personMapper;
    @MockBean
    private PersonRepository personRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        id = 1L;
        name = "name";
        pageRequest = PageRequest.of(1, 10, Sort.by(Sort.Direction.ASC, "name"));

        mockPersonRequestDto = new PersonRequestDto();
        mockPersonRequestDto.setName(name);
        mockPersonRequestDto.setEmail("email@email.com");
        mockPersonRequestDto.setAge(52);
        mockPersonRequestDto.setPassword("52");

        mockPerson = new Person();
        mockPerson.setId(id);
        mockPerson.setName(mockPersonRequestDto.getName());
        mockPerson.setEmail(mockPersonRequestDto.getEmail());
        mockPerson.setAge(mockPersonRequestDto.getAge());
        mockPerson.setPassword(mockPersonRequestDto.getPassword());
        mockPerson.setRole(UserRole.ROLE_BASE);

        mockPersonResponseDto = new PersonResponseDto();
        mockPersonResponseDto.setId(mockPerson.getId());
        mockPersonResponseDto.setName(mockPerson.getName());
        mockPersonResponseDto.setEmail(mockPerson.getEmail());
        mockPersonResponseDto.setAge(mockPerson.getAge());
        mockPersonResponseDto.setPassword(mockPerson.getPassword());
        mockPersonResponseDto.setRole(mockPerson.getRole().name());

        mockPersonList = new ArrayList<>(List.of(mockPerson));
    }

    @Test
    void create_EntityExists_ThrowsDataIntegrityViolationException() {
        when(personMapper.requestDtoToEntity(mockPersonRequestDto)).thenReturn(mockPerson);
        when(personRepository.save(any(Person.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DataIntegrityViolationException.class,
                () -> personService.create(mockPersonRequestDto));

        verify(personMapper).requestDtoToEntity(mockPersonRequestDto);
        verify(personRepository).save(mockPerson);
    }

    @Test
    void getById_EntityDoesntExist_ThrowsPersonNotFoundException() {
        when(personRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(personMapper.entityToResponseDto(mockPerson)).thenReturn(mockPersonResponseDto);

        assertThrows(PersonNotFoundException.class, () -> personService.getById(id));

        verify(personRepository).findById(id);
        verify(personMapper, never()).entityToResponseDto(mockPerson);
    }

    @Test
    void getByName_EntityDoesntExist_ThrowsPersonNotFoundException() {
        when(personMapper.entityToResponseDto(mockPerson)).thenReturn(mockPersonResponseDto);
        when(personRepository.findByName(anyString())).thenReturn(Optional.empty());

        assertThrows(PersonNotFoundException.class, () -> personService.getByName(name));

        verify(personMapper, never()).entityToResponseDto(mockPerson);
        verify(personRepository).findByName(name);
    }

    @Test
    void getAll_EntitiesDontExist_ThrowsPersonNotFoundException() {
        when(personRepository.findAll()).thenReturn(List.of());
        when(personMapper.entityListToResponseDtoList(mockPersonList)).thenReturn(List.of());

        assertThrows(PersonNotFoundException.class, () -> personService.getAll());

        verify(personRepository).findAll();
        verify(personMapper, never()).entityListToResponseDtoList(mockPersonList);
    }

    @Test
    void getAllByNameContaining_EntitiesDontExist_ThrowsPersonNotFoundException() {
        when(personRepository.findAllByNameContainingIgnoreCase(any(Pageable.class), anyString())).thenReturn(Page.empty());
        when(personMapper.entityListToResponseDtoList(mockPersonList)).thenReturn(List.of());

        assertThrows(PersonNotFoundException.class,
                () -> personService.getAllByNameContaining(name, pageRequest));

        verify(personRepository).findAllByNameContainingIgnoreCase(pageRequest, name);
        verify(personMapper, never()).entityListToResponseDtoList(List.of());
    }

    @Test
    void updateById_EntityDoesntExist_ThrowsPersonNotFoundException() {
        when(personRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(personMapper.entityToResponseDto(mockPerson)).thenReturn(mockPersonResponseDto);
        when(personRepository.save(any(Person.class))).thenReturn(mockPerson);

        assertThrows(PersonNotFoundException.class,
                () -> personService.updateById(mockPersonRequestDto, id));

        verify(personRepository).findById(id);
        verify(personMapper, never()).updateEntityFromRequestDto(mockPerson, mockPersonRequestDto);
        verify(personRepository, never()).save(mockPerson);
        verify(personMapper, never()).entityToResponseDto(mockPerson);
    }

    @Test
    void updateByName_EntityDoesntExist_ThrowsPersonNotFoundException() {
        when(personRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(personRepository.save(any(Person.class))).thenReturn(mockPerson);
        when(personMapper.entityToResponseDto(mockPerson)).thenReturn(mockPersonResponseDto);

        assertThrows(PersonNotFoundException.class,
                () -> personService.updateByName(mockPersonRequestDto, name));

        verify(personRepository).findByName(name);
        verify(personMapper, never()).updateEntityFromRequestDto(mockPerson, mockPersonRequestDto);
        verify(personRepository, never()).save(mockPerson);
        verify(personMapper, never()).entityToResponseDto(mockPerson);
    }

    @Test
    void deleteById_EntityDoesntExist_ThrowsPersonNotFoundException() {
        when(personRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(PersonNotFoundException.class,
                () -> personService.deleteById(id));

        verify(personRepository, never()).deleteById(id);
    }

    @Test
    void deleteByName_EntityDoesntExist_ThrowsPersonNotFoundException() {
        when(personRepository.existsByName(anyString())).thenReturn(false);

        assertThrows(PersonNotFoundException.class,
                () -> personService.deleteByName(name));

        verify(personRepository).existsByName(name);
        verify(personRepository, never()).deleteByName(name);
    }

    @Test
    void upgradeRole_EntityDoesntExist_ThrowsPersonNotFoundException() {
        when(personRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(personRepository.save(any(Person.class))).thenReturn(mockPerson);
        when(personMapper.entityToResponseDto(any(Person.class))).thenReturn(mockPersonResponseDto);

        assertThrows(PersonNotFoundException.class,
                () -> personService.upgradeRole(name, id));

        verify(personRepository).findById(id);
        verify(passwordEncoder, never()).matches(name, mockPerson.getPassword());
        verify(personRepository, never()).save(mockPerson);
        verify(personMapper, never()).entityToResponseDto(mockPerson);
    }

    @Test
    void upgradeRole_BadCredentials_ThrowsBadRequestException() {
        when(personRepository.findById(anyLong())).thenReturn(Optional.of(mockPerson));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        when(personRepository.save(any(Person.class))).thenReturn(mockPerson);
        when(personMapper.entityToResponseDto(any(Person.class))).thenReturn(mockPersonResponseDto);

        assertThrows(WrongPasswordException.class,
                () -> personService.upgradeRole(name, id));

        verify(personRepository).findById(id);
        verify(passwordEncoder).matches(name, mockPerson.getPassword());
        verify(personRepository, never()).save(mockPerson);
        verify(personMapper, never()).entityToResponseDto(mockPerson);
    }
}