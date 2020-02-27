package com.zenika.talk.office.control;

import com.zenika.talk.office.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {

	List<Contract> findByCustomer(String customer);

}
