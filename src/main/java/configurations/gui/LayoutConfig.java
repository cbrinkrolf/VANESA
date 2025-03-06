package configurations.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.*;

import graph.GraphInstance;
import gui.AsyncTaskExecutor;
import gui.MainWindow;

public class LayoutConfig {
	public static void show(final LayoutConfigPanel layoutConfigPanel) {
		if (!askBeforeLayoutIfClustersPresent()) {
			return;
		}
		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(layoutConfigPanel, BorderLayout.CENTER);
		final JButton cancelButton = new JButton("cancel");
		cancelButton.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getOptionPane(panel).setValue(e.getSource());
			}
		});
		final JButton resetButton = new JButton("reset");
		resetButton.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				layoutConfigPanel.resetValues();
			}
		});
		final JButton applyButton = new JButton("apply");
		applyButton.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				layoutConfigPanel.setValues();
				getOptionPane(panel).setValue(e.getSource());
				AsyncTaskExecutor.runUIBlocking("Layouting...", layoutConfigPanel::applySettings);
			}
		});
		JOptionPane.showOptionDialog(MainWindow.getInstance().getFrame(), panel, "Layout Settings",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				new JButton[] { applyButton, resetButton, cancelButton }, applyButton);
	}

	public static boolean askBeforeLayoutIfClustersPresent() {
		if (GraphInstance.getMyGraph().getAnnotationManager().getAnnotations().isEmpty()) {
			return true;
		}
		final int option = JOptionPane.showConfirmDialog(GraphInstance.getMyGraph().getVisualizationViewer(),
				"This graph contains selected clusters,\nall selected clusters maybe out of date\nafter new layout!",
				"continue", JOptionPane.YES_NO_OPTION);
		return option == JOptionPane.YES_OPTION;
	}

	public static JOptionPane getOptionPane(Container comp) {
		try {
			return (JOptionPane) comp;
		} catch (ClassCastException e) {
			return getOptionPane(comp.getParent());
		}
	}
}
