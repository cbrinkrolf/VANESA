package database.gui;

import gui.AsyncTaskExecutor;
import gui.PopUpDialog;
import gui.ImagePath;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class QueryMask {
    protected final JPanel panel;

    public QueryMask() {
        MigLayout layout = new MigLayout(null, "[grow, fill]");
        panel = new JPanel(layout);
    }

    protected void addControlButtons() {
        JButton search = new JButton("search");
        search.addActionListener(e -> searchGUI());
        JButton reset = new JButton("reset");
        reset.addActionListener(e -> reset());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(reset);
        buttonPanel.add(search);
        panel.add(new JSeparator(), "span, growx, wrap");
        panel.add(buttonPanel, "span");
    }

    public abstract String getMaskName();

    public final JPanel getPanel() {
        return panel;
    }

    public JPanel getTitleTab() {
        JPanel pan = new JPanel(new MigLayout("ins 0"));
        pan.add(new JLabel(getMaskName()));
        JButton info = new JButton(ImagePath.getInstance().getImageIcon("infoButton.png"));
        info.setMaximumSize(new Dimension(20, 20));
        info.addActionListener(e -> showInfoWindow());
        info.setBorderPainted(false);
        info.setContentAreaFilled(false);
        info.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                info.setContentAreaFilled(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                info.setContentAreaFilled(false);
            }
        });
        pan.add(info);
        return pan;
    }

    protected abstract void reset();

    private void searchGUI() {
        if (!doSearchCriteriaExist()) {
            PopUpDialog.getInstance().show("Error", "Please type something into the search form.");
            return;
        }
        AsyncTaskExecutor.runUIBlocking(getMaskName() + " Query", () -> {
            final String error = search();
            if (StringUtils.isNotEmpty(error)) {
                PopUpDialog.getInstance().show("Error", error);
            }
        });
    }

    protected abstract boolean doSearchCriteriaExist();

    protected abstract String search();

    protected abstract void showInfoWindow();
}
