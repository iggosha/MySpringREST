package ru.golovkov.myrestapp.mapper;

import org.mapstruct.*;
import ru.golovkov.myrestapp.model.dto.request.MessageRequestDto;
import ru.golovkov.myrestapp.model.dto.response.MessageResponseDto;
import ru.golovkov.myrestapp.model.entity.Message;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MessageMapper {

    @Mapping(source = "senderId", target = "sender.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    Message requestDtoToEntity(MessageRequestDto requestDto);

    @Mapping(source = "sender.id", target = "senderId")
    MessageResponseDto entityToResponseDto(Message entity);

    List<MessageResponseDto> entityListToResponseDtoList(List<Message> entityList);

    void updateEntityFromRequestDto(@MappingTarget Message entity, MessageRequestDto requestDto);
}
