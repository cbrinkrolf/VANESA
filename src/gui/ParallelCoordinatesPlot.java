package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
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
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNEdge;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
import edu.uci.ics.jung.visualization.picking.PickedState;
import graph.GraphInstance;
import graph.animations.RegulationTabelModel;
import graph.jung.graphDrawing.VertexShapes;
import gui.algorithms.ScreenSize;
import io.SaveDialog;
import miscalleanous.tables.MyTable;
import net.miginfocom.swing.MigLayout;
import petriNet.AnimationThread;
import petriNet.PlotsPanel;
import petriNet.SimulationResult;
import petriNet.SimulationResultController;
import util.TripleHashMap;

/**
 * This class creates a parallel coordinates plot to be shown in the side bar.
 * The plot will be used to visualize all microarray data of the current pathway
 * for all timesteps.
 *
 * @author tschoeni
 *
 */
public class ParallelCoordinatesPlot implements ActionListener, ChangeListener {

	// prepare data sets
	// private Hashtable<Integer, BiologicalNodeAbstract> nodeTabel = new
	// Hashtable<Integer, BiologicalNodeAbstract>();
	// instance of main window
	private MainWindow w;

	// create GUI components
	private JButton showTable = new JButton("show detailed simulation results");
	// private JButton drawPlots = new
	// JButton("show all animation result plots");

	private JLabel stepLabel = new JLabel("Step 0");
	private JSlider slider = new JSlider();
	private JPanel dialogPanel;
	private JPanel main;
	private JPanel controlPanel;
	private JFreeChart chart;
	private ValueMarker marker;
	private JFrame dialog;
	private GraphInstance graphInstance = null;
	private Pathway pw = null;
	// private RegulationTabelModel model;
	private MyTable table;
	// private boolean first = true;
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

	private JButton exportSimResult;
	private JButton zoomGraph;

	// private JPanel invariants = new JPanel();

	// An Object to store microarray data
	private Object[][] rows;
	// number of Places
	private int rowsSize;
	// size of Vector in each Place
	private int rowsDim;

	private ArrayList<String> labelsR1 = new ArrayList<String>();

	private int animationStartInit = 0;

	private int animationStopInit = 1;

	private int animationSpeedInit = 20;

	// private ArrayList<Place> plotEntities = new ArrayList<Place>();

	private ArrayList<BiologicalNodeAbstract> places;

	private ArrayList<XYSeries> seriesListR1 = new ArrayList<XYSeries>();
	private ArrayList<XYSeries> seriesListR2 = new ArrayList<XYSeries>();
	private int r1Count = 0;
	private int r2Count = 0;

	private XYSeriesCollection dataset = new XYSeriesCollection();
	private XYSeriesCollection dataset2 = new XYSeriesCollection();

	private TripleHashMap<GraphElementAbstract, Integer, String, Integer> series2idx = new TripleHashMap<GraphElementAbstract, Integer, String, Integer>();
	private HashMap<Integer, SimulationResult> idx2simR1 = new HashMap<Integer, SimulationResult>();
	private HashMap<Integer, SimulationResult> idx2simR2 = new HashMap<Integer, SimulationResult>();
	// private HashMap<XYSeries, Integer> series2id = new HashMap<XYSeries,
	// Integer>();

	private XYLineAndShapeRenderer renderer;
	private XYLineAndShapeRenderer renderer2;

	private LegendTitle legend;

	private boolean lockUpdate = false;

	private static int TOKEN = SimulationResultController.SIM_TOKEN;
	private static int ACTUAL_FIRING_SPEED = SimulationResultController.SIM_ACTUAL_FIRING_SPEED;
	public static int FIRE = SimulationResultController.SIM_FIRE;
	public static int SUM_OF_TOKEN = SimulationResultController.SIM_SUM_OF_TOKEN;
	public static int ACTUAL_TOKEN_FLOW = SimulationResultController.SIM_ACTUAL_TOKEN_FLOW;

