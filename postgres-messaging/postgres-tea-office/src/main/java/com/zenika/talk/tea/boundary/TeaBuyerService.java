package com.zenika.talk.tea.boundary;

import com.zenika.talk.events.boundary.EventEmitter;
import com.zenika.talk.office.entity.Office;
import com.zenika.talk.tea.entity.TeaContract;
import com.zenika.talk.tea.entity.TeaOrder;
import com.zenika.talk.tea.entity.TeaSupply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TeaBuyerService {

	@Value("${events.orders.topic}")
	String ordersTopic;

	@Value("${events.contracts.topic}")
	String contractsTopic;

	@Autowired
	EventEmitter eventEmitter;

	@Autowired
	Office office;

	private static final Logger LOGGER = LoggerFactory.getLogger(TeaBuyerService.class);


	@PostConstruct
	public void initTest() {
		LOGGER.info("Initializing and sending a test order");
		orderTea(new TeaOrder(office.getName(), 300L, "t"));
	}

	public void onTeaSupply(TeaSupply teaSupply) {
		LOGGER.info("Received Tea Supply: {}", teaSupply);
	}

	public void orderTea(TeaOrder order) {
		LOGGER.info("Ordering tea: {}", order);
		eventEmitter.sendEvent(ordersTopic, order);
	}

	public void sendContract(TeaContract contract) {
		LOGGER.info("Sending contract: {}", contract);
		eventEmitter.sendEvent(contractsTopic, contract);
	}
}
