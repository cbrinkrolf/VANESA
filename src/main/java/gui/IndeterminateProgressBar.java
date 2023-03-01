package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

public class IndeterminateProgressBar {
    private final JProgressBar bar;
    private final int barMax;
    private final JPanel glass;

    public IndeterminateProgressBar(int max, String info, String text) {
        bar = new JProgressBar();
        glass = (JPanel) MainWindow.getInstance().getFrame().getGlassPane();
        glass.setVisible(false);
        int width = 250;
        int height = 58;
        int labelHeight = 20;
        barMax = max;
        JLabel label1 = new JLabel();
        label1.setFont(new Font(info, Font.BOLD, 12));
        label1.setBounds(3, 5, width - 6, labelHeight);
        label1.setText(info);
        label1.setForeground(Color.WHITE);
        label1.setVerticalTextPosition(SwingConstants.BOTTOM);
        label1.setHorizontalTextPosition(SwingConstants.CENTER);
        bar.setBackground(Color.WHITE);
        bar.setPreferredSize(new Dimension(width, 20));
        bar.setBorderPainted(true);
        bar.setMaximum(barMax);
        bar.setStringPainted(true);
        bar.setIndeterminate(true);
        bar.setString(text);
        MigLayout layout = new MigLayout();
        JPanel mainPanel = new JPanel(layout);
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        mainPanel.setBackground(new Color(66, 135, 200));
        mainPanel.add(label1, "span 1, align center, wrap");
        mainPanel.add(bar, "span, growx, wrap");
        mainPanel.setSize(width, height);
        glass.setLayout(new GridBagLayout());
        glass.add(mainPanel, new GridBagConstraints());
        glass.revalidate();
        glass.setVisible(true);
    }

    public void closeWindow() {
        bar.setValue(barMax);
        if (glass != null) {
            glass.removeAll();
        }
    }

    public void closeThread() {
        if (glass != null) {
            glass.removeAll();
            glass.setVisible(false);
        }
    }
}