	public ParallelCoordinatesPlot() {

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

	/**
	 * This method retrieves the micro array data from the biological node
	 * abstracts. On first call, the GUI is created using these values.
	 */
	public void revalidateView() {

		// System.out.println("revalditate");
		graphInstance = new GraphInstance();
		pw = graphInstance.getPathway();

		SimulationResult simRes = pw.getPetriNet().getSimResController().getLastActive();

		if (pw.isPetriNet() && pw.getPetriNet().isPetriNetSimulation() && simRes != null) {
			// System.out.println(main.getComponentCount());
			// most of the time unnecessary to remove all
			main.removeAll();
			// System.out.println(main.getComponentCount());
			w = MainWindow.getInstance();

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
			slider.setToolTipText("Time: 0");

			controlPanel = new JPanel(new MigLayout());
			JPanel controlPanel2 = new JPanel(new MigLayout());

			petriNetAnimationButton.setBackground(Color.GREEN);
			petriNetStopAnimationButton.setBackground(Color.RED);
			resetPetriNet.setBackground(Color.white);
			petriNetStopAnimationButton.setEnabled(false);

			controlPanel.add(zoomGraph);
			controlPanel.add(showTable, "align left");
			controlPanel.add(new JLabel(""), "align left,wrap 10, growx");

			controlPanel.add(new JSeparator(), "span, wrap 10, growx");

			controlPanel.add(new JLabel("Animation Start:"), "align left");
			controlPanel.add(animationStart, "align left,wrap 10, growx");

			controlPanel.add(new JLabel("Animation Stop:"), "align left");
			controlPanel.add(animationStop, "align left,wrap 10, growx");

			controlPanel.add(new JLabel("Animation Speed x:"), "align left");
			controlPanel.add(animationSpeed, "align left,wrap 10, growx");

			controlPanel.add(new JLabel("Animation Colour:"), "align left");
			controlPanel.add(animationColour, "align left,wrap 10, growx");

			controlPanel2.add(petriNetAnimationButton, "align left");
			controlPanel2.add(petriNetStopAnimationButton, "align left");
			controlPanel2.add(resetPetriNet, "align left");

			controlPanel.add(controlPanel2, "span, wrap,growx");

			controlPanel.add(slider, "span");
			controlPanel.add(stepLabel, "align left,wrap, growx");

			main.add(new JLabel("Petri Net Simulation Result Plots for Places within the Network"), "span, wrap 10, growx, align center");
			main.add(pane, "span, wrap");
			main.add(controlPanel, "gap 10, wrap 15, growx");
			pane.setPreferredSize(new Dimension(400, 200));
			// main.add(mainPanel);
			main.setVisible(true);

			drawPlot();
		} else {
			removeAllElements();
		}

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
		int pickedV = GraphInstance.getMyGraph().getVisualizationViewer().getPickedVertexState().getPicked().size();
		int pickedE = GraphInstance.getMyGraph().getVisualizationViewer().getPickedEdgeState().getPicked().size();

		if (pickedV == 1) {
			bna = GraphInstance.getMyGraph().getVisualizationViewer().getPickedVertexState().getPicked().iterator().next();
		}

		for (int i = 0; i < seriesListR1.size(); i++) {
			renderer.setSeriesVisible(i, false);
		}

		for (int i = 0; i < seriesListR2.size(); i++) {
			renderer2.setSeriesVisible(i, false);
		}
		SimulationResult simRes = pw.getPetriNet().getSimResController().getLastActive();
		boolean secondAxis = false;
		if (pickedV == 0 && pickedE == 1) {
			BiologicalEdgeAbstract bea = GraphInstance.getMyGraph().getVisualizationViewer().getPickedEdgeState().getPicked().iterator().next();
			if (bea instanceof PNEdge) {
				secondAxis = true;
				PNEdge edge = (PNEdge) bea;
				List<SimulationResult> listActive = pw.getPetriNet().getSimResController().getAllActiveWithData(edge, ACTUAL_TOKEN_FLOW);
				SimulationResult result;

				// System.out.println(edge.getID());
				// if (simRes.contains(edge, SUM_OF_TOKEN) &&
				// simRes.contains(edge, ACTUAL_TOKEN_FLOW) && simRes.get(edge,
				// SUM_OF_TOKEN).size() > 0) {
				for (int i = 0; i < listActive.size(); i++) {
					result = listActive.get(i);
					int idxFlow = series2idx.get(edge, ACTUAL_TOKEN_FLOW, result.getId());
					int idxSum = series2idx.get(edge, SUM_OF_TOKEN, result.getId());

					Stroke dash1 = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f, new float[] { 2.0f, 6 }, 0.0f);
					renderer.setSeriesStroke(idxFlow, dash1);
					// System.out.println("stroke set");
					Color c = Color.getHSBColor(i * 1.0f / (listActive.size()), 1, 1);
					renderer.setSeriesPaint(idxFlow, c);
					renderer2.setSeriesPaint(idxSum, c);

					renderer.setSeriesVisible(idxFlow, true);
					renderer2.setSeriesVisible(idxSum, true);
				}

				// }
			}
		} else if (pickedV == 1 && pickedE == 0) {
			//System.out.println("one picked");
			SimulationResult result;
			List<SimulationResult> listActive = null;
			if(bna instanceof Place){
				if(bna.hasRef() && bna.getRef() instanceof Place){
					bna = bna.getRef();
				}
				listActive = pw.getPetriNet().getSimResController().getAllActiveWithData(bna, TOKEN);
				//System.out.println(listActive.size());
			}else if (bna instanceof Transition){
				listActive = pw.getPetriNet().getSimResController().getAllActiveWithData(bna, ACTUAL_FIRING_SPEED);
			}
			for (int i = 0; i < listActive.size(); i++) {
				result = listActive.get(i);
				int idx = 0;
				if(bna instanceof Place){
					idx = series2idx.get(bna, TOKEN, result.getId());
				}else if (bna instanceof Transition){
					idx = series2idx.get(bna, ACTUAL_FIRING_SPEED, result.getId());
				}
				
				renderer.setSeriesVisible(idx, true);
				// Stroke dash1 = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE,
				// BasicStroke.JOIN_MITER, 1.0f, new float[] { 2.0f, i * 2 },
				// 0.0f);
				// renderer.setSeriesStroke(idx, dash1);
				// System.out.println("stroke set");
				Color c = Color.getHSBColor(i * 1.0f / (listActive.size()), 1, 1);
				renderer.setSeriesPaint(idx, c);
			}

		} else {

			boolean onlyT = true;
			if (GraphInstance.getMyGraph().getVisualizationViewer().getPickedVertexState().getPicked().size() > 0) {
				Iterator<BiologicalNodeAbstract> it = GraphInstance.getMyGraph().getVisualizationViewer().getPickedVertexState().getPicked()
						.iterator();
				while (onlyT && it.hasNext()) {
					if (it.next() instanceof Place) {
						onlyT = false;
					}
				}
			} else {
				onlyT = false;
			}

			Iterator<BiologicalNodeAbstract> iterator = null;
			if (pickedV > 0) {
				iterator = GraphInstance.getMyGraph().getVisualizationViewer().getPickedVertexState().getPicked().iterator();
				// System.out.println("picked");
			} else {
				iterator = GraphInstance.getMyGraph().getAllVertices().iterator();
			}

			// int j = 0;
			while (iterator.hasNext()) {
				bna = iterator.next();

				if (bna instanceof Place) {
					place = (Place) bna;
					// System.out.println(place.getPetriNetSimulationData().size());
					if (place.hasRef() && place.getRef() instanceof Place) {
						place = (Place) place.getRef();
					}
					if (this.series2idx.contains(place, TOKEN)) {
						// System.out.println("set stroke back");
						renderer.setSeriesStroke(series2idx.get(place, TOKEN, simRes.getId()), new BasicStroke(1));
						renderer.setSeriesPaint(series2idx.get(place, TOKEN, simRes.getId()), place.getPlotColor());
						renderer.setSeriesVisible((int) series2idx.get(place, TOKEN, simRes.getId()), true);
					}
				} else if (bna instanceof Transition && onlyT) {
					transition = (Transition) bna;
					if (transition.hasRef() && transition.getRef() instanceof Transition) {
						transition = (Transition) transition.getRef();
					}
					if (series2idx.contains(transition, ACTUAL_FIRING_SPEED)) {
						renderer.setSeriesVisible((int) series2idx.get(transition, ACTUAL_FIRING_SPEED, simRes.getId()), true);
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
			NumberAxis na = new NumberAxis("Tokens");

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

		pane.requestFocus();
		chart.fireChartChanged();

	}

	public void updateDateCurrentSimulation() {
		if (pw.getPetriNet().getSimResController().getLastActive() != null) {
			this.updateData(pw.getPetriNet().getSimResController().getLastActive().getId());
		}
	}

	public void updateData(String simId) {
		// System.out.println("update data begin");
		SimulationResult simRes = pw.getPetriNet().getSimResController().get(simId);

		Set<GraphElementAbstract> keys = series2idx.getKeys();
		Iterator<GraphElementAbstract> itKeys = keys.iterator();
		// System.out.println("-------------------keys----------------------");
		while (itKeys.hasNext()) {
			GraphElementAbstract gea = itKeys.next();
			if (gea instanceof BiologicalNodeAbstract) {
				// System.out.println(gea + ":"+ gea.getName());
			} else if (gea instanceof BiologicalEdgeAbstract) {
				// System.out.println(gea+":"+((BiologicalEdgeAbstract)
				// gea).getFrom().getName()+" -> "+((BiologicalEdgeAbstract)
				// gea).getTo().getName());
			}

		}

		// System.out.println("-----------------------------");
		if (pw.isPetriNet() && pw.getPetriNet().isPetriNetSimulation() && simRes != null) {

			BiologicalNodeAbstract bna;
			BiologicalEdgeAbstract bea;
			PNEdge edge;
			XYSeries series;
			XYSeries series2;
			Place place;
			Double value;
			Transition transition;
			int stop;
			int steps = 0;

			rowsDim = simRes.getTime().size();

			Iterator<BiologicalNodeAbstract> itBna = pw.getAllGraphNodes().iterator();
			List<Double> time = simRes.getTimeValues();// pw.getPetriNet().getTime();
			while (itBna.hasNext()) {
				bna = itBna.next();
				// System.out.println(series.getItemCount());
				if (bna instanceof Place && !bna.hasRef()) {
					place = (Place) bna;
					//System.out.println(simRes.contains(place, TOKEN));
					if (simRes.contains(place, TOKEN) && simRes.get(place, TOKEN).size() > 0) {
						//System.out.println(place + " " + TOKEN + " " + simRes.getId());
						series = this.seriesListR1.get(series2idx.get(place, TOKEN, simRes.getId()));
						stop = Math.min(simRes.get(place, TOKEN).size(), time.size());
						steps = stop - Math.min(series.getItemCount(), series.getItemCount());
						if (stop > 0) {
							for (int i = series.getItemCount(); i < stop; i++) {

								if (simRes.get(place, TOKEN).size() > i) {
									value = simRes.get(place, TOKEN).get(i);
								} else {
									value = 0.0;
								}
								series.add(simRes.getTime().get(i), value, false);
							}
							series.fireSeriesChanged();
						}
					}
				} else if (bna instanceof Transition) {

					transition = (Transition) bna;
					if (simRes.contains(transition, ACTUAL_FIRING_SPEED) && simRes.get(transition, ACTUAL_FIRING_SPEED).size() > 0) {
						series = this.seriesListR1.get(series2idx.get(transition, ACTUAL_FIRING_SPEED, simRes.getId()));
						stop = Math.min(simRes.get(transition, ACTUAL_FIRING_SPEED).size(), time.size());
						if (stop > 0) {
							for (int i = series.getItemCount(); i < stop; i++) {

								value = simRes.get(transition, ACTUAL_FIRING_SPEED).get(i);
								series.add(simRes.getTime().get(i), value, false);
							}
							series.fireSeriesChanged();
						}
					}
				}

			}
			Iterator<BiologicalEdgeAbstract> itBea = pw.getAllEdges().iterator();
			while (itBea.hasNext()) {
				bea = itBea.next();

				if (bea instanceof PNEdge) {
					edge = (PNEdge) bea;
					if (simRes.contains(edge)) {
						series = this.seriesListR1.get(series2idx.get(edge, ACTUAL_TOKEN_FLOW, simRes.getId()));
						series2 = this.seriesListR2.get(series2idx.get(edge, SUM_OF_TOKEN, simRes.getId()));
						if (simRes.contains(edge, SUM_OF_TOKEN) && simRes.get(edge, SUM_OF_TOKEN).size() > 0
								&& simRes.get(edge, ACTUAL_TOKEN_FLOW).size() > 0) {
							stop = Math.min(Math.min(simRes.get(edge, ACTUAL_TOKEN_FLOW).size(), simRes.get(edge, SUM_OF_TOKEN).size()), time.size());
							steps = stop - Math.min(series.getItemCount(), series2.getItemCount());
							if (steps > 0) {
								for (int i = Math.min(series.getItemCount(), series2.getItemCount()); i < stop; i++) {

									value = simRes.get(edge, ACTUAL_TOKEN_FLOW).get(i);
									if (series == null) {
										System.out.println(edge.getFrom().getName() + " => " + edge.getTo().getName());
									}
									if (series2 == null) {
										System.out.println("null: " + edge.getFrom().getName() + " -> " + edge.getTo().getName());
									}
									series.add(simRes.getTime().get(i), value, false);
									series2.add(simRes.getTime().get(i), simRes.get(edge, SUM_OF_TOKEN).get(i), false);
								}
								series.fireSeriesChanged();
								series2.fireSeriesChanged();
							}
						}
					}
				}
			}
			if (chart != null) {
				// System.out.println("chart fired changed");
				chart.fireChartChanged();
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
			// System.out.println(panel.getPreferredSize());
			f.add(panel);
			f.pack();
			f.setVisible(true);

		} else if ("reset".equals(event)) {
			for (Iterator<BiologicalNodeAbstract> i = pw.getAllGraphNodes().iterator(); i.hasNext();) {
				BiologicalNodeAbstract bna = (BiologicalNodeAbstract) i.next();
				if (bna instanceof Place) {
					bna.rebuildShape(new VertexShapes());
					bna.setColor(Color.WHITE);
				}
			}
		} else if ("show".equals(event)) {
			table = new MyTable();
			table.setModel(this.getTableModel());
			table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			table.setColumnControlVisible(false);
			table.setHighlighters(HighlighterFactory.createSimpleStriping());
			table.setFillsViewportHeight(true);
			table.addHighlighter(new ColorHighlighter(new Color(192, 215, 227), Color.BLACK));
			table.setHorizontalScrollEnabled(true);
			table.getTableHeader().setReorderingAllowed(true);
			table.getTableHeader().setResizingAllowed(true);
			table.getColumn("Label").setPreferredWidth(100);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

			JScrollPane sp = new JScrollPane(table);
			sp.setPreferredSize(new Dimension(600, 200));

			MigLayout layout2 = new MigLayout();
			dialogPanel = new JPanel(layout2);
			dialogPanel.add(new JLabel("Results for each Timestep t and for all Places:"), "span 2");
			dialogPanel.add(new JSeparator(), "gap 10, wrap 15, growx");
			dialogPanel.add(sp, "span 4, growx, wrap");

			exportSimResult = new JButton("Export Simulation Result");
			exportSimResult.setActionCommand("exportSimResult");
			exportSimResult.addActionListener(this);

			dialogPanel.add(exportSimResult, "wrap");

			dialogPanel.add(new JSeparator(), "span, growx, wrap 15, gaptop 10");

			// draw a new plot according to the current time step selection

			PlotsPanel pp = new PlotsPanel();
			dialogPanel.add(pp, "wrap");
			JButton button = new JButton("Save Results");
			button.addActionListener(pp);

			dialogPanel.add(button);

			// System.out.println("show");
			// show table containing all data
			dialog = new JFrame("Simulation results");
			dialog.setTitle("Simulation Results");
			dialog.setResizable(true);
			dialog.setContentPane(dialogPanel);
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			ScreenSize screen = new ScreenSize();
			int screenHeight = (int) screen.getheight();
			int screenWidth = (int) screen.getwidth();
			// dialog.setAlwaysOnTop(false);
			dialog.pack();
			// dialog.setLocation((screenWidth / 2) - dialog.getSize().width /
			// 2,
			// (screenHeight / 2) - dialog.getSize().height / 2);
			dialog.setVisible(true);
			dialog.setLocationRelativeTo(w);
		} else if (event.equals("animatePetriNet")) {
			// redraw plot and set new colors/sizes

			petriNetStopAnimationButton.setText("Stop");
			petriNetStopAnimationButton.setBackground(Color.RED);
			petriNetStopAnimationButton.setEnabled(true);
			petriNetStopAnimationButton.revalidate();
			resetPetriNet.setEnabled(false);
			thread = new AnimationThread(slider, (Integer) animationStart.getValue(), (Integer) animationStop.getValue(),
					animationColour.isSelected(), (Integer) animationSpeed.getValue(), petriNetAnimationButton, petriNetStopAnimationButton);
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

						thread = new AnimationThread(slider, animationThreadStep, (Integer) animationStop.getValue(), animationColour.isSelected(),
								(Integer) animationSpeed.getValue(), petriNetAnimationButton, petriNetStopAnimationButton);
						thread.start();
					}
				}
			}
		} else if (event.equals("exportSimResult")) {
			new SaveDialog(SaveDialog.FORMAT_CSV);

			// System.out.println("click");
		}
		// TODO actions fuer T-Inv. und P-Inv. Test
		/*
		 * else if (event.equals("testP")) { System.out.println("TESTEN");
		 * Set<BiologicalNodeAbstract> hsVertex = new
		 * HashSet<BiologicalNodeAbstract>(); hsVertex =
		 * graphInstance.getPathway().getAllNodes();//
		 * GraphInstance.getMyGraph().getAllvertices();
		 * Set<BiologicalEdgeAbstract> hsEdge = new
		 * HashSet<BiologicalEdgeAbstract>(); hsEdge =
		 * graphInstance.getPathway().getAllEdges(); Iterator hsit =
		 * hsVertex.iterator(); BiologicalNodeAbstract bna; Place p;
		 * HashMap<String, Integer> hmplaces = new HashMap<String, Integer>();
		 * HashMap<String, Integer> hmtransitions = new HashMap<String,
		 * Integer>(); int numberPlaces = 0; int numberTransitions = 0;
		 * ArrayList<String> names = new ArrayList<String>(); while
		 * (hsit.hasNext()) { bna = (BiologicalNodeAbstract) hsit.next(); if
		 * (bna instanceof Transition) {
		 * hmtransitions.put(bna.getVertex().toString(), new Integer(
		 * numberTransitions)); numberTransitions++; } else { p = (Place) bna;
		 * hmplaces.put(bna.getVertex().toString(), new Integer( numberPlaces));
		 * names.add(p.getName()); numberPlaces++; } } double[][] f =
		 * this.initArray(numberPlaces, numberTransitions); double[][] b =
		 * this.initArray(numberPlaces, numberTransitions); // einkommende
		 * Kanten (backward matrix) Iterator edgeit = hsEdge.iterator(); PNEdge
		 * edge; Pair pair; while (edgeit.hasNext()) { edge = (PNEdge)
		 * edgeit.next(); pair = edge.getEdge().getEndpoints(); // T->P if
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
		 * double[] vd = new double[names.size()]; HashMap<String, Double>
		 * values = new HashMap<String, Double>();
		 *
		 *
		 * //this.t.getColumn(0);
		 * //System.out.println(t.getModel().getValueAt(1, 1));
		 *
		 * for (int i = 0; i < names.size(); i++) {
		 * values.put(t.getModel().getValueAt(i, 0).toString(),
		 * Double.parseDouble(t.getModel().getValueAt(i, 1).toString())); } for
		 * (int i = 0; i < names.size(); i++) { vd[i] =
		 * values.get(names.get(i)); }
		 *
		 * DenseDoubleMatrix2D c = new DenseDoubleMatrix2D(cMatrix.getData());
		 * DenseDoubleMatrix1D v = new DenseDoubleMatrix1D(vd);
		 * DenseDoubleMatrix1D x = new DenseDoubleMatrix1D(5); c.zMult(v, x, 1,
		 * 0, false); // System.out.println(x); IntArrayList l = new
		 * IntArrayList(); x.getNonZeros(l, null); //
		 * System.out.println(l.size()); if (l.size() == 0) {
		 * System.out.println("ist Invariante"); } else { System.out.println(
		 * "ist keine Invariante"); }
		 *
		 * for (int i = 0; i < this.r.length; i++) { //
		 * System.out.println(r[i][1]); } }
		 */
	}

	/**
	 * Handles changes of the time step slider. Calculates new size and color
	 * for each biological node apparent in the pathway (i.e. not being a
	 * reference).
	 */
	public void stateChanged(ChangeEvent e) {
		if (pw.isPetriNet() && pw.getPetriNet().isPetriNetSimulation()) {
			if (e.getSource().equals(animationStart))
				animationStartInit = (Integer) animationStart.getValue();
			else if (e.getSource().equals(animationStop))
				animationStopInit = (Integer) animationStop.getValue();
			else if (e.getSource().equals(animationSpeed))
				animationSpeedInit = (Integer) animationSpeed.getValue();
			else {
				SimulationResult simRes = pw.getPetriNet().getSimResController().getLastActive();
				pw.getPetriNet().setCurrentTimeStep(this.slider.getValue());
				slider.setToolTipText("Time: " + this.slider.getValue());
				if (simRes == null) {
					graphInstance.getPathway().getPetriNet().setPetriNetSimulation(false);
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
					stepLabel.setText("Time: " + (double) Math.round((step * 100)) / 100);

					XYPlot plot = (XYPlot) chart.getPlot();
					plot.removeDomainMarker(marker);

					marker = new ValueMarker(step); // position is the value on
													// the
													// axis
					marker.setPaint(Color.black);
					// marker.setLabel("here"); // see JavaDoc for labels,
					// colors,
					// strokes

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
					graphInstance = new GraphInstance();
					pw = graphInstance.getPathway();
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
							// System.out.println(bna.getName());

							ref = 1.0;
							if (simRes.contains(bna) && slider.getValue() >= 0) {
								// ref = Math.abs(ref);
								if (bna instanceof Place) {
									ref = simRes.get(bna, TOKEN).get(slider.getValue());
									((Place) bna).setToken(ref);
								} else if (bna instanceof Transition) {
									ref = simRes.get(bna, FIRE).get(slider.getValue());
									if (ref == 1) {
										((Transition) bna).setSimulationActive(true);
										// System.out.println("aktiv");
									} else {
										((Transition) bna).setSimulationActive(false);
										// System.out.println("inaktiv");
									}
								}

								if (slider.getValue() >= 2) {
									// ggf TODO
									val = 1.0;
									// val = bna.getMicroArrayValue(slider
									// .getValue() - 1);

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

									// System.out.println(ref);
									// prepare size modification
									// v.setUserDatum("madata", 0.4,
									// UserData.SHARED);

									// NodeRankingVertexSizeFunction sf = new
									// NodeRankingVertexSizeFunction(
									// "madata", ref2);

									VertexShapes vs = new VertexShapes(ref2, 1.0f);

									// apply change in color and size only if
									// current
									// node is
									// not a
									// reference
									bna.rebuildShape(vs);
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
		graphInstance = new GraphInstance();
		pw = graphInstance.getPathway();

		if (!pw.isPetriNet() || !pw.hasGotAtLeastOneElement()) {
			return;
		}
		//System.out.println("init Graphs");
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

		Iterator<String> it = pw.getPetriNet().getSimResController().getSimIds().iterator();
		String simId;
		while (it.hasNext()) {
			simId = it.next();
			addSimulationToChart(simId);
			updateData(simId);
		}

		renderer.setToolTipGenerator(new XYToolTipGenerator() {

			@Override
			public String generateToolTip(XYDataset arg0, int seriesIdx, int arg2) {
				int pickedV = GraphInstance.getMyGraph().getVisualizationViewer().getPickedVertexState().getPicked().size();
				int pickedE = GraphInstance.getMyGraph().getVisualizationViewer().getPickedEdgeState().getPicked().size();

				if (pickedV == 1 && pickedE == 0) {
					return labelsR1.get(seriesIdx) + "(" + idx2simR1.get(seriesIdx).getName() + ")";
				}
				return labelsR1.get(seriesIdx);
			}
		});

		renderer.setBaseItemLabelsVisible(true);
		renderer.setLegendItemLabelGenerator(new XYSeriesLabelGenerator() {

			@Override
			public String generateLabel(XYDataset arg0, int seriesIdx) {
				int pickedV = GraphInstance.getMyGraph().getVisualizationViewer().getPickedVertexState().getPicked().size();
				int pickedE = GraphInstance.getMyGraph().getVisualizationViewer().getPickedEdgeState().getPicked().size();

				if (pickedV == 1 && pickedE == 0) {
					BiologicalNodeAbstract bna = GraphInstance.getMyGraph().getVisualizationViewer().getPickedVertexState().getPicked().iterator().next();
					if(pw.getPetriNet().getSimResController().getAllActiveWithData(bna, TOKEN).size() <=1){
						return bna.getName();
					}
					return idx2simR1.get(seriesIdx).getName();
				}
				if (pickedV == 0 && pickedE == 1) {
					BiologicalEdgeAbstract bea = GraphInstance.getMyGraph().getVisualizationViewer().getPickedEdgeState().getPicked().iterator().next();
					if(pw.getPetriNet().getSimResController().getAllActiveWithData(bea, SUM_OF_TOKEN).size() <=1){
						return labelsR1.get(seriesIdx);
					}
					return idx2simR1.get(seriesIdx).getName()+"("+labelsR1.get(seriesIdx)+")";
				}
				if(labelsR1.size() > seriesIdx){
					return labelsR1.get(seriesIdx);
				}
				return "";
			}
		});

		renderer2.setLegendItemLabelGenerator(new XYSeriesLabelGenerator() {

			@Override
			public String generateLabel(XYDataset arg0, int seriesIdx) {
				int pickedV = GraphInstance.getMyGraph().getVisualizationViewer().getPickedVertexState().getPicked().size();
				int pickedE = GraphInstance.getMyGraph().getVisualizationViewer().getPickedEdgeState().getPicked().size();
				if (pickedV == 0 && pickedE == 1) {
					BiologicalEdgeAbstract bea = GraphInstance.getMyGraph().getVisualizationViewer().getPickedEdgeState().getPicked().iterator().next();
					if(pw.getPetriNet().getSimResController().getAllActiveWithData(bea, SUM_OF_TOKEN).size() <=1){
						return "Sum";
					}
					return idx2simR2.get(seriesIdx).getName() + "(Sum)";
				}
				return "";
			}
		});

		chart = ChartFactory.createXYLineChart("", "Time", "Token", dataset, PlotOrientation.VERTICAL, true, true, false);
		// set rendering options: all lines in black, domain steps as integers
		final XYPlot plot = chart.getXYPlot();

		// draw plot for Places with random colors

		plot.setDataset(0, dataset);
		plot.setRenderer(0, renderer);

		plot.setDataset(1, dataset2);
		plot.setRenderer(1, renderer2);

		// add chart to pane and refresh GUI

		pane = new ChartPanel(chart);
		// pane.setPreferredSize(new java.awt.Dimension(320, 200));

		pane.addChartMouseListener(new ChartMouseListener() {
			@Override
			public void chartMouseClicked(ChartMouseEvent event) {
				if (event.getEntity() != null && event.getEntity() instanceof XYItemEntity) {

					int pickedV = GraphInstance.getMyGraph().getVisualizationViewer().getPickedVertexState().getPicked().size();
					int pickedE = GraphInstance.getMyGraph().getVisualizationViewer().getPickedEdgeState().getPicked().size();
					// System.out.println(pickedV);
					if (pickedE == 0 && pickedV == 0) {

						XYItemEntity entity = (XYItemEntity) event.getEntity();
						//System.out.println(entity.getSeriesIndex());
						BiologicalNodeAbstract p = places.get(entity.getSeriesIndex());
						PickedState<BiologicalNodeAbstract> ps = GraphInstance.getMyGraph().getVisualizationViewer().getPickedVertexState();
						// ps.clearPickedVertices();
						ps.clear();
						ps.pick(p, true);
					}
				}
			}

			@Override
			public void chartMouseMoved(ChartMouseEvent e) {
				// TODO Auto-generated method stub

			}
		});
		this.legend = pane.getChart().getLegend();

		// p.add(pane, BorderLayout.CENTER);

		// p.setVisible(true);
		this.revalidateView();
	}

	private void addSimulationToChart(String simId) {
		Place place;
		Transition transition;

		BiologicalNodeAbstract bna;
		BiologicalEdgeAbstract bea;
		if (pw == null) {
			return;
		}
		Iterator<BiologicalNodeAbstract> itNodes = pw.getAllGraphNodes().iterator();
		XYSeries s;
		SimulationResultController simResController = pw.getPetriNet().getSimResController();
		//System.out.println("resultssize: " + pw.getPetriNet().getSimResController().getSimNames().size());
		if (pw.getPetriNet().getSimResController().get(simId) == null) {
			System.out.println("PCPError, no such simulation name");
			return;
		}
		while (itNodes.hasNext()) {
			bna = itNodes.next();
			if (!bna.hasRef()) {
				if (bna instanceof Place) {
					place = (Place) bna;
					// if
					// (pw.getPetriNet().getSimResController().get(simName).contains(place,
					// TOKEN)) {
					// if (place.getPetriNetSimulationData().size() > 0) {
					places.add(place);
					s = new XYSeries(r1Count);
					//System.out.println("put: " + place +" "+simId+" "+r1Count);
					series2idx.put(place, TOKEN, simId, r1Count);
					idx2simR1.put(r1Count, simResController.get(simId));
					// System.out.println(r1Count+" "+place.getName()+" ");
					seriesListR1.add(s);

					// System.out.println(System.identityHashCode(place.getPetriNetSimulationData())
					// + " added");
					dataset.addSeries(s);

					labelsR1.add(place.getName());// rows[indices.get(j)][0].toString());

					renderer.setSeriesPaint(r1Count, place.getPlotColor());

					renderer.setSeriesItemLabelsVisible(r1Count, true);
					renderer.setSeriesShapesVisible(r1Count, false);
					r1Count++;
					// }
					// }
				} else if (bna instanceof Transition) {
					transition = (Transition) bna;
					// if (transition.getPetriNetSimulationData().size() > 0) {
					places.add(transition);
					s = new XYSeries(r1Count);
					series2idx.put(transition, ACTUAL_FIRING_SPEED, simId, r1Count);
					idx2simR1.put(r1Count, simResController.get(simId));
					// System.out.println(r1Count+" "+transition.getName()+" ");
					// seriesList.add(new XYSeries(j));
					// series2id.put(s, count);
					seriesListR1.add(s);
					dataset.addSeries(s);
					labelsR1.add(transition.getName());// rows[indices.get(j)][0].toString());

					renderer.setSeriesPaint(r1Count, Color.BLACK);

					renderer.setSeriesItemLabelsVisible(r1Count, true);
					renderer.setSeriesShapesVisible(r1Count, false);
					renderer.setSeriesVisible(r1Count, false);
					r1Count++;
					// }
				}
			}
		}

		Iterator<BiologicalEdgeAbstract> itEdges = pw.getAllEdges().iterator();
		while (itEdges.hasNext()) {
			bea = itEdges.next();

			if (bea instanceof PNEdge) {
				PNEdge edge = (PNEdge) bea;
				places.add(edge.getFrom());
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
				//System.out.println(r2Count + " edgeF:" + edge.getFrom().getName() + " ");
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
	}

	private RegulationTabelModel getTableModel() {

		BiologicalNodeAbstract bna;
		SimulationResult simRes = pw.getPetriNet().getSimResController().getLastActive();

		rowsSize = pw.getPetriNet().getPlaces();

		rowsDim = simRes.getTime().size();
		// get Data from all Places
		Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();
		rows = new Object[rowsSize][rowsDim + 1];
		int i = 0;

		while (it.hasNext()) {
			bna = it.next();
			if (bna instanceof Place) {
				rows[i][0] = bna.getName();
				for (int j = 1; j <= rowsDim; j++) {
					if (simRes.contains(bna, TOKEN) && simRes.get(bna, TOKEN).size() > j - 1) {
						rows[i][j] = Math.max(0, simRes.get(bna, TOKEN).get(j - 1));
					} else {
						rows[i][j] = "-";
					}
				}
				i++;
			}
		}
		// create column labels for table view
		String columNames[] = new String[rowsDim + 1];
		// String selectorValues[] = new String[rowsSize];
		columNames[0] = "Label";
		for (i = 0; i < rowsDim; i++) {
			columNames[i + 1] = "t=" + simRes.getTime().get(i);
		}
		return new RegulationTabelModel(rows, columNames);
	}
}
