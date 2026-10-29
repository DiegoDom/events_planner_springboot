package com.ddominguez.events.planner.api.service;

import com.ddominguez.events.planner.api.domain.Event;
import com.ddominguez.events.planner.api.exception.ResourceNotFoundException;
import com.ddominguez.events.planner.api.repository.EventRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService implements IEventService {

  private final EventRepository eventRepository;

  @Override
  public List<Event> findAll() {
    return eventRepository.findAll();
  }

  @Override
  public Event findById(Long id) {
    return eventRepository
        .findById(id)
        .orElseThrow(
            () -> new ResourceNotFoundException("Event with the id: " + id + " does not exits"));
  }

  @Override
  public Event save(Event event) {
    return eventRepository.save(event);
  }

  @Override
  public void deleteById(Long id) {
    Event event = this.findById(id);
    eventRepository.delete(event);
  }
}
