package graph.gui;

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
import java.beans.PropertyChangeListener;
import java.io.IOException;

/**
 * @author Martin Zurowietz
 */
public class FormulaPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final DefaultHighlightPainter errorHighlighter;
    final JFileChooser fileChooser;
    private final JLabel status = new JLabel();
    private final JLabel formulaDisplay = new JLabel();
    private final JTextPane textField;

    public FormulaPanel(JTextPane textField, String formula, PropertyChangeListener pcListener) {
        this.textField = textField;
        initComponents(formula, pcListener);
        errorHighlighter = new DefaultHighlightPainter(Color.red);
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("SVG file", "svg", "SVG"));
        jTextPane1KeyReleased();
    }

    private void initComponents(String formula, PropertyChangeListener pcListener) {
        if (pcListener != null) {
            formulaDisplay.addPropertyChangeListener(pcListener);
        }
        JScrollPane jScrollPane1 = new JScrollPane();
        JButton exportButton = new JButton();
        textField.setText(formula);
        status.setForeground(new Color(176, 1, 1));
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                jTextPane1KeyReleased();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                jTextPane1KeyReleased();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                jTextPane1KeyReleased();
            }
        });
        textField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent evt) {
                jTextPane1KeyReleased();
            }

            public void keyTyped(KeyEvent evt) {
                jTextPane1KeyTyped();
            }
        });
        jScrollPane1.setViewportView(textField);
        exportButton.setText("export");
        exportButton.addActionListener(e -> onExportClicked());
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(
                                                                                        GroupLayout.Alignment.LEADING).addComponent(status, GroupLayout.DEFAULT_SIZE,
                                                                                                                                    GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                .addComponent(formulaDisplay,
                                                                                              GroupLayout.DEFAULT_SIZE,
                                                                                              GroupLayout.DEFAULT_SIZE,
                                                                                              Short.MAX_VALUE).addGroup(
                                layout.createSequentialGroup().addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 313,
                                                                            Short.MAX_VALUE)
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                      .addComponent(exportButton))).addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                      .addGroup(layout.createSequentialGroup().addContainerGap().addGroup(
                                                              layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                    .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE,
                                                                                  GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                                    .addComponent(exportButton)).addPreferredGap(
                                                              LayoutStyle.ComponentPlacement.RELATED).addComponent(status,
                                                                                                                   GroupLayout.PREFERRED_SIZE,
                                                                                                                   15,
                                                                                                                   GroupLayout.PREFERRED_SIZE)
                                                      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                      .addComponent(formulaDisplay, GroupLayout.DEFAULT_SIZE, 96,
                                                                    Short.MAX_VALUE).addContainerGap()));
        status.setText("");
        formulaDisplay.setVisible(true);
        try {
            BufferedImage image = FormulaParser.parseToImage(textField.getText());
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

    private void jTextPane1KeyReleased() {
        status.setText("");
        textField.getHighlighter().removeAllHighlights();
        try {
            BufferedImage image = FormulaParser.parseToImage(textField.getText());
            formulaDisplay.setIcon(new ImageIcon(image));
        } catch (DetailedParseCancellationException e) {
            handleDetailedParseCancellationException(e);
        } catch (ParseException e) {
            status.setText(e.getMessage());
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
