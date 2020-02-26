package com.zenika.talk.events.control;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/*
 * In order to support postgres Jsonb type, we can use Vlad Mihalcea library.
 * See https://vladmihalcea.com/map-string-jpa-property-json-column-hibernate/ for further details.
 */

@Entity
@Table(name = "messages")
@TypeDef(
		name = "jsonb",
		typeClass = JsonBinaryType.class
)
public class Event {

	@Id
	@GeneratedValue
	private UUID id;

	@Column(nullable = false, length = 128)
	@NotNull
	private String topic;

	@Type(type = "jsonb")
	@Column(nullable = false, columnDefinition = "jsonb")
	@NotNull
	private String payload;

	public Event() {
	}

	public static Event of(@NotNull String topic, @NotNull String payload) {
		return new Event(topic, payload);
	}

	Event(@NotNull String topic, @NotNull String payload) {
		this.topic = topic;
		this.payload = payload;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}
}
