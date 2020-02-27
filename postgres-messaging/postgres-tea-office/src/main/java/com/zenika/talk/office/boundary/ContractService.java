package com.zenika.talk.office.boundary;

import com.zenika.talk.office.control.ContractRepository;
import com.zenika.talk.office.entity.Contract;
import com.zenika.talk.office.entity.Office;
import com.zenika.talk.tea.entity.TeaSupply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class ContractService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContractService.class);

	@Value("${office.initial.tea.needed}")
	long amountToOrder;

	@Autowired
	Office office;

	@Autowired
	ContractRepository contractRepository;

	@PostConstruct
	public void init() {
		LOGGER.info("This office ({}) needs {} tons of tea", office.getName(), amountToOrder);
	}

	/**
	 * Receives the supply notification. Decides based on the existing contract if more supplies of tea is needed. If
	 * that is the case, fill and send a new contract. If it's not, return no contract.
	 * @param supply The supply received from one of the localized office
	 * @return A contract if needed, or else no contract.
	 */
	public Optional<Contract> handleSupplyNotification(TeaSupply supply) {
		LOGGER.info("Handling tea supply notification: {}", supply);
		LOGGER.info("Total amount of tea needed: {}", amountToOrder);

		List<Contract> contracts = contractRepository.findByCustomer(office.getName());
		long amountOrdered = calculateAmountOrdered(contracts);

		LOGGER.info("Current ordered amount: {}", amountOrdered);

		long amountLeftToFill = amountToOrder - amountOrdered;
		LOGGER.info("Amount needed left: {}", amountLeftToFill);

		if(needMoreTea(amountLeftToFill) && hasSomeTea(supply)) {
			Contract contract = makeContract(amountLeftToFill, supply);
			return Optional.of(contract);
		} else {
			LOGGER.info("No more tea is needed, or this office has no tea left");
			return noContract();
		}
	}

	private boolean hasSomeTea(TeaSupply supply) {
		return supply.getAmount() >= 0;
	}

	private Contract makeContract(long amountLeftToFill, TeaSupply supply) {
		long orderedAmount = Math.min(amountLeftToFill, supply.getAmount());
		Contract contract = new Contract(office.getName(), supply.getOffice(), orderedAmount, supply.getUnit());

		LOGGER.info("Drafting a contract from {} to {} for {} {} of tea", office.getName(), supply.getOffice(),
				orderedAmount, contract.getUnit());

		return contractRepository.save(contract);
	}

	private long calculateAmountOrdered(List<Contract> contracts) {
		return contracts.stream().mapToLong(Contract::getAmount).sum();
	}

	private boolean needMoreTea(long amountLeftToFill) {
		return amountLeftToFill > 0;
	}

	private Optional<Contract> noContract() {
		return Optional.empty();
	}

	public void setAmountToOrder(long amount) {
		amountToOrder = amount;
	}
}
