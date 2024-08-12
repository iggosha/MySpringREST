package ru.golovkov.myrestapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.golovkov.myrestapp.model.dto.request.PersonRequestDto;
import ru.golovkov.myrestapp.model.dto.response.PersonResponseDto;
import ru.golovkov.myrestapp.model.entity.Person;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PersonMapper {

    PersonResponseDto entityToResponseDto(Person entity);

    Person requestDtoToEntity(PersonRequestDto requestDto);

    List<PersonResponseDto> entityListToResponseDtoList(List<Person> entityList);

    void updateEntityFromRequestDto(@MappingTarget Person entity, PersonRequestDto requestDto);
}
