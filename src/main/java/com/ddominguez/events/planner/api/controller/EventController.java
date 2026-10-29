package com.ddominguez.events.planner.api.controller;

import com.ddominguez.events.planner.api.domain.Event;
import com.ddominguez.events.planner.api.dto.EventRequestDto;
import com.ddominguez.events.planner.api.dto.EventResponseDto;
import com.ddominguez.events.planner.api.mapper.EventMapper;
import com.ddominguez.events.planner.api.service.IEventService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {
  private final IEventService eventService;
  private final EventMapper eventMapper;

  @GetMapping
  public List<EventResponseDto> findAll() {
    List<Event> events = eventService.findAll();

    return eventMapper.toEventResponseDtoList(events);
  }

  @GetMapping("/{id}")
  public ResponseEntity<EventResponseDto> findById(@PathVariable Long id) {
    Event event = eventService.findById(id);
    EventResponseDto responseDto = eventMapper.toResponseDto(event);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<EventResponseDto> create(
      @Valid @RequestBody EventRequestDto eventRequestDto) {
    Event event = eventMapper.toEntity(eventRequestDto);
    Event eventToSave = eventService.save(event);
    EventResponseDto eventResponseDto = eventMapper.toResponseDto(eventToSave);
    return new ResponseEntity<>(eventResponseDto, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<EventResponseDto> update(
      @PathVariable Long id, @Valid @RequestBody EventRequestDto eventRequestDto) {
    Event eventToUpdate = eventService.findById(id);
    eventMapper.updateEventFromDto(eventRequestDto, eventToUpdate);
    Event updatedEvent = eventService.save(eventToUpdate);
    return new ResponseEntity<>(eventMapper.toResponseDto(updatedEvent), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    eventService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
