package com.zenika.talk.events.boundary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenika.talk.events.control.Event;
import com.zenika.talk.events.control.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventEmitter {

	@Autowired
	EventRepository repository;

	@Autowired
	ObjectMapper objectMapper;

	public <T> void sendEvent(String topic, T payload) {
		try {
			String jsonPayload = objectMapper.writeValueAsString(payload);
			Event event = Event.of(topic, jsonPayload);
			repository.save(event);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

}
