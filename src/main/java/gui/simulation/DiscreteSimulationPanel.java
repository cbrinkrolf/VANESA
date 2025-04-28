package gui.simulation;

import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import gui.JDecimalTextField;
import gui.JIntTextField;
import gui.MainWindow;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.time.DurationFormatUtils;
import petriNet.SimulationResult;
import petriNet.SimulationResultController;
import petriNet.SimulationResultSeriesKey;
import simulation.DiscreteSimulator;
import simulation.SimulationException;
import simulation.Xorshift128Plus;
import util.VanesaUtility;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;

public class DiscreteSimulationPanel extends JPanel {
	private static final int PROGRESS_SCALE = 10000;

	private final SimulationWindow.UpdateSimulationResultsListener updateSimulationResultsListener;
	private final JButton startButton = new JButton("Start");
	private final JButton stopButton = new JButton("Stop");
	private final JLabel elapsedTimeLabel = new JLabel("-");
	private final JDecimalTextField startInput = new JDecimalTextField();
	private final JDecimalTextField stopInput = new JDecimalTextField();
	private final JIntTextField seedInput = new JIntTextField();
	private final JCheckBox seedCheckBox = new JCheckBox("Random");
	private final JProgressBar progressBar = new JProgressBar(SwingConstants.HORIZONTAL);
	private final JTextArea logTextArea = new JTextArea();

	private final Pathway pathway;
	private DiscreteSimulator simulator;
	private Thread simulationThread;
	private boolean running = false;

	public DiscreteSimulationPanel(final Pathway pathway,
			final SimulationWindow.UpdateSimulationResultsListener updateSimulationResultsListener) {
		super(new MigLayout("fill, wrap", "", "[][][][grow]"));
		this.pathway = pathway;
		this.updateSimulationResultsListener = updateSimulationResultsListener;
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

		add(parametersPanel, "growx");
		add(startStopPanel, "growx");
		add(progressBar, "growx");
		add(new JScrollPane(logTextArea), "grow");
	}

