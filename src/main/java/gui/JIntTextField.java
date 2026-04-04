package gui;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.math.BigInteger;
import java.util.regex.Pattern;

public class JIntTextField extends JTextField {
	private static final long serialVersionUID = 1355389533216433243L;
	private static final Pattern UINT_PATTERN = Pattern.compile("\\+?\\d*");
	private static final Pattern INT_PATTERN = Pattern.compile("[+\\-]?\\d*");

	private final Pattern pattern;
	private final boolean allowNegative;

	public JIntTextField(final int columns, final boolean allowNegative) {
		super(columns);
		this.pattern = allowNegative ? INT_PATTERN : UINT_PATTERN;
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
				final var targetText = getText(0, offset) + text
						+ getText(offset + length, getText().length() - offset - length);
				if (pattern.matcher(targetText).matches()) {
					super.replace(fb, offset, length, text, attrs);
				}
			}
		});
	}

	public JIntTextField(final boolean allowNegative) {
		this(0, allowNegative);
	}

	public JIntTextField(final int columns) {
		this(columns, false);
	}

	public JIntTextField() {
		this(0, false);
	}

	public Integer getValue() {
		return getValue(null);
	}

	public Integer getValue(final Integer fallback) {
		final String text = getText();
		return StringUtils.isEmpty(text) || (text.length() == 1 && (text.charAt(0) == '+') || text.charAt(0) == '-')
				? fallback
				: Integer.parseInt(text);
	}

	public Long getLongValue() {
		return getLongValue(null);
	}

	public Long getLongValue(final Long fallback) {
		final String text = getText();
		return StringUtils.isEmpty(text) || (text.length() == 1 && (text.charAt(0) == '+') || text.charAt(0) == '-')
				? fallback
				: Long.parseLong(text);
	}

	public BigInteger getBigIntegerValue() {
		return getBigIntegerValue(null);
	}

	public BigInteger getBigIntegerValue(final BigInteger fallback) {
		final String text = getText();
		return StringUtils.isEmpty(text) || (text.length() == 1 && (text.charAt(0) == '+') || text.charAt(0) == '-')
				? fallback
				: new BigInteger(text);
	}

	public void setValue(final int value) {
		setText(value >= 0 || allowNegative ? String.valueOf(value) : "");
	}

	public void setValue(final long value) {
		setText(value >= 0 || allowNegative ? String.valueOf(value) : "");
	}

	public void setValue(final BigInteger value) {
		setText(value != null && (value.signum() >= 0 || allowNegative) ? value.toString() : "");
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
