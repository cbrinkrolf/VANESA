package gui.optionPanelWindows;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.util.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import configurations.Workspace;
import org.apache.commons.lang3.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.SeriesException;
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
import net.miginfocom.swing.MigLayout;
import petriNet.SimulationResult;
import petriNet.SimulationResultController;
import util.TripleHashMap;

import static petriNet.SimulationResultController.SIM_TOKEN;
import static petriNet.SimulationResultController.SIM_ACTUAL_FIRING_SPEED;
import static petriNet.SimulationResultController.SIM_ACTIVE;
import static petriNet.SimulationResultController.SIM_FIRE;
import static petriNet.SimulationResultController.SIM_DELAY;
import static petriNet.SimulationResultController.SIM_SUM_OF_TOKEN;
import static petriNet.SimulationResultController.SIM_ACTUAL_TOKEN_FLOW;

public class SimulationResultsPlot extends JPanel implements ChangeListener {
	private static final BasicStroke SERIES_STROKE = new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,
			1, new float[] { 2, 6 }, 0);
	private final JLabel stepLabel = new JLabel("Step 0");
	private final JSlider slider = new JSlider();
	private JFreeChart chart;
	private ValueMarker marker;
	private Pathway pw = null;
	private PickedState<BiologicalNodeAbstract> vState;
	private PickedState<BiologicalEdgeAbstract> eState;
	private SimulationResultController simResController;
	private boolean hiddenPN = false;
	private final JButton zoomGraphButton = new JButton("Enlarge Graph");
	private final JButton showDetailedResultsButton = new JButton("Show Detailed Simulation Results");

	private ChartPanel pane;

	// size of Vector in each Place
	private int rowsDim;

	private final ArrayList<String> labelsR1 = new ArrayList<>();

	private ArrayList<BiologicalNodeAbstract> places;

	private final ArrayList<XYSeries> seriesListR1 = new ArrayList<>();
	private final ArrayList<XYSeries> seriesListR2 = new ArrayList<>();
	private int r1Count = 0;
	private int r2Count = 0;

	private final XYSeriesCollection dataset = new XYSeriesCollection();
	private final XYSeriesCollection dataset2 = new XYSeriesCollection();

	private final TripleHashMap<GraphElementAbstract, Integer, String, Integer> series2idx = new TripleHashMap<>();
	private final HashMap<Integer, SimulationResult> idx2simR1 = new HashMap<>();
	private final HashMap<Integer, SimulationResult> idx2simR2 = new HashMap<>();

	private XYLineAndShapeRenderer renderer;
	private XYLineAndShapeRenderer renderer2;

	private LegendTitle legend;

	private final Set<XYSeries> dirtyVisibleSeries = new HashSet<>();
	private final Set<XYSeries> dirtyVisibleSeriesR1 = new HashSet<>();
	private boolean isIterating = false;

	private int sliderPosition = 0;

	private Set<String> simIds = new HashSet<>();

	public SimulationResultsPlot() {
		setLayout(new MigLayout("ins 0, wrap, fill"));
		zoomGraphButton.addActionListener((e) -> onOpenGraphInWindowClicked());
		showDetailedResultsButton.addActionListener((e) -> onShowDetailedSimResultsClicked());
		setVisible(false);
	}

	/**
	 * Removes all Elements from the main panel.
	 */
	public void removeAllElements() {
		removeAll();
		setVisible(false);
	}

	public void revalidateView() {
		if (pw == null) {
			return;
		}
		for (final BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
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
			final SimulationResult simRes = simResController.getLastActive();
			if (pw.getPetriPropertiesNet().isPetriNetSimulation() && simRes != null) {
				// most of the time unnecessary to remove all
				removeAll();
				rowsDim = simRes.getTime().size();
				slider.setMinimum(0);
				slider.setMaximum(rowsDim - 1);
				slider.setMajorTickSpacing(1);
				slider.addChangeListener(this);

				slider.setToolTipText("Step: 0, use arrow keys for single steps");
				slider.setValue(sliderPosition);

				add(new JLabel("Petri Net Simulation Result Plots for Places within the Network"), "growx");
				add(pane, "growx, height 200:200:200");
				pane.setPreferredSize(new Dimension(360, 200));

				final JPanel controlPanel = new JPanel(new MigLayout("ins 0, wrap 2, fill"));
				controlPanel.add(zoomGraphButton, "growx");
				controlPanel.add(showDetailedResultsButton, "growx");
				controlPanel.add(new JSeparator(), "growx, span 2");
				controlPanel.add(slider, "growx, span 2");
				controlPanel.add(stepLabel, "growx, span 2");
				add(controlPanel, "growx");
				setVisible(true);
				drawPlot();
				if (simRes.getTime().size() > 0) {
					for (final BiologicalNodeAbstract node : pw.getAllGraphNodes()) {
						if (node instanceof Transition) {
							if (simRes.contains(node) && simRes.get(node, SIM_ACTIVE) != null) {
								final Double ref = simRes.get(node, SIM_ACTIVE).get(slider.getValue());
								((Transition) node).setSimulationActive(ref != null && ref == 1);
							}
						}
					}
					pw.getGraph().getVisualizationViewer().repaint();
				}
			} else {
				removeAllElements();
				pw.getGraph().getVisualizationViewer().repaint();
			}
		} else {
			removeAllElements();
		}
	}

	/**
	 * This method redraws the time series plot in the left sidebar.
	 */
	private void drawPlot() {
		chart.setNotify(false);
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
				final PNArc edge = (PNArc) bea;
				final List<SimulationResult> listActive = simResController.getAllActiveWithData(edge, SIM_SUM_OF_TOKEN);
				for (int i = 0; i < listActive.size(); i++) {
					final SimulationResult result = listActive.get(i);
					final int idxFlow = series2idx.get(edge, SIM_ACTUAL_TOKEN_FLOW, result.getId());
					final int idxSum = series2idx.get(edge, SIM_SUM_OF_TOKEN, result.getId());
					renderer.setSeriesStroke(idxFlow, SERIES_STROKE);
					final Color c = Color.getHSBColor(i / (float) listActive.size(), 1, 1);
					renderer.setSeriesPaint(idxFlow, c);
					renderer2.setSeriesPaint(idxSum, c);
					renderer.setSeriesVisible(idxFlow, true);
					renderer2.setSeriesVisible(idxSum, true);
				}
			}
		} else if (pickedV == 0 && pickedE > 1) {
			final Set<PNArc> validSet = new HashSet<>();
			for (BiologicalEdgeAbstract bea : eState.getPicked()) {
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
			for (final PNArc arc : validSet) {
				secondAxis = true;
				final SimulationResult result = simResController.getLastActive();
				int idxFlow = series2idx.get(arc, SIM_ACTUAL_TOKEN_FLOW, result.getId());
				int idxSum = series2idx.get(arc, SIM_SUM_OF_TOKEN, result.getId());
				renderer.setSeriesStroke(idxFlow, SERIES_STROKE);
				final Color c = Color.getHSBColor(i / (float) validSet.size(), 1, 1);
				renderer.setSeriesPaint(idxFlow, c);
				renderer2.setSeriesPaint(idxSum, c);
				renderer.setSeriesVisible(idxFlow, true);
				renderer2.setSeriesVisible(idxSum, true);
				i++;
			}

		} else if (pickedV == 1) {
			BiologicalNodeAbstract bna = vState.getPicked().iterator().next();
			bna = resolveReference(bna);
			bna = resolveHidden(bna);
			if (bna instanceof PNNode) {
				List<SimulationResult> listActive;
				if (bna instanceof Place) {
					listActive = simResController.getAllActiveWithData(bna, SIM_TOKEN);
				} else if (bna instanceof ContinuousTransition) {
					listActive = simResController.getAllActiveWithData(bna, SIM_ACTUAL_FIRING_SPEED);
					legendY = "Speed";
				} else {
					listActive = simResController.getAllActiveWithData(bna, SIM_DELAY);
					legendY = "Delay";
				}
				for (int i = 0; i < listActive.size(); i++) {
					final SimulationResult result = listActive.get(i);
					final int idx;
					if (bna instanceof Place) {
						idx = series2idx.get(bna, SIM_TOKEN, result.getId());
					} else if (bna instanceof ContinuousTransition) {
						idx = series2idx.get(bna, SIM_ACTUAL_FIRING_SPEED, result.getId());
					} else {
						idx = series2idx.get(bna, SIM_DELAY, result.getId());
					}
					renderer.setSeriesVisible(idx, true);
					final Color c = Color.getHSBColor(i / (float) listActive.size(), 1, 1);
					if (listActive.size() == 1) {
						// renderer.setSeriesPaint(idx, pn.getPlotColor());
						renderer.setSeriesPaint(idx, Color.red);
					} else {
						renderer.setSeriesPaint(idx, c);
					}
				}
			}
		} else {
			boolean onlyT = true;
			boolean onlyDiscreteT = true;
			if (vState.getPicked().size() > 0) {
				Iterator<BiologicalNodeAbstract> it = vState.getPicked().iterator();
				while (onlyT && it.hasNext()) {
					BiologicalNodeAbstract bna = it.next();
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
			} else {
				onlyT = false;
			}

			final Iterator<BiologicalNodeAbstract> iterator;
			if (pickedV > 0) {
				iterator = vState.getPicked().iterator();
			} else {
				iterator = pw.getAllGraphNodes().iterator();
			}
			while (iterator.hasNext()) {
				BiologicalNodeAbstract bna = iterator.next();
				bna = resolveReference(bna);
				bna = resolveHidden(bna);
				if (bna instanceof Place) {
					final Place place = (Place) bna;
					if (series2idx.containsKey(place, SIM_TOKEN) && series2idx.get(place, SIM_TOKEN, simRes.getId())
							!= null) {
						renderer.setSeriesStroke(series2idx.get(place, SIM_TOKEN, simRes.getId()), new BasicStroke(1));
						renderer.setSeriesPaint(series2idx.get(place, SIM_TOKEN, simRes.getId()), place.getPlotColor());
						renderer.setSeriesVisible(series2idx.get(place, SIM_TOKEN, simRes.getId()), true);
					}
				} else if (bna instanceof Transition && onlyT && onlyDiscreteT) {
					legendY = "Delay";
					final Transition transition = (Transition) bna;
					if (series2idx.containsKey(transition, SIM_DELAY)) {
						renderer.setSeriesPaint(series2idx.get(transition, SIM_DELAY, simRes.getId()),
								transition.getPlotColor());
						renderer.setSeriesVisible(series2idx.get(transition, SIM_DELAY, simRes.getId()), true);
					}
				} else if (bna instanceof Transition && onlyT) {
					legendY = "Speed";
					final Transition transition = (Transition) bna;
					if (series2idx.containsKey(transition, SIM_ACTUAL_FIRING_SPEED)) {
						renderer.setSeriesPaint(series2idx.get(transition, SIM_ACTUAL_FIRING_SPEED, simRes.getId()),
								transition.getPlotColor());
						renderer.setSeriesVisible(series2idx.get(transition, SIM_ACTUAL_FIRING_SPEED, simRes.getId()),
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
			na.setAutoRange(true);
			na.setAutoRangeIncludesZero(false);
			plot.setRangeAxis(na);
			if (pane.getChart().getLegend() == null) {
				legend.setBackgroundPaint(chart.getXYPlot().getBackgroundPaint());
				pane.getChart().addLegend(legend);
			}
			legend.setBackgroundPaint(plot.getBackgroundPaint());
		}
		chart.setNotify(true);
		// CHRIS maybe enable requestFocus again, but deleting via KeyEvent of graph
		// element won't work, because VV is out of focus
		// pane.requestFocus();
		chart.fireChartChanged();
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

	private void updateData(String simId, boolean fireSerieState) {
		SimulationResult simRes = simResController.get(simId);
		boolean isValidPN = pw.isPetriNet() && pw.getPetriPropertiesNet().isPetriNetSimulation() && simRes != null;
		boolean isValidHiddenPN = !pw.isPetriNet() && pw.getTransformationInformation() != null
				&& pw.getTransformationInformation().getPetriNet() != null && pw.getPetriPropertiesNet()
				.isPetriNetSimulation() && simRes != null;
		if (isValidPN || isValidHiddenPN) {
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
				BiologicalNodeAbstract bna = itBna.next();
				// System.out.println(series.getItemCount());
				if (bna instanceof Place && !bna.isLogical()) {
					// System.out.println(simRes.contains(place, TOKEN));
					if (simRes.contains(bna, SIM_TOKEN) && simRes.get(bna, SIM_TOKEN).size() > 0) {
						// System.out.println(place + " " + TOKEN + " " + simRes.getId());
						// System.out.println(series2idx.get(place, TOKEN, simRes.getId()));
						seriesId = series2idx.get(bna, SIM_TOKEN, simRes.getId());
						XYSeries series = this.seriesListR1.get(seriesId);
						int stop = Math.min(simRes.get(bna, SIM_TOKEN).size(), time.size());
						int steps = stop - series.getItemCount();
						if (steps > 0) {
							for (int i = series.getItemCount(); i < stop; i++) {
								// if (simRes.get(place, TOKEN).size() > i) {
								Double value = simRes.get(bna, SIM_TOKEN).get(i);
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
					if (simRes.contains(bna, SIM_ACTUAL_FIRING_SPEED) && simRes.get(bna, SIM_ACTUAL_FIRING_SPEED).size()
							> 0) {
						seriesId = series2idx.get(bna, SIM_ACTUAL_FIRING_SPEED, simRes.getId());
						XYSeries series = this.seriesListR1.get(seriesId);
						int stop = Math.min(simRes.get(bna, SIM_ACTUAL_FIRING_SPEED).size(), time.size());
						int steps = stop - series.getItemCount();
						if (steps > 0) {
							for (int i = series.getItemCount(); i < stop; i++) {
								Double value = simRes.get(bna, SIM_ACTUAL_FIRING_SPEED).get(i);
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
					if (simRes.contains(bna, SIM_DELAY) && simRes.get(bna, SIM_DELAY).size() > 0) {
						seriesId = series2idx.get(bna, SIM_DELAY, simRes.getId());
						XYSeries series = this.seriesListR1.get(seriesId);
						int stop = Math.min(simRes.get(bna, SIM_DELAY).size(), time.size());
						int steps = stop - series.getItemCount();
						if (steps > 0) {
							for (int i = series.getItemCount(); i < stop; i++) {
								Double value = simRes.get(bna, SIM_DELAY).get(i);
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
				BiologicalEdgeAbstract bea = itBea.next();
				if (bea instanceof PNArc) {
					PNArc edge = (PNArc) bea;
					if (simRes.contains(edge)) {
						seriesId = series2idx.get(edge, SIM_ACTUAL_TOKEN_FLOW, simRes.getId());
						XYSeries series = this.seriesListR1.get(seriesId);
						XYSeries series2 = this.seriesListR2.get(
								series2idx.get(edge, SIM_SUM_OF_TOKEN, simRes.getId()));
						if (simRes.contains(edge, SIM_ACTUAL_TOKEN_FLOW) && simRes.get(edge, SIM_ACTUAL_TOKEN_FLOW)
								.size() > 0) {
							int stop = Math.min(simRes.get(edge, SIM_ACTUAL_TOKEN_FLOW).size(), time.size());
							int steps = stop - series.getItemCount();
							if (steps > 0) {
								for (int i = series.getItemCount(); i < stop; i++) {
									Double value = simRes.get(edge, SIM_ACTUAL_TOKEN_FLOW).get(i);
									series.add(simRes.getTime().get(i), value, false);
								}
								if (fireSerieState && renderer.isSeriesVisible(seriesId) && !isIterating) {
									// series.fireSeriesChanged();
									dirtyVisibleSeries.add(series);
								}
							}
						}

						if (simRes.contains(edge, SIM_SUM_OF_TOKEN) && simRes.get(edge, SIM_SUM_OF_TOKEN).size() > 0) {
							seriesId = series2idx.get(edge, SIM_SUM_OF_TOKEN, simRes.getId());
							int stop = Math.min(simRes.get(edge, SIM_SUM_OF_TOKEN).size(), time.size());
							int steps = stop - series2.getItemCount();
							if (steps > 0) {
								for (int i = series2.getItemCount(); i < stop; i++) {
									Double value = simRes.get(edge, SIM_SUM_OF_TOKEN).get(i);
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
				chart.fireChartChanged();
			}
		}
	}

	private void onOpenGraphInWindowClicked() {
		final JFrame f = new JFrame("Simulation Results");
		final ChartPanel panel = new ChartPanel(chart);
		f.setPreferredSize(panel.getPreferredSize());
		f.setIconImages(MainWindow.getInstance().getFrame().getIconImages());
		f.add(panel);
		f.pack();
		f.setVisible(true);
	}

	private void onShowDetailedSimResultsClicked() {
		new DetailedSimRes(pw, null);
	}

	/**
	 * Handles changes of the time step slider. Calculates new size and color for each biological node apparent in the
	 * pathway (i.e. not being a reference).
	 */
	public void stateChanged(ChangeEvent e) {
		boolean isValidPN = pw.isPetriNet() && pw.getPetriPropertiesNet().isPetriNetSimulation();
		boolean isValidHiddenPN = !pw.isPetriNet() && pw.getTransformationInformation() != null
				&& pw.getTransformationInformation().getPetriNet() != null && pw.getPetriPropertiesNet()
				.isPetriNetSimulation();
		if (isValidPN || isValidHiddenPN) {
			sliderPosition = slider.getValue();
			SimulationResult simRes = simResController.getLastActive();
			pw.getPetriPropertiesNet().setCurrentTimeStep(slider.getValue());
			slider.setToolTipText("Step: " + slider.getValue() + ", use arrow keys for single steps");
			if (simRes == null) {
				pw.getPetriPropertiesNet().setPetriNetSimulation(false);
				MainWindow w = MainWindow.getInstance();
				w.updateAllGuiElements();
				return;
			}
			if (simRes.getTime().size() > 0) {
				double step = simRes.getTime().get(slider.getValue());
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
				Vector<Integer> colors = new Vector<>();
				colors.add(0, 0x0000ff);
				colors.add(1, 0xff0000);
				colors.add(2, 0xdedede);
				final Collection<BiologicalNodeAbstract> ns = pw.getGraph().getAllVertices();
				if (ns != null) {
					for (BiologicalNodeAbstract bna : ns) {
						bna = resolveHidden(bna);
						if (simRes.contains(bna) && slider.getValue() >= 0) {
							if (bna instanceof Place) {
								((Place) bna).setToken(simRes.get(bna, SIM_TOKEN).get(slider.getValue()));
								MainWindow.getInstance().redrawTokens();
							} else if (bna instanceof Transition) {
								Transition t = (Transition) bna;
								if (simRes.get(t, SIM_ACTIVE) != null) {
									t.setSimulationActive(simRes.get(t, SIM_ACTIVE).get(slider.getValue()) == 1);
								}
								if (simRes.get(t, SIM_FIRE) != null) {
									t.setSimulationFire(simRes.get(t, SIM_FIRE).get(slider.getValue()) == 1);
								}
							}
						}
					}
				}
				pw.getGraph().getVisualizationViewer().repaint();
			}
		}
	}

	public void initGraphs() {
		pw = GraphInstance.getPathway();
		if (pw == null) {
			return;
		}
		if (!pw.isPetriNet()) {
			if (pw.getTransformationInformation() == null || pw.getTransformationInformation().getPetriNet() == null
					|| !pw.hasGotAtLeastOneElement()) {
				return;
			}
		}

		hiddenPN = !pw.isPetriNet() && pw.getTransformationInformation().getPetriNet() != null;
		vState = pw.getGraph().getVisualizationViewer().getPickedVertexState();
		eState = pw.getGraph().getVisualizationViewer().getPickedEdgeState();
		simResController = pw.getPetriPropertiesNet().getSimResController();
		renderer = new XYLineAndShapeRenderer();
		renderer.setDrawSeriesLineAsPath(true);
		renderer2 = new XYLineAndShapeRenderer();

		labelsR1.clear();
		seriesListR1.clear();
		seriesListR2.clear();
		dataset.removeAllSeries();
		dataset2.removeAllSeries();
		r1Count = 0;
		r2Count = 0;
		series2idx.clear();
		idx2simR1.clear();
		idx2simR2.clear();
		places = new ArrayList<>();
		simIds = new HashSet<>();
		// get Selected Places and their index+label
		for (final String simId : simResController.getSimIds()) {
			addSimulationToChart(simId);
			try {
				updateData(simId, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		renderer.setDefaultToolTipGenerator((arg0, seriesIdx, arg2) -> {
			int pickedV = vState.getPicked().size();
			int pickedE = eState.getPicked().size();
			if (seriesIdx > labelsR1.size()) {
				// if a PN element of a transformed graph is deleted and corresponding BN node is picked
				return "";
			}

			if (pickedV == 1 && pickedE == 0) {
				BiologicalNodeAbstract graphElement = vState.getPicked().iterator().next();
				graphElement = resolveReference(graphElement);
				final BiologicalNodeAbstract bna = hiddenPN ? resolveHidden(graphElement) : graphElement;
				if (bna instanceof Place && simResController.getAllActiveWithData(bna, SIM_TOKEN).size() <= 1) {
					return labelsR1.get(seriesIdx);
				}
				if (bna instanceof Transition && simResController.getAllActiveWithData(bna, SIM_ACTUAL_FIRING_SPEED)
						.size() <= 1) {
					return labelsR1.get(seriesIdx);
				}

				return labelsR1.get(seriesIdx) + "(" + idx2simR1.get(seriesIdx).getName() + ")";
			}
			return labelsR1.get(seriesIdx);
		});

		renderer.setLegendItemLabelGenerator((arg0, seriesIdx) -> {
			final int pickedV = vState.getPicked().size();
			final int pickedE = eState.getPicked().size();
			if (pickedV == 1 && pickedE == 0) {
				BiologicalNodeAbstract graphElement = vState.getPicked().iterator().next();
				graphElement = resolveReference(graphElement);
				final BiologicalNodeAbstract bna = hiddenPN ? resolveHidden(graphElement) : graphElement;
				if (bna instanceof Place && simResController.getAllActiveWithData(bna, SIM_TOKEN).size() <= 1) {
					return graphElement.getName();
				}
				if (bna instanceof ContinuousTransition && simResController.getAllActiveWithData(bna,
						SIM_ACTUAL_FIRING_SPEED).size() <= 1) {
					return graphElement.getName();
				}
				if ((bna instanceof DiscreteTransition || bna instanceof StochasticTransition)
						&& simResController.getAllActiveWithData(bna, SIM_DELAY).size() <= 1) {
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
				if (simResController.getAllActiveWithData(bea, SIM_SUM_OF_TOKEN).size() <= 1) {
					return labelsR1.get(seriesIdx);
				}
				return idx2simR1.get(seriesIdx).getName() + "(" + labelsR1.get(seriesIdx) + ")";
			}
			if (labelsR1.size() > seriesIdx) {
				return labelsR1.get(seriesIdx);
			}
			return "";
		});

		renderer2.setLegendItemLabelGenerator((arg0, seriesIdx) -> {
			int pickedV = vState.getPicked().size();
			int pickedE = eState.getPicked().size();
			if (pickedV == 0 && pickedE == 1) {
				BiologicalEdgeAbstract bea = eState.getPicked().iterator().next();
				if (hiddenPN) {
					bea = pw.getTransformationInformation().getPetriNet().getEdge(
							pw.getTransformationInformation().getBnToPnMapping().get(bea.getFrom()),
							pw.getTransformationInformation().getBnToPnMapping().get(bea.getTo()));
				}
				if (simResController.getAllActiveWithData(bea, SIM_SUM_OF_TOKEN).size() <= 1) {
					return "Sum";
				}
				return idx2simR2.get(seriesIdx).getName() + "(Sum)";
			}
			return "Sum";
		});

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

		String path = Workspace.getCurrentSettings().getSaveDialogPath();
		if (StringUtils.isNotEmpty(path)) {
			File dir = new File(path);
			if (dir.exists() && dir.isDirectory()) {
				pane.setDefaultDirectoryForSaveAs(dir);
			}
		}

		pane.addChartMouseListener(new ChartMouseListener() {
			@Override
			public void chartMouseClicked(final ChartMouseEvent event) {
				onChartMouseClicked(event);
			}

			@Override
			public void chartMouseMoved(final ChartMouseEvent e) {
			}
		});
		legend = pane.getChart().getLegend();
		revalidateView();
	}

	private void onChartMouseClicked(final ChartMouseEvent event) {
		if (event.getEntity() == null || !(event.getEntity() instanceof XYItemEntity)) {
			return;
		}
		final int pickedV = vState.getPicked().size();
		final int pickedE = eState.getPicked().size();
		if (pickedE == 0 && pickedV == 0) {
			final XYItemEntity entity = (XYItemEntity) event.getEntity();
			final BiologicalNodeAbstract p = places.get(entity.getSeriesIndex());
			vState.clear();
			if (hiddenPN) {
				final Map<BiologicalNodeAbstract, PNNode> bnToPNMap = pw.getTransformationInformation()
						.getBnToPnMapping();
				for (final BiologicalNodeAbstract key : bnToPNMap.keySet()) {
					if (bnToPNMap.get(key) == p) {
						vState.pick(key, true);
						break;
					}
				}
			} else {
				vState.pick(p, true);
			}
		}
	}

	public void addSimulationResults() {
		if (simIds.isEmpty()) {
			initGraphs();
		}
		for (String simId : simResController.getSimIds()) {
			if (simIds.contains(simId)) {
				continue;
			}
			System.out.println("adding");
			addSimulationToChart(simId);
			try {
				updateData(simId, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void addSimulationToChart(final String simId) {
		if (simIds.contains(simId)) {
			return;
		}
		dataset.setNotify(false);
		dataset2.setNotify(false);
		if (pw == null) {
			return;
		}
		Iterator<BiologicalNodeAbstract> itNodes;
		if (hiddenPN) {
			itNodes = pw.getTransformationInformation().getPetriNet().getAllGraphNodesSortedAlphabetically().iterator();
		} else {
			itNodes = pw.getAllGraphNodesSortedAlphabetically().iterator();
		}

		if (simResController.get(simId) == null) {
			System.err.println("SimulationResultsPlotError, no such simulation name");
			return;
		}

		while (itNodes.hasNext()) {
			final BiologicalNodeAbstract bna = itNodes.next();
			if (!bna.isLogical()) {
				if (bna instanceof Place) {
					final Place place = (Place) bna;
					// if
					// (pw.getPetriNet().getSimResController().get(simName).contains(place,
					// TOKEN)) {
					// if (place.getPetriNetSimulationData().size() > 0) {
					places.add(place);
					XYSeries s = new XYSeries(r1Count);

					series2idx.put(place, SIM_TOKEN, simId, r1Count);
					idx2simR1.put(r1Count, simResController.get(simId));
					seriesListR1.add(s);
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
					final Transition transition = (Transition) bna;
					// if (transition.getPetriNetSimulationData().size() > 0) {
					// places.add(transition);
					XYSeries s = new XYSeries(r1Count);
					if (transition instanceof ContinuousTransition) {
						series2idx.put(transition, SIM_ACTUAL_FIRING_SPEED, simId, r1Count);
					} else {
						series2idx.put(transition, SIM_DELAY, simId, r1Count);
					}
					idx2simR1.put(r1Count, simResController.get(simId));
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
		Iterator<BiologicalEdgeAbstract> itEdges;
		if (hiddenPN) {
			itEdges = pw.getTransformationInformation().getPetriNet().getAllEdges().iterator();
		} else {
			itEdges = pw.getAllEdges().iterator();
		}
		while (itEdges.hasNext()) {
			final BiologicalEdgeAbstract bea = itEdges.next();
			if (bea instanceof PNArc) {
				final PNArc edge = (PNArc) bea;
				XYSeries s = new XYSeries(r1Count);
				series2idx.put(edge, SIM_ACTUAL_TOKEN_FLOW, simId, r1Count);
				idx2simR1.put(r1Count, simResController.get(simId));
				seriesListR1.add(s);
				dataset.addSeries(s);
				renderer.setSeriesPaint(r1Count, Color.black);
				renderer.setSeriesShapesVisible(r1Count, false);
				renderer.setSeriesVisible(r1Count, false);

				labelsR1.add("Flow");
				r1Count++;
				s = new XYSeries(r2Count);
				series2idx.put(edge, SIM_SUM_OF_TOKEN, simId, seriesListR2.size());
				idx2simR2.put(r2Count, simResController.get(simId));
				renderer2.setSeriesPaint(seriesListR2.size(), Color.RED);
				renderer2.setSeriesShapesVisible(seriesListR2.size(), false);
				renderer2.setSeriesVisible(seriesListR2.size(), false);
				seriesListR2.add(s);
				dataset2.addSeries(s);
				r2Count++;
			}
		}
		dataset.setNotify(true);
		dataset2.setNotify(true);
		simIds.add(simId);
	}

	private BiologicalNodeAbstract resolveReference(final BiologicalNodeAbstract bna) {
		return bna.isLogical() ? bna.getLogicalReference() : bna;
	}

	private BiologicalNodeAbstract resolveHidden(final BiologicalNodeAbstract bna) {
		if (hiddenPN) {
			return pw.getTransformationInformation().getBnToPnMapping().get(bna);
		}
		return bna;
	}
}
