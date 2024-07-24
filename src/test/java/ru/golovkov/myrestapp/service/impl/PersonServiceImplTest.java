package ru.golovkov.myrestapp.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.golovkov.myrestapp.exc.BadRequestException;
import ru.golovkov.myrestapp.exc.PersonNotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
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
    private PersonServiceImpl personService;
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
        when(personRepository.save(any())).thenReturn(mockPerson);
        when(personMapper.toEntity(mockPersonRequestDto)).thenReturn(mockPerson);

        personService.create(mockPersonRequestDto);

        verify(personRepository).save(mockPerson);
        verify(personMapper).toEntity(mockPersonRequestDto);
    }

    @Test
    void create_EntityExists_ThrowsDataIntegrityViolationException() {
        when(personMapper.toEntity(mockPersonRequestDto)).thenReturn(mockPerson);
        when(personRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DataIntegrityViolationException.class,
                () -> personService.create(mockPersonRequestDto));

        verify(personMapper).toEntity(mockPersonRequestDto);
        verify(personRepository).save(mockPerson);
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
    void getById_EntityDoesntExist_ThrowsPersonNotFoundException() {
        when(personRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(personMapper.toResponseDto(mockPerson)).thenReturn(mockPersonResponseDto);

        assertThrows(PersonNotFoundException.class, () -> personService.getById(id));

        verify(personRepository).findById(id);
        verify(personMapper, never()).toResponseDto(mockPerson);
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
    void getByName_EntityDoesntExist_ThrowsPersonNotFoundException() {
        when(personMapper.toResponseDto(mockPerson)).thenReturn(mockPersonResponseDto);
        when(personRepository.findByName(anyString())).thenReturn(Optional.empty());

        assertThrows(PersonNotFoundException.class, () -> personService.getByName(name));

        verify(personMapper, never()).toResponseDto(mockPerson);
        verify(personRepository).findByName(name);
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
    void getAll_EntitiesDontExist_ThrowsPersonNotFoundException() {
        when(personMapper.personListToPersonResponseDtoList(mockPersonList)).thenReturn(List.of());
        when(personRepository.findAll()).thenReturn(List.of());

        assertThrows(PersonNotFoundException.class, () -> personService.getAll());

        verify(personRepository).findAll();
        verify(personMapper, never()).personListToPersonResponseDtoList(mockPersonList);
    }

    @Test
    void getAllByNameContaining() {
        when(personRepository.findAllByNameContainingIgnoreCase(any(), anyString())).thenReturn(mockPersonPage);
        when(personMapper.personListToPersonResponseDtoList(mockPersonList)).thenReturn(mockPersonResponseDtoList);

        List<PersonResponseDto> personResponseDtoList =
                personService.getAllByNameContaining(name, pageRequest.getPageNumber(), pageRequest.getPageSize());

        assertEquals(mockPersonResponseDtoList, personResponseDtoList);
        verify(personRepository).findAllByNameContainingIgnoreCase(pageRequest, name);
        verify(personMapper).personListToPersonResponseDtoList(mockPersonList);
    }

    @Test
    void getAllByNameContaining_EntitiesDontExist_ThrowsPersonNotFoundException() {
        when(personRepository.findAllByNameContainingIgnoreCase(any(), anyString())).thenReturn(new PageImpl<>(List.of()));
        when(personMapper.personListToPersonResponseDtoList(mockPersonList)).thenReturn(List.of());

        assertThrows(PersonNotFoundException.class,
                () -> personService.getAllByNameContaining(name, 1, 10));

        verify(personRepository).findAllByNameContainingIgnoreCase(pageRequest, name);
        verify(personMapper, never()).personListToPersonResponseDtoList(List.of());
    }

    @Test
    void updateById() {
        when(personRepository.save(any())).thenReturn(mockPerson);
        when(personRepository.findById(anyLong())).thenReturn(Optional.of(mockPerson));

        personService.updateById(mockPersonRequestDto, id);

        verify(personRepository).save(mockPerson);
        verify(personRepository).findById(id);
        verify(personMapper).updateEntityFromRequestDto(mockPerson, mockPersonRequestDto);
    }

    @Test
    void updateById_EntityDoesntExist_ThrowsPersonNotFoundException() {
        when(personRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(personRepository.save(any())).thenReturn(mockPerson);

        assertThrows(PersonNotFoundException.class,
                () -> personService.updateById(mockPersonRequestDto, id));

        verify(personRepository).findById(id);
        verify(personRepository, never()).save(mockPerson);
        verify(personMapper, never()).updateEntityFromRequestDto(mockPerson, mockPersonRequestDto);
    }

    @Test
    void updateByName() {
        when(personRepository.save(any())).thenReturn(mockPerson);
        when(personRepository.findByName(anyString())).thenReturn(Optional.of(mockPerson));

        personService.updateByName(mockPersonRequestDto, name);

        verify(personRepository).save(mockPerson);
        verify(personRepository).findByName(name);
        verify(personMapper).updateEntityFromRequestDto(mockPerson, mockPersonRequestDto);
    }

    @Test
    void updateByName_EntityDoesntExist_ThrowsPersonNotFoundException() {
        when(personRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(personRepository.save(any())).thenReturn(mockPerson);

        assertThrows(PersonNotFoundException.class,
                () -> personService.updateByName(mockPersonRequestDto, name));

        verify(personRepository).findByName(name);
        verify(personRepository, never()).save(mockPerson);
        verify(personMapper, never()).updateEntityFromRequestDto(mockPerson, mockPersonRequestDto);
    }

    @Test
    void deleteById() {
        when(personRepository.existsById(anyLong())).thenReturn(true);

        personService.deleteById(id);

        verify(personRepository).deleteById(id);
    }

    @Test
    void deleteById_EntityDoesntExist_ThrowsPersonNotFoundException() {
        when(personRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(PersonNotFoundException.class,
                () -> personService.deleteById(id));

        verify(personRepository, never()).deleteById(id);
    }

    @Test
    void deleteByName() {
        when(personRepository.existsByName(anyString())).thenReturn(true);

        personService.deleteByName(name);

        verify(personRepository).deleteByName(name);
    }

    @Test
    void deleteByName_EntityDoesntExist_ThrowsPersonNotFoundException() {
        when(personRepository.existsByName(anyString())).thenReturn(false);

        assertThrows(PersonNotFoundException.class,
                () -> personService.deleteByName(name));

        verify(personRepository, never()).deleteByName(name);
    }

    @Test
    void upgradeRole() {
        when(personRepository.findById(anyLong())).thenReturn(Optional.of(mockPerson));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(personRepository.save(any())).thenReturn(mockPerson);

        personService.upgradeRole(name, id);
        assertEquals(UserRole.ROLE_ADMIN, mockPerson.getRole());

        verify(personRepository).findById(id);
        verify(passwordEncoder).matches(name, mockPerson.getPassword());
        verify(personRepository).save(mockPerson);
    }

    @Test
    void upgradeRole_EntityDoesntExist_ThrowsPersonNotFoundException() {
        when(personRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(personRepository.save(any())).thenReturn(mockPerson);

        assertThrows(PersonNotFoundException.class,
                () -> personService.upgradeRole(name, id));

        verify(personRepository).findById(id);
        verify(passwordEncoder, never()).matches(name, mockPerson.getPassword());
        verify(personRepository, never()).save(mockPerson);
    }

    @Test
    void upgradeRole_BadCredentials_ThrowsBadRequestException() {
        when(personRepository.findById(anyLong())).thenReturn(Optional.of(mockPerson));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        when(personRepository.save(any())).thenReturn(mockPerson);

        assertThrows(BadRequestException.class,
                () -> personService.upgradeRole(name, id));

        verify(personRepository).findById(id);
        verify(passwordEncoder).matches(name, mockPerson.getPassword());
        verify(personRepository, never()).save(mockPerson);
    }
}