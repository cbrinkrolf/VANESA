package gui.simulation;

import biologicalElements.Pathway;
import gui.JDecimalTextField;
import gui.JIntTextField;
import gui.MainWindow;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.time.DurationFormatUtils;
import petriNet.SimulationResultController;
import simulation.DiscreteSimulator;
import simulation.SimulationException;
import util.VanesaUtility;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.Random;

public class DiscreteSimulationPanel extends JPanel {
	private static final int PROGRESS_SCALE = 10000;

	private final JButton startButton = new JButton("Start");
	private final JButton stopButton = new JButton("Stop");
	private final JLabel elapsedTimeLabel = new JLabel("-");
	private final JDecimalTextField startInput = new JDecimalTextField();
	private final JDecimalTextField stopInput = new JDecimalTextField();
	private final JIntTextField seedInput = new JIntTextField();
	private final JCheckBox seedCheckBox = new JCheckBox("Random");
	private final JProgressBar progressBar = new JProgressBar(SwingConstants.HORIZONTAL);
	private final JTextArea logTextArea = new JTextArea();
	private final SimulationResultsListPanel simulationResultsList = new SimulationResultsListPanel(logTextArea);

	private final Pathway pathway;
	private DiscreteSimulator simulator;
	private Thread simulationThread;
	private boolean running = false;

	public DiscreteSimulationPanel(final Pathway pathway) {
		super(new MigLayout("ins 0, fill", "[][][grow]"));
		this.pathway = pathway;
		startInput.setValue(BigDecimal.ZERO);
		startInput.setEnabled(false);
		stopInput.setValue(BigDecimal.ONE);
		seedInput.setValue(42);
		startButton.setBackground(VanesaUtility.POSITIVE_COLOR);
		startButton.addActionListener(e -> onStartClicked());
		stopButton.setBackground(VanesaUtility.NEGATIVE_COLOR);
		stopButton.addActionListener(e -> onStopClicked());
		stopButton.setEnabled(false);
		final JLabel seedLbl = new JLabel("Seed:");
		seedLbl.setToolTipText("Seed for stochastic processes");
		seedCheckBox.setToolTipText("Set random seed for stochastic processes");
		seedCheckBox.addActionListener(e -> seedInput.setEnabled(!seedCheckBox.isSelected()));
		progressBar.setForeground(VanesaUtility.NEUTRAL_COLOR);
		logTextArea.setEditable(false);

		final JPanel startStopPanel = new JPanel(new MigLayout("ins 0, fillx", "[][][grow][grow]"));
		startStopPanel.add(startButton);
		startStopPanel.add(stopButton);
		startStopPanel.add(new JLabel("Elapsed Time:"), "right");
		startStopPanel.add(elapsedTimeLabel, "growx");

		final JPanel parametersPanel = new JPanel(new MigLayout("ins 0, fillx", "[][grow][][grow][][grow][]"));
		parametersPanel.add(new JLabel("Start:"));
		parametersPanel.add(startInput, "growx");
		parametersPanel.add(new JLabel("Stop:"));
		parametersPanel.add(stopInput, "growx");
		parametersPanel.add(seedLbl);
		parametersPanel.add(seedInput, "growx");
		parametersPanel.add(seedCheckBox);

		final JPanel simulationPanel = new JPanel(new MigLayout("fill, wrap", "", "[][][][grow]"));
		simulationPanel.add(parametersPanel, "growx");
		simulationPanel.add(startStopPanel, "growx");
		simulationPanel.add(progressBar, "growx");
		simulationPanel.add(new JScrollPane(logTextArea), "grow");

		add(simulationResultsList, "growy");
		add(new JSeparator(SwingConstants.VERTICAL), "growy, width 3:3:3");
		add(simulationPanel, "grow");

		simulationResultsList.updateSimulationResults(pathway);
	}

	private void onStartClicked() {
		logTextArea.setText("");
		setElapsedTime(null);
		progressBar.setValue(0);
		progressBar.setMinimum(0);
		progressBar.setMaximum(PROGRESS_SCALE);
		startButton.setEnabled(false);
		stopButton.setEnabled(true);
		if (seedCheckBox.isSelected()) {
			seedInput.setValue(new Random().nextInt(Integer.MAX_VALUE));
		}
		running = true;
		simulationThread = new Thread(this::runSimulation);
		simulationThread.start();
	}

