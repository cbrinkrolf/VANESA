package gui.simulation;

import biologicalElements.Pathway;
import gui.MainWindow;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class SimulationWindow extends JFrame {
	private final JTabbedPane tabbedPanel = new JTabbedPane();
	private final SimulationResultsListPanel simulationResultsList = new SimulationResultsListPanel();
	private final DiscreteSimulationPanel discreteSimulationPanel;
	private final Pathway pathway;

	public SimulationWindow(final Pathway pathway) {
		super("VANESA - Simulation");
		discreteSimulationPanel = new DiscreteSimulationPanel(pathway, this::onUpdateSimulationResults);
		this.pathway = pathway;
		tabbedPanel.addTab("Discrete [built-in]", null, discreteSimulationPanel,
				"Simulate discrete Petri nets using the built-in simulator");
		tabbedPanel.addTab("OpenModelica", null, new JPanel(), "Simulate using OpenModelica and the PNlib");

		tabbedPanel.addChangeListener(e -> {
			if (e.getSource() instanceof JTabbedPane) {
				if (tabbedPanel.getSelectedComponent() == discreteSimulationPanel) {
					simulationResultsList.setLogTextArea(discreteSimulationPanel.getLogTextArea());
				} else {
					// TODO: simulationResultsList.setLogTextArea(omSimulationPanel.getLogTextArea());
				}
			}
		});

		final JPanel contentPanel = new JPanel(new MigLayout("fill", "[][grow]"));
		contentPanel.add(simulationResultsList, "growy");
		contentPanel.add(tabbedPanel, "grow");
		setContentPane(contentPanel);
		setMinimumSize(new Dimension(800, 400));
		setPreferredSize(new Dimension(800, 400));
		setIconImages(MainWindow.getInstance().getFrame().getIconImages());
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainWindow.getInstance().getFrame());
		pack();
		setVisible(true);

		simulationResultsList.updateSimulationResults(pathway);
	}

	private void onUpdateSimulationResults() {
		simulationResultsList.updateSimulationResults(pathway);
	}

	public interface UpdateSimulationResultsListener {
		void onUpdate();
	}
}
