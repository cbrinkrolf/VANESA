package gui.simulation;

import biologicalElements.Pathway;
import gui.DetailedSimRes;
import gui.MainWindow;
import io.SaveDialog;
import io.SuffixAwareFilter;
import net.miginfocom.swing.MigLayout;
import petriNet.SimulationResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.HashMap;
import java.util.List;

public class SimulationResultsListPanel extends JPanel {
	private final JTextArea logTextArea;
	private boolean triggerUIUpdate = true;
	private final HashMap<JTextField, SimulationResult> text2sim = new HashMap<>();

	public SimulationResultsListPanel(final JTextArea logTextArea) {
		super(new MigLayout("fillx, wrap 6"));
		this.logTextArea = logTextArea;
	}

	public void updateSimulationResults(final Pathway pathway) {
		final List<SimulationResult> results = pathway.getPetriPropertiesNet().getSimResController().getAll();

		triggerUIUpdate = false;
		text2sim.clear();
		removeAll();

		final JCheckBox activateAllCheckBox = new JCheckBox("active");
		activateAllCheckBox.setToolTipText("de-/select all");
		activateAllCheckBox.setSelected(results.stream().allMatch(SimulationResult::isActive));
		activateAllCheckBox.addItemListener(e -> onDeSelectAll(pathway, activateAllCheckBox.isSelected()));

		add(activateAllCheckBox);
		add(new JLabel("Simulation Name"), "span 3");

		final JButton delAllButton = new JButton("Delete All");
		delAllButton.setToolTipText("Delete all results");
		delAllButton.addActionListener(e -> {
			for (final SimulationResult simulationResult : results) {
				pathway.getPetriPropertiesNet().getSimResController().remove(simulationResult);
			}
			updateSimulationResults(pathway);
		});
		add(delAllButton, "span 2, right");

		for (final SimulationResult simulationResult : results) {
			final JCheckBox activationCheckBox = new JCheckBox();
			activationCheckBox.addItemListener(e -> {
				simulationResult.setActive(activationCheckBox.isSelected());
				if (triggerUIUpdate) {
					MainWindow.getInstance().updateSimulationResultView();
				}
			});
			activationCheckBox.setSelected(simulationResult.isActive());
			add(activationCheckBox);

			final JButton del = new JButton("del");
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
			add(simName);
			add(log);
			add(detailsButton);
			add(exportButton);
			add(del);
		}
		triggerUIUpdate = true;
	}

	private void onDeSelectAll(final Pathway pathway, final boolean selected) {
		triggerUIUpdate = false;
		for (final Component component : getComponents()) {
			if (component instanceof JCheckBox) {
				((JCheckBox) component).setSelected(selected);
			}
		}
		final List<SimulationResult> resList = pathway.getPetriPropertiesNet().getSimResController().getAll();
		for (final SimulationResult result : resList) {
			result.setActive(selected);
		}
		triggerUIUpdate = true;
		MainWindow.getInstance().updateSimulationResultView();
	}
}
