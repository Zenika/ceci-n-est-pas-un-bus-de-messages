package com.zenika.talk.office.entity;

import com.zenika.talk.tea.entity.TeaContract;

import javax.persistence.*;

@Entity
public class Contract {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 128, nullable = false)
	private String customer;


	@Column(length = 128, nullable = false)
	private String supplier;

	@Column(nullable = false)
	private Long amount;

	@Column(length = 1, nullable = false)
	private String unit = "t";

	public Contract() {
	}

	public Contract(String customer, String supplier, Long amount, String unit) {
		this.customer = customer;
		this.supplier = supplier;
		this.amount = amount;
		this.unit = unit;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public String toString() {
		return "Contract{" +
				"id=" + id +
				", customer='" + customer + '\'' +
				", supplier='" + supplier + '\'' +
				", amount=" + amount +
				", unit='" + unit + '\'' +
				'}';
	}

	public TeaContract generate() {
		return new TeaContract(this.customer, this.supplier, this.amount, this.unit);
	}
}
