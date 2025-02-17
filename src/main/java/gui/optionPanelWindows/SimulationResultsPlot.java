package gui.optionPanelWindows;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import configurations.SettingsManager;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.SeriesException;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.PNNode;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.StochasticTransition;
import biologicalObjects.nodes.petriNet.Transition;
import edu.uci.ics.jung.visualization.picking.PickedState;
import graph.GraphInstance;
import gui.DetailedSimRes;
import gui.MainWindow;
import io.SaveDialog;
import io.SuffixAwareFilter;
import net.miginfocom.swing.MigLayout;
import petriNet.AnimationThread;
import petriNet.SimulationResult;
import petriNet.SimulationResultController;
import util.TripleHashMap;
import util.VanesaUtility;

public class SimulationResultsPlot implements ActionListener, ChangeListener {

	// prepare data sets
	// private Hashtable<Integer, BiologicalNodeAbstract> nodeTabel = new
	// Hashtable<Integer, BiologicalNodeAbstract>();
	// instance of main window
	// create GUI components
	private JButton showTable = new JButton("show detailed simulation results");
	// private JButton drawPlots = new
	// JButton("show all animation result plots");

	private JLabel stepLabel = new JLabel("Step 0");
	private JSlider slider = new JSlider();
	private JPanel main;
	private JPanel controlPanel;
	private JFreeChart chart;
	private ValueMarker marker;
	private Pathway pw = null;
	private PickedState<BiologicalNodeAbstract> vState;
	private PickedState<BiologicalEdgeAbstract> eState;
	private SimulationResultController simResController;
	private boolean hiddenPN = false;
	private JButton petriNetAnimationButton = new JButton("Start Animation");
	private JButton petriNetStopAnimationButton = new JButton("Stop");
	private JButton resetPetriNet = new JButton("Reset");

	private int animationThreadStep = -1;
	private JSpinner animationStart;
	private JSpinner animationStop;
	private JSpinner animationSpeed;
	private JCheckBox animationColour = new JCheckBox();
	private Thread thread;
	private ChartPanel pane;

	private JButton zoomGraph;

	// private JPanel invariants = new JPanel();

	// An Object to store microarray data
	// private Object[][] rows;
	// number of Places
	// private int rowsSize;
	// size of Vector in each Place
	private int rowsDim;

	private ArrayList<String> labelsR1 = new ArrayList<String>();

	private int animationStartInit = 0;
	private int animationStopInit = 1;
	private int animationSpeedInit = 20;

	private ArrayList<BiologicalNodeAbstract> places;

	private ArrayList<XYSeries> seriesListR1 = new ArrayList<>();
	private ArrayList<XYSeries> seriesListR2 = new ArrayList<>();
	private int r1Count = 0;
	private int r2Count = 0;

	private XYSeriesCollection dataset = new XYSeriesCollection();
	private XYSeriesCollection dataset2 = new XYSeriesCollection();

	private TripleHashMap<GraphElementAbstract, Integer, String, Integer> series2idx = new TripleHashMap<>();
	private HashMap<Integer, SimulationResult> idx2simR1 = new HashMap<>();
	private HashMap<Integer, SimulationResult> idx2simR2 = new HashMap<>();
	// private HashMap<XYSeries, Integer> series2id = new HashMap<XYSeries,
	// Integer>();

	private XYLineAndShapeRenderer renderer;
	private XYLineAndShapeRenderer renderer2;

	private LegendTitle legend;

	private boolean lockUpdate = false;

	private static int TOKEN = SimulationResultController.SIM_TOKEN;
	private static int ACTUAL_FIRING_SPEED = SimulationResultController.SIM_ACTUAL_FIRING_SPEED;
	private static int ACTIVE = SimulationResultController.SIM_ACTIVE;
	private static int FIRE = SimulationResultController.SIM_FIRE;
	private static int DELAY = SimulationResultController.SIM_DELAY;
	private static int SUM_OF_TOKEN = SimulationResultController.SIM_SUM_OF_TOKEN;
	private static int ACTUAL_TOKEN_FLOW = SimulationResultController.SIM_ACTUAL_TOKEN_FLOW;

	private Set<XYSeries> dirtyVisibleSeries = new HashSet<>();
	private Set<XYSeries> dirtyVisibleSeriesR1 = new HashSet<>();
	private boolean isIterating = false;

	private int sliderPosition = 0;

	public SimulationResultsPlot() {

		main = new JPanel(new MigLayout());

		petriNetAnimationButton.addActionListener(this);
		petriNetAnimationButton.setActionCommand("animatePetriNet");

		petriNetStopAnimationButton.addActionListener(this);
		petriNetStopAnimationButton.setActionCommand("animatePetriNetStop");

		resetPetriNet.addActionListener(this);
		resetPetriNet.setActionCommand("reset");

	}

	/**
	 * Getter for the main GUI panel. Is used in OptionPanel.
	 *
	 * @return main
	 */
	public JPanel getPanel() {
		return main;
	}

	/**
	 * Removes all Elements from the main panel.
	 */
	public void removeAllElements() {
		main.removeAll();
		main.setVisible(false);
	}

