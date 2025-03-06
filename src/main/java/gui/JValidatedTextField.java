package gui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * JTextField with additional validation and an error label. To implement a custom validation, the {@link #validateText}
 * method needs to be implemented by a subclass.
 */
public class JValidatedTextField extends JPanel {
	private final JLabel errorLabel = new JLabel();
	private final JTextField input = new JTextField();
	private boolean errorVisible = false;
	private final Border defaultBorder;
	private final Border errorBorder;
	private final boolean validateLazy;

	public JValidatedTextField() {
		this(false);
	}

	/**
	 * @param validateLazy If lazy validation is used, the text field will only be validated on loosing focus.
	 *                     Otherwise, all changes to the text field are directly validated.
	 */
	public JValidatedTextField(final boolean validateLazy) {
		this.validateLazy = validateLazy;
		setLayout(new MigLayout("ins 0, fill, wrap"));
		setBackground(null);
		add(input, "growx");
		errorLabel.setForeground(Color.RED);
		defaultBorder = input.getBorder();
		errorBorder = BorderFactory.createLineBorder(Color.RED);
		input.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(final FocusEvent e) {
			}

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
				add(errorLabel, "growx");
				revalidate();
				repaint();
			}
			input.setBorder(errorBorder);
			errorVisible = true;
		} else {
			errorLabel.setText("");
			if (errorVisible) {
				remove(errorLabel);
				revalidate();
				repaint();
			}
			input.setBorder(defaultBorder);
			errorVisible = false;
		}
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

	public boolean isValid() {
		return validateLazy ? validateText(input.getText()) == null : !errorVisible;
	}
}