	public JTextArea getLogTextArea() {
		return logTextArea;
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
		final var endTime = getEndTime();
		final int seed = seedInput.getValue(42);
		final BigDecimal progressFactor = BigDecimal.valueOf(PROGRESS_SCALE).divide(endTime.subtract(startTime), 24,
				RoundingMode.HALF_UP);
		addLogText("Preparing simulation...\n");
		addLogText("- Using random seed " + seed + "\n");
		try {
			simulator = new DiscreteSimulator(pathway, new Xorshift128Plus(seed), false);
		} catch (final SimulationException e) {
			addLogText("Preparing simulation failed:\n");
			addLogText("\t" + e.getMessage() + "\n");
			resetAfterStart();
			return;
		}
		addLogText("Started simulation...\n");
		try {
			while (running && !simulator.isDead()) {
				simulator.step(endTime);
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
		collectResults(endTime);
		resetAfterStart();
		addLogText("done.\n");
	}

	private BigDecimal getEndTime() {
		return stopInput.getBigDecimalValue(BigDecimal.ONE);
	}

	public void addLogText(final String text) {
		logTextArea.setText(logTextArea.getText() + text);
	}

	private void onStopClicked() {
		running = false;
		if (simulator != null && simulationThread != null) {
			addLogText("Simulation stop requested. Waiting for simulation to stop...\n");
			try {
				simulationThread.join();
			} catch (final InterruptedException ignored) {
			}
			addLogText("Simulation stopped after " + elapsedTimeLabel.getText() + ".\n");
			addLogText("Collecting results...\n");
			collectResults(getEndTime());
		}
		resetAfterStart();
		addLogText("done.\n");
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

	private void collectResults(final BigDecimal endTime) {
		if (simulator == null) {
			return;
		}
		final SimulationResultController simResultController = pathway.getPetriPropertiesNet().getSimResController();
		final List<DiscreteSimulator.Marking[]> markingTimelines = simulator.getAllMarkingTimelines();
		if (markingTimelines.size() > 1) {
			addLogText("- Found " + markingTimelines.size() + " marking timelines.\n");
		}
		// Compress all marking timelines to unique time-points
		markingTimelines.replaceAll(this::compressMarkingTimeline);
		final var uniqueMarkingTimelines = deduplicateMarkingTimelines(markingTimelines);
		if (markingTimelines.size() > 1) {
			addLogText("- " + uniqueMarkingTimelines.size() + " marking timelines are unique.\n");
		}
		for (final DiscreteSimulator.Marking[] markingTimeline : uniqueMarkingTimelines.keySet()) {
			collectSimulationResult(simResultController, markingTimeline, uniqueMarkingTimelines.get(markingTimeline),
					endTime);
		}
		// Update UI
		pathway.setPlotColorPlacesTransitions(false);
		pathway.getPetriPropertiesNet().setPetriNetSimulation(true);
		if (updateSimulationResultsListener != null) {
			updateSimulationResultsListener.onUpdate();
		}
		MainWindow.getInstance().addSimulationResults();
	}

	private DiscreteSimulator.Marking[] compressMarkingTimeline(final DiscreteSimulator.Marking[] markingTimeline) {
		final List<DiscreteSimulator.Marking> result = new ArrayList<>();
		BigDecimal lastTime = BigDecimal.ZERO;
		for (int i = 1; i < markingTimeline.length; i++) {
			final BigDecimal newTime = markingTimeline[i].time;
			if (newTime.compareTo(lastTime) > 0) {
				result.add(markingTimeline[i - 1]);
			}
			lastTime = newTime;
		}
		result.add(markingTimeline[markingTimeline.length - 1]);
		return result.toArray(new DiscreteSimulator.Marking[0]);
	}

	private Map<DiscreteSimulator.Marking[], Integer> deduplicateMarkingTimelines(
			final List<DiscreteSimulator.Marking[]> markingTimelines) {
		final Map<DiscreteSimulator.Marking[], Integer> result = new HashMap<>();
		result.put(markingTimelines.get(0), 1);
		for (int i = 1; i < markingTimelines.size(); i++) {
			final var timeline = markingTimelines.get(i);
			DiscreteSimulator.Marking[] equalTimeline = null;
			for (final DiscreteSimulator.Marking[] otherTimeline : result.keySet()) {
				if (otherTimeline.length != timeline.length) {
					continue;
				}
				boolean allMarkingsEqual = true;
				for (int j = 0; j < timeline.length; j++) {
					final var markingA = timeline[j];
					final var markingB = otherTimeline[j];
					if (!markingA.hasEqualTokens(markingB)) {
						allMarkingsEqual = false;
						break;
					}
				}
				if (allMarkingsEqual) {
					equalTimeline = otherTimeline;
					break;
				}
			}
			if (equalTimeline == null) {
				result.put(timeline, 1);
			} else {
				result.put(equalTimeline, result.get(equalTimeline) + 1);
			}
		}
		return result;
	}

	private void collectSimulationResult(final SimulationResultController simResultController,
			final DiscreteSimulator.Marking[] markingTimeline, int occurrences, final BigDecimal endTime) {
		if (markingTimeline.length == 0) {
			return;
		}
		String simResId = "discrete sim";
		if (occurrences > 1) {
			simResId += " [" + occurrences + "]";
		}
		if (simResultController.containsSimId(simResId)) {
			int i = 1;
			while (simResultController.containsSimId(simResId + "(" + i + ")")) {
				i++;
			}
			simResId += "(" + i + ")";
		}
		// TODO: SIM_DELAY, SIM_ACTUAL_TOKEN_FLOW
		final var simResult = simResultController.get(simResId);
		simResult.setName(simResId);
		simResult.getLogMessage().append(logTextArea.getText());
		// Add markings to the simulation results
		for (int i = 0; i < markingTimeline.length; i++) {
			final var marking = markingTimeline[i];
			if (i > 0) {
				duplicateListSeries(simulator, simResult, marking.time);
			}
			simResult.addTime(marking.time.doubleValue());
			for (final var place : simulator.getPlaces()) {
				simResult.addValue(place, SimulationResultSeriesKey.PLACE_TOKEN,
						simulator.getTokens(marking, place).doubleValue());
			}
			for (final var transition : simulator.getTransitions()) {
				final boolean isTransitionActive = Arrays.stream(marking.concessionsOrderedByDelay).anyMatch(
						c -> c.transition.transition == transition);
				simResult.addValue(transition, SimulationResultSeriesKey.ACTIVE, isTransitionActive ? 1.0 : 0.0);
			}
			// Look forward what transitions will fire and update the transition's FIRE data
			if (i < markingTimeline.length - 1) {
				final var nextMarking = markingTimeline[i + 1];
				final var firingEdge = simulator.getFiringEdge(marking, nextMarking);
				for (final var transition : simulator.getTransitions()) {
					final boolean firing = Arrays.stream(firingEdge.transitions).anyMatch(
							t -> t.transition == transition);
					simResult.addValue(transition, SimulationResultSeriesKey.FIRE, firing ? 1.0 : 0.0);
				}
			} else {
				for (final var transition : simulator.getTransitions()) {
					simResult.addValue(transition, SimulationResultSeriesKey.FIRE, 0.0);
				}
			}
			// Look backward what transitions fired and update the arc's SUM_OF_TOKEN data
			if (i > 0) {
				final var previousMarking = markingTimeline[i - 1];
				final var firingEdge = simulator.getFiringEdge(previousMarking, marking);
				final Set<DiscreteSimulator.Concession> firedConcessions = new HashSet<>();
				for (final var concession : previousMarking.concessionsOrderedByDelay) {
					for (final var transition : firingEdge.transitions) {
						if (transition == concession.transition) {
							firedConcessions.add(concession);
							break;
						}
					}
				}
				for (final var arc : simulator.getArcs()) {
					final var series = simResult.get(arc, SimulationResultSeriesKey.ARC_SUM_OF_TOKEN);
					BigInteger tokens = series == null || series.size() == 0 ? BigInteger.ZERO : BigInteger.valueOf(
							series.get(series.size() - 1).longValue());
					for (final var concession : firedConcessions) {
						final BigInteger arcTokens = concession.fixedArcWeights.get(arc);
						if (arcTokens != null) {
							tokens = tokens.add(arcTokens);
							break;
						}
					}
					simResult.addValue(arc, SimulationResultSeriesKey.ARC_SUM_OF_TOKEN, tokens.doubleValue());
				}
			} else {
				for (final var arc : simulator.getArcs()) {
					final var series = simResult.get(arc, SimulationResultSeriesKey.ARC_SUM_OF_TOKEN);
					final BigInteger tokens =
							series == null || series.size() == 0 ? BigInteger.ZERO : BigInteger.valueOf(
									series.get(series.size() - 1).longValue());
					simResult.addValue(arc, SimulationResultSeriesKey.ARC_SUM_OF_TOKEN, tokens.doubleValue());
				}
			}
		}
		// If the last marking is before the user defined end time, we add a last time-point at the end time
		final var lastMarking = markingTimeline[markingTimeline.length - 1];
		if (lastMarking.time.compareTo(endTime) < 0) {
			duplicateListSeries(simulator, simResult, endTime);
		}
	}

	private static void duplicateListSeries(final DiscreteSimulator simulator, final SimulationResult simResult,
			final BigDecimal time) {
		simResult.addTime(time.doubleValue());
		for (final var place : simulator.getPlaces()) {
			duplicateLastSeriesPoint(simResult, place, SimulationResultSeriesKey.PLACE_TOKEN);
		}
		for (final var transition : simulator.getTransitions()) {
			duplicateLastSeriesPoint(simResult, transition, SimulationResultSeriesKey.ACTIVE);
			duplicateLastSeriesPoint(simResult, transition, SimulationResultSeriesKey.FIRE);
			duplicateLastSeriesPoint(simResult, transition, SimulationResultSeriesKey.DELAY);
		}
		for (final var arc : simulator.getArcs()) {
			duplicateLastSeriesPoint(simResult, arc, SimulationResultSeriesKey.ARC_SUM_OF_TOKEN);
			duplicateLastSeriesPoint(simResult, arc, SimulationResultSeriesKey.ARC_ACTUAL_TOKEN_FLOW);
		}
	}

	private static void duplicateLastSeriesPoint(final SimulationResult simResult, final GraphElementAbstract gea,
			final SimulationResultSeriesKey type) {
		final var series = simResult.get(gea, type);
		if (series != null && series.size() > 0) {
			series.add(series.get(series.size() - 1));
		}
	}
}
