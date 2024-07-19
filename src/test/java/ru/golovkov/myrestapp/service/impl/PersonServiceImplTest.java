package ru.golovkov.myrestapp.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.golovkov.myrestapp.mapper.PersonMapper;
import ru.golovkov.myrestapp.model.dto.request.PersonRequestDto;
import ru.golovkov.myrestapp.model.dto.response.PersonResponseDto;
import ru.golovkov.myrestapp.model.entity.Person;
import ru.golovkov.myrestapp.model.entity.UserRole;
import ru.golovkov.myrestapp.repository.PersonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class PersonServiceImplTest {

    private static Person mockPerson;
    private static PersonRequestDto mockPersonRequestDto;
    private static PersonResponseDto mockPersonResponseDto;
    private static List<Person> mockPersonList;
    private static List<PersonResponseDto> mockPersonResponseDtoList;
    private static long id;
    private static String name;
    @Autowired
    private PersonServiceImpl personService;
    @MockBean
    private PersonMapper personMapper;
    @MockBean
    private PersonRepository personRepository;

    @BeforeEach
    void setUp() {
        id = 1L;
        name = "name";
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
        mockPersonResponseDtoList = new ArrayList<>(List.of(mockPersonResponseDto));
    }


    @Test
    void create() {
        when(personRepository.save(any())).thenReturn(mockPerson);
        when(personMapper.toEntity(mockPersonRequestDto)).thenReturn(mockPerson);

        personService.create(mockPersonRequestDto);

        verify(personRepository).save(mockPerson);
        verify(personMapper).toEntity(mockPersonRequestDto);
    }

    @Test
    void getById() {
        when(personRepository.findById(anyLong())).thenReturn(Optional.of(mockPerson));
        when(personMapper.toResponseDto(mockPerson)).thenReturn(mockPersonResponseDto);

        PersonResponseDto personResponseDto = personService.getById(id);

        assertEquals(mockPersonResponseDto, personResponseDto);
        verify(personRepository).findById(id);
        verify(personMapper).toResponseDto(mockPerson);
    }

    @Test
    void getByName() {
        when(personRepository.findByName(anyString())).thenReturn(Optional.of(mockPerson));
        when(personMapper.toResponseDto(mockPerson)).thenReturn(mockPersonResponseDto);

        PersonResponseDto personResponseDto = personService.getByName(name);

        assertEquals(mockPersonResponseDto, personResponseDto);
        verify(personRepository).findByName(name);
        verify(personMapper).toResponseDto(mockPerson);
    }

    @Test
    void getAll() {
        when(personRepository.findAll()).thenReturn(mockPersonList);
        when(personMapper.personListToPersonResponseDtoList(mockPersonList)).thenReturn(mockPersonResponseDtoList);

        List<PersonResponseDto> personResponseDtoList = personService.getAll();

        assertEquals(mockPersonResponseDtoList, personResponseDtoList);
        verify(personRepository).findAll();
        verify(personMapper).personListToPersonResponseDtoList(mockPersonList);

    }

    @Test
    void getAllByNameContaining() {
        when(personRepository.findAllByNameContainingIgnoreCase(anyString())).thenReturn(mockPersonList);
        when(personMapper.personListToPersonResponseDtoList(mockPersonList)).thenReturn(mockPersonResponseDtoList);

        List<PersonResponseDto> personResponseDtoList = personService.getAllByNameContaining(name);

        assertEquals(mockPersonResponseDtoList, personResponseDtoList);
        verify(personRepository).findAllByNameContainingIgnoreCase(name);
        verify(personMapper).personListToPersonResponseDtoList(mockPersonList);
    }

    @Test
    void updateById() {
        when(personRepository.save(any())).thenReturn(mockPerson);
        when(personRepository.findById(anyLong())).thenReturn(Optional.of(mockPerson));

        personService.updateById(mockPersonRequestDto, id);

        verify(personRepository).save(mockPerson);
        verify(personRepository).findById(id);
    }

    @Test
    void updateByName() {
        when(personRepository.save(any())).thenReturn(mockPerson);
        when(personRepository.findByName(anyString())).thenReturn(Optional.of(mockPerson));

        personService.updateByName(mockPersonRequestDto, name);

        verify(personRepository).save(mockPerson);
        verify(personRepository).findByName(name);
    }

    @Test
    void deleteById() {
        when(personRepository.existsById(anyLong())).thenReturn(true);

        personService.deleteById(id);

        verify(personRepository).deleteById(id);
    }

    @Test
    void deleteByName() {
        when(personRepository.existsByName(anyString())).thenReturn(true);

        personService.deleteByName(name);

        verify(personRepository).deleteByName(name);
    }
}