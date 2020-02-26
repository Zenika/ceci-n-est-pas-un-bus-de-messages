package com.zenika.talk.tea.boundary;

import com.zenika.talk.tea.entity.TeaContract;
import com.zenika.talk.tea.entity.TeaOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TeaSupplierService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TeaSupplierService.class);

	public void onTeaOrder(TeaOrder teaOrder) {
		LOGGER.info("Received Tea Order: {}", teaOrder);
	}

	public void onTeaContract(TeaContract teaContract) {
		LOGGER.info("Received Tea Contract: {}", teaContract);
	}
}
