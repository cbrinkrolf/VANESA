package graph.gui;

import net.miginfocom.swing.MigLayout;
import org.scilab.forge.jlatexmath.ParseException;
import prettyFormula.DetailedParseCancellationException;
import prettyFormula.FormulaParser;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class FormulaPanel extends JPanel {
	private static final long serialVersionUID = -5384885466046701104L;
	private static final Color ERROR_COLOR = new Color(176, 0, 0);
	private final DefaultHighlightPainter errorHighlighter = new DefaultHighlightPainter(ERROR_COLOR);
	final JFileChooser fileChooser = new JFileChooser();
	private final JLabel status = new JLabel();
	private final JLabel formulaDisplay = new JLabel();
	private final JTextPane textField;

	public FormulaPanel(JTextPane textField, String formula) {
		super(new MigLayout("ins 0, fill, wrap 2", "[grow][]", "[][]"));
		this.textField = textField;
		textField.setText(formula);
		status.setForeground(ERROR_COLOR);
		fileChooser.setFileFilter(new FileNameExtensionFilter("SVG file", "svg", "SVG"));
		textField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				evaluateFormula();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				evaluateFormula();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				evaluateFormula();
			}
		});
		textField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent evt) {
				evaluateFormula();
			}

			public void keyTyped(KeyEvent evt) {
				jTextPane1KeyTyped();
			}
		});
		final JButton exportButton = new JButton();
		exportButton.setText("export");
		exportButton.addActionListener(e -> onExportClicked());
		add(textField, "growx");
		add(exportButton);
		add(status, "growx, span 2");
		add(formulaDisplay, "growx, span 2");
		evaluateFormula();
	}

	private void evaluateFormula() {
		status.setText("");
		textField.getHighlighter().removeAllHighlights();
		try {
			final BufferedImage image = FormulaParser.parseToImage(textField.getText());
			formulaDisplay.setIcon(new ImageIcon(image));
		} catch (DetailedParseCancellationException e) {
			handleDetailedParseCancellationException(e);
		} catch (ParseException e) {
			status.setText(e.getMessage());
		}
	}

	private void jTextPane1KeyTyped() {
		// disallow multiple lines
		String formula = textField.getText();
		if (formula.contains("\n")) {
			textField.setText(formula.replace("\n", ""));
		}
	}

	private void onExportClicked() {
		int returnVal = fileChooser.showSaveDialog(textField);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				FormulaParser.saveToSVG(textField.getText(), fileChooser.getSelectedFile());
			} catch (IOException | ParseException e) {
				status.setText(e.getMessage());
			} catch (DetailedParseCancellationException e) {
				handleDetailedParseCancellationException(e);
			}
		}
		status.setText("saved");
	}

	private void handleDetailedParseCancellationException(DetailedParseCancellationException e) {
		try {
			// highlight the position at which the error occurred
			textField.getHighlighter().addHighlight(e.getCharPositionInLine(), e.getEndCharPositionInLine(),
					errorHighlighter);
		} catch (BadLocationException ignored) {
		}
		status.setText(e.getMessage());
	}

	public String getFormula() {
		return textField.getText();
	}
}
