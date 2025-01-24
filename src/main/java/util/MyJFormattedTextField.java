package util;

import java.text.Format;
import java.text.ParseException;

import javax.swing.JFormattedTextField;

public class MyJFormattedTextField extends JFormattedTextField {
	private static final long serialVersionUID = -6435884038796106377L;
	private final boolean allowSigned;

	public MyJFormattedTextField(Format format) {
		this(format, false);
	}

	public MyJFormattedTextField(Format format, boolean allowSigned) {
		super(format);
		this.allowSigned = allowSigned;
	}

	public MyJFormattedTextField(AbstractFormatter formatter) {
		super(formatter);
		this.allowSigned = false;
	}

	public MyJFormattedTextField(AbstractFormatterFactory aff) {
		super(aff);
		this.allowSigned = false;
	}

	@Override
	public Object getValue() {
		try {
			commitEdit();
		} catch (ParseException ignored) {
		}
		Object value = super.getValue();
		if (value == null) {
			return null;
		}
		if (allowSigned) {
			return ((Number) value).doubleValue();
		} else {
			return Math.abs(((Number) value).doubleValue());
		}
	}
}
