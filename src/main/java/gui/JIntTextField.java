package gui;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class JIntTextField extends JTextField {
	private static final DocumentFilter INT_FILTER = new DocumentFilter() {
		@Override
		public void insertString(final FilterBypass fb, final int offset, final String string, final AttributeSet attr)
				throws BadLocationException {
			if (NumberUtils.isDigits(string)) {
				super.insertString(fb, offset, string, attr);
			}
		}

		@Override
		public void replace(final FilterBypass fb, final int offset, final int length, final String text,
				final AttributeSet attrs) throws BadLocationException {
			if (NumberUtils.isDigits(text)) {
				super.replace(fb, offset, length, text, attrs);
			}
		}
	};

	public JIntTextField(int columns) {
		super(columns);
		((PlainDocument) getDocument()).setDocumentFilter(INT_FILTER);
	}

	public JIntTextField() {
		((PlainDocument) getDocument()).setDocumentFilter(INT_FILTER);
	}

	public Integer getValue() {
		final String text = getText();
		return StringUtils.isEmpty(text) ? null : Integer.parseInt(text);
	}

	@Override
	public void setText(final String text) {
		if (NumberUtils.isDigits(text)) {
			super.setText(text);
		} else {
			super.setText("");
		}
	}
}
