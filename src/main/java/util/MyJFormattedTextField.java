package util;

import java.text.Format;
import java.text.ParseException;

import javax.swing.JFormattedTextField;

public class MyJFormattedTextField extends JFormattedTextField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyJFormattedTextField() {
		super();
	}

	public MyJFormattedTextField(Format format) {
		super(format);
	}

	public MyJFormattedTextField(AbstractFormatter formatter) {
		super(formatter);
	}

	public MyJFormattedTextField(AbstractFormatterFactory aff) {
		super(aff);
	}

	@Override
	public Object getValue() {
		try {
			this.commitEdit();
		} catch (ParseException e) {
			// e.printStackTrace();
		}
		// this.setValue(((Number) super.getValue()).doubleValue());
		if ((super.getValue()) != null) {
			return Math.abs(((Number) super.getValue()).doubleValue());
		} else {
			return super.getValue();
		}
	}

}
