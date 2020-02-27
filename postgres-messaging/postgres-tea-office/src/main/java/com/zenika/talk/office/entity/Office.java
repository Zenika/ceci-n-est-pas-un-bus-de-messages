package com.zenika.talk.office.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Office {

	@Value("${office.name}")
	private String name;

	@Value("${office.type}")
	private OfficeType type;

	@Value("${office.tea.stock}")
	private long teaStock;

	public String getName() {
		return name;
	}

	public OfficeType getType() {
		return type;
	}

	public long getTeaStock() {
		return teaStock;
	}
}
