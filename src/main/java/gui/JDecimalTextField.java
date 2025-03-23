package gui;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.math.BigDecimal;
import java.util.regex.Pattern;

public class JDecimalTextField extends JTextField {
	private static final Pattern UDECIMAL_PATTERN = Pattern.compile("\\+?([0-9]+\\.)?[0-9]*");
	private static final Pattern DECIMAL_PATTERN = Pattern.compile("[+\\-]?([0-9]+\\.)?[0-9]*");

	private final Pattern pattern;
	private final boolean allowNegative;

	public JDecimalTextField(final int columns, final boolean allowNegative) {
		super(columns);
		this.pattern = allowNegative ? DECIMAL_PATTERN : UDECIMAL_PATTERN;
		this.allowNegative = allowNegative;
		setHorizontalAlignment(RIGHT);
		((PlainDocument) getDocument()).setDocumentFilter(new DocumentFilter() {
			@Override
			public void insertString(final FilterBypass fb, final int offset, final String string,
					final AttributeSet attr) throws BadLocationException {
				if (StringUtils.isNotEmpty(string)) {
					final var targetText = getText(0, offset) + string + getText(offset, getText().length() - offset);
					if (pattern.matcher(targetText).matches()) {
						super.insertString(fb, offset, string, attr);
					}
				}
			}

			@Override
			public void replace(final FilterBypass fb, final int offset, final int length, final String text,
					final AttributeSet attrs) throws BadLocationException {
				final var targetText = getText(0, offset) + text + getText(offset + length,
						getText().length() - offset - length);
				if (pattern.matcher(targetText).matches()) {
					super.replace(fb, offset, length, text, attrs);
				}
			}
		});
	}

	public JDecimalTextField(final boolean allowNegative) {
		this(0, allowNegative);
	}

	public JDecimalTextField(final int columns) {
		this(columns, false);
	}

	public JDecimalTextField() {
		this(0, false);
	}

	public Float getValue() {
		return getValue(null);
	}

	public Float getValue(final Float fallback) {
		final String text = getText();
		return StringUtils.isEmpty(text) || (text.length() == 1 && (text.charAt(0) == '+') || text.charAt(0) == '-')
				? fallback
				: Float.parseFloat(text);
	}

	public Double getDoubleValue() {
		return getDoubleValue(null);
	}

	public Double getDoubleValue(final Double fallback) {
		final String text = getText();
		return StringUtils.isEmpty(text) || (text.length() == 1 && (text.charAt(0) == '+') || text.charAt(0) == '-')
				? fallback
				: Double.parseDouble(text);
	}

	public BigDecimal getBigDecimalValue() {
		return getBigDecimalValue(null);
	}

	public BigDecimal getBigDecimalValue(final BigDecimal fallback) {
		final String text = getText();
		return StringUtils.isEmpty(text) || (text.length() == 1 && (text.charAt(0) == '+') || text.charAt(0) == '-')
				? fallback
				: new BigDecimal(text);
	}

	public void setValue(final float value) {
		setText(value >= 0 || allowNegative ? String.valueOf(value) : "");
	}

	public void setValue(final double value) {
		setText(value >= 0 || allowNegative ? String.valueOf(value) : "");
	}

	public void setValue(final BigDecimal value) {
		setText(value != null && (value.signum() >= 0 || allowNegative) ? value.toPlainString() : "");
	}

	@Override
	public void setText(final String text) {
		if (StringUtils.isBlank(text) || (text.length() == 1 && (text.charAt(0) == '+' || text.charAt(0) == '-'))) {
			super.setText("");
		} else {
			super.setText(pattern.matcher(text).matches() ? text : "");
		}
	}
}
