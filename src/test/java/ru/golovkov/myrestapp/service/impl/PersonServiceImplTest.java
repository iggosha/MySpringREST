package ru.golovkov.myrestapp.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class PersonServiceImplTest {

    private static Person mockPerson;
    private static PersonRequestDto mockPersonRequestDto;
    private static PersonResponseDto mockPersonResponseDto;
    private static List<Person> mockPersonList;
    private static List<PersonResponseDto> mockPersonResponseDtoList;
    private static Page<Person> mockPersonPage;
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
        mockPersonResponseDtoList = new ArrayList<>(List.of(mockPersonResponseDto));
        mockPersonPage = new PageImpl<>(mockPersonList);
    }

    @Test
    void create() {
        when(personMapper.requestDtoToEntity(mockPersonRequestDto)).thenReturn(mockPerson);
        when(personRepository.save(any(Person.class))).thenReturn(mockPerson);
        when(personMapper.entityToResponseDto(mockPerson)).thenReturn(mockPersonResponseDto);

        PersonResponseDto personResponseDto = personService.create(mockPersonRequestDto);

        assertEquals(mockPersonResponseDto, personResponseDto);

        verify(personMapper).requestDtoToEntity(mockPersonRequestDto);
        verify(personRepository).save(mockPerson);
        verify(personMapper).entityToResponseDto(mockPerson);
    }

    @Test
    void getById() {
        when(personRepository.findById(anyLong())).thenReturn(Optional.of(mockPerson));
        when(personMapper.entityToResponseDto(mockPerson)).thenReturn(mockPersonResponseDto);

        PersonResponseDto personResponseDto = personService.getById(id);

        assertEquals(mockPersonResponseDto, personResponseDto);
        verify(personRepository).findById(id);
        verify(personMapper).entityToResponseDto(mockPerson);
    }

    @Test
    void getByName() {
        when(personRepository.findByName(anyString())).thenReturn(Optional.of(mockPerson));
        when(personMapper.entityToResponseDto(mockPerson)).thenReturn(mockPersonResponseDto);

        PersonResponseDto personResponseDto = personService.getByName(name);

        assertEquals(mockPersonResponseDto, personResponseDto);
        verify(personRepository).findByName(name);
        verify(personMapper).entityToResponseDto(mockPerson);
    }

    @Test
    void getAll() {
        when(personRepository.findAll()).thenReturn(mockPersonList);
        when(personMapper.entityListToResponseDtoList(mockPersonList)).thenReturn(mockPersonResponseDtoList);

        List<PersonResponseDto> personResponseDtoList = personService.getAll();

        assertEquals(mockPersonResponseDtoList, personResponseDtoList);
        verify(personRepository).findAll();
        verify(personMapper).entityListToResponseDtoList(mockPersonList);
    }

    @Test
    void getAllByNameContaining() {
        when(personRepository.findAllByNameContainingIgnoreCase(any(Pageable.class), anyString())).thenReturn(mockPersonPage);
        when(personMapper.entityListToResponseDtoList(mockPersonList)).thenReturn(mockPersonResponseDtoList);

        List<PersonResponseDto> personResponseDtoList =
                personService.getAllByNameContaining(name, pageRequest);

        assertEquals(mockPersonResponseDtoList, personResponseDtoList);
        verify(personRepository).findAllByNameContainingIgnoreCase(pageRequest, name);
        verify(personMapper).entityListToResponseDtoList(mockPersonList);
    }

    @Test
    void updateById() {
        when(personRepository.findById(anyLong())).thenReturn(Optional.of(mockPerson));
        when(personRepository.save(any(Person.class))).thenReturn(mockPerson);
        when(personMapper.entityToResponseDto(mockPerson)).thenReturn(mockPersonResponseDto);

        PersonResponseDto personResponseDto = personService.updateById(mockPersonRequestDto, id);

        assertEquals(mockPersonResponseDto, personResponseDto);
        verify(personRepository).findById(id);
        verify(personMapper).updateEntityFromRequestDto(mockPerson, mockPersonRequestDto);
        verify(personRepository).save(mockPerson);
        verify(personMapper).entityToResponseDto(mockPerson);
    }

    @Test
    void updateByName() {
        when(personRepository.findByName(anyString())).thenReturn(Optional.of(mockPerson));
        when(personRepository.save(any(Person.class))).thenReturn(mockPerson);
        when(personMapper.entityToResponseDto(mockPerson)).thenReturn(mockPersonResponseDto);

        PersonResponseDto personResponseDto = personService.updateByName(mockPersonRequestDto, name);

        assertEquals(mockPersonResponseDto, personResponseDto);
        verify(personRepository).findByName(name);
        verify(personMapper).updateEntityFromRequestDto(mockPerson, mockPersonRequestDto);
        verify(personRepository).save(mockPerson);
        verify(personMapper).entityToResponseDto(mockPerson);
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

    @Test
    void upgradeRole() {
        when(personRepository.findById(anyLong())).thenReturn(Optional.of(mockPerson));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(personRepository.save(any(Person.class))).thenReturn(mockPerson);
        when(personMapper.entityToResponseDto(mockPerson)).thenReturn(mockPersonResponseDto);

        PersonResponseDto personResponseDto = personService.upgradeRole(name, id);
        assertEquals(UserRole.ROLE_ADMIN, mockPerson.getRole());
        assertEquals(mockPersonResponseDto, personResponseDto);

        verify(personRepository).findById(id);
        verify(passwordEncoder).matches(name, mockPerson.getPassword());
        verify(personRepository).save(mockPerson);
        verify(personMapper).entityToResponseDto(mockPerson);
    }
}