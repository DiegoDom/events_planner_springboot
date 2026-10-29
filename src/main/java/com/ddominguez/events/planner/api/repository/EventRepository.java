package com.ddominguez.events.planner.api.repository;

import com.ddominguez.events.planner.api.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {}
