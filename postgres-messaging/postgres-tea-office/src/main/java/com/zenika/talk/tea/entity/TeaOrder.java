package com.zenika.talk.tea.entity;

import com.zenika.talk.office.entity.Office;

import java.util.Objects;

public class TeaOrder {

	private String office;
	private long amount;
	private String unit;

	public TeaOrder() {
	}

	public static TeaOrder orderFor(Office office, long amount) {
		return new TeaOrder(office.getName(), amount, "t");
	}

	public static TeaOrder orderFor(Office office, long amount, String unit) {
		return new TeaOrder(office.getName(), amount, unit);
	}

	public TeaOrder(String office, long amount, String unit) {
		this.office = office;
		this.amount = amount;
		this.unit = unit;
	}

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
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
		TeaOrder teaOrder = (TeaOrder) o;
		return amount == teaOrder.amount &&
			Objects.equals(office, teaOrder.office) &&
			Objects.equals(unit, teaOrder.unit);
	}

	@Override
	public int hashCode() {
		return Objects.hash(office, amount, unit);
	}

	@Override
	public String toString() {
		return "TeaOrder{" +
			"office='" + office + '\'' +
			", amount=" + amount +
			", unit='" + unit + '\'' +
			'}';
	}
}