	private void runSimulation() {
		long elapsedTimeStart = System.currentTimeMillis();
		final var startTime = startInput.getBigDecimalValue(BigDecimal.ZERO);
		final var endTime = stopInput.getBigDecimalValue(BigDecimal.ONE);
		final int seed = seedInput.getValue(42);
		final BigDecimal progressFactor = BigDecimal.valueOf(PROGRESS_SCALE).divide(endTime.subtract(startTime));
		addLogText("Preparing simulation...\n");
		addLogText("- Using random seed " + seed + "\n");
		try {
			simulator = new DiscreteSimulator(pathway, seed, false);
		} catch (final SimulationException e) {
			addLogText("Preparing simulation failed:\n");
			addLogText("\t" + e.getMessage() + "\n");
			resetAfterStart();
			return;
		}
		addLogText("Started simulation...\n");
		try {
			while (running && !simulator.isDead() && endTime.compareTo(simulator.getMaxTime()) > 0) {
				simulator.step();
				progressBar.setValue(simulator.getMaxTime().multiply(progressFactor).intValue());
				setElapsedTime(System.currentTimeMillis() - elapsedTimeStart);
			}
		} catch (final SimulationException e) {
			addLogText("Simulation failed:\n");
			addLogText("\t" + e.getMessage() + "\n");
			resetAfterStart();
			return;
		}
		setElapsedTime(System.currentTimeMillis() - elapsedTimeStart);
		addLogText("Simulation finished in " + elapsedTimeLabel.getText() + ".\n");
		addLogText("Collecting results...\n");
		collectResults();
		resetAfterStart();
		addLogText("done.\n");
	}

	public void addLogText(final String text) {
		logTextArea.setText(logTextArea.getText() + text);
	}

	private void onStopClicked() {
		running = false;
		if (simulator != null && simulationThread != null) {
			try {
				simulationThread.join();
			} catch (final InterruptedException ignored) {
			}
			collectResults();
		}
		resetAfterStart();
	}

	private void resetAfterStart() {
		running = false;
		simulationThread = null;
		simulator = null;
		startButton.setEnabled(true);
		stopButton.setEnabled(false);
	}

	private void setElapsedTime(final Long duration) {
		if (duration == null) {
			elapsedTimeLabel.setText("-");
		} else {
			elapsedTimeLabel.setText(DurationFormatUtils.formatDuration(duration, "HH:mm:ss") + " (HH:mm:ss)");
		}
		elapsedTimeLabel.repaint();
	}

	private void collectResults() {
		if (simulator == null) {
			return;
		}
		final SimulationResultController simResultController = pathway.getPetriPropertiesNet().getSimResController();
		String simResId = "discrete sim";
		if (simResultController.containsSimId(simResId)) {
			int i = 1;
			while (simResultController.containsSimId(simResId + "(" + i + ")")) {
				i++;
			}
			simResId += "(" + i + ")";
		}
		final var simResult = simResultController.get(simResId);
		simResult.setName(simResId);
		simResult.getLogMessage().append(logTextArea.getText());
		// Add markings to the simulation results, collapsing markings with the same time signature to the last state
		final DiscreteSimulator.Marking[] markingTimeline = simulator.getMarkingTimeline();
		BigDecimal lastTime = BigDecimal.ZERO;
		for (int i = 1; i < markingTimeline.length; i++) {
			final BigDecimal newTime = markingTimeline[i].time;
			if (newTime.compareTo(lastTime) > 0) {
				var marking = markingTimeline[i - 1];
				simResult.addTime(marking.time.doubleValue());
				for (final var place : simulator.getPlaces()) {
					simResult.addValue(place, SimulationResultController.SIM_TOKEN,
							simulator.getTokens(marking, place).doubleValue());
				}
				simResult.addTime(newTime.doubleValue());
				for (final var place : simulator.getPlaces()) {
					simResult.addValue(place, SimulationResultController.SIM_TOKEN,
							simulator.getTokens(marking, place).doubleValue());
				}
			}
			lastTime = newTime;
		}
		simResult.addTime(markingTimeline[markingTimeline.length - 1].time.doubleValue());
		for (final var place : simulator.getPlaces()) {
			simResult.addValue(place, SimulationResultController.SIM_TOKEN,
					simulator.getTokens(markingTimeline[markingTimeline.length - 1], place).doubleValue());
		}
		// Update UI
		pathway.setPlotColorPlacesTransitions(false);
		pathway.getPetriPropertiesNet().setPetriNetSimulation(true);
		simulationResultsList.updateSimulationResults(pathway);
		MainWindow.getInstance().addSimulationResults();
	}
}
