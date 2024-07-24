package ru.golovkov.myrestapp.service;

import ru.golovkov.myrestapp.model.dto.request.PersonRequestDto;
import ru.golovkov.myrestapp.model.dto.response.PersonResponseDto;

import java.util.List;

public interface PersonService extends CrudService<PersonRequestDto, PersonResponseDto> {

    PersonResponseDto getByName(String name);

    List<PersonResponseDto> getAllByNameContaining(String name, int pageNumber, int pageSize);

    void updateByName(PersonRequestDto personRequestDto, String name);

    void deleteByName(String name);

    void upgradeRole(String rawPassword, Long id);
}
