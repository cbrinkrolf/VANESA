package graph.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;

public class SuggestionLabel extends JLabel {
	private static final long serialVersionUID = -7792594909870037395L;

	private final JWindow autoSuggestionsPopUpWindow;
	private final AutoSuggester autoSuggester;
	private final LineBorder focusBorder;

	public SuggestionLabel(final String string, final Color focusBorderColor, final Color textColor,
			final AutoSuggester autoSuggester) {
		super(string);
		this.autoSuggester = autoSuggester;
		focusBorder = new LineBorder(focusBorderColor);
		this.autoSuggestionsPopUpWindow = autoSuggester.getAutoSuggestionPopUpWindow();
		setFocusable(true);
		setForeground(textColor);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				super.mouseClicked(me);
				replaceWithSuggestedText();
				autoSuggestionsPopUpWindow.setVisible(false);
			}
		});
		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "Enter released");
		getActionMap().put("Enter released", new AbstractAction() {
			private static final long serialVersionUID = 7144999705332637662L;

			@Override
			public void actionPerformed(ActionEvent ae) {
				replaceWithSuggestedText();
				autoSuggestionsPopUpWindow.setVisible(false);
			}
		});
	}

	public void setFocused(final boolean focused) {
		setBorder(focused ? focusBorder : null);
		repaint();
	}

	private void replaceWithSuggestedText() {
		final JTextPane textField = autoSuggester.getTextField();
		final String text = textField.getText();
		final String suggestedWord = getText();
		final String typedWord = autoSuggester.getCurrentlyTypedWord();
		final String preText = text.substring(0, textField.getCaretPosition() - typedWord.length());
		final String postText = text.substring(textField.getCaretPosition());
		final int newCaretPosition = textField.getCaretPosition() - typedWord.length() + suggestedWord.length() + 1;
		textField.setText(preText + suggestedWord + " " + postText);
		textField.setCaretPosition(newCaretPosition);
	}
}