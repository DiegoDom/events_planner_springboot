package com.ddominguez.events.planner.api.mapper;

import com.ddominguez.events.planner.api.domain.Event;
import com.ddominguez.events.planner.api.dto.EventRequestDto;
import com.ddominguez.events.planner.api.dto.EventResponseDto;
import com.ddominguez.events.planner.api.dto.EventUpdateRequestDto;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface EventMapper {
  List<EventResponseDto> toEventResponseDtoList(List<Event> events);

  Event toEntity(EventRequestDto eventRequestDto);

  EventResponseDto toResponseDto(Event event);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEventFromDto(EventUpdateRequestDto requestDto, @MappingTarget Event event);
}
