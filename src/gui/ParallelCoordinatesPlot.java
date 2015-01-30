package gui;

/*import edu.uci.ics.jung.graph.Vertex;
 import edu.uci.ics.jung.graph.decorators.ConstantVertexAspectRatioFunction;
 import edu.uci.ics.jung.utils.Pair;
 import edu.uci.ics.jung.utils.UserData;
 import edu.uci.ics.jung.visualization.PickedState;*/
import edu.uci.ics.jung.visualization.picking.PickedState;
import graph.GraphInstance;
import graph.animations.RegulationTabelModel;
import graph.jung.graphDrawing.VertexShapes;
import gui.algorithms.ScreenSize;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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

import miscalleanous.tables.MyTable;
import net.miginfocom.swing.MigLayout;

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

import petriNet.AnimationThread;
import petriNet.PNEdge;
import petriNet.Place;
import petriNet.PlotsPanel;
import petriNet.Transition;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

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
	//private Hashtable<Integer, BiologicalNodeAbstract> nodeTabel = new Hashtable<Integer, BiologicalNodeAbstract>();
	// instance of main window
	MainWindow w;

	// create GUI components
	private JButton showTable = new JButton("show detailed simulation results");
	//private JButton drawPlots = new JButton("show all animation result plots");

	private JButton drawPCP = new JButton("Draw Timeseries");
	private JLabel stepLabel = new JLabel("Step 0");
	private JSlider slider = new JSlider();
	private JPanel mainPanel;
	private JPanel dialogPanel;
	private JPanel p;
	private JPanel main = new JPanel();
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

	// private JPanel invariants = new JPanel();

	// An Object to store microarray data
	private Object[][] rows;
	// number of Places
	private int rowsSize;
	// size of Vector in each Place
	private int rowsDim;

	private ArrayList<String> labels = new ArrayList<String>();

	private int animationStartInit = 0;

	private int animationStopInit = 1;

	private int animationSpeedInit = 20;

	// private ArrayList<Place> plotEntities = new ArrayList<Place>();

	private ArrayList<BiologicalNodeAbstract> places;

	private ArrayList<XYSeries> seriesListR1 = new ArrayList<XYSeries>();
	private ArrayList<XYSeries> seriesListR2 = new ArrayList<XYSeries>();

	private HashMap<Integer, Integer> vector2idx = new HashMap<Integer, Integer>();

	// private HashMap<XYSeries, Integer> series2id = new HashMap<XYSeries,
	// Integer>();

	private XYLineAndShapeRenderer renderer;
	private XYLineAndShapeRenderer renderer2;

	private LegendTitle legend;

	private boolean lockUpdate = false;

	// private HashMap<XYSeries, BiologicalNodeAbstract> series2Edge = new
	// HashMap<XYSeries, BiologicalNodeAbstract>();
	public ParallelCoordinatesPlot() {

		petriNetAnimationButton.addActionListener(this);
		petriNetAnimationButton.setActionCommand("animatePetriNet");

		petriNetStopAnimationButton.addActionListener(this);
		petriNetStopAnimationButton.setActionCommand("animatePetriNetStop");

		resetPetriNet.addActionListener(this);
		resetPetriNet.setActionCommand("reset");

		drawPCP.addActionListener(this);
		drawPCP.setActionCommand("drawplot");
		p = new JPanel();

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

		if (pw.isPetriNet() && pw.isPetriNetSimulation()) {

			main.removeAll();
			w = MainWindowSingleton.getInstance();

			// get pathway and nodes

			// build GUI

			rowsDim = pw.getPetriNet().getTime().size();// getPlaces();
			MigLayout layout = new MigLayout();
			// System.out.println("an: "+animationStartInit);
			// System.out.println("rows: "+rowsDim);
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

			SpinnerModel modelSpeed = new SpinnerNumberModel(
					animationSpeedInit, // initial
										// value
					0, // min
					20, // max
					1); // step

			animationSpeed = new JSpinner(modelSpeed);
			animationSpeed.addChangeListener(this);

			animationColour.setSelected(false);

			mainPanel = new JPanel(layout);
			showTable = new JButton("show detailed simulation results");
			showTable.addActionListener(this);
			showTable.setActionCommand("show");
			slider.setMinimum(0);
			slider.setMaximum(rowsDim - 1);
			slider.setMajorTickSpacing(1);
			slider.addChangeListener(this);
			slider.setToolTipText("Time: 0");

			MigLayout migLayout2 = new MigLayout();
			controlPanel = new JPanel(migLayout2);

			MigLayout migLayout3 = new MigLayout();
			JPanel controlPanel2 = new JPanel(migLayout3);

			petriNetAnimationButton.setBackground(Color.GREEN);
			petriNetStopAnimationButton.setBackground(Color.RED);
			resetPetriNet.setBackground(Color.white);
			petriNetStopAnimationButton.setEnabled(false);

			controlPanel.add(new JSeparator(), "span, wrap 10, growx");

			controlPanel.add(new JLabel("Animation Start:"), "align left");
			controlPanel.add(animationStart, "align left,wrap 10, growx");

			controlPanel.add(new JLabel("Animation Stop:"), "align left");
			controlPanel.add(animationStop, "align left,wrap 10, growx");

			controlPanel.add(new JLabel("Animation Speed x:"), "align left");
			controlPanel.add(animationSpeed, "align left,wrap 10, growx");

			controlPanel.add(new JLabel("Animation Colour:"), "align left");
			controlPanel.add(animationColour, "align left,wrap 10, growx");

			controlPanel.add(showTable, "align left");
			controlPanel.add(new JLabel(""), "align left,wrap 10, growx");

			controlPanel2.add(petriNetAnimationButton, "align left");
			controlPanel2.add(petriNetStopAnimationButton, "align left");
			controlPanel2.add(resetPetriNet, "align left");

			controlPanel.add(controlPanel2, "span, wrap,growx");
			controlPanel.add(new JSeparator(), "span, wrap 10, growx");
			controlPanel.add(slider, "align left");
			controlPanel.add(stepLabel, "align left,wrap, growx");

			mainPanel
					.add(new JLabel(
							"Petri Net Simulation Result Plots for Places within the Network"),
							"span, wrap 10, growx, align center");
			mainPanel.add(p, "span");
			mainPanel.add(controlPanel, "gap 10, wrap 15, growx");

			main.add(mainPanel);
			main.setVisible(true);

			// create dialog and fill table with values and labels

			// System.out.println(columNames.length);
			drawPlot();
			// dialogPanel.add(button, "wrap");

		} else {
			removeAllElements();
		}

	}

	/**
	 * This method redraws the time series plot in the left sidebar.
	 */
	private void drawPlot() {
		Place place;
		Transition transition;
		// places = new ArrayList<Place>();
		BiologicalNodeAbstract bna;
		int pickedV = GraphInstance.getMyGraph().getVisualizationViewer()
				.getPickedVertexState().getPicked().size();
		int pickedE = GraphInstance.getMyGraph().getVisualizationViewer()
				.getPickedEdgeState().getPicked().size();

		for (int i = 0; i < seriesListR1.size(); i++) {
			renderer.setSeriesVisible(i, false);
		}

		for (int i = 0; i < seriesListR2.size(); i++) {
			renderer2.setSeriesVisible(i, false);
		}

		boolean secondAxis = false;
		if (pickedV == 0 && pickedE == 1) {
			BiologicalEdgeAbstract bea = GraphInstance.getMyGraph()
					.getVisualizationViewer().getPickedEdgeState().getPicked()
					.iterator().next();
			if (bea instanceof PNEdge) {
				secondAxis = true;
				PNEdge edge = (PNEdge) bea;
				// System.out.println(edge.getID());
				if (edge.getSim_tokensSum() != null
						&& edge.getSim_tokensSum().size() > 0) {
					renderer.setSeriesVisible((int) vector2idx.get(System
							.identityHashCode(edge.getSim_tokens())), true);
					renderer2.setSeriesVisible((int) vector2idx.get(System
							.identityHashCode(edge.getSim_tokensSum())), true);
					// System.out.println(min);
				}
			}
		} else {

			boolean onlyT = true;
			if (GraphInstance.getMyGraph().getVisualizationViewer()
					.getPickedVertexState().getPicked().size() > 0) {
				Iterator<BiologicalNodeAbstract> it = GraphInstance
						.getMyGraph().getVisualizationViewer()
						.getPickedVertexState().getPicked().iterator();
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
				iterator = GraphInstance.getMyGraph().getVisualizationViewer()
						.getPickedVertexState().getPicked().iterator();
				// System.out.println("picked");
			} else {
				iterator = GraphInstance.getMyGraph().getAllVertices()
						.iterator();
			}

			//int j = 0;
			while (iterator.hasNext()) {
				bna = iterator.next();

				if (bna instanceof Place) {
					place = (Place) bna;
					// System.out.println(place.getPetriNetSimulationData().size());
					if (place.hasRef() && place.getRef() instanceof Place) {
						place = (Place) place.getRef();
					}
					// if (place.getPetriNetSimulationData().size() > 0) {
					if (vector2idx.get(System.identityHashCode(place
							.getPetriNetSimulationData())) != null) {
						renderer.setSeriesVisible((int) vector2idx.get(System
								.identityHashCode(place
										.getPetriNetSimulationData())), true);
					}
				} else if (bna instanceof Transition && onlyT) {
					transition = (Transition) bna;
					if (transition.hasRef()
							&& transition.getRef() instanceof Transition) {
						transition = (Transition) transition.getRef();
					}
					if (vector2idx.get(System.identityHashCode(transition
							.getSimActualSpeed())) != null) {
						renderer.setSeriesVisible((int) vector2idx.get(System
								.identityHashCode(transition
										.getSimActualSpeed())), true);
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

		} else {
			plot.setRenderer(renderer);
			NumberAxis domainAxis = new NumberAxis("Time");
			plot.setDomainAxis(domainAxis);
			NumberAxis na = new NumberAxis("Tokens");
			
			//na.setRange(min * 0.95, 1.05 * max);
			na.setAutoRange(true);
			na.setAutoRangeIncludesZero(false);

			plot.setRangeAxis(na);

			plot.setRenderer(1, renderer2);
			NumberAxis axis = new NumberAxis("Tokens");
			axis.setAxisLinePaint(Color.WHITE);
			axis.setLabelPaint(Color.WHITE);
			axis.setTickLabelPaint(Color.WHITE);
			axis.setTickMarkPaint(Color.WHITE);
			//
			plot.setRangeAxis(1, axis);
			if (pane.getChart().getLegend() == null) {
				pane.getChart().addLegend(this.legend);
			}

		}

		// updateData();

		// pane.repaint();
		// pane.requestFocus();
		// chart.fireChartChanged();

	}

	public void updateData() {

		chart.setNotify(true);

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
		// Iterator<BiologicalNodeAbstract> itBna =
		// pw.getGraph().getVisualizationViewer().getPickedVertexState().getPicked().iterator();
		Iterator<BiologicalNodeAbstract> itBna = pw.getAllNodes().iterator();
		Vector<Double> time = pw.getPetriNet().getTime();
		while (itBna.hasNext()) {
			bna = itBna.next();
			long diff = 0;

			// System.out.println("transition");

			// System.out.println(series.getItemCount());
			if (bna instanceof Place) {
				place = (Place) bna;
				// System.out.println("size: "+place.getPetriNetSimulationData().size());
				if (place.getPetriNetSimulationData().size() > 0) {
					series = this.seriesListR1
							.get(vector2idx.get(System.identityHashCode(bna
									.getPetriNetSimulationData())));
					// System.out.println(seriesList.get(j).getItemCount());
					if (pw.getPetriNet().getTime().size() != place
							.getPetriNetSimulationData().size()) {
					}
					stop = Math.min(pw.getPetriNet().getTime().size(), place
							.getPetriNetSimulationData().size());
					// System.out.println("vor");
					// System.out.println(place.getName());
					steps = stop
							- Math.min(series.getItemCount(),
									series.getItemCount());
					long begin2 = System.currentTimeMillis();
					if (stop - series.getItemCount() > 0) {
						// System.out.println(bna.getName() + " "
						// + (stop - series.getItemCount()));
					}
					for (int i = series.getItemCount(); i < stop; i++) {

						if (place.getID() == 196) {
							// System.out.println("node: "+i);
						}
						if (place.getPetriNetSimulationData().size() > i) {
							value = place.getPetriNetSimulationData().get(i);
						} else {
							value = 0.0;
						}
						// System.out.println(step);
						series.add(pw.getPetriNet().getTime().get(i), value);
					}
					diff = System.currentTimeMillis() - begin2;
					if (stop > 0) {
						// System.out.println("node: "+bna.getID()+" time: "
						// + diff / (double)steps + " steps: "+steps);
					}
				}
			} else if (bna instanceof Transition) {

				transition = (Transition) bna;
				// System.out.println("only t");
				// System.out.println(transition.getSimActualSpeed().size());
				if (transition.getSimActualSpeed().size() > 0) {
					series = this.seriesListR1.get(vector2idx.get(System
							.identityHashCode(transition.getSimActualSpeed())));
					// System.out.println(transition.getName());
					stop = Math.min(transition.getSimActualSpeed().size(),
							time.size());
					// System.out.println(stop);
					for (int i = series.getItemCount(); i < stop; i++) {

						value = transition.getSimActualSpeed().get(i);
						series.add(pw.getPetriNet().getTime().get(i), value);
					}
				}
			}
		}

		// Iterator<BiologicalEdgeAbstract> itBea =
		// pw.getGraph().getVisualizationViewer().getPickedEdgeState().getPicked().iterator();
		Iterator<BiologicalEdgeAbstract> itBea = pw.getAllEdges().iterator();
		while (itBea.hasNext()) {
			bea = itBea.next();

			if (bea instanceof PNEdge) {
				long begin = System.currentTimeMillis();
				long diff = 0;
				edge = (PNEdge) bea;
				series = this.seriesListR1.get(vector2idx.get(System
						.identityHashCode(edge.getSim_tokens())));
				series2 = this.seriesListR2.get(vector2idx.get(System
						.identityHashCode(edge.getSim_tokensSum())));
				if (edge.getSim_tokensSum().size() > 0
						&& edge.getSim_tokensSum().size() > 0) {
					stop = Math.min(Math.min(edge.getSim_tokens().size(), edge
							.getSim_tokensSum().size()), time.size());
					// System.out.println(edge.getFrom().getName()+" -> "+edge.getTo().getName());
					// System.out.println("s: "+step);
					// System.out.println(edge.getID());
					long begin2 = System.currentTimeMillis();
					steps = stop
							- Math.min(series.getItemCount(),
									series2.getItemCount());
					for (int i = Math.min(series.getItemCount(),
							series2.getItemCount()); i < stop; i++) {

						if (bea.getID() == 299) {
							// System.out.println("edge: "+i);
						}
						value = edge.getSim_tokens().get(i);
						if (series == null) {
							System.out.println(edge.getFrom().getName()
									+ " => " + edge.getTo().getName());
						}
						if (series2 == null) {
							System.out.println("null: "
									+ edge.getFrom().getName() + " -> "
									+ edge.getTo().getName());
						}
						series.add(pw.getPetriNet().getTime().get(i), value);
						// System.out.println(series2.getItemCount());
						// System.out.println(pw.getPetriNet().getTime().get(i)
						// +" "+ edge
						// .getSim_tokensSum().get(i));
						series2.add(pw.getPetriNet().getTime().get(i), edge
								.getSim_tokensSum().get(i));
					}
					diff = System.currentTimeMillis() - begin2;
					if (stop > 0) {
						// System.out.println("edge: "+edge.getID()+" time: " +
						// diff / (double)steps + " steps: "+steps);
					}
				}
				// long end = System.currentTimeMillis();

			}
		}
		chart.setNotify(true);
		// chart.fireChartChanged();
		// pane.setPreferredSize(new Dimension(320, 500));
		// pane.repaint();
		// mainPanel.repaint();
	}

	/** Event handler function. Handles Button Clicks **/
	public void actionPerformed(ActionEvent e) {

		String event = e.getActionCommand();
		if ("reset".equals(event)) {
			for (Iterator i = pw.getAllNodes().iterator(); i.hasNext();) {
				BiologicalNodeAbstract bna = (BiologicalNodeAbstract) i.next();
				if (bna instanceof Place) {
					bna.rebuildShape(new VertexShapes());
					bna.setColor(Color.WHITE);
				}
			}
		} else if ("show".equals(event)) {
			table = new MyTable();
			// System.out.println(model.getColumnCount());
			// System.out.println(model.getRowCount());
			table.setModel(this.getTableModel());
			table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			table.setColumnControlVisible(false);
			table.setHighlighters(HighlighterFactory.createSimpleStriping());
			table.setFillsViewportHeight(true);
			table.addHighlighter(new ColorHighlighter(new Color(192, 215, 227),
					Color.BLACK));
			table.setHorizontalScrollEnabled(true);
			table.getTableHeader().setReorderingAllowed(true);
			table.getTableHeader().setResizingAllowed(true);
			table.getColumn("Label").setPreferredWidth(100);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

			JScrollPane sp = new JScrollPane(table);
			sp.setPreferredSize(new Dimension(600, 200));

			MigLayout layout2 = new MigLayout();
			dialogPanel = new JPanel(layout2);
			dialogPanel.add(new JLabel(
					"Results for each Timestep t and for all Places:"),
					"span 2");
			dialogPanel.add(new JSeparator(), "gap 10, wrap 15, growx");
			dialogPanel.add(sp, "span 4, growx, wrap");

			//dialogPanel
			//.add(new JSeparator(), "span, growx, wrap 15, gaptop 10");

			//JPanel selectPanel = new JPanel();

			//dialogPanel.add(selectPanel, "span,gaptop 1,align right,wrap");

			exportSimResult = new JButton("Export Simulation Result");
			exportSimResult.setActionCommand("exportSimResult");
			exportSimResult.addActionListener(this);
			
			dialogPanel.add(exportSimResult, "wrap");
			
			dialogPanel
			.add(new JSeparator(), "span, growx, wrap 15, gaptop 10");
			
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
		} else if (event.equals("animatePetriNet")) {
			// redraw plot and set new colors/sizes

			petriNetStopAnimationButton.setText("Stop");
			petriNetStopAnimationButton.setBackground(Color.RED);
			petriNetStopAnimationButton.setEnabled(true);
			petriNetStopAnimationButton.revalidate();
			resetPetriNet.setEnabled(false);
			thread = new AnimationThread(slider,
					(Integer) animationStart.getValue(),
					(Integer) animationStop.getValue(),
					animationColour.isSelected(),
					(Integer) animationSpeed.getValue(),
					petriNetAnimationButton, petriNetStopAnimationButton);
			thread.start();

		} else if (event.equals("animatePetriNetStop")) {
			// redraw plot and set new colors/sizes
			resetPetriNet.setEnabled(true);
			petriNetAnimationButton.setEnabled(false);
			slider.setEnabled(true);

			if (thread != null) {
				if (thread.isAlive()) {
					animationThreadStep = ((AnimationThread) thread)
							.getThreadStep();
					((AnimationThread) thread).stopThread();
					petriNetStopAnimationButton.setText("Continue");
					petriNetStopAnimationButton.setBackground(Color.GREEN);
					petriNetStopAnimationButton.revalidate();

				} else {
					if (animationThreadStep != -1) {

						petriNetStopAnimationButton.setText("Stop");
						petriNetStopAnimationButton.setBackground(Color.RED);
						petriNetStopAnimationButton.revalidate();

						thread = new AnimationThread(slider,
								animationThreadStep,
								(Integer) animationStop.getValue(),
								animationColour.isSelected(),
								(Integer) animationSpeed.getValue(),
								petriNetAnimationButton,
								petriNetStopAnimationButton);
						thread.start();
					}
				}
			}
		}else if(event.equals("exportSimResult")){
			//TODO CHRIS bit empty, yet :)
			
			System.out.println("click");
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
		 * System.out.println("ist Invariante"); } else {
		 * System.out.println("ist keine Invariante"); }
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
		if (pw.isPetriNet() && pw.isPetriNetSimulation()) {
			if (e.getSource().equals(animationStart))
				animationStartInit = (Integer) animationStart.getValue();
			else if (e.getSource().equals(animationStop))
				animationStopInit = (Integer) animationStop.getValue();
			else if (e.getSource().equals(animationSpeed))
				animationSpeedInit = (Integer) animationSpeed.getValue();
			else {
				pw.getPetriNet().setCurrentTimeStep(this.slider.getValue());
				slider.setToolTipText("Time: " + this.slider.getValue());
				if (pw.getPetriNet().getTime().size() > 0) {

					double step = pw.getPetriNet().getTime()
							.get(this.slider.getValue());
					// System.out.println(slider.getValue() +" "+step);
					stepLabel.setText("Time: "
							+ (double) Math.round((step * 100)) / 100);

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
					Pathway pw = graphInstance.getPathway();
					Collection<BiologicalNodeAbstract> ns = pw.getGraph()
							.getAllVertices();
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
							if (bna.getPetriNetSimulationData().size() > 0
									&& slider.getValue() >= 0) {
								ref = bna.getMicroArrayValue(slider.getValue());
								// ref = Math.abs(ref);
								if (bna instanceof Place) {
									((Place) bna).setToken(ref);
								} else if (bna instanceof Transition) {
									if (ref == 1) {
										((Transition) bna)
												.setSimulationActive(true);
										// System.out.println("aktiv");
									} else {
										((Transition) bna)
												.setSimulationActive(false);
										// System.out.println("inaktiv");
									}
								}

								if (slider.getValue() >= 2) {
									val = bna.getMicroArrayValue(slider
											.getValue() - 1);

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

									float ref2 = (float) Math.sqrt(Math
											.sqrt(ref));

									if (ref2 > 3.0)
										ref2 = 3.0f;

									// System.out.println(ref);
									// prepare size modification
									// v.setUserDatum("madata", 0.4,
									// UserData.SHARED);

									// NodeRankingVertexSizeFunction sf = new
									// NodeRankingVertexSizeFunction(
									// "madata", ref2);

									VertexShapes vs = new VertexShapes(ref2,
											1.0f);

									// apply change in color and size only if
									// current
									// node is
									// not a
									// reference
									if (!bna.isReference()) {
										bna.rebuildShape(vs);
										if (animationColour.isSelected()) {
											bna.setColor(new Color(colors
													.get(take)));
										}
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

		// System.out.println("inti Graphs");
		p.removeAll();
		final XYSeriesCollection dataset = new XYSeriesCollection();
		final XYSeriesCollection dataset2 = new XYSeriesCollection();

		renderer = new XYLineAndShapeRenderer();
		renderer2 = new XYLineAndShapeRenderer();

		labels.clear();
		// plotEntities.clear();
		seriesListR1.clear();
		seriesListR2.clear();
		// series2id.clear();
		vector2idx.clear();
		// get Selected Places and their index+label
		Place place;
		Transition transition;
		places = new ArrayList<BiologicalNodeAbstract>();

		int count = 0;
		BiologicalNodeAbstract bna;
		BiologicalEdgeAbstract bea;
		if (pw == null) {
			return;
		}
		Iterator<BiologicalNodeAbstract> itNodes = pw.getAllNodes().iterator();
		XYSeries s;
		while (itNodes.hasNext()) {
			bna = itNodes.next();
			if (!bna.hasRef()) {
				if (bna instanceof Place) {
					place = (Place) bna;
					// if (place.getPetriNetSimulationData().size() > 0) {
					places.add(place);
					s = new XYSeries(count);
					vector2idx.put(System.identityHashCode(place
							.getPetriNetSimulationData()), count);
					seriesListR1.add(s);
					// System.out.println(System.identityHashCode(place.getPetriNetSimulationData())
					// + " added");
					dataset.addSeries(s);
					labels.add(place.getName());// rows[indices.get(j)][0].toString());

					renderer.setSeriesPaint(count, place.getPlotColor());

					renderer.setSeriesItemLabelsVisible(count, true);
					renderer.setSeriesShapesVisible(count, false);
					count++;
					// }
				} else if (bna instanceof Transition) {
					transition = (Transition) bna;
					// if (transition.getPetriNetSimulationData().size() > 0) {
					s = new XYSeries(count);
					vector2idx.put(System.identityHashCode(transition
							.getSimActualSpeed()), count);
					// seriesList.add(new XYSeries(j));
					// series2id.put(s, count);
					seriesListR1.add(s);
					dataset.addSeries(s);
					labels.add(transition.getName());// rows[indices.get(j)][0].toString());

					renderer.setSeriesPaint(count, Color.BLACK);

					renderer.setSeriesItemLabelsVisible(count, true);
					renderer.setSeriesShapesVisible(count, false);
					renderer.setSeriesVisible(count, false);
					count++;
					// }
				}
			}
		}

		Iterator<BiologicalEdgeAbstract> itEdges = pw.getAllEdges().iterator();
		while (itEdges.hasNext()) {
			bea = itEdges.next();

			if (bea instanceof PNEdge) {
				PNEdge edge = (PNEdge) bea;

				s = new XYSeries(count);
				vector2idx.put(System.identityHashCode(edge.getSim_tokens()),
						count);

				seriesListR1.add(s);
				// series2id.put(s, count);
				dataset.addSeries(s);
				renderer.setSeriesPaint(count, Color.black);
				renderer.setSeriesShapesVisible(count, false);
				renderer.setSeriesVisible(count, false);

				labels.add("Tokens");
				count++;
				s = new XYSeries(count);
				vector2idx.put(
						System.identityHashCode(edge.getSim_tokensSum()),
						seriesListR2.size());

				// System.out.println(count + ": " + edge.getID());
				// series2id.put(s, count);
				renderer2.setSeriesPaint(seriesListR2.size(), Color.RED);
				renderer2.setSeriesShapesVisible(seriesListR2.size(), false);
				renderer2.setSeriesVisible(seriesListR2.size(), false);
				labels.add("Sum of tokens");
				seriesListR2.add(s);
				// dataset.gets
				dataset2.addSeries(s);

				// count++;
			}

		}

		renderer.setToolTipGenerator(new XYToolTipGenerator() {

			@Override
			public String generateToolTip(XYDataset arg0, int arg1, int arg2) {
				return labels.get(arg1);
			}
		});

		renderer.setBaseItemLabelsVisible(true);
		renderer.setLegendItemLabelGenerator(new XYSeriesLabelGenerator() {

			@Override
			public String generateLabel(XYDataset arg0, int arg1) {
				return labels.get(arg1);
			}
		});

		renderer2.setLegendItemLabelGenerator(new XYSeriesLabelGenerator() {

			@Override
			public String generateLabel(XYDataset arg0, int arg1) {
				return "Tokens";
			}
		});

		chart = ChartFactory.createXYLineChart("", "Time", "Token", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		// set rendering options: all lines in black, domain steps as integers
		final XYPlot plot = chart.getXYPlot();

		// draw plot for Places with random colors

		plot.setDataset(0, dataset);
		plot.setRenderer(0, renderer);

		plot.setDataset(1, dataset2);
		plot.setRenderer(1, renderer2);

		// add chart to pane and refresh GUI

		pane = new ChartPanel(chart);
		pane.setPreferredSize(new java.awt.Dimension(320, 200));

		pane.addChartMouseListener(new ChartMouseListener() {
			@Override
			public void chartMouseClicked(ChartMouseEvent event) {
				if (event.getEntity() != null
						&& event.getEntity() instanceof XYItemEntity) {

					int pickedV = GraphInstance.getMyGraph()
							.getVisualizationViewer().getPickedVertexState()
							.getPicked().size();
					int pickedE = GraphInstance.getMyGraph()
							.getVisualizationViewer().getPickedEdgeState()
							.getPicked().size();
					// System.out.println(pickedV);
					if (pickedE == 0 && pickedV == 0) {

						XYItemEntity entity = (XYItemEntity) event.getEntity();
						BiologicalNodeAbstract p = places.get(entity
								.getSeriesIndex());
						PickedState<BiologicalNodeAbstract> ps = GraphInstance
								.getMyGraph().getVisualizationViewer()
								.getPickedVertexState();
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

		p.add(pane, BorderLayout.CENTER);
		p.setVisible(true);
		this.revalidateView();
		updateData();
	}

	private RegulationTabelModel getTableModel() {

		BiologicalNodeAbstract bna;

		rowsSize = pw.getPetriNet().getPlaces();

		// System.out.println(rowsSize);
		// rowsSize = pw.getPetriNet().getNumberOfPlaces();
		rowsDim = pw.getPetriNet().getResultDimension();
		// System.out.println("rows: "+rowsSize);
		// System.out.println("rowsDim: "+rowsDim);
		// get Data from all Places
		Iterator<BiologicalNodeAbstract> it = pw.getAllNodes().iterator();
		rows = new Object[rowsSize][rowsDim + 1];
		int i = 0;
		// Vector<Double> MAData;

		// System.out.println("while");
		while (it.hasNext()) {
			// Object elem = it.next();
			bna = it.next();
			if (bna instanceof Place) {
				// MAData = bna.getPetriNetSimulationData();
				// System.out.println("size:");
				// System.out.println(MAData.size());
				rows[i][0] = bna.getLabel();
				for (int j = 1; j <= rowsDim; j++) {
					if (bna.getPetriNetSimulationData().size() > j - 1) {
						rows[i][j] = Math.max(0, bna
								.getPetriNetSimulationData().get(j - 1));
					} else {
						rows[i][j] = 0;
					}
					// System.out.println(i+" "+j+": "+rows[i][j]);
					// System.out.println(i+" "+j +" "+ MAData.get(j -
					// 1));
				}
				i++;
			}
		}
		// create column labels for table view
		String columNames[] = new String[rowsDim + 1];
		// String selectorValues[] = new String[rowsSize];
		columNames[0] = "Label";
		for (i = 0; i < rowsDim; i++) {
			columNames[i + 1] = "t=" + pw.getPetriNet().getTime().get(i);
		}
		return new RegulationTabelModel(rows, columNames);
	}
}