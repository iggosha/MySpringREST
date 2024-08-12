package ru.golovkov.myrestapp.service;

import org.springframework.data.domain.Pageable;
import ru.golovkov.myrestapp.model.dto.request.PersonRequestDto;
import ru.golovkov.myrestapp.model.dto.response.PersonResponseDto;

import java.util.List;

public interface PersonService extends CrudService<PersonRequestDto, PersonResponseDto> {

    PersonResponseDto getByName(String name);

    List<PersonResponseDto> getAllByNameContaining(String name, Pageable pageable);

    PersonResponseDto updateByName(PersonRequestDto personRequestDto, String name);

    void deleteByName(String name);

    PersonResponseDto upgradeRole(String rawPassword, Long id);
}
