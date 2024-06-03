package ru.golovkov.myrestapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.golovkov.myrestapp.model.dto.request.PersonRequestDto;
import ru.golovkov.myrestapp.model.dto.response.PersonResponseDto;
import ru.golovkov.myrestapp.model.entity.Person;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PersonMapper {

    PersonResponseDto toResponseDto(Person person);

    Person toEntity(PersonRequestDto personRequestDto);

    void updateEntityFromRequestDto(@MappingTarget Person person, PersonRequestDto requestDto);
}
