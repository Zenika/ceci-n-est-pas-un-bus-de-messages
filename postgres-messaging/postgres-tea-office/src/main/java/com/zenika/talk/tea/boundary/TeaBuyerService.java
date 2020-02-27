package com.zenika.talk.tea.boundary;

import com.zenika.talk.events.boundary.EventEmitter;
import com.zenika.talk.office.boundary.ContractService;
import com.zenika.talk.office.entity.Contract;
import com.zenika.talk.office.entity.Office;
import com.zenika.talk.tea.entity.TeaContract;
import com.zenika.talk.tea.entity.TeaOrder;
import com.zenika.talk.tea.entity.TeaSupply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

	@Autowired
	ContractService contractService;


	private static final Logger LOGGER = LoggerFactory.getLogger(TeaBuyerService.class);

	public void orderTea(long amount) {
		LOGGER.info("Ordering tea: {} tons", amount);

		contractService.setAmountToOrder(amount);

		eventEmitter.sendEvent(ordersTopic, TeaOrder.orderFor(office, amount, "t"));
	}

	public void onTeaSupply(final TeaSupply teaSupply) {
		LOGGER.info("Received a message from {} office. Amount of tea supply: {} {}",
				teaSupply.getOffice(), teaSupply.getAmount(), teaSupply.getUnit());

		Optional<Contract> contract = contractService.handleSupplyNotification(teaSupply);

		contract.ifPresent(c -> sendContract(c.generate()));
	}

	public void sendContract(TeaContract contract) {
		LOGGER.info("Sending a contract from this office ({}) to supplier {} for {} {} of tea",
				contract.getCustomer(), contract.getSupplier(), contract.getAmount(), contract.getUnit());

		eventEmitter.sendEvent(contractsTopic, contract);
	}
}
