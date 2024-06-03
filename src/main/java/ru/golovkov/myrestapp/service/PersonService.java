package ru.golovkov.myrestapp.service;

import ru.golovkov.myrestapp.model.dto.request.PersonRequestDto;
import ru.golovkov.myrestapp.model.dto.response.PersonResponseDto;

public interface PersonService extends CrudService<PersonRequestDto, PersonResponseDto> {

}
