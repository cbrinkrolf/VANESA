package graph.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class AutoSuggester {
	private final JTextPane textField;
	private final Window container;
	private final JPanel suggestionsPanel;
	private final JWindow autoSuggestionPopUpWindow;
	private final Set<String> dictionary = new HashSet<>();
	private int tW = 0;
	private int tH = 0;
	private final Color suggestionsTextColor;
	private final Color suggestionFocusedColor;
	private int lastFocusableIndex = 0;

	public AutoSuggester(final JTextPane textField, final Window mainWindow, final Set<String> words) {
		this(textField, mainWindow, words, Color.WHITE, Color.BLUE, Color.RED, 1f);
	}

	public AutoSuggester(final JTextPane textField, final Window mainWindow, final Set<String> words,
			final Color popUpBackground, final Color textColor, final Color suggestionFocusedColor,
			final float opacity) {
		this.textField = textField;
		this.suggestionsTextColor = textColor;
		this.container = mainWindow;
		this.suggestionFocusedColor = suggestionFocusedColor;
		this.textField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(final DocumentEvent de) {
				checkForAndShowSuggestions();
			}

			@Override
			public void removeUpdate(final DocumentEvent de) {
				checkForAndShowSuggestions();
			}

			@Override
			public void changedUpdate(final DocumentEvent de) {
				checkForAndShowSuggestions();
			}
		});
		if (words != null) {
			dictionary.addAll(words);
		}
		autoSuggestionPopUpWindow = new JWindow(mainWindow);
		autoSuggestionPopUpWindow.setOpacity(opacity);
		suggestionsPanel = new JPanel();
		suggestionsPanel.setLayout(new GridLayout(0, 1));
		suggestionsPanel.setBackground(popUpBackground);
		suggestionsPanel.setBorder(new LineBorder(Color.BLACK));
		addKeyBindingToRequestFocusInPopUpWindow();
	}

	private void addKeyBindingToRequestFocusInPopUpWindow() {
		textField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true),
				"Down released");
		textField.getActionMap().put("Down released", new AbstractAction() {
			private static final long serialVersionUID = 9052168155182385575L;

			@Override
			public void actionPerformed(final ActionEvent ae) {
				// focuses the first label
				lastFocusableIndex = 0;
				for (int i = 0; i < suggestionsPanel.getComponentCount(); i++) {
					if (suggestionsPanel.getComponent(i) instanceof SuggestionLabel) {
						((SuggestionLabel) suggestionsPanel.getComponent(i)).setFocused(true);
						autoSuggestionPopUpWindow.toFront();
						autoSuggestionPopUpWindow.requestFocusInWindow();
						suggestionsPanel.requestFocusInWindow();
						suggestionsPanel.getComponent(i).requestFocusInWindow();
						break;
					}
				}
			}
		});
		suggestionsPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "Down released");
		suggestionsPanel.getActionMap().put("Down released", new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent ae) {
				moveSelectedFocus(1);
			}
		});
		suggestionsPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), "Up released");
		suggestionsPanel.getActionMap().put("Up released", new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent ae) {
				moveSelectedFocus(-1);
			}
		});
	}

	private void moveSelectedFocus(final int offset) {
		final List<SuggestionLabel> sls = getAddedSuggestionLabels();
		for (final var suggestionLabel : sls) {
			suggestionLabel.setFocused(false);
		}
		lastFocusableIndex += offset;
		if (lastFocusableIndex <= -1 || lastFocusableIndex >= sls.size()) {
			lastFocusableIndex = 0;
			setFocusToTextField();
			checkForAndShowSuggestions();
		} else {
			sls.get(lastFocusableIndex).setFocused(true);
			autoSuggestionPopUpWindow.toFront();
			autoSuggestionPopUpWindow.requestFocusInWindow();
			suggestionsPanel.requestFocusInWindow();
			suggestionsPanel.getComponent(lastFocusableIndex).requestFocusInWindow();
		}
	}

	private void setFocusToTextField() {
		container.toFront();
		container.requestFocusInWindow();
		textField.requestFocusInWindow();
	}

	public List<SuggestionLabel> getAddedSuggestionLabels() {
		List<SuggestionLabel> sls = new ArrayList<>();
		for (int i = 0; i < suggestionsPanel.getComponentCount(); i++) {
			if (suggestionsPanel.getComponent(i) instanceof SuggestionLabel) {
				sls.add((SuggestionLabel) suggestionsPanel.getComponent(i));
			}
		}
		return sls;
	}

	private void checkForAndShowSuggestions() {
		// Postpone retrieving the currently typed word, as the caret position has not been updated at this point
		SwingUtilities.invokeLater(() -> {
			final String typedWord = getCurrentlyTypedWord();
			// remove previous words/JLabels that were added
			suggestionsPanel.removeAll();
			// used to calculate size of JWindow as new JLabels are added
			tW = 0;
			tH = 0;
			boolean added = wordTyped(typedWord);
			if (!added) {
				if (autoSuggestionPopUpWindow.isVisible()) {
					autoSuggestionPopUpWindow.setVisible(false);
				}
			} else {
				showPopUpWindow();
				setFocusToTextField();
			}
		});
	}

	protected void addWordToSuggestions(final String word) {
		SuggestionLabel suggestionLabel = new SuggestionLabel(word, suggestionFocusedColor, suggestionsTextColor, this);
		calculatePopUpWindowSize(suggestionLabel);
		suggestionsPanel.add(suggestionLabel);
	}

	public String getCurrentlyTypedWord() {
		final String text = textField.getText();
		final int pos = textField.getCaretPosition();
		final int startSearchIndex = Math.min(pos, text.length()) - 1;
		int maxIndex = 0;
		maxIndex = Math.max(maxIndex, text.lastIndexOf(' ', startSearchIndex) + 1);
		maxIndex = Math.max(maxIndex, text.lastIndexOf('/', startSearchIndex) + 1);
		maxIndex = Math.max(maxIndex, text.lastIndexOf('*', startSearchIndex) + 1);
		maxIndex = Math.max(maxIndex, text.lastIndexOf('+', startSearchIndex) + 1);
		maxIndex = Math.max(maxIndex, text.lastIndexOf('-', startSearchIndex) + 1);
		maxIndex = Math.max(maxIndex, text.lastIndexOf('^', startSearchIndex) + 1);
		// Potentially add: <, >, =
		return text.substring(maxIndex, Math.min(pos, text.length())).trim();
	}

	private void calculatePopUpWindowSize(final JLabel label) {
		// Update width and height so we can size the JWindow correctly
		final Dimension preferredSize = label.getPreferredSize();
		tW = Math.max(tW, preferredSize.width);
		tH += preferredSize.height;
	}

	private void showPopUpWindow() {
		autoSuggestionPopUpWindow.getContentPane().add(suggestionsPanel);
		autoSuggestionPopUpWindow.setMinimumSize(new Dimension(textField.getWidth(), 30));
		autoSuggestionPopUpWindow.setSize(tW, tH);
		autoSuggestionPopUpWindow.setVisible(true);

		final int windowX = container.getX() + textField.getX() + 30;
		final int windowY;
		if (suggestionsPanel.getHeight() > autoSuggestionPopUpWindow.getMinimumSize().height) {
			windowY = 30 + container.getY() + textField.getY() + textField.getHeight()
					+ autoSuggestionPopUpWindow.getMinimumSize().height;
		} else {
			windowY = 30 + container.getY() + textField.getY() + textField.getHeight()
					+ autoSuggestionPopUpWindow.getHeight();
		}

		autoSuggestionPopUpWindow.setLocation(windowX, windowY);
		autoSuggestionPopUpWindow.setMinimumSize(new Dimension(textField.getWidth(), 30));
		autoSuggestionPopUpWindow.revalidate();
		autoSuggestionPopUpWindow.repaint();
	}

	public void setDictionary(final Set<String> words) {
		dictionary.clear();
		if (words == null) {
			return;//so we can call constructor with null value for dictionary without exception thrown
		}
		dictionary.addAll(words);
	}

	public JWindow getAutoSuggestionPopUpWindow() {
		return autoSuggestionPopUpWindow;
	}

	public Window getContainer() {
		return container;
	}

	public JTextPane getTextField() {
		return textField;
	}

	public void addToDictionary(final String word) {
		dictionary.add(word);
	}

	boolean wordTyped(final String typedWord) {
		if (typedWord.isEmpty()) {
			return false;
		}
		boolean suggestionAdded = false;
		for (String word : dictionary) {
			if (typedWord.length() > word.length()) {
				continue;
			}
			if (word.startsWith(typedWord)) {
				addWordToSuggestions(word);
				suggestionAdded = true;
			}
		}
		return suggestionAdded;
	}
}
