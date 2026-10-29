package com.ddominguez.events.planner.api.service;

import com.ddominguez.events.planner.api.domain.Event;
import java.util.List;

public interface IEventService {
  List<Event> findAll();

  Event findById(Long id);

  Event save(Event event);

  void deleteById(Long id);
}