	public void revalidateView() {
		// System.out.println("start revalidating view");

		if (pw == null) {
			return;
		}
		for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
			if (bna instanceof Transition) {
				((Transition) bna).setSimulationActive(false);
			}
		}
		if (simResController == null) {
			pw.getGraph().getVisualizationViewer().repaint();
			return;
		}
		if (pw.isPetriNet() || pw.getTransformationInformation() != null
				&& pw.getTransformationInformation().getPetriNet() != null) {
			SimulationResult simRes = simResController.getLastActive();
			if (pw.getPetriPropertiesNet().isPetriNetSimulation() && simRes != null) {

				// System.out.println(main.getComponentCount());
				// most of the time unnecessary to remove all
				main.removeAll();
				// System.out.println(main.getComponentCount());

				rowsDim = simRes.getTime().size();

				SpinnerModel modelStart = new SpinnerNumberModel(

						animationStartInit, // initial value
						0, // min
						rowsDim, // max
						1); // step

				animationStart = new JSpinner(modelStart);

				animationStart.addChangeListener(this);

				SpinnerModel modelEnd = new SpinnerNumberModel(rowsDim, // initial
																		// value
						0, // min
						rowsDim, // max
						1); // step

				animationStop = new JSpinner(modelEnd);
				animationStop.addChangeListener(this);

				SpinnerModel modelSpeed = new SpinnerNumberModel(animationSpeedInit, // initial
																						// value
						0, // min
						20, // max
						1); // step

				animationSpeed = new JSpinner(modelSpeed);
				animationSpeed.addChangeListener(this);

				animationColour.setSelected(false);

				zoomGraph = new JButton("enlarge Graph");
				zoomGraph.addActionListener(this);
				zoomGraph.setActionCommand("zoomGraph");

				showTable = new JButton("show detailed simulation results");
				showTable.addActionListener(this);
				showTable.setActionCommand("show");

				slider.setMinimum(0);
				slider.setMaximum(rowsDim - 1);
				slider.setMajorTickSpacing(1);
				slider.addChangeListener(this);

				slider.setToolTipText("Step: 0, use arrow keys for single steps");
				slider.setValue(sliderPosition);

				controlPanel = new JPanel(new MigLayout());

				petriNetAnimationButton.setBackground(Color.GREEN);
				petriNetStopAnimationButton.setBackground(Color.RED);
				resetPetriNet.setBackground(Color.white);
				petriNetStopAnimationButton.setEnabled(false);

				controlPanel.add(zoomGraph);
				controlPanel.add(showTable, "align left");
				controlPanel.add(new JLabel(""), "align left,wrap 10, growx");

				controlPanel.add(new JSeparator(), "span, wrap 10, growx");

				// JPanel controlPanel2 = new JPanel(new MigLayout());
				// controlPanel.add(new JLabel("Animation Start:"), "align left");
				// controlPanel.add(animationStart, "align left,wrap 10, growx");

				// controlPanel.add(new JLabel("Animation Stop:"), "align left");
				// controlPanel.add(animationStop, "align left,wrap 10, growx");

				// controlPanel.add(new JLabel("Animation Speed x:"), "align left");
				// controlPanel.add(animationSpeed, "align left,wrap 10, growx");

				// controlPanel.add(new JLabel("Animation Colour:"), "align left");
				// controlPanel.add(animationColour, "align left,wrap 10, growx");

				// controlPanel2.add(petriNetAnimationButton, "align left");
				// controlPanel2.add(petriNetStopAnimationButton, "align left");
				// controlPanel2.add(resetPetriNet, "align left");

				// controlPanel.add(controlPanel2, "span, wrap,growx");

				controlPanel.add(slider, "span");
				controlPanel.add(stepLabel, "align left,wrap, growx");

				main.add(new JLabel("Petri Net Simulation Result Plots for Places within the Network"),
						"span, wrap 10, growx, align center");
				main.add(pane, "span, wrap");
				main.add(controlPanel, "gap 10, wrap 15, growx");
				pane.setPreferredSize(new Dimension(400, 200));
				// main.add(mainPanel);
				main.setVisible(true);

				drawPlot();
				Double ref;
				if (simRes.getTime().size() > 0) {
					for (BiologicalNodeAbstract node : pw.getAllGraphNodes()) {
						if (node instanceof Transition) {
							// System.out.println(node.getName());
							if (simRes.contains(node) && simRes.get(node, ACTIVE) != null) {
								ref = simRes.get(node, ACTIVE).get(slider.getValue());
								if (ref != null && ref == 1) {
									((Transition) node).setSimulationActive(true);
								} else {
									((Transition) node).setSimulationActive(false);
								}
							}
						}
					}
					pw.getGraph().getVisualizationViewer().repaint();
				} else {
					// System.out.println("possible null pointer...............................");
				}
			} else {
				removeAllElements();
				pw.getGraph().getVisualizationViewer().repaint();
			}
		} else {
			removeAllElements();
		}
		// System.out.println("done revalidating view");
	}

	/**
	 * This method redraws the time series plot in the left sidebar.
	 */
	private void drawPlot() {

		// System.out.println("draw plot");
		Place place;
		Transition transition;
		// places = new ArrayList<Place>();
		BiologicalNodeAbstract bna = null;
		int pickedV = vState.getPicked().size();
		int pickedE = eState.getPicked().size();

		for (int i = 0; i < seriesListR1.size(); i++) {
			try {
				renderer.setSeriesVisible(i, false);
			} catch (SeriesException e) {
				e.printStackTrace();
			}
		}

		for (int i = 0; i < seriesListR2.size(); i++) {
			renderer2.setSeriesVisible(i, false);
		}
		SimulationResult simRes = simResController.getLastActive();
		boolean secondAxis = false;
		String legendY = "Tokens";
		if (pickedV == 0 && pickedE == 1) {
			BiologicalEdgeAbstract bea = eState.getPicked().iterator().next();
			if (hiddenPN) {
				bea = pw.getTransformationInformation().getPetriNet().getEdge(
						pw.getTransformationInformation().getBnToPnMapping().get(bea.getFrom()),
						pw.getTransformationInformation().getBnToPnMapping().get(bea.getTo()));
			}
			if (bea instanceof PNArc) {
				secondAxis = true;
				PNArc edge = (PNArc) bea;
				List<SimulationResult> listActive = simResController.getAllActiveWithData(edge, SUM_OF_TOKEN);
				SimulationResult result;

				// System.out.println(edge.getID());
				// if (simRes.contains(edge, SUM_OF_TOKEN) &&
				// simRes.contains(edge, ACTUAL_TOKEN_FLOW) && simRes.get(edge,
				// SUM_OF_TOKEN).size() > 0) {
				for (int i = 0; i < listActive.size(); i++) {
					result = listActive.get(i);

					int idxFlow = series2idx.get(edge, ACTUAL_TOKEN_FLOW, result.getId());
					int idxSum = series2idx.get(edge, SUM_OF_TOKEN, result.getId());

					Stroke dash1 = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f,
							new float[] { 2.0f, 6 }, 0.0f);
					renderer.setSeriesStroke(idxFlow, dash1);
					// System.out.println("stroke set");
					Color c = Color.getHSBColor(i * 1.0f / (listActive.size()), 1, 1);
					renderer.setSeriesPaint(idxFlow, c);
					renderer2.setSeriesPaint(idxSum, c);

					renderer.setSeriesVisible(idxFlow, true);
					renderer2.setSeriesVisible(idxSum, true);

					//// DEBUGGIMG test code
					// System.out.println(result.getTime().size());
					// System.out.println(result.get(edge, ));

				}

			}
		} else if (pickedV == 0 && pickedE > 1) {
			Set<PNArc> validSet = new HashSet<>();
			BiologicalEdgeAbstract bea;
			Iterator<BiologicalEdgeAbstract> it = eState.getPicked().iterator();
			while (it.hasNext()) {
				bea = it.next();
				if (hiddenPN) {
					bea = pw.getTransformationInformation().getPetriNet().getEdge(
							pw.getTransformationInformation().getBnToPnMapping().get(bea.getFrom()),
							pw.getTransformationInformation().getBnToPnMapping().get(bea.getTo()));
				}
				if (bea instanceof PNArc) {
					validSet.add((PNArc) bea);
				}
			}
			int i = 0;
			for (PNArc arc : validSet) {
				secondAxis = true;
				SimulationResult result = simResController.getLastActive();

				int idxFlow = series2idx.get(arc, ACTUAL_TOKEN_FLOW, result.getId());
				int idxSum = series2idx.get(arc, SUM_OF_TOKEN, result.getId());

				Stroke dash1 = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f,
						new float[] { 2.0f, 6 }, 0.0f);
				renderer.setSeriesStroke(idxFlow, dash1);
				// System.out.println("stroke set");
				Color c = Color.getHSBColor(i * 1.0f / (validSet.size()), 1, 1);
				renderer.setSeriesPaint(idxFlow, c);
				renderer2.setSeriesPaint(idxSum, c);

				renderer.setSeriesVisible(idxFlow, true);
				renderer2.setSeriesVisible(idxSum, true);

				i++;
			}

		} else if (pickedV == 1) {
			// System.out.println("one picked");
			bna = vState.getPicked().iterator().next();
			SimulationResult result;
			List<SimulationResult> listActive = null;
			bna = resolveReference(bna);
			bna = resolveHidden(bna);

			if (bna instanceof PNNode) {
				if (bna instanceof Place) {
					listActive = simResController.getAllActiveWithData(bna, TOKEN);
				} else if (bna instanceof ContinuousTransition) {
					listActive = simResController.getAllActiveWithData(bna, ACTUAL_FIRING_SPEED);
					legendY = "Speed";
				} else {
					listActive = simResController.getAllActiveWithData(bna, DELAY);
					legendY = "Delay";
				}
				Color c;
				for (int i = 0; i < listActive.size(); i++) {
					result = listActive.get(i);
					int idx = 0;
					if (bna instanceof Place) {
						idx = series2idx.get(bna, TOKEN, result.getId());
					} else if (bna instanceof ContinuousTransition) {
						idx = series2idx.get(bna, ACTUAL_FIRING_SPEED, result.getId());
					} else {
						idx = series2idx.get(bna, DELAY, result.getId());
						// System.out.println(idx);
					}

					renderer.setSeriesVisible(idx, true);
					// Stroke dash1 = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE,
					// BasicStroke.JOIN_MITER, 1.0f, new float[] { 2.0f, i * 2 },
					// 0.0f);
					// renderer.setSeriesStroke(idx, dash1);
					// System.out.println("stroke set");
					c = Color.getHSBColor(i * 1.0f / (listActive.size()), 1, 1);
					if (listActive.size() == 1) {
						// renderer.setSeriesPaint(idx, pn.getPlotColor());
						renderer.setSeriesPaint(idx, Color.red);
					} else {
						renderer.setSeriesPaint(idx, c);
					}
				}
			}
		} else {
			// System.out.println("else picked");
			boolean onlyT = true;
			boolean onlyDiscreteT = true;
			if (vState.getPicked().size() > 0) {
				Iterator<BiologicalNodeAbstract> it = vState.getPicked().iterator();
				while (onlyT && it.hasNext()) {
					if (it.hasNext()) {
						bna = it.next();
						bna = resolveReference(bna);
						if (hiddenPN) {
							bna = resolveHidden(bna);
						}
						if (bna instanceof Place) {
							onlyT = false;
							onlyDiscreteT = false;
						} else if (bna instanceof ContinuousTransition) {
							// prioritize continuous transitions over discrete/stochastic transitions
							onlyDiscreteT = false;
						}
					}
				}
			} else {
				onlyT = false;
			}

			Iterator<BiologicalNodeAbstract> iterator = null;
			if (pickedV > 0) {
				iterator = vState.getPicked().iterator();
				// System.out.println("picked");
			} else {
				iterator = pw.getAllGraphNodes().iterator();
			}

			// int j = 0;
			while (iterator.hasNext()) {
				bna = iterator.next();
				bna = resolveReference(bna);
				bna = resolveHidden(bna);
				if (bna instanceof Place) {
					place = (Place) bna;
					// System.out.println(place.getPetriNetSimulationData().size());
					if (this.series2idx.contains(place, TOKEN)
							&& series2idx.get(place, TOKEN, simRes.getId()) != null) {
						renderer.setSeriesStroke(series2idx.get(place, TOKEN, simRes.getId()), new BasicStroke(1));
						renderer.setSeriesPaint(series2idx.get(place, TOKEN, simRes.getId()), place.getPlotColor());
						renderer.setSeriesVisible((int) series2idx.get(place, TOKEN, simRes.getId()), true);
					} else {
						// System.out.println("does not contain");
					}
				} else if (bna instanceof Transition && onlyT && onlyDiscreteT) {
					legendY = "Delay";
					transition = (Transition) bna;
					if (series2idx.contains(transition, DELAY)) {
						renderer.setSeriesPaint(series2idx.get(transition, DELAY, simRes.getId()),
								transition.getPlotColor());
						renderer.setSeriesVisible((int) series2idx.get(transition, DELAY, simRes.getId()), true);
					}
				} else if (bna instanceof Transition && onlyT) {
					legendY = "Speed";
					transition = (Transition) bna;
					if (series2idx.contains(transition, ACTUAL_FIRING_SPEED)) {
						renderer.setSeriesPaint(series2idx.get(transition, ACTUAL_FIRING_SPEED, simRes.getId()),
								transition.getPlotColor());
						renderer.setSeriesVisible((int) series2idx.get(transition, ACTUAL_FIRING_SPEED, simRes.getId()),
								true);
					}
				}
			}
		}

		// set rendering options: all lines in black, domain steps as integers
		final XYPlot plot = chart.getXYPlot();

		// draw plot for Places with random colors
		if (secondAxis) {
			NumberAxis domainAxis = new NumberAxis("Time");
			plot.setDomainAxis(domainAxis);
			NumberAxis na = new NumberAxis("Tokens");

			plot.setRangeAxis(na);

			plot.setRenderer(1, renderer2);
			NumberAxis axis = new NumberAxis("Sum of tokens");
			axis.setAxisLinePaint(Color.RED);
			axis.setLabelPaint(Color.RED);
			axis.setTickLabelPaint(Color.RED);
			//
			plot.setRangeAxis(1, axis);

			plot.mapDatasetToRangeAxis(0, 0);// 1st dataset to 1st y-axis
			plot.mapDatasetToRangeAxis(1, 1); // 2nd dataset to 2nd y-axis
			axis.setRange(0, 1.3 * axis.getRange().getUpperBound());
			pane.getChart().removeLegend();

			if (pane.getChart().getLegend() == null) {
				legend.setBackgroundPaint(chart.getXYPlot().getBackgroundPaint());
				pane.getChart().addLegend(this.legend);
			}
			legend.setBackgroundPaint(plot.getBackgroundPaint());
		} else {
			plot.clearRangeAxes();
			plot.setRenderer(renderer);
			NumberAxis domainAxis = new NumberAxis("Time");
			plot.setDomainAxis(domainAxis);
			NumberAxis na = new NumberAxis(legendY);

			// na.setRange(min * 0.95, 1.05 * max);
			na.setAutoRange(true);
			na.setAutoRangeIncludesZero(false);

			plot.setRangeAxis(na);

			if (pane.getChart().getLegend() == null) {
				legend.setBackgroundPaint(chart.getXYPlot().getBackgroundPaint());
				pane.getChart().addLegend(this.legend);
			}
			legend.setBackgroundPaint(plot.getBackgroundPaint());
		}
		// updateData();
		// System.out.println("almost done drawing");
		// CHRIS maybe enable requestFocus again, but deleting via KeyEvent of graph
		// element wont work, because VV is out of focus
		// pane.requestFocus();
		chart.fireChartChanged();
		// System.out.println("done drawing");
	}

	public void updateDateCurrentSimulation(boolean fireSerieState) {
		if (simResController == null) {
			return;
		}
		if (simResController.getLastActive() != null) {
			try {
				this.updateData(simResController.getLastActive().getId(), fireSerieState);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void updateData(String simId, boolean fireSerieState) throws Exception {
		SimulationResult simRes = simResController.get(simId);
		// System.out.println("update Data method");
		// System.out.println("-----------------------------");
		boolean isValidPN = pw.isPetriNet() && pw.getPetriPropertiesNet().isPetriNetSimulation() && simRes != null;
		boolean isValidHiddenPN = !pw.isPetriNet() && pw.getTransformationInformation() != null
				&& pw.getTransformationInformation().getPetriNet() != null
				&& pw.getPetriPropertiesNet().isPetriNetSimulation() && simRes != null;
		if (isValidPN || isValidHiddenPN) {
			BiologicalNodeAbstract bna;
			BiologicalEdgeAbstract bea;
			PNArc edge;
			XYSeries series;
			XYSeries series2;
			Double value;
			int stop;
			int steps = 0;

			rowsDim = simRes.getTime().size();

			Iterator<BiologicalNodeAbstract> itBna;
			if (hiddenPN) {
				itBna = pw.getTransformationInformation().getPetriNet().getAllGraphNodes().iterator();
			} else {
				itBna = pw.getAllGraphNodes().iterator();
			}
			List<Double> time = simRes.getTimeValues();// pw.getPetriNet().getTime();
			int seriesId;
			while (itBna.hasNext()) {
				bna = itBna.next();
				// System.out.println(series.getItemCount());
				if (bna instanceof Place && !bna.isLogical()) {
					// System.out.println(simRes.contains(place, TOKEN));
					if (simRes.contains(bna, TOKEN) && simRes.get(bna, TOKEN).size() > 0) {
						// System.out.println(place + " " + TOKEN + " " + simRes.getId());
						// System.out.println(series2idx.get(place, TOKEN, simRes.getId()));
						seriesId = series2idx.get(bna, TOKEN, simRes.getId());
						series = this.seriesListR1.get(seriesId);
						stop = Math.min(simRes.get(bna, TOKEN).size(), time.size());
						steps = stop - series.getItemCount();
						if (steps > 0) {
							for (int i = series.getItemCount(); i < stop; i++) {
								// if (simRes.get(place, TOKEN).size() > i) {
								value = simRes.get(bna, TOKEN).get(i);
								// } else {
								// value = 0.0;
								// }
								series.add(simRes.getTime().get(i), value, false);
							}
							if (fireSerieState && renderer.isSeriesVisible(seriesId) && !isIterating) {
								dirtyVisibleSeries.add(series);
								// series.fireSeriesChanged();
								// System.out.println(bna.getName());
							}
						}
					}
				} else if (bna instanceof ContinuousTransition && !bna.isLogical()) {
					if (simRes.contains(bna, ACTUAL_FIRING_SPEED) && simRes.get(bna, ACTUAL_FIRING_SPEED).size() > 0) {
						seriesId = series2idx.get(bna, ACTUAL_FIRING_SPEED, simRes.getId());
						series = this.seriesListR1.get(seriesId);
						stop = Math.min(simRes.get(bna, ACTUAL_FIRING_SPEED).size(), time.size());
						steps = stop - series.getItemCount();
						if (steps > 0) {
							for (int i = series.getItemCount(); i < stop; i++) {
								value = simRes.get(bna, ACTUAL_FIRING_SPEED).get(i);
								series.add(simRes.getTime().get(i), value, false);
							}
							if (fireSerieState && renderer.isSeriesVisible(seriesId) && !isIterating) {
								// series.fireSeriesChanged();
								dirtyVisibleSeries.add(series);
							}
						}
					}
				} else if ((bna instanceof DiscreteTransition || bna instanceof StochasticTransition)
						&& !bna.isLogical()) {
					if (simRes.contains(bna, DELAY) && simRes.get(bna, DELAY).size() > 0) {
						seriesId = series2idx.get(bna, DELAY, simRes.getId());
						series = this.seriesListR1.get(seriesId);
						stop = Math.min(simRes.get(bna, DELAY).size(), time.size());
						steps = stop - series.getItemCount();
						if (steps > 0) {
							for (int i = series.getItemCount(); i < stop; i++) {
								value = simRes.get(bna, DELAY).get(i);
								series.add(simRes.getTime().get(i), value, false);
							}
							if (fireSerieState && renderer.isSeriesVisible(seriesId) && !isIterating) {
								dirtyVisibleSeries.add(series);
							}
						}
					}
				}
			}
			Iterator<BiologicalEdgeAbstract> itBea;
			if (hiddenPN) {
				itBea = pw.getTransformationInformation().getPetriNet().getAllEdges().iterator();
			} else {
				itBea = pw.getAllEdges().iterator();
			}
			while (itBea.hasNext()) {
				bea = itBea.next();

				if (bea instanceof PNArc) {
					edge = (PNArc) bea;
					if (simRes.contains(edge)) {
						seriesId = series2idx.get(edge, ACTUAL_TOKEN_FLOW, simRes.getId());
						series = this.seriesListR1.get(seriesId);
						series2 = this.seriesListR2.get(series2idx.get(edge, SUM_OF_TOKEN, simRes.getId()));
						// System.out.println(series.getItemCount() + " " + series2.getItemCount());
						if (simRes.contains(edge, ACTUAL_TOKEN_FLOW)
								&& simRes.get(edge, ACTUAL_TOKEN_FLOW).size() > 0) {
							// System.out.println("drin");
							stop = Math.min(simRes.get(edge, ACTUAL_TOKEN_FLOW).size(), time.size());
							steps = stop - series.getItemCount();
							if (steps > 0) {
								for (int i = series.getItemCount(); i < stop; i++) {
									value = simRes.get(edge, ACTUAL_TOKEN_FLOW).get(i);
									if (series == null) {
										System.out.println(edge.getFrom().getName() + " => " + edge.getTo().getName());
									}
									series.add(simRes.getTime().get(i), value, false);
								}
								if (fireSerieState && renderer.isSeriesVisible(seriesId) && !isIterating) {
									// series.fireSeriesChanged();
									dirtyVisibleSeries.add(series);
								}
							}
						}

						if (simRes.contains(edge, SUM_OF_TOKEN) && simRes.get(edge, SUM_OF_TOKEN).size() > 0) {
							seriesId = series2idx.get(edge, SUM_OF_TOKEN, simRes.getId());
							stop = Math.min(simRes.get(edge, SUM_OF_TOKEN).size(), time.size());
							steps = stop - series2.getItemCount();
							if (steps > 0) {
								for (int i = series2.getItemCount(); i < stop; i++) {
									value = simRes.get(edge, SUM_OF_TOKEN).get(i);
									if (series2 == null) {
										System.out.println(
												"null: " + edge.getFrom().getName() + " -> " + edge.getTo().getName());
									}
									series2.add(simRes.getTime().get(i), value, false);
								}
								if (fireSerieState && renderer2.isSeriesVisible(seriesId) && !isIterating) {
									// series2.fireSeriesChanged();
									dirtyVisibleSeriesR1.add(series2);
								}
							}
						}
					}
				}
			}

			if (fireSerieState) {
				try {
					isIterating = true;
					for (XYSeries s : dirtyVisibleSeries) {
						if (renderer.isSeriesVisible(seriesListR1.indexOf(s))) {
							s.fireSeriesChanged();
						}
					}
					for (XYSeries s : dirtyVisibleSeriesR1) {
						if (renderer.isSeriesVisible(seriesListR2.indexOf(s))) {
							s.fireSeriesChanged();
						}
					}
					dirtyVisibleSeries.clear();
					dirtyVisibleSeriesR1.clear();
					isIterating = false;
				} catch (ConcurrentModificationException e) {
					System.err.println("Simulation Result Plot during repaint: ConcurrentModificationException");
				} catch (IllegalArgumentException e) {
					System.err.println("Simulation Result Plot during repaint: IllegalArgumentException");
				} catch (NullPointerException e) {
					System.err.println("Simulation Result Plot during repaint: NullPointerException");
				} catch (IndexOutOfBoundsException e) {
					System.err.println("Simulation Result Plot during repaint: IndexOutOfBoundsException");
				}
			}
			if (chart != null) {
				// System.out.println("chart start firing");
				chart.fireChartChanged();
				// pane.repaint();
				// System.out.println("chart done firing");
				// System.out.println(seriesListR1.size()+"
				// "+seriesListR2.size());
			}
		}
		// chart.fireChartChanged();
		// pane.setPreferredSize(new Dimension(320, 500));
		// pane.repaint();
		// mainPanel.repaint();

	}

	/** Event handler function. Handles Button Clicks **/
	public void actionPerformed(ActionEvent e) {

		String event = e.getActionCommand();

		if ("zoomGraph".equals(event)) {
			// this.drawPlot();
			JFrame f = new JFrame("Simulation Results");

			ChartPanel panel = new ChartPanel(chart);
			f.setPreferredSize(panel.getPreferredSize());
			f.setIconImages(MainWindow.getInstance().getFrame().getIconImages());
			// System.out.println(panel.getPreferredSize());
			f.add(panel);
			f.pack();
			f.setVisible(true);

		} else if ("reset".equals(event)) {
			for (Iterator<BiologicalNodeAbstract> i = pw.getAllGraphNodes().iterator(); i.hasNext();) {
				BiologicalNodeAbstract bna = (BiologicalNodeAbstract) i.next();
				if (bna instanceof Place) {
					bna.setColor(Color.WHITE);
				}
			}
		} else if ("show".equals(event)) {
			new DetailedSimRes(pw, null);
		} else if (event.equals("animatePetriNet")) {
			// redraw plot and set new colors/sizes

			petriNetStopAnimationButton.setText("Stop");
			petriNetStopAnimationButton.setBackground(Color.RED);
			petriNetStopAnimationButton.setEnabled(true);
			petriNetStopAnimationButton.revalidate();
			resetPetriNet.setEnabled(false);
			thread = new AnimationThread(slider, (Integer) animationStart.getValue(),
					(Integer) animationStop.getValue(), animationColour.isSelected(),
					(Integer) animationSpeed.getValue(), petriNetAnimationButton, petriNetStopAnimationButton);
			thread.start();

		} else if (event.equals("animatePetriNetStop")) {
			// redraw plot and set new colors/sizes
			resetPetriNet.setEnabled(true);
			petriNetAnimationButton.setEnabled(false);
			slider.setEnabled(true);

			if (thread != null) {
				if (thread.isAlive()) {
					animationThreadStep = ((AnimationThread) thread).getThreadStep();
					((AnimationThread) thread).stopThread();
					petriNetStopAnimationButton.setText("Continue");
					petriNetStopAnimationButton.setBackground(Color.GREEN);
					petriNetStopAnimationButton.revalidate();

				} else {
					if (animationThreadStep != -1) {

						petriNetStopAnimationButton.setText("Stop");
						petriNetStopAnimationButton.setBackground(Color.RED);
						petriNetStopAnimationButton.revalidate();

						thread = new AnimationThread(slider, animationThreadStep, (Integer) animationStop.getValue(),
								animationColour.isSelected(), (Integer) animationSpeed.getValue(),
								petriNetAnimationButton, petriNetStopAnimationButton);
						thread.start();
					}
				}
			}
		} else if (event.equals("exportSimResult")) {
			new SaveDialog(new SuffixAwareFilter[] { SuffixAwareFilter.CSV_RESULT },
					SaveDialog.DATA_TYPE_SIMULATION_RESULTS);

			// System.out.println("click");
		}
		// CHRIS actions fuer T-Inv. und P-Inv. Test
		/*
		 * else if (event.equals("testP")) { System.out.println("TESTEN");
		 * Set<BiologicalNodeAbstract> hsVertex = new HashSet<BiologicalNodeAbstract>();
		 * hsVertex = graphInstance.getPathway().getAllNodes();//
		 * GraphInstance.getMyGraph().getAllvertices(); Set<BiologicalEdgeAbstract>
		 * hsEdge = new HashSet<BiologicalEdgeAbstract>(); hsEdge =
		 * graphInstance.getPathway().getAllEdges(); Iterator hsit =
		 * hsVertex.iterator(); BiologicalNodeAbstract bna; Place p; HashMap<String,
		 * Integer> hmplaces = new HashMap<String, Integer>(); HashMap<String, Integer>
		 * hmtransitions = new HashMap<String, Integer>(); int numberPlaces = 0; int
		 * numberTransitions = 0; ArrayList<String> names = new ArrayList<String>();
		 * while (hsit.hasNext()) { bna = (BiologicalNodeAbstract) hsit.next(); if (bna
		 * instanceof Transition) { hmtransitions.put(bna.getVertex().toString(), new
		 * Integer( numberTransitions)); numberTransitions++; } else { p = (Place) bna;
		 * hmplaces.put(bna.getVertex().toString(), new Integer( numberPlaces));
		 * names.add(p.getName()); numberPlaces++; } } double[][] f =
		 * this.initArray(numberPlaces, numberTransitions); double[][] b =
		 * this.initArray(numberPlaces, numberTransitions); // einkommende Kanten
		 * (backward matrix) Iterator edgeit = hsEdge.iterator(); PNEdge edge; Pair
		 * pair; while (edgeit.hasNext()) { edge = (PNEdge) edgeit.next(); pair =
		 * edge.getEdge().getEndpoints(); // T->P if
		 * (hmplaces.containsKey(pair.getSecond().toString())) { int i =
		 * hmplaces.get(pair.getSecond().toString()); int j =
		 * hmtransitions.get(pair.getFirst().toString()); b[i][j] +=
		 * edge.getPassingTokens(); } // P->T else { int i =
		 * hmplaces.get(pair.getFirst().toString()); int j =
		 * hmtransitions.get(pair.getSecond().toString()); f[i][j] -=
		 * edge.getPassingTokens(); } } SimpleMatrixDouble bMatrix = new
		 * SimpleMatrixDouble(b); SimpleMatrixDouble fMatrix = new
		 * SimpleMatrixDouble(f); SimpleMatrixDouble cMatrix = new
		 * SimpleMatrixDouble(this.initArray( numberPlaces, numberTransitions));
		 * cMatrix.add(bMatrix); cMatrix.add(fMatrix);
		 *
		 * double[] vd = new double[names.size()]; HashMap<String, Double> values = new
		 * HashMap<String, Double>();
		 *
		 *
		 * //this.t.getColumn(0); //System.out.println(t.getModel().getValueAt(1, 1));
		 *
		 * for (int i = 0; i < names.size(); i++) {
		 * values.put(t.getModel().getValueAt(i, 0).toString(),
		 * Double.parseDouble(t.getModel().getValueAt(i, 1).toString())); } for (int i =
		 * 0; i < names.size(); i++) { vd[i] = values.get(names.get(i)); }
		 *
		 * DenseDoubleMatrix2D c = new DenseDoubleMatrix2D(cMatrix.getData());
		 * DenseDoubleMatrix1D v = new DenseDoubleMatrix1D(vd); DenseDoubleMatrix1D x =
		 * new DenseDoubleMatrix1D(5); c.zMult(v, x, 1, 0, false); //
		 * System.out.println(x); IntArrayList l = new IntArrayList(); x.getNonZeros(l,
		 * null); // System.out.println(l.size()); if (l.size() == 0) {
		 * System.out.println("ist Invariante"); } else { System.out.println(
		 * "ist keine Invariante"); }
		 *
		 * for (int i = 0; i < this.r.length; i++) { // System.out.println(r[i][1]); } }
		 */
	}

	/**
	 * Handles changes of the time step slider. Calculates new size and color for
	 * each biological node apparent in the pathway (i.e. not being a reference).
	 */
	public void stateChanged(ChangeEvent e) {
		// System.out.println("state changed");
		boolean isValidPN = pw.isPetriNet() && pw.getPetriPropertiesNet().isPetriNetSimulation();
		boolean isValidHiddenPN = !pw.isPetriNet() && pw.getTransformationInformation() != null
				&& pw.getTransformationInformation().getPetriNet() != null
				&& pw.getPetriPropertiesNet().isPetriNetSimulation();
		if (isValidPN || isValidHiddenPN) {
			// if (pw.isPetriNet() && pw.getPetriPropertiesNet().isPetriNetSimulation()) {
			if (e.getSource().equals(animationStart))
				animationStartInit = (Integer) animationStart.getValue();
			else if (e.getSource().equals(animationStop))
				animationStopInit = (Integer) animationStop.getValue();
			else if (e.getSource().equals(animationSpeed))
				animationSpeedInit = (Integer) animationSpeed.getValue();
			else {
				sliderPosition = slider.getValue();
				SimulationResult simRes = simResController.getLastActive();
				pw.getPetriPropertiesNet().setCurrentTimeStep(this.slider.getValue());
				slider.setToolTipText("Step: " + this.slider.getValue() + ", use arrow keys for single steps");
				if (simRes == null) {
					pw.getPetriPropertiesNet().setPetriNetSimulation(false);
					MainWindow w = MainWindow.getInstance();
					w.updateAllGuiElements();
					return;
				}
				if (simRes.getTime().size() > 0) {

					// System.out.println("time size:
					// "+simRes.getTime().size());
					// System.out.println("slider: "+slider.getValue());
					double step = simRes.getTime().get(this.slider.getValue());
					// System.out.println(slider.getValue() +" "+step);
					stepLabel.setText("Time: " + (double) Math.round((step * 10000)) / 10000);

					XYPlot plot = (XYPlot) chart.getPlot();
					plot.removeDomainMarker(marker);

					marker = new ValueMarker(step);
					marker.setPaint(Color.black);
					plot.addDomainMarker(marker);
					// create node color set
					// 0 -> blue -> lower expression
					// 1 -> red -> higher expression
					// 2 -> gray -> no change in expression

					Vector<Integer> colors = new Vector<Integer>();
					colors.add(0, 0x0000ff);
					colors.add(1, 0xff0000);
					colors.add(2, 0xdedede);

					// initialize loop variables
					Double val;
					Double ref;
					int take = 0;
					// Vertex v;

					// get pathway and iterate over its JUNG vertices
					// graphInstance = new GraphInstance();
					// pw = graphInstance.getPathway();
					Collection<BiologicalNodeAbstract> ns = pw.getGraph().getAllVertices();
					if (ns != null) {
						Iterator<BiologicalNodeAbstract> it = ns.iterator();
						BiologicalNodeAbstract bna;
						while (it.hasNext()) {

							// cast to biological node type and retrieve
							// microarray
							// value
							// for current time step
							bna = it.next();
							bna = resolveHidden(bna);
							// System.out.println(bna.getName());

							ref = 1.0;
							if (simRes.contains(bna) && slider.getValue() >= 0) {
								// ref = Math.abs(ref);
								if (bna instanceof Place) {
									ref = simRes.get(bna, TOKEN).get(slider.getValue());
									((Place) bna).setToken(ref);
									MainWindow.getInstance().redrawTokens();
								} else if (bna instanceof Transition) {
									Transition t = (Transition) bna;
									if (simRes.get(t, ACTIVE) != null) {
										ref = simRes.get(t, ACTIVE).get(slider.getValue());
										if (ref == 1) {
											t.setSimulationActive(true);
										} else {
											t.setSimulationActive(false);
										}
									}
									if (simRes.get(t, FIRE) != null) {
										ref = simRes.get(t, FIRE).get(slider.getValue());
										if (ref == 1) {
											t.setSimulationFire(true);
										} else {
											t.setSimulationFire(false);
										}
									}
								}

								if (slider.getValue() >= 2) {
									// maybe reimplement
									val = 1.0;
									// get microarray value for reference time
									// step
									// and
									// compare
									// it
									// with the current value

									if (val > ref)
										take = 1;
									else if (val < ref)
										take = 0;
									else
										take = 2;

									// ref -= val;

									float ref2 = (float) Math.sqrt(Math.sqrt(ref));

									if (ref2 > 3.0)
										ref2 = 3.0f;
									// VertexShapes vs = new VertexShapes(ref2, 1.0f);
									if (animationColour.isSelected()) {
										bna.setColor(new Color(colors.get(take)));
									}
								}
							}
						}
					}
					// refresh GUI
					pw.getGraph().getVisualizationViewer().repaint();
				}
			}
		}
	}

	public void initGraphs() {
		//System.out.println("start init");
		pw = GraphInstance.getPathway();
		//System.out.println("PN: " + pw.isPetriNet());
		if (!pw.isPetriNet()) {
			//System.out.println(pw.getTransformationInformation());
			//System.out.println(pw.getTransformationInformation().getPetriNet());
			//System.out.println(pw.getTransformationInformation().getPetriNet().getPetriNetSimulation());
			//System.out.println(
			//		pw.getTransformationInformation().getPetriNet().getPetriPropertiesNet().getSimResController());
			//System.out.println(pw.getTransformationInformation().getPetriNet().getPetriPropertiesNet()
			//		.getSimResController().getAll().size());
			//System.out.println(pw.getTransformationInformation().getPetriNet().getPetriPropertiesNet()
			//		.getSimResController().getAllActive().size());
		}
		if (!pw.isPetriNet() && pw.getTransformationInformation() == null) {
			//System.out.println("ret1");
			return;
		}
		if (!pw.isPetriNet() && pw.getTransformationInformation().getPetriNet() == null) {
			//System.out.println("ret2");
			return;
		}
		if (!pw.isPetriNet() && pw.getTransformationInformation().getPetriNet() != null
				&& !pw.hasGotAtLeastOneElement()) {
			//System.out.println("ret3");
			return;
		}

		if (!pw.isPetriNet() && pw.getTransformationInformation().getPetriNet() != null) {
			hiddenPN = true;
		} else {
			hiddenPN = false;
		}
		//System.out.println("durch");
		vState = pw.getGraph().getVisualizationViewer().getPickedVertexState();
		eState = pw.getGraph().getVisualizationViewer().getPickedEdgeState();
		simResController = pw.getPetriPropertiesNet().getSimResController();
		// System.out.println("init Graphs");
		// pane.removeAll();

		renderer = new XYLineAndShapeRenderer();
		renderer.setDrawSeriesLineAsPath(true);// this line is the solution

		renderer2 = new XYLineAndShapeRenderer();

		labelsR1.clear();
		// plotEntities.clear();
		seriesListR1.clear();
		seriesListR2.clear();
		dataset.removeAllSeries();
		dataset2.removeAllSeries();
		r1Count = 0;
		r2Count = 0;
		// series2id.clear();
		series2idx.clear();
		idx2simR1.clear();
		idx2simR2.clear();
		places = new ArrayList<BiologicalNodeAbstract>();
		// get Selected Places and their index+label

		// System.out.println("process");
		Iterator<String> it = simResController.getSimIds().iterator();
		String simId;
		while (it.hasNext()) {
			simId = it.next();
			// System.out.println("adding");
			addSimulationToChart(simId);
			// System.out.println("update");
			try {
				updateData(simId, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// System.out.println("done processing");
		renderer.setDefaultToolTipGenerator(new XYToolTipGenerator() {

			@Override
			public String generateToolTip(XYDataset arg0, int seriesIdx, int arg2) {
				int pickedV = vState.getPicked().size();
				int pickedE = eState.getPicked().size();
				if(seriesIdx > labelsR1.size()){
					// if a PN element of a transformed graph is deleted and corresponding BN node is picked
					return "";
				}
				
				if (pickedV == 1 && pickedE == 0) {
					BiologicalNodeAbstract graphElement = vState.getPicked().iterator().next();
					graphElement = resolveReference(graphElement);
					BiologicalNodeAbstract bna;
					if (hiddenPN) {
						bna = resolveHidden(graphElement);
					} else {
						bna = graphElement;
					}
					if (bna instanceof Place && simResController.getAllActiveWithData(bna, TOKEN).size() <= 1) {
						return labelsR1.get(seriesIdx);
					}
					if (bna instanceof Transition
							&& simResController.getAllActiveWithData(bna, ACTUAL_FIRING_SPEED).size() <= 1) {
						return labelsR1.get(seriesIdx);
					}

					return labelsR1.get(seriesIdx) + "(" + idx2simR1.get(seriesIdx).getName() + ")";
				}
				return labelsR1.get(seriesIdx);
			}
		});

		// renderer.setBaseItemLabelsVisible(true);
		renderer.setLegendItemLabelGenerator(new XYSeriesLabelGenerator() {

			@Override
			public String generateLabel(XYDataset arg0, int seriesIdx) {
				int pickedV = vState.getPicked().size();
				int pickedE = eState.getPicked().size();
				if (pickedV == 1 && pickedE == 0) {
					BiologicalNodeAbstract graphElement = vState.getPicked().iterator().next();
					graphElement = resolveReference(graphElement);
					BiologicalNodeAbstract bna;
					if (hiddenPN) {
						bna = resolveHidden(graphElement);
					} else {
						bna = graphElement;
					}
					if (bna instanceof Place && simResController.getAllActiveWithData(bna, TOKEN).size() <= 1) {
						return graphElement.getName();
					}
					if (bna instanceof ContinuousTransition
							&& simResController.getAllActiveWithData(bna, ACTUAL_FIRING_SPEED).size() <= 1) {
						return graphElement.getName();
					}
					if ((bna instanceof DiscreteTransition || bna instanceof StochasticTransition)
							&& simResController.getAllActiveWithData(bna, DELAY).size() <= 1) {
						return graphElement.getName();
					}
					return idx2simR1.get(seriesIdx).getName();
				}
				if (pickedV == 0 && pickedE == 1) {
					BiologicalEdgeAbstract bea = eState.getPicked().iterator().next();
					if (hiddenPN) {
						bea = pw.getTransformationInformation().getPetriNet().getEdge(
								pw.getTransformationInformation().getBnToPnMapping().get(bea.getFrom()),
								pw.getTransformationInformation().getBnToPnMapping().get(bea.getTo()));
					}
					if (simResController.getAllActiveWithData(bea, SUM_OF_TOKEN).size() <= 1) {
						return labelsR1.get(seriesIdx);
					}
					return idx2simR1.get(seriesIdx).getName() + "(" + labelsR1.get(seriesIdx) + ")";
				}
				if (labelsR1.size() > seriesIdx) {
					return labelsR1.get(seriesIdx);
				}
				return "";
			}
		});

		renderer2.setLegendItemLabelGenerator(new XYSeriesLabelGenerator() {

			@Override
			public String generateLabel(XYDataset arg0, int seriesIdx) {
				int pickedV = vState.getPicked().size();
				int pickedE = eState.getPicked().size();
				if (pickedV == 0 && pickedE == 1) {
					BiologicalEdgeAbstract bea = eState.getPicked().iterator().next();
					if (hiddenPN) {
						bea = pw.getTransformationInformation().getPetriNet().getEdge(
								pw.getTransformationInformation().getBnToPnMapping().get(bea.getFrom()),
								pw.getTransformationInformation().getBnToPnMapping().get(bea.getTo()));
					}
					if (simResController.getAllActiveWithData(bea, SUM_OF_TOKEN).size() <= 1) {
						return "Sum";
					}
					return idx2simR2.get(seriesIdx).getName() + "(Sum)";
				}
				return "Sum";
			}
		});

		// System.out.println("done setting renderer");
		chart = ChartFactory.createXYLineChart("", "Time", "Token", dataset, PlotOrientation.VERTICAL, true, true,
				false);
		// set rendering options: all lines in black, domain steps as integers
		final XYPlot plot = chart.getXYPlot();

		// draw plot for Places with random colors

		plot.setDataset(0, dataset);
		plot.setRenderer(0, renderer);

		plot.setDataset(1, dataset2);
		plot.setRenderer(1, renderer2);

		// add chart to pane and refresh GUI

		pane = new ChartPanel(chart);

		Path settingsFilePath = SettingsManager.getSettingsFilePath();
		String path = null;
		try {
			XMLConfiguration xmlSettings = VanesaUtility.getFileBasedXMLConfiguration(settingsFilePath);
			path = xmlSettings.getString("SaveDialog-Path");
		} catch (ConfigurationException e) {
			System.out.println("There is probably no \"" + settingsFilePath + "\" yet.");
			e.printStackTrace();
		}
		if (StringUtils.isNotEmpty(path)) {
			File dir = new File(path);
			if (dir.exists() && dir.isDirectory()) {
				pane.setDefaultDirectoryForSaveAs(dir);
			}
		}

		// pane.setPreferredSize(new java.awt.Dimension(320, 200));

		pane.addChartMouseListener(new ChartMouseListener() {
			@Override
			public void chartMouseClicked(ChartMouseEvent event) {
				if (event.getEntity() != null && event.getEntity() instanceof XYItemEntity) {

					int pickedV = vState.getPicked().size();
					int pickedE = eState.getPicked().size();
					// System.out.println(pickedV);
					if (pickedE == 0 && pickedV == 0) {

						XYItemEntity entity = (XYItemEntity) event.getEntity();
						// System.out.println(entity.getSeriesIndex());
						BiologicalNodeAbstract p = places.get(entity.getSeriesIndex());

						// ps.clearPickedVertices();
						vState.clear();
						if (hiddenPN) {
							if (pw.getTransformationInformation().getBnToPnMapping().containsValue(p)) {
								for (BiologicalNodeAbstract key : pw.getTransformationInformation().getBnToPnMapping()
										.keySet()) {
									if (pw.getTransformationInformation().getBnToPnMapping().get(key) == p) {
										vState.pick(key, true);
										break;
									}
								}
							}
						} else {
							vState.pick(p, true);
						}
					}
				}
			}

			@Override
			public void chartMouseMoved(ChartMouseEvent e) {
			}
		});
		this.legend = pane.getChart().getLegend();
		// System.out.println("done init");
		// p.add(pane, BorderLayout.CENTER);

		// p.setVisible(true);

		this.revalidateView();
		// System.out.println("done init");
	}

	private void addSimulationToChart(String simId) {
		// System.out.println("start adding");
		dataset.setNotify(false);
		dataset2.setNotify(false);
		Place place;
		Transition transition;

		BiologicalNodeAbstract bna;
		BiologicalEdgeAbstract bea;
		if (pw == null) {
			return;
		}
		Iterator<BiologicalNodeAbstract> itNodes;
		if (hiddenPN) {
			itNodes = pw.getTransformationInformation().getPetriNet().getAllGraphNodesSortedAlphabetically().iterator();
		} else {
			itNodes = pw.getAllGraphNodesSortedAlphabetically().iterator();
		}

		XYSeries s;
		// System.out.println("resultssize: " +
		// pw.getPetriNet().getSimResController().getSimNames().size());
		if (simResController.get(simId) == null) {
			System.err.println("SimulationResultsPlotError, no such simulation name");
			return;
		}

		while (itNodes.hasNext()) {
			bna = itNodes.next();
			if (!bna.isLogical()) {
				if (bna instanceof Place) {
					place = (Place) bna;
					// if
					// (pw.getPetriNet().getSimResController().get(simName).contains(place,
					// TOKEN)) {
					// if (place.getPetriNetSimulationData().size() > 0) {
					places.add(place);
					s = new XYSeries(r1Count);

					// System.out.println("put: " + place +" "+simId+" "+r1Count);
					series2idx.put(place, TOKEN, simId, r1Count);
					idx2simR1.put(r1Count, simResController.get(simId));
					// System.out.println(r1Count+" "+place.getName()+" ");
					seriesListR1.add(s);

					// System.out.println(System.identityHashCode(place.getPetriNetSimulationData())
					// + " added");
					dataset.addSeries(s);

					if (hiddenPN) {
						if (pw.getTransformationInformation().getBnToPnMapping().containsValue(place)) {
							for (BiologicalNodeAbstract key : pw.getTransformationInformation().getBnToPnMapping()
									.keySet()) {
								if (pw.getTransformationInformation().getBnToPnMapping().get(key) == place) {
									labelsR1.add(key.getName());
									break;
								}
							}
						}
					} else {
						labelsR1.add(place.getName());
					}
					renderer.setSeriesPaint(r1Count, place.getPlotColor());

					renderer.setSeriesItemLabelsVisible(r1Count, true);
					renderer.setSeriesShapesVisible(r1Count, false);
					r1Count++;
					// }
				} else if (bna instanceof Transition) {
					transition = (Transition) bna;
					// if (transition.getPetriNetSimulationData().size() > 0) {
					// places.add(transition);
					s = new XYSeries(r1Count);
					if (transition instanceof ContinuousTransition) {
						series2idx.put(transition, ACTUAL_FIRING_SPEED, simId, r1Count);
					} else {
						series2idx.put(transition, DELAY, simId, r1Count);
					}
					idx2simR1.put(r1Count, simResController.get(simId));
					// System.out.println(r1Count+" "+transition.getName()+" ");
					// seriesList.add(new XYSeries(j));
					// series2id.put(s, count);
					seriesListR1.add(s);
					dataset.addSeries(s);
					if (hiddenPN) {
						if (pw.getTransformationInformation().getBnToPnMapping().containsValue(transition)) {
							for (BiologicalNodeAbstract key : pw.getTransformationInformation().getBnToPnMapping()
									.keySet()) {
								if (pw.getTransformationInformation().getBnToPnMapping().get(key) == transition) {
									labelsR1.add(key.getName());
									break;
								}
							}
						}
					} else {
						labelsR1.add(transition.getName());
					}

					renderer.setSeriesPaint(r1Count, Color.BLACK);

					renderer.setSeriesItemLabelsVisible(r1Count, true);
					renderer.setSeriesShapesVisible(r1Count, false);
					renderer.setSeriesVisible(r1Count, false);
					r1Count++;
					// }
				}
			}
		}
		// System.out.println("done nodes");
		Iterator<BiologicalEdgeAbstract> itEdges;
		if (hiddenPN) {
			itEdges = pw.getTransformationInformation().getPetriNet().getAllEdges().iterator();
		} else {
			itEdges = pw.getAllEdges().iterator();
		}
		while (itEdges.hasNext()) {
			bea = itEdges.next();
			if (bea instanceof PNArc) {
				PNArc edge = (PNArc) bea;
				// places.add(edge.getFrom());
				s = new XYSeries(r1Count);
				series2idx.put(edge, ACTUAL_TOKEN_FLOW, simId, r1Count);
				idx2simR1.put(r1Count, simResController.get(simId));
				// System.out.println(r1Count+"
				// edgeF:"+edge.getFrom().getName()+" ");
				seriesListR1.add(s);
				// series2id.put(s, count);
				dataset.addSeries(s);
				renderer.setSeriesPaint(r1Count, Color.black);
				renderer.setSeriesShapesVisible(r1Count, false);
				renderer.setSeriesVisible(r1Count, false);

				labelsR1.add("Flow");
				r1Count++;
				s = new XYSeries(r2Count);
				series2idx.put(edge, SUM_OF_TOKEN, simId, seriesListR2.size());
				// System.out.println(r2Count + " edgeF:" + edge.getFrom().getName() + " ");
				// System.out.println(count + ": " + edge.getID());
				// series2id.put(s, count);
				idx2simR2.put(r2Count, simResController.get(simId));
				renderer2.setSeriesPaint(seriesListR2.size(), Color.RED);
				renderer2.setSeriesShapesVisible(seriesListR2.size(), false);
				renderer2.setSeriesVisible(seriesListR2.size(), false);
				// labels.add("Sum of tokens");
				seriesListR2.add(s);
				// dataset.gets
				dataset2.addSeries(s);
				r2Count++;
			}
		}
		dataset.setNotify(true);
		dataset2.setNotify(true);
		// System.out.println("finished adding");
	}

	private BiologicalNodeAbstract resolveReference(BiologicalNodeAbstract bna) {
		if (bna.isLogical()) {
			return bna.getLogicalReference();
		}
		return bna;
	}

	private BiologicalNodeAbstract resolveHidden(BiologicalNodeAbstract bna) {
		if (hiddenPN) {
			return pw.getTransformationInformation().getBnToPnMapping().get(bna);
		}
		return bna;
	}
}
