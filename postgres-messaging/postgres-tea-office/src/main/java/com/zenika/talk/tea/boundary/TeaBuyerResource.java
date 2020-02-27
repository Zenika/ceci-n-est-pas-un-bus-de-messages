package com.zenika.talk.tea.boundary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TeaBuyerResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(TeaBuyerResource.class);

	@Autowired
	TeaBuyerService service;

	@PostMapping("/tea/order")
	public void orderTea(@RequestParam("amount") long amount) {
		service.orderTea(1000);
	}
}
