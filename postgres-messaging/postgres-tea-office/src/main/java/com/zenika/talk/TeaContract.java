package com.zenika.talk;

import java.util.Objects;

public class TeaContract {

	private String customer;
	private String supplier;
	private long amount;
	private String unit;

	public TeaContract() {
	}

	public TeaContract(String customer, String supplier, long amount, String unit) {
		this.customer = customer;
		this.supplier = supplier;
		this.amount = amount;
		this.unit = unit;
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

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TeaContract that = (TeaContract) o;
		return amount == that.amount &&
			Objects.equals(customer, that.customer) &&
			Objects.equals(supplier, that.supplier) &&
			Objects.equals(unit, that.unit);
	}

	@Override
	public int hashCode() {
		return Objects.hash(customer, supplier, amount, unit);
	}

	@Override
	public String toString() {
		return "TeaContract{" +
			"customer='" + customer + '\'' +
			", supplier='" + supplier + '\'' +
			", amout=" + amount +
			", unit='" + unit + '\'' +
			'}';
	}
}
