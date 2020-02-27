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

@Service
public class TeaSupplierService {

	@Value("${events.supply.topic}")
	String supplyTopic;

	@Autowired
	EventEmitter eventEmitter;

	@Autowired
	Office office;

	private static final Logger LOGGER = LoggerFactory.getLogger(TeaSupplierService.class);

	public void onTeaOrder(TeaOrder teaOrder) {
		LOGGER.info("Received tea order: {} {} from {} office", teaOrder.getAmount(), teaOrder.getUnit(), teaOrder.getOffice());
		LOGGER.info("This office ({}) has {} t available", office.getName(), office.getTeaStock());
		LOGGER.info("Sending a possible supply of {} t", office.getTeaStock());
		eventEmitter.sendEvent(supplyTopic, TeaSupply.availabilityFor(office, "t"));
	}

	public void onTeaContract(TeaContract teaContract) {
		if(teaContract.getSupplier().equals(office.getName())) {
			LOGGER.info("Received Tea Contract: {}", teaContract);
		}
	}
}
