package graph.gui;

import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import com.ezylang.evalex.Expression;
import gui.PopUpDialog;
import net.miginfocom.swing.MigLayout;
import org.scilab.forge.jlatexmath.ParseException;
import util.FormulaParser;
import simulation.VanesaExpressionConfiguration;

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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FormulaPanel extends JPanel {
	private static final long serialVersionUID = -5384885466046701104L;
	private static final Color ERROR_COLOR = new Color(176, 0, 0);
	private final DefaultHighlightPainter errorHighlighter = new DefaultHighlightPainter(ERROR_COLOR);
	private final List<ChangedListener> changedListeners = new ArrayList<>();
	private final JFileChooser fileChooser = new JFileChooser();
	private final JLabel status = new JLabel();
	private final JLabel formulaDisplay = new JLabel();
	private final JTextPane textField = new JTextPane();
	private final JButton createMissingParametersButton = new JButton("create missing parameters");
	private final GraphElementAbstract gea;
	private final Pathway pathway;

	public FormulaPanel(final String formula, final GraphElementAbstract gea, final Pathway pathway,
			final Frame frame) {
		super(new MigLayout("ins 0, fill, wrap 2", "[grow][]", "[][]"));
		this.gea = gea;
		this.pathway = pathway;
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
		final JButton exportButton = new JButton("export");
		exportButton.addActionListener(e -> onExportClicked());
		createMissingParametersButton.addActionListener(e -> onCreateMissingParametersClicked());
		createMissingParametersButton.setEnabled(false);
		add(textField, "growx");
		add(exportButton);
		add(status, "growx, span 2");
		add(createMissingParametersButton, "span 2");
		add(formulaDisplay, "growx, span 2");
		evaluateFormula();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new AutoSuggester(textField, frame, null, Color.WHITE, Color.BLUE, Color.RED, 0.75f) {
					@Override
					boolean wordTyped(final String typedWord) {
						final Set<String> words = new HashSet<>();
						for (final BiologicalNodeAbstract bna : pathway.getAllGraphNodes()) {
							if (!bna.isLogical()) {
								words.add(bna.getName());
							}
						}
						for (final Parameter p : gea.getParameters()) {
							words.add(p.getName());
						}
						setDictionary(words);
						return super.wordTyped(typedWord);
					}
				};
			}
		});
	}

	private void evaluateFormula() {
		boolean failed = false;
		textField.getHighlighter().removeAllHighlights();
		final var expression = createExpression();
		try {
			expression.getAbstractSyntaxTree();
			final String[] undefinedVariables = expression.getUndefinedVariables().toArray(new String[0]);
			if (undefinedVariables.length > 0) {
				createMissingParametersButton.setEnabled(true);
				final StringBuilder errorText = new StringBuilder("<html>Formula uses undefined variables: ");
				for (int i = 0; i < undefinedVariables.length; i++) {
					if (i > 0) {
						errorText.append(", ");
					}
					errorText.append("'").append(undefinedVariables[i]).append("'");
				}
				errorText.append(". Verify they are spelled correctly or add them as parameters or nodes.</html>");
				status.setText(errorText.toString());
				failed = true;
			} else {
				createMissingParametersButton.setEnabled(false);
			}
			try {
				final BufferedImage image = FormulaParser.parseToImage(textField.getText());
				formulaDisplay.setIcon(new ImageIcon(image));
			} catch (ParseException ignored) {
				formulaDisplay.setIcon(null);
				if (failed) {
					status.setText(status.getText() + "<br/>Failed to generate Latex image of formula");
				} else {
					status.setText("Failed to generate Latex image of formula");
				}
				failed = true;
			}
		} catch (com.ezylang.evalex.parser.ParseException e) {
			try {
				// highlight the position at which the error occurred
				textField.getHighlighter().addHighlight(e.getStartPosition(), e.getEndPosition(), errorHighlighter);
			} catch (BadLocationException ignored) {
			}
			status.setText(e.getMessage());
			failed = true;
		}
		if (!failed) {
			status.setText("");
		}
	}

	private Expression createExpression() {
		final var expression = new Expression(textField.getText(),
				VanesaExpressionConfiguration.EXPRESSION_CONFIGURATION);
		for (final BiologicalNodeAbstract bna : pathway.getAllGraphNodes()) {
			if (!bna.isLogical()) {
				// Dummy value to make the variable name known
				expression.with(bna.getName(), 1);
			}
		}
		for (final Parameter p : gea.getParameters()) {
			expression.with(p.getName(), p.getValue());
		}
		return expression;
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
				PopUpDialog.getInstance().show("Information", "File saved");
			} catch (com.ezylang.evalex.parser.ParseException | IOException | ParseException e) {
				PopUpDialog.getInstance().show("Information", "Exporting formula failed");
			}
		}
	}

	private void onCreateMissingParametersClicked() {
		final var expression = createExpression();
		try {
			expression.getAbstractSyntaxTree();
			for (final String name : expression.getUndefinedVariables()) {
				gea.getParameters().add(new Parameter(name, 0, ""));
			}
		} catch (com.ezylang.evalex.parser.ParseException ignored) {
		}
		for (final var listener : changedListeners) {
			listener.onParametersChanged();
		}
		evaluateFormula();
	}

	public String getFormula() {
		return textField.getText();
	}

	public void setFormula(final String formula) {
		textField.setText(formula.replace("\n", ""));
		textField.requestFocus();
	}

	public void addChangedListener(final ChangedListener changedListener) {
		if (!changedListeners.contains(changedListener)) {
			changedListeners.add(changedListener);
		}
	}

	public void removeChangedListener(final ChangedListener changedListener) {
		changedListeners.remove(changedListener);
	}

	public interface ChangedListener {
		void onParametersChanged();
	}
}
