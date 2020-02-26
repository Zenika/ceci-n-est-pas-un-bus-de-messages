package com.zenika.talk;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Office {

	@Value("${office.name}")
	private String name;

	@Value("${office.type}")
	private OfficeType type;

	public String getName() {
		return name;
	}

	public OfficeType getType() {
		return type;
	}
}
