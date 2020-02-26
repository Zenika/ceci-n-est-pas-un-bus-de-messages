package com.zenika.talk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TeaBuyerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TeaBuyerService.class);

	public void onTeaSupply(TeaSupply teaSupply) {
		LOGGER.info("Received Tea Supply: {}", teaSupply);
	}
}
