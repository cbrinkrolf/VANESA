package gui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * JTextField with additional validation and an error label. To implement a custom validation, the {@link #validateText}
 * method needs to be implemented by a subclass.
 */
public class JValidatedTextField extends JPanel {
	private static final Dimension HIDE_DIMENSION = new Dimension(0, 0);
	private final JLabel errorLabel = new JLabel();
	private final JTextField input = new JTextField();
	private boolean errorVisible = false;
	private final Border defaultBorder;
	private final Border errorBorder;
	private final boolean validateLazy;
	private final Dimension errorLabelPreferredSize;

	public JValidatedTextField() {
		this(false);
	}

	/**
	 * @param validateLazy If lazy validation is used, the text field will only be validated on loosing focus.
	 *                     Otherwise, all changes to the text field are directly validated.
	 */
	public JValidatedTextField(final boolean validateLazy) {
		super(new MigLayout("ins 0, gap 0, fill, wrap"));
		this.validateLazy = validateLazy;
		errorLabelPreferredSize = errorLabel.getPreferredSize();
		setBackground(null);
		add(input, "growx");
		add(errorLabel, "growx");
		errorLabel.setSize(HIDE_DIMENSION);
		errorLabel.setForeground(Color.RED);
		defaultBorder = input.getBorder();
		errorBorder = BorderFactory.createLineBorder(Color.RED);
		input.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final FocusEvent e) {
				updateValidation(input.getText());
			}
		});
		if (!validateLazy) {
			input.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void insertUpdate(final DocumentEvent e) {
					updateValidation(input.getText());
				}

				@Override
				public void removeUpdate(final DocumentEvent e) {
					updateValidation(input.getText());
				}

				@Override
				public void changedUpdate(final DocumentEvent e) {
				}
			});
		}
	}

	private void updateValidation(final String text) {
		final String errorText = validateText(text);
		if (errorText != null) {
			errorLabel.setText(errorText);
			if (!errorVisible) {
				errorLabel.setSize(errorLabelPreferredSize);
				input.setBorder(errorBorder);
				revalidate();
				repaint();
			}
		} else {
			errorLabel.setText("");
			if (errorVisible) {
				errorLabel.setSize(HIDE_DIMENSION);
				input.setBorder(defaultBorder);
				revalidate();
				repaint();
			}
		}
		errorVisible = errorText != null;
	}

	/**
	 * Validate the text and in case of a validation error, return the error message. Otherwise, return null.
	 */
	public String validateText(final String text) {
		return null;
	}

	public String getText() {
		return input.getText();
	}

	public void setText(final String text) {
		input.setText(text);
		if (validateLazy) {
			updateValidation(text);
		}
	}

	public boolean isTextValid() {
		return validateLazy ? validateText(input.getText()) == null : !errorVisible;
	}
}
