package gui.simulation;

import biologicalElements.Pathway;
import gui.DetailedSimRes;
import gui.MainWindow;
import io.SaveDialog;
import io.SuffixAwareFilter;
import net.miginfocom.swing.MigLayout;
import petriNet.SimulationResult;
import petriNet.SimulationResultController;
import util.VanesaUtility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.HashMap;
import java.util.List;

public class SimulationResultsListPanel extends JPanel {
	private JTextArea logTextArea;
	private boolean triggerUIUpdate = true;
	private final HashMap<JTextField, SimulationResult> text2sim = new HashMap<>();
	private final JPanel resultsListPanel = new JPanel(new MigLayout("ins 0, fillx, wrap 6"));
	private final JScrollPane resultsListScrollPanel;

	private Pathway currentPathway;

	public SimulationResultsListPanel() {
		super(new MigLayout("fill, wrap", "", "[][grow, align top]"));

		final JPanel headerPanel = new JPanel(new MigLayout("ins 0, fillx", "[][][grow]"));

		final JButton activateAllButton = new JButton("Activate All");
		activateAllButton.setToolTipText("Set all simulation results as active");
		activateAllButton.addActionListener(e -> onDeSelectAll(true));

		final JButton deactivateAllButton = new JButton("Deactivate All");
		deactivateAllButton.setToolTipText("Set all simulation results as inactive");
		deactivateAllButton.addActionListener(e -> onDeSelectAll(false));

		final JButton delAllButton = new JButton("Delete All");
		delAllButton.setBackground(VanesaUtility.NEGATIVE_COLOR);
		delAllButton.setToolTipText("Delete all results");
		delAllButton.addActionListener(e -> onDeleteAllClicked());

		headerPanel.add(activateAllButton);
		headerPanel.add(deactivateAllButton);
		headerPanel.add(delAllButton, "right");

		add(headerPanel, "growx");
		resultsListScrollPanel = new JScrollPane(resultsListPanel);
		resultsListScrollPanel.setBorder(null);
		resultsListScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		resultsListScrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(resultsListScrollPanel, "grow");
	}

	public void setLogTextArea(JTextArea logTextArea) {
		this.logTextArea = logTextArea;
	}

	public void updateSimulationResults(final Pathway pathway) {
		currentPathway = pathway;
		final List<SimulationResult> results = pathway.getPetriPropertiesNet().getSimResController().getAll();

		triggerUIUpdate = false;
		text2sim.clear();
		resultsListPanel.removeAll();
		resultsListScrollPanel.setVisible(!results.isEmpty());

		for (final SimulationResult simulationResult : results) {
			final JCheckBox activationCheckBox = new JCheckBox();
			activationCheckBox.addItemListener(e -> {
				simulationResult.setActive(activationCheckBox.isSelected());
				if (triggerUIUpdate) {
					MainWindow.getInstance().updateSimulationResultView();
				}
			});
			activationCheckBox.setSelected(simulationResult.isActive());

			final JButton del = new JButton("del");
			del.setBackground(VanesaUtility.NEGATIVE_COLOR);
			del.setToolTipText("Delete result");
			del.addActionListener(e -> {
				pathway.getPetriPropertiesNet().getSimResController().remove(simulationResult);
				updateSimulationResults(pathway);
			});
			final JButton log = new JButton("log");
			log.setToolTipText("Show log");
			log.addActionListener(e -> {
				if (logTextArea != null) {
					logTextArea.setText(simulationResult.getLogMessage().toString());
				}
			});
			final JButton detailsButton = new JButton("Details");
			detailsButton.setToolTipText("Show detailed result");
			detailsButton.addActionListener(e -> new DetailedSimRes(pathway, simulationResult.getId()));
			final JButton exportButton = new JButton("Export");
			exportButton.setToolTipText("Export Result");
			exportButton.addActionListener(
					e -> new SaveDialog(new SuffixAwareFilter[] { SuffixAwareFilter.VANESA_SIM_RESULT },
							SaveDialog.DATA_TYPE_SIMULATION_RESULTS, null, this, simulationResult.getId()));
			final JTextField simName = new JTextField(10);
			simName.setText(simulationResult.getName());
			text2sim.put(simName, simulationResult);
			simName.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(final FocusEvent e) {
					if (!simName.getText().trim().equals(text2sim.get(e.getSource()).getName())) {
						text2sim.get(e.getSource()).setName(simName.getText().trim());
					}
				}
			});

			resultsListPanel.add(activationCheckBox);
			resultsListPanel.add(simName);
			resultsListPanel.add(log);
			resultsListPanel.add(detailsButton);
			resultsListPanel.add(exportButton);
			resultsListPanel.add(del);
		}
		triggerUIUpdate = true;
		revalidate();
		repaint();
	}

	private void onDeSelectAll(final boolean selected) {
		if (currentPathway == null) {
			return;
		}
		triggerUIUpdate = false;
		for (final Component component : getComponents()) {
			if (component instanceof JCheckBox) {
				((JCheckBox) component).setSelected(selected);
			}
		}
		final List<SimulationResult> resList = currentPathway.getPetriPropertiesNet().getSimResController().getAll();
		for (final SimulationResult result : resList) {
			result.setActive(selected);
		}
		triggerUIUpdate = true;
		updateSimulationResults(currentPathway);
		MainWindow.getInstance().updateSimulationResultView();
	}

	private void onDeleteAllClicked() {
		if (currentPathway == null) {
			return;
		}
		triggerUIUpdate = false;
		final SimulationResultController controller = currentPathway.getPetriPropertiesNet().getSimResController();
		final List<SimulationResult> results = controller.getAll();
		for (final SimulationResult simulationResult : results) {
			controller.remove(simulationResult);
		}
		triggerUIUpdate = true;
		updateSimulationResults(currentPathway);
	}
}
