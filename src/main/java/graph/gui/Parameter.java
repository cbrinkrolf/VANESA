package graph.gui;

import java.math.BigDecimal;

public class Parameter {
	private String name;
	private BigDecimal value;
	private String unit;

	public Parameter(final String name, final BigDecimal value, final String unit) {
		this.name = name.trim();
		this.value = value != null ? value : BigDecimal.ZERO;
		this.unit = unit;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name.trim();
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(final BigDecimal value) {
		this.value = value != null ? value : BigDecimal.ZERO;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(final String unit) {
		this.unit = unit;
	}

	@Override
	@SuppressWarnings("MethodDoesntCallSuperMethod")
	public Parameter clone() {
		return new Parameter(name, value, unit);
	}
}
