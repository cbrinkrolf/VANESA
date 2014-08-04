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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Rectangle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
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
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import miscalleanous.tables.MyTable;
import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.sun.javafx.geom.Line2D;
import com.sun.javafx.geom.Point2D;

import cern.colt.list.IntArrayList;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import petriNet.AnimationThread;
import petriNet.PNEdge;
import petriNet.Place;
import petriNet.PlotsPanel;
import petriNet.SimpleMatrixDouble;
import petriNet.Transition;
import sun.invoke.util.BytecodeName;
import biologicalElements.GraphElementAbstract;
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
	private Hashtable<Integer, BiologicalNodeAbstract> nodeTabel = new Hashtable<Integer, BiologicalNodeAbstract>();
	// instance of main window
	MainWindow w;

	// create GUI components
	private JButton showTable = new JButton("show detailed simulation results");
	private JButton drawPlots = new JButton("show all animation result plots");

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
	private JDialog dialog;
	private GraphInstance graphInstance = null;
	private Pathway pw = null;
	private RegulationTabelModel model;
	private MyTable table;
	private boolean first = true;
	private JButton petriNetAnimationButton = new JButton("Start Animation");
	private JButton petriNetStopAnimationButton = new JButton("Stop");
	private JButton resetPetriNet = new JButton("Reset");

	private int animationThreadStep = -1;
	private JSpinner animationStart;
	private JSpinner animationStop;
	private JSpinner animationSpeed;
	private JCheckBox animationColour = new JCheckBox();
	private Thread thread;

	private JPanel invariants = new JPanel();

	// An Object to store microarray data
	private Object[][] rows;
	// number of Places
	private int rowsSize;
	// size of Vector in each Place
	private int rowsDim;

	// Indices of selected Places
	// private ArrayList<Integer> indices = new ArrayList<Integer>();
	// Labels of selected Places
	private ArrayList<String> labels = new ArrayList<String>();
	// Colors for Plot
	private ArrayList<Color> colors = new ArrayList<Color>();

	// for P-Invariants
	private Object[][] rP;
	private MyTable tP;

	// for T-Invariants
	private Object[][] rT;
	private MyTable tT;

	private int animationStartInit = 1;

	private int animationStopInit = 1;

	private int animationSpeedInit = 20;

	//private ArrayList<Place> plotEntities = new ArrayList<Place>();

	private ArrayList<Place> places;
	
	private ArrayList<XYSeries> seriesList = new ArrayList<XYSeries>();

	public ParallelCoordinatesPlot() {

		petriNetAnimationButton.addActionListener(this);
		petriNetAnimationButton.setActionCommand("animatePetriNet");

		petriNetStopAnimationButton.addActionListener(this);
		petriNetStopAnimationButton.setActionCommand("animatePetriNetStop");

		resetPetriNet.addActionListener(this);
		resetPetriNet.setActionCommand("reset");

		drawPCP.addActionListener(this);
		drawPCP.setActionCommand("drawplot");

		// some defined colors
		/*
		 * colors.add(new Color(0, 0, 0)); colors.add(new Color(255, 0, 0));
		 * colors.add(new Color(128, 0, 0)); colors.add(new Color(0, 255, 0));
		 * colors.add(new Color(0, 128, 0)); colors.add(new Color(0, 0, 255));
		 * colors.add(new Color(0, 0, 128)); colors.add(new Color(255, 255, 0));
		 * colors.add(new Color(255, 0, 255)); colors.add(new Color(0, 255,
		 * 255)); colors.add(new Color(065, 105, 225)); colors.add(new
		 * Color(124, 252, 000)); colors.add(new Color(178, 034, 034));
		 * colors.add(new Color(160, 032, 240)); colors.add(new Color(000, 255,
		 * 127)); colors.add(new Color(255, 127, 000)); colors.add(new
		 * Color(000, 100, 000));
		 */
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
		graphInstance = new GraphInstance();
		pw = graphInstance.getPathway();

		if (pw.isPetriNet() && pw.isPetriNetSimulation()) {

			main.removeAll();
			first = true;

			// get main window instance
			w = MainWindowSingelton.getInstance();

			// get pathway and nodes

			if (first) {
				BiologicalNodeAbstract bna;
				Place p;
				Iterator<BiologicalNodeAbstract> it = pw.getAllNodes()
						.iterator();
				rowsSize = 0;
				while (it.hasNext()) {
					bna = it.next();
					if (bna instanceof Place) {
						p = (Place) bna;
						if (p.getPetriNetSimulationData().size() > 0) {
							rowsSize++;
						}
					}
				}
				// System.out.println(rowsSize);
				// rowsSize = pw.getPetriNet().getNumberOfPlaces();
				rowsDim = pw.getPetriNet().getResultDimension();
				// System.out.println("rows: "+rowsSize);
				// System.out.println("rowsDim: "+rowsDim);
				// get Data from all Places
				it = pw.getAllNodes().iterator();
				rows = new Object[rowsSize][rowsDim + 1];
				int i = 0;
				Vector<Double> MAData;

				// System.out.println("while");
				while (it.hasNext()) {
					// Object elem = it.next();
					bna = it.next();
					if (bna instanceof Place
							&& pw.getPetriNet().getPnResult()
									.containsKey("'" + bna.getName() + "'.t")) {
						MAData = bna.getPetriNetSimulationData();
						// System.out.println("size:");
						// System.out.println(MAData.size());
						rows[i][0] = bna.getLabel();
						for (int j = 1; j <= MAData.size(); j++) {
							rows[i][j] = MAData.get(j - 1);
							// System.out.println(i+" "+j+": "+rows[i][j]);
							// System.out.println(i+" "+j +" "+ MAData.get(j -
							// 1));
						}
						i++;
					}
				}
			}
			first = false;

			// create column labels for table view
			String columNames[] = new String[rowsDim + 1];
			String selectorValues[] = new String[rowsSize];
			columNames[0] = "Label";
			for (int i = 0; i < rowsDim; i++) {
				columNames[i + 1] = "t="
						+ pw.getPetriNet().getPnResult().get("time").get(i);

			}

			for (int i = 0; i < rowsSize; i++) {
				selectorValues[i] = "Place: " + rows[i][0];
			}

			// build GUI
			p = new JPanel();

			MigLayout layout = new MigLayout();
			SpinnerModel modelStart = new SpinnerNumberModel(
					animationStartInit, // initial value
					1, // min
					rowsDim, // max
					1); // step

			animationStart = new JSpinner(modelStart);

			animationStart.addChangeListener(this);

			SpinnerModel modelEnd = new SpinnerNumberModel(rowsDim, // initial
																	// value
					1, // min
					rowsDim, // max
					1); // step

			animationStop = new JSpinner(modelEnd);
			animationStop.addChangeListener(this);

			SpinnerModel modelSpeed = new SpinnerNumberModel(
					animationSpeedInit, // initial
										// value
					1, // min
					20, // max
					1); // step

			animationSpeed = new JSpinner(modelSpeed);
			animationSpeed.addChangeListener(this);

			animationColour.setSelected(false);

			mainPanel = new JPanel(layout);
			showTable = new JButton("show detailed simulation results");
			showTable.addActionListener(this);
			showTable.setActionCommand("show");
			slider.setMinimum(1);
			slider.setMaximum(rowsDim);
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

			// // /INVARIANTEN TEST TODO
			// //P-Invariante
			// rP = new Object[this.rowsSize][2];
			//
			// Iterator<GraphElementAbstract> it = hs.iterator();
			// int i = 0;
			//
			// while (it.hasNext()) {
			// Object elem = it.next();
			// BiologicalNodeAbstract bna = (BiologicalNodeAbstract) elem;
			// if (bna instanceof Place) {
			// // MAData = bna.getPetriNetSimulationData();
			// // System.out.println("size:");
			// // System.out.println(MAData.size());
			// rP[i][0] = bna.getName();
			// rP[i][1] = i;
			// i++;
			// }
			// }
			// String[] cNames = new String[2];
			// cNames[0] = "Vertex";
			// cNames[1] = "Value";
			// RegulationTabelModel m = new RegulationTabelModel(rP, cNames,
			// nodeTabel);
			//
			// tP = new MyTable(rP, cNames);
			// //tP.setModel(m);
			// //tP.set
			// /*tP.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			// tP.setColumnControlVisible(false);
			// tP.setHighlighters(HighlighterFactory.createSimpleStriping());
			// tP.setFillsViewportHeight(true);
			// tP.addHighlighter(new ColorHighlighter(new Color(192, 215, 227),
			// Color.BLACK));
			// tP.setHorizontalScrollEnabled(true);
			// tP.getTableHeader().setReorderingAllowed(true);
			// tP.getTableHeader().setResizingAllowed(true);
			// tP.getColumn(cNames[0]).setPreferredWidth(100);
			// tP.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);*/
			//
			//
			//
			// invariants.add(new JSeparator(), "gap 10, wrap 15, growx");
			// invariants.add(tP);
			//
			// //mainPanel.add(t);
			// JButton testP = new JButton("Test P Invariante");
			// testP.setActionCommand("testP");
			// testP.addActionListener(this);
			//
			// //mainPanel.add(testP);
			// invariants.add(testP);
			//
			//
			// // T-Invariante
			//
			// rT = new Object[hs.size()-this.rowsSize][2];
			//
			// Iterator<GraphElementAbstract> it2 = hs.iterator();
			// int j = 0;
			//
			// Object elem;
			// BiologicalNodeAbstract bna;
			// while (it2.hasNext()) {
			// elem = it2.next();
			// bna = (BiologicalNodeAbstract) elem;
			// if (bna instanceof Transition) {
			// // MAData = bna.getPetriNetSimulationData();
			// // System.out.println("size:");
			// // System.out.println(MAData.size());
			// rT[j][0] = bna.getName();
			// rT[j][1] = j+5;
			// System.out.println(j+5+" "+bna.getName());
			// j++;
			// }
			// }
			// String[] cNamesT = new String[2];
			// cNamesT[0] = "Transition";
			// cNamesT[1] = "Value";
			// RegulationTabelModel mT = new RegulationTabelModel(rT, cNamesT,
			// nodeTabel);
			//
			// tT = new MyTable();
			// tT.setModel(mT);
			// tT.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			// tT.setColumnControlVisible(false);
			// tT.setHighlighters(HighlighterFactory.createSimpleStriping());
			// tT.setFillsViewportHeight(true);
			// tT.addHighlighter(new ColorHighlighter(new Color(192, 215, 227),
			// Color.BLACK));
			// tT.setHorizontalScrollEnabled(true);
			// tT.getTableHeader().setReorderingAllowed(true);
			// tT.getTableHeader().setResizingAllowed(true);
			// //tT.getColumn(cNamesT[0]).setPreferredWidth(100);
			// tT.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			//
			//
			// invariants.add(new JSeparator(), "gap 10, wrap 15, growx");
			// invariants.add(tT);
			//
			// //mainPanel.add(t);
			// JButton testT = new JButton("Test T Invariante");
			// testT.setActionCommand("testT");
			// testT.addActionListener(this);
			//
			// //mainPanel.add(testP);
			// invariants.add(testT);
			//
			//
			// main.add(invariants);
			// // TEST ENDE TODO
			//
			//

			main.add(mainPanel);
			main.setVisible(true);

			// create dialog and fill table with values and labels
			model = new RegulationTabelModel(rows, columNames, nodeTabel);

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
		labels.clear();
		//plotEntities.clear();
		seriesList.clear();
		// get Selected Places and their index+label
		Place place;
		Transition transition;
		places = new ArrayList<Place>();
		BiologicalNodeAbstract bna;
		// System.out.println(GraphInstance.getMyGraph().getVisualizationViewer().getPickedVertexState().getPicked().size());

		/*
		 * int picked = GraphInstance.getMyGraph().getVisualizationViewer()
		 * .getPickedVertexState().getPicked().size(); while (it.hasNext()) {
		 * bna = it.next(); if (bna instanceof Place) { place = (Place) bna;
		 * actualLabel = place.getLabel(); // System.out.println(rowsSize); //
		 * System.out.println(bna.getID()); if (picked == 0 ||
		 * GraphInstance.getMyGraph().getVisualizationViewer()
		 * .getPickedVertexState().isPicked(bna)) {//
		 * .isVertexPicked(bna.getVertex())) // { //
		 * System.out.println(bna.getID() + " is picked."); for (int j = 0; j <
		 * rowsSize; j++) { // System.out.println("String: "+ rows[j][0]); idx =
		 * rows[j][0].toString(); if (idx.equals(actualLabel)) { indices.add(j);
		 * places.add(place); break; } } } } }
		 */
		// clear GUI
		// p.removeAll();

		// create plot dataset
		final XYSeriesCollection dataset = new XYSeriesCollection();
		final XYSeriesCollection dataset2 = new XYSeriesCollection();

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		final XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer();

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

		// fill series with time series of values
		//Vector<XYSeries> series = new Vector<XYSeries>();
		Double value;
		int pickedV = GraphInstance.getMyGraph().getVisualizationViewer()
				.getPickedVertexState().getPicked().size();
		int pickedE = GraphInstance.getMyGraph().getVisualizationViewer()
				.getPickedEdgeState().getPicked().size();
		// System.out.println(pickedE+" "+pickedV);
		// Double tmp = 0.0;
		// Double diff;

		boolean secondAxis = false;
		if (pickedV == 0 && pickedE == 1) {
			BiologicalEdgeAbstract bea = GraphInstance.getMyGraph()
					.getVisualizationViewer().getPickedEdgeState().getPicked()
					.iterator().next();
			if (bea instanceof PNEdge) {
				secondAxis = true;
				PNEdge edge = (PNEdge) bea;

				if (edge.getSim_tokensSum() != null
						&& edge.getSim_tokensSum().size() > 0) {
					seriesList.add(new XYSeries(0));
					seriesList.add(new XYSeries(1));
					
					dataset.addSeries(seriesList.get(0));
					labels.add("Sum of tokens");

					dataset2.addSeries(seriesList.get(1));
					// labels.add("Tokens");

					renderer.setSeriesPaint(0, Color.black);
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
			} else {
				iterator = GraphInstance.getMyGraph().getAllVertices()
						.iterator();
			}
			// System.out.println(rowsDim);
			// Place place;
			int j = 0;
			while (iterator.hasNext()) {
				// System.out.println(j);
				// System.out.println(j);
				bna = iterator.next();
				if (bna instanceof Place) {
					place = (Place) bna;
					if (place.getPetriNetSimulationData().size() > 0) {
						places.add(place);
						// System.out.println("size: "+places.size());
						seriesList.add(new XYSeries(j));
						
						dataset.addSeries(seriesList.get(j));
						labels.add(place.getName());// rows[indices.get(j)][0].toString());

						renderer.setSeriesPaint(j, place.getPlotColor());

						renderer.setSeriesItemLabelsVisible(j, true);
						renderer.setSeriesShapesVisible(j, false);
						j++;
					}
				} else if (bna instanceof Transition && onlyT) {
					transition = (Transition) bna;
					if (transition.getPetriNetSimulationData().size() > 0) {
						seriesList.add(new XYSeries(j));
						
						dataset.addSeries(seriesList.get(j));
						labels.add(transition.getName());// rows[indices.get(j)][0].toString());

						renderer.setSeriesPaint(j, transition.getPlotColor());

						renderer.setSeriesItemLabelsVisible(j, true);
						renderer.setSeriesShapesVisible(j, false);
						j++;
					}
				}

			}
		}
		
		updateData();
		// if one or some Places are selected
		/*
		 * if (indices.size() != 0) { for (int j = 0; j < indices.size(); j++) {
		 * series.add(new XYSeries(j)); for (int i = 0; i < rowsDim; i++) {
		 * value = Double.parseDouble(rows[indices.get(j)][i + 1] .toString());
		 * series.get(j).add( pw.getPetriNet().getPnResult().get("time").get(i),
		 * value); } dataset.addSeries(series.get(j));
		 * labels.add(rows[indices.get(j)][0].toString()); }
		 * 
		 * }
		 */

		// if no Place is selected (after starting)
		// else {
		// for (int j = 0; j < rowsSize; j++) {
		// series.add(new XYSeries(j));
		// for (int i = 1; i < rowsDim; i++) {
		// value = Double.parseDouble(rows[j][i].toString());
		// series.get(j).add(i, value);
		// }
		// dataset.addSeries(series.get(j));
		// labels.add(rows[j][0].toString());
		// }
		// }

		// create a chart...
		chart = ChartFactory.createXYLineChart("", "Time", "Token", dataset,
				PlotOrientation.VERTICAL, true, true, false);

		// set rendering options: all lines in black, domain steps as integers
		final XYPlot plot = chart.getXYPlot();

		// draw plot for Places with random colors
		if (secondAxis) {
			renderer.setSeriesItemLabelsVisible(0, true);
			renderer.setSeriesShapesVisible(0, false);

			// renderer.setSeriesPaint(1, Color.red);

			renderer2.setSeriesItemLabelsVisible(0, true);
			renderer2.setSeriesShapesVisible(0, false);
			renderer2.setSeriesPaint(0, Color.red);

			// XYDataset dataset1 = getDataset1();
			plot.setDataset(0, dataset);
			plot.setRenderer(0, renderer);
			NumberAxis domainAxis = new NumberAxis("Time");
			plot.setDomainAxis(domainAxis);
			NumberAxis na = new NumberAxis("Sum of tokens");

			plot.setRangeAxis(na);

			// XYDataset dataset2 = getDataset2();
			plot.setDataset(1, dataset2);
			plot.setRenderer(1, renderer2);
			NumberAxis axis = new NumberAxis("Tokens");
			axis.setAxisLinePaint(Color.RED);
			axis.setLabelPaint(Color.RED);
			axis.setTickLabelPaint(Color.RED);
			//
			plot.setRangeAxis(1, axis);

			plot.mapDatasetToRangeAxis(0, 0);// 1st dataset to 1st y-axis
			plot.mapDatasetToRangeAxis(1, 1); // 2nd dataset to 2nd y-axis
			axis.setRange(0, 1.3 * axis.getRange().getUpperBound());
		} else {
			plot.setRenderer(renderer);
			NumberAxis rangeAxis = (NumberAxis) plot.getDomainAxis();
			rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		}

		// add chart to pane and refresh GUI

		ChartPanel pane = new ChartPanel(chart);
		pane.setPreferredSize(new java.awt.Dimension(320, 200));

		pane.addChartMouseListener(new ChartMouseListener() {
			@Override
			public void chartMouseClicked(ChartMouseEvent event) {
				if (event.getEntity() != null
						&& event.getEntity() instanceof XYItemEntity) {
					XYItemEntity entity = (XYItemEntity) event.getEntity();
					// System.out.println("Entity seriesindex: "
					// + entity.getSeriesIndex());
					Place p = places.get(entity.getSeriesIndex());
					PickedState<BiologicalNodeAbstract> ps = GraphInstance
							.getMyGraph().getVisualizationViewer()
							.getPickedVertexState();
					// ps.clearPickedVertices();
					ps.clear();
					// System.out.println(entity.getSeriesIndex());
					// System.out.println("sizeunten: "+places.size());
					// System.out.println(p.getName());
					ps.pick(p, true);

				}

			}

			@Override
			public void chartMouseMoved(ChartMouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		p.add(pane, BorderLayout.CENTER);
		p.setVisible(true);

		main.revalidate();
	}
	
	public void updateData(){
		Double value;
		
		int pickedV = GraphInstance.getMyGraph().getVisualizationViewer()
				.getPickedVertexState().getPicked().size();
		int pickedE = GraphInstance.getMyGraph().getVisualizationViewer()
				.getPickedEdgeState().getPicked().size();
		// System.out.println(pickedE+" "+pickedV);
		// Double tmp = 0.0;
		// Double diff;

		if (pickedV == 0 && pickedE == 1) {
			BiologicalEdgeAbstract bea = GraphInstance.getMyGraph()
					.getVisualizationViewer().getPickedEdgeState().getPicked()
					.iterator().next();
			if (bea instanceof PNEdge) {
				PNEdge edge = (PNEdge) bea;

				if (edge.getSim_tokensSum() != null
						&& edge.getSim_tokensSum().size() > 0) {
					for (int i = 0; i < rowsDim; i++) {
						value = edge.getSim_tokensSum().get(i);// )Double.parseDouble(rows[indices.get(j)][i
																// +
						// 1]
						// .toString());
						// diff = value - tmp;
						// System.out.println(diff);
						seriesList.get(0).add(
								pw.getPetriNet().getPnResult().get("time")
										.get(i), value);
						seriesList.get(1).add(
								pw.getPetriNet().getPnResult().get("time")
										.get(i), edge.getSim_tokens().get(i));
						// tmp = value;
					}
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
			} else {
				iterator = GraphInstance.getMyGraph().getAllVertices()
						.iterator();
			}
			// System.out.println(rowsDim);
			// Place place;
			int j = 0;
			BiologicalNodeAbstract bna;
			Place place;
			Transition transition;
			while (iterator.hasNext()) {
				// System.out.println(j);
				// System.out.println(j);
				bna = iterator.next();
				if (bna instanceof Place) {
					place = (Place) bna;
					if (place.getPetriNetSimulationData().size() > 0) {
						// System.out.println("size: "+places.size());
						for (int i = 0; i < rowsDim; i++) {

							if (place.getPetriNetSimulationData().size() > i) {
								value = place.getPetriNetSimulationData()
										.get(i);
							} else {
								value = 0.0;
							}

							// )Double.parseDouble(rows[indices.get(j)][i
							// +
							// 1]
							// .toString());
							seriesList.get(j).add(
									pw.getPetriNet().getPnResult().get("time")
											.get(i), value);
						}
						j++;
					}
				} else if (bna instanceof Transition && onlyT) {
					transition = (Transition) bna;
					if (transition.getPetriNetSimulationData().size() > 0) {
						for (int i = 0; i < rowsDim; i++) {
							value = transition.getPetriNetSimulationData().get(
									i);// )Double.parseDouble(rows[indices.get(j)][i
										// +
										// 1]
							// .toString());
							seriesList.get(j).add(
									pw.getPetriNet().getPnResult().get("time")
											.get(i), value);
						}
					}
				}

			}
		}
		
		
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
			table.setModel(model);
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
					"Results for each Timestep t and for all Places V:"),
					"span 2");
			dialogPanel.add(new JSeparator(), "gap 10, wrap 15, growx");
			dialogPanel.add(sp, "span 4, growx");

			dialogPanel
					.add(new JSeparator(), "span, growx, wrap 15, gaptop 10");

			JPanel selectPanel = new JPanel();

			dialogPanel.add(selectPanel, "span,gaptop 1,align right,wrap");

			// draw a new plot according to the current time step selection
			
			
			
			PlotsPanel pp = new PlotsPanel();
			dialogPanel.add(pp, "wrap");
			JButton button = new JButton("Save Results");
			button.addActionListener(pp);
			
			dialogPanel.add(button);
			
			
			// System.out.println("show");
			// show table containing all data
			dialog = new JDialog(w, true);
			dialog.setTitle("Simulation Results");
			dialog.setResizable(true);
			dialog.setContentPane(dialogPanel);
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

			ScreenSize screen = new ScreenSize();
			int screenHeight = (int) screen.getheight();
			int screenWidth = (int) screen.getwidth();

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
				// System.out.println("");

				// chart.draw(Graphics2D().drawLine(0,0, 20, 20), new
				// Rectangle2D(0, 0, 50, 50));
				// System.out.println(pw.getPetriNet().getPnResult().get("time").size());
				pw.getPetriNet().setCurrentTimeStep(this.slider.getValue() - 1);
				slider.setToolTipText("Time: " + this.slider.getValue());
				double step = pw.getPetriNet().getPnResult().get("time")
						.get(this.slider.getValue() - 1);
				stepLabel.setText("Time: " + (double) Math.round((step * 100))
						/ 100);

				XYPlot plot = (XYPlot) chart.getPlot();
				plot.removeDomainMarker(marker);

				marker = new ValueMarker(step); // position is the value on the
												// axis
				marker.setPaint(Color.black);
				// marker.setLabel("here"); // see JavaDoc for labels, colors,
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

						// cast to biological node type and retrieve microarray
						// value
						// for current time step
						bna = it.next();
						// System.out.println(bna.getName());
						if (bna.getPetriNetSimulationData().size() > 0
								&& slider.getValue() >= 1) {
							ref = bna.getMicroArrayValue(slider.getValue() - 1);
							ref = Math.abs(ref);
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
								val = bna
										.getMicroArrayValue(slider.getValue() - 2);

								// get microarray value for reference time step
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
								if (!bna.isReference()) {
									bna.rebuildShape(vs);
									if (animationColour.isSelected()) {
										bna.setColor(new Color(colors.get(take)));
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

	private double[][] initArray(int m, int n) {
		double[][] array = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				array[i][j] = 0;
			}
		}
		return array;
	}
}