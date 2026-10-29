package com.ddominguez.events.planner.api.mapper;

import com.ddominguez.events.planner.api.domain.Event;
import com.ddominguez.events.planner.api.dto.EventRequestDto;
import com.ddominguez.events.planner.api.dto.EventResponseDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EventMapper {
  List<EventResponseDto> toEventResponseDtoList(List<Event> events);

  Event toEntity(EventRequestDto eventRequestDto);

  EventResponseDto toResponseDto(Event event);

  void updateEventFromDto(EventRequestDto requestDto, @MappingTarget Event event);
}
