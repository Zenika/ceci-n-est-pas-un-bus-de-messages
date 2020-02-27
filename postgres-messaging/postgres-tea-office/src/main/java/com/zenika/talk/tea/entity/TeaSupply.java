package com.zenika.talk.tea.entity;

import com.zenika.talk.office.entity.Office;

import java.util.Objects;

public class TeaSupply {

	private String office;
	private long amount;
	private String unit;

	public static TeaSupply availabilityFor(Office office, String unit) {
		return new TeaSupply(office.getName(), office.getTeaStock(),  unit);
	}

	public TeaSupply() {
	}

	public TeaSupply(String office, long amount, String unit) {
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
		TeaSupply teaSupply = (TeaSupply) o;
		return amount == teaSupply.amount &&
			Objects.equals(office, teaSupply.office) &&
			Objects.equals(unit, teaSupply.unit);
	}

	@Override
	public int hashCode() {
		return Objects.hash(office, amount, unit);
	}

	@Override
	public String toString() {
		return "TeaSupply{" +
			"office='" + office + '\'' +
			", amount=" + amount +
			", unit='" + unit + '\'' +
			'}';
	}
}
