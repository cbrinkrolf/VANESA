package graph.jung.classes;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;

import biologicalElements.NodeStateChanged;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Place;
import configurations.NetworkSettings;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationServer.Paintable;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.EditingPopupGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.SatelliteVisualizationViewer;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.picking.ShapePickSupport;
import graph.GraphInstance;
import graph.eventhandlers.MyEditingModalGraphMouse;
import graph.gui.GraphPopUp;
import graph.jung.graphDrawing.MyEdgeArrowFunction;
import graph.jung.graphDrawing.MyEdgeDrawPaintFunction;
import graph.jung.graphDrawing.MyEdgeFillPaintFunction;
import graph.jung.graphDrawing.MyEdgeLabelRenderer;
import graph.jung.graphDrawing.MyEdgeShapeFunction;
import graph.jung.graphDrawing.MyEdgeStringer;
import graph.jung.graphDrawing.MyEdgeStrokeHighlighting;
import graph.jung.graphDrawing.MyVertexDrawPaintFunction;
import graph.jung.graphDrawing.MyVertexFillPaintFunction;
import graph.jung.graphDrawing.MyVertexIconTransformer;
import graph.jung.graphDrawing.MyVertexLabelRenderer;
import graph.jung.graphDrawing.MyVertexShapeTransformer;
import graph.jung.graphDrawing.MyVertexStringer;
import graph.jung.graphDrawing.MyVertexStrokeHighlighting;
import graph.layouts.GraphCenter;
import graph.layouts.gemLayout.GEMLayout;
import graph.layouts.hctLayout.HCTLayout;
import graph.layouts.hebLayout.HEBLayout;
import gui.MainWindow;
import gui.MyAnnotationManager;
import gui.PopUpDialog;
import gui.RangeSelector;
import gui.algorithms.ScreenSize;
import gui.visualization.TokenRenderer;

public class MyGraph {
	private int VisualizationViewerWidth = 1000;
	private int VisualizationViewerHeigth = 1000;
	private Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g = new SparseMultigraph<>();
	private final MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv;
	private AbstractLayout<BiologicalNodeAbstract, BiologicalEdgeAbstract> layout;
	final MyEditingModalGraphMouse graphMouse;
	private final SatelliteVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv2;
	private final ShapePickSupport<BiologicalNodeAbstract, BiologicalEdgeAbstract> pickSupport;
	private final ScalingControl scaler = new CrossoverScalingControl();
	// private final HashMap<BiologicalNodeAbstract, Point2D> vertexLocations;
	private final RenderContext<BiologicalNodeAbstract, BiologicalEdgeAbstract> satellitePr;
	private final RenderContext<BiologicalNodeAbstract, BiologicalEdgeAbstract> pr;
	// private AbstractRenderer pr;
	private RenderContext<BiologicalNodeAbstract, BiologicalEdgeAbstract> pr_compare;
	private final MyVertexStringer vertexStringer;
	private final MyEdgeStringer edgeStringer;
	private final MyVertexShapeTransformer vertexShapeTransformer;
	// private VertexShapeSize vssa;
	protected MyVertexStrokeHighlighting vsh;
	protected MyVertexDrawPaintFunction vdpf;
	protected MyVertexFillPaintFunction vfpf;
	protected MyVertexIconTransformer vit;
	protected MyEdgeStrokeHighlighting esh;
	protected MyEdgeDrawPaintFunction edpf;
	protected MyEdgeFillPaintFunction efpf;
	protected MyEdgeShapeFunction esf;
	protected MyEdgeArrowFunction eaf;

	protected PickedState<BiologicalNodeAbstract> stateV;
	protected PickedState<BiologicalEdgeAbstract> stateE;
	private GraphInstance graphInstance = new GraphInstance();
	private final VisualizationModel<BiologicalNodeAbstract, BiologicalEdgeAbstract> visualizationModel;
	private final AggregateLayout<BiologicalNodeAbstract, BiologicalEdgeAbstract> clusteringLayout;
	private MyVertexLabelRenderer vlr = new MyVertexLabelRenderer(Color.blue);
	private MyEdgeLabelRenderer elr = new MyEdgeLabelRenderer(Color.blue);

	private MyAnnotationManager annotationManager;

	private boolean animatedPicking = false;

	NetworkSettings settings = NetworkSettings.getInstance();

	public AggregateLayout<BiologicalNodeAbstract, BiologicalEdgeAbstract> getClusteringLayout() {
		return clusteringLayout;
	}

	// private Paintable viewGrid;
	private MyGraphZoomScrollPane pane = null;
	private final Pathway pathway;

	public MyGraph(Pathway pw) {

		ScreenSize screenSize = new ScreenSize();
		VisualizationViewerWidth = (int) screenSize.getwidth() - 70;
		VisualizationViewerHeigth = (int) screenSize.getheight() - 100;

		this.pathway = pw;
		graphMouse = new MyEditingModalGraphMouse();
		Dimension preferredSize = new Dimension(VisualizationViewerWidth, VisualizationViewerHeigth);
		Dimension preferredSize2 = new Dimension(300, 200);

		// vertexLocations = new HashMap();

		/*
		 * Transformer<BiologicalNodeAbstract, Point2D> locationTransformer = new
		 * Transformer<BiologicalNodeAbstract, Point2D>() {
		 * 
		 * @Override public Point2D transform(BiologicalNodeAbstract vertex) {
		 * 
		 * // System.out.println(vertex); // System.out.println("pos: " +
		 * nodePositions.get(vertex)); Point2D p =
		 * vv.getRenderContext().getMultiLayerTransformer()
		 * .inverseTransform(nodePositions.get(vertex)); // System.out.println(
		 * "trans: "+p); // return layout.transform(vertex); return p; // return
		 * nodePositions.get(vertex); // return null; // int value = (vertex.intValue()
		 * * 40) + 20;
		 * 
		 * } };
		 */

		layout = new StaticLayout<BiologicalNodeAbstract, BiologicalEdgeAbstract>(g);// ,
																						// locationTransformer);

		layout.setSize(preferredSize);
		// layout.initialize(preferredSize, nodePositions);
		clusteringLayout = new AggregateLayout<BiologicalNodeAbstract, BiologicalEdgeAbstract>(layout);

		visualizationModel = new DefaultVisualizationModel<BiologicalNodeAbstract, BiologicalEdgeAbstract>(
				clusteringLayout, preferredSize);
		visualizationModel.getRelaxer().setSleepTime(10);
		// visualizationModel.setRelaxerThreadSleepTime(10);
		vv = new MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract>(visualizationModel,
				preferredSize, pathway);

		vv.setSize(preferredSize);
		vv.setMinimumSize(preferredSize);

		// vv.setFocusable(false);

		// vv.addKeyListener(new EventListener());
		vv.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// nothing to do
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// del-key
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					pathway.removeSelection();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					pathway.moveSelection(0, -1);
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					pathway.moveSelection(0, 1);
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					pathway.moveSelection(-1, 0);
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					pathway.moveSelection(1, 0);
				}
			}
		});

		vv.getRenderer().setVertexRenderer(new MultiVertexRenderer<BiologicalNodeAbstract, BiologicalEdgeAbstract>());
		vv2 = new SatelliteVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract>(vv, preferredSize2);
		vv2.setSize(preferredSize2);
		vv2.setMinimumSize(preferredSize2);

		// ScalingControl vv2Scaler = new CrossoverScalingControl();
		// vv2.scaleToLayout(vv2Scaler);

		satellitePr = vv2.getRenderContext();
		// viewGrid = (Paintable) new ViewGrid(vv2, vv);
		// set the Ranges-Layer to be painted before the actual graph
		vv.addPreRenderPaintable(RangeSelector.getInstance());

		// set the heatmap-layer to be painted before the actual graph
		// vv.addPreRenderPaintable(HeatgraphLayer.getInstance());

		// set the Ranges-Layer to get notified of mouse movements and clicks
		vv.addMouseListener(RangeSelector.getInstance());
		vv.addMouseMotionListener(RangeSelector.getInstance());

		vv.addMouseListener(RangeSelector.getInstance().getRangeShapeEditor());
		vv.addMouseMotionListener(RangeSelector.getInstance().getRangeShapeEditor());

		// set the inner nodes to be painted after the actual graph
		// vv.addPostRenderPaintable(new InnerNodeRenderer(vv));
		// vv.addPostRenderPaintable(new TokenRenderer(vv));
		pickSupport = new ShapePickSupport<BiologicalNodeAbstract, BiologicalEdgeAbstract>(vv);

		stateV = vv.getPickedVertexState();
		stateE = vv.getPickedEdgeState();

		vsh = new MyVertexStrokeHighlighting(stateV, stateE, pathway);
		vdpf = new MyVertexDrawPaintFunction(stateV, stateE, pathway);
		vfpf = new MyVertexFillPaintFunction(stateV, stateE, pathway);
		vit = new MyVertexIconTransformer();
		// vssa = new VertexShapeSize(pathway);
		esh = new MyEdgeStrokeHighlighting(stateV, stateE, pathway);
		edpf = new MyEdgeDrawPaintFunction(stateV, stateE);
		efpf = new MyEdgeFillPaintFunction(stateV, stateE);

		vertexStringer = new MyVertexStringer();
		vertexShapeTransformer = new MyVertexShapeTransformer();
		edgeStringer = new MyEdgeStringer(pathway);
		esf = new MyEdgeShapeFunction(this.g);

		eaf = new MyEdgeArrowFunction();

		// graphMouse.setVertexLocations(nodePositions);
		// 1. vertexFactor, 2. edgeFactory
		graphMouse.add(new EditingPopupGraphMousePlugin<BiologicalNodeAbstract, BiologicalEdgeAbstract>(null, null));

		graphMouse.setMode(ModalGraphMouse.Mode.EDITING);

		vv.setPickSupport(pickSupport);

		if (settings != null) {
			if (settings.isBackgroundColor()) {
				vv.setBackground(Color.BLACK);
			} else {
				vv.setBackground(Color.WHITE);
			}
		} else {
			vv.setBackground(Color.WHITE);
		}
		vv.setGraphMouse(graphMouse);
		vv.setComponentPopupMenu(new GraphPopUp().getPopUp());
		// vv.setComponentPopupMenu(null);
		// vv.setToolTipFunction(new ToolTips());

		vlr.setRotateEdgeLabels(false);
		vlr.setForeground(Color.WHITE);

		pr = vv.getRenderContext();

		annotationManager = new MyAnnotationManager(pr);
		makeDefaultObjectVisualization();

		vv2.scaleToLayout(new CrossoverScalingControl());

		// g.addListener(new GraphListener(), GraphEventType.ALL_SINGLE_EVENTS);
		// stateV.addListener(new PickListener());
		// vv.getPickedEdgeState().addItemListener(new EdgePickListener());
		stateV.addItemListener(new ItemListener() {

			MainWindow w = MainWindow.getInstance();

			@Override
			public void itemStateChanged(ItemEvent e) {
				// System.out.println("changed");
				if (stateV.getSelectedObjects().length == 1) {
					graphInstance.setSelectedObject((BiologicalNodeAbstract) stateV.getSelectedObjects()[0]);

					if (!pathway.isHeadless()) {
						w.updateElementProperties();
					}
				}
				if (pathway.isPetriNet() || pathway.getTransformationInformation() != null
						&& pathway.getTransformationInformation().getPetriNet() != null) {
					if (pathway.getPetriPropertiesNet().isPetriNetSimulation()) {
						// System.out.println("sim");
						w.updateSimulationResultView();
					}
				}
			}
		});

		stateE.addItemListener(new ItemListener() {
			MainWindow w = MainWindow.getInstance();

			@Override
			public void itemStateChanged(ItemEvent e) {
				// System.out.println("changed");
				if (stateE.getSelectedObjects().length == 1) {
					graphInstance.setSelectedObject((BiologicalEdgeAbstract) stateE.getSelectedObjects()[0]);
					if (!pathway.isHeadless()) {
						w.updateElementProperties();
					}
				}
				if (pathway.isPetriNet() || pathway.getTransformationInformation() != null
						&& pathway.getTransformationInformation().getPetriNet() != null) {
					if (pathway.getPetriPropertiesNet().isPetriNetSimulation()) {
						// System.out.println("sim");
						w.updateSimulationResultView();
					}
				}
			}
		});
		setMouseModePick();

	}

	public void makeDefaultObjectVisualization() {
		pr.setEdgeStrokeTransformer(esh);

		pr.setEdgeDrawPaintTransformer(edpf);
		pr.setEdgeFillPaintTransformer(efpf);

		pr.setEdgeShapeTransformer(esf);

		pr.setVertexLabelRenderer(vlr);
		pr.setEdgeLabelRenderer(elr);

		pr.setEdgeArrowTransformer(eaf);

		pr.setVertexStrokeTransformer(vsh);
		pr.setVertexLabelTransformer(vertexStringer);

		pr.setVertexShapeTransformer(vertexShapeTransformer);

		pr.setEdgeLabelTransformer(this.edgeStringer);

		pr.setVertexDrawPaintTransformer(vdpf);
		pr.setVertexFillPaintTransformer(vfpf);

		pr.setVertexIconTransformer(vit);

		vv.addPostRenderPaintable(new TokenRenderer(pathway));

		satellitePr.setVertexStrokeTransformer(vsh);
		satellitePr.setVertexLabelTransformer(vertexStringer);

		satellitePr.setVertexShapeTransformer(vertexShapeTransformer);
		satellitePr.setEdgeLabelTransformer(this.edgeStringer);
		satellitePr.setVertexDrawPaintTransformer(vdpf);
		satellitePr.setVertexFillPaintTransformer(vfpf);
		satellitePr.setVertexIconTransformer(vit);
		satellitePr.setEdgeStrokeTransformer(esh);
		satellitePr.setEdgeDrawPaintTransformer(edpf);
		satellitePr.setEdgeFillPaintTransformer(efpf);
		satellitePr.setEdgeShapeTransformer(esf);
		satellitePr.setVertexLabelRenderer(vlr);
		satellitePr.setEdgeLabelRenderer(elr);
		satellitePr.setEdgeArrowTransformer(eaf);
	}

	public void restartVisualizationModel() {
		pathway.updateMyGraph();
		// vv.getModel().restart();
	}

	/*
	 * public void markNewVertices() { HashSet<BiologicalNodeAbstract> set =
	 * pathway.getNewLoadedNodes(); for (Iterator<BiologicalNodeAbstract> it =
	 * set.iterator(); it.hasNext();) { Vertex v = it.next().getVertex();
	 * vpf.getNewLoadedDrawPaint(v); } }
	 */

	public void moveVertex(BiologicalNodeAbstract vertex, double xPos, double yPos) {
		Point2D p = new Point.Double(xPos, yPos);
		// nodePositions.setLocation(vertex, vv.getRenderContext()
		// .getMultiLayerTransformer().inverseTransform(p));
		// this.nodePositions.remove(vertex);
		// this.nodePositions.put(vertex, p);
		layout.setLocation(vertex, p);
	}

	public MyGraphZoomScrollPane getGraphVisualization() {
		if (pane == null) {
			pane = new MyGraphZoomScrollPane(vv) {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;
				// public void setVisible(boolean v){
				// if(!v){
				// vv.stop();
				// }
				// }
			};
		}
		return pane;
	}

	public boolean addVertex(BiologicalNodeAbstract bna, Point2D p) {

		/*
		 * Point2D p = this.nodePositions.get(bna);
		 * 
		 * if (bna == null){ System.out.println("bna null"); } if(p == null){
		 * System.out.println("p null"); }
		 */
		// BiologicalNodeAbstract bna = new BiologicalNodeAbstract("", "");
		// bna = bna1;
		// bna.setName("");
		// bna.setLabel("");
		// System.out.println("drin");
		// System.out.println(p.toString());
		// System.out.println(bna.isReference());
		// System.out.println(bna1.isReference());
		// System.out.println(this.nodePositions.size());
		// this.layout.setLocation(bna, p);
		// this.layout.lock(bna, true);
		// g.addVertex(bna);
		// bna.setName("adf");
		// vv.repaint();
		// System.out.println("V added: "+bna);
		this.layout.setLocation(bna, p);
		// this.layout.lock(bna, true);

		return g.addVertex(bna);

	}

	/*
	 * public Vertex createNewVertex() { return g.addVertex(new SparseVertex()); }
	 */

	public SatelliteVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> getSatelliteView() {
		vv2.setDoubleBuffered(true);
		return vv2;
	}

	// TEST ******************
	/*
	 * public boolean isVertexPicked(Vertex v) { return stateV.isPicked(v); }
	 * 
	 * public boolean isEdgePicked(Edge e) { return stateV.isPicked(e); }
	 */

	public Point2D getVertexLocation(BiologicalNodeAbstract vertex) {
		// System.out.println("1: "+this.getVertexLocation(vertex));
		// System.out.println("2:
		// "+visualizationModel.getGraphLayout().transform(vertex));

		// return visualizationModel.getGraphLayout().transform(vertex);
		return layout.apply(vertex);
		// return (Point2D) nodePositions.get(vertex);
	}

	// TEST ENDE *************

	// public void pickVertex(Vertex v) {
	// stateV.pick(v, true);
	// }
	//
	// public void pickEdge(Edge e) {
	// stateV.pick(e, true);
	// }

	// public Edge createEdge(Vertex vertex1, Vertex vertex2, boolean directed)
	// {
	// if (vertex1 != null && vertex2 != null) {
	// Edge newEdge = null;
	// if (directed) {
	// newEdge = new DirectedSparseEdge(vertex1, vertex2);
	// } else {
	// newEdge = new UndirectedSparseEdge(vertex1, vertex2);
	// }
	// return newEdge;
	// }
	// return null;
	// }

	public boolean addEdge(BiologicalEdgeAbstract bea) {
		// Graph gs=vv.getGraphLayout().getGraph();

		// BiologicalEdgeAbstract bea = (BiologicalEdgeAbstract) edge;

		// System.out.println("from: " + bea.getFrom());
		// System.out.println("to: " + bea.getTo());
		// g.addVertex("2");
		// g.addVertex(bea.getFrom());
		// g.addVertex(bea.getTo());

		if (bea.isDirected()) {

			// BiologicalNodeAbstract f;
			// BiologicalNodeAbstract t;

			// g.removeVertex(bea.getFrom());
			// //g.removeVertex(bea.getTo());
			// BiologicalNodeAbstract bna = new BiologicalNodeAbstract("",
			// "");
			// nodePositions.put(bna, new Point(10, 10));
			// System.out.println(g.containsVertex(bea.getFrom()));
			// System.out.println(g.containsVertex(bea.getTo()));
			// System.out.println("from: " + bea.getFrom().getID());
			// System.out.println("to: " + bea.getTo().getID());
			// BiologicalNodeAbstract bna1 = new BiologicalNodeAbstract("",
			// "");
			// BiologicalNodeAbstract bna2 = new BiologicalNodeAbstract("",
			// "");
			// Iterator<BiologicalNodeAbstract> it = g.getVertices()
			// .iterator();
			// bna1 = it.next();
			// bna2 = it.next();
			// this.addVertex(bna1, new Point(30, 30));
			// this.addVertex(bna2, new Point(50, 50));
			// System.out.println("pos2: "+g.getVertices());
			// System.out.println("pos2: "+nodePositions.get(bea.getTo()));
			// g.addVertex(new BiologicalNodeAbstract("", ""));
			// System.out.println(layout.getX(bea.getFrom()));
			// System.out.println(layout.getY(bea.getFrom()));
			// System.out.println(layout.getX(bea.getTo()));
			// System.out.println(layout.getY(bea.getTo()));
			// System.out.println(layout.transform(bea.getFrom()));
			// System.out.println(layout.transform(bea.getTo()));
			// System.out.println(bea.getFrom() == bea.getTo());
			// System.out.println(g.addEdge(new BiologicalEdgeAbstract("",
			// "", bna1, bna2), bna1,bna2, EdgeType.DIRECTED));//,from, to,
			// EdgeType.DIRECTED);
			// layout.setLocation(bea.getFrom(), 20, 20);
			return g.addEdge(bea, bea.getFrom(), bea.getTo(), EdgeType.DIRECTED);
		} else {
			return g.addEdge(bea, bea.getFrom(), bea.getTo(), EdgeType.UNDIRECTED);
		}
		// Pair p = g.getEndpoints(bea.toString());
		// System.out.println("f: "+layout.transform(p.getFirst()));
		// System.out.println("s: "+layout.transform(p.getSecond()));
		// System.out.println(g.findEdge(bea.getFrom(), bea.getTo()));
		// getEdgeStringer().addEdge(edge);
		// System.out.println(g);
		// vv.revalidate();
		// vv.

		// this.fitScaleOfViewer(vv);
		// System.out.println(this.nodePositions.size());
		// vv.repaint();
	}

	public void updateGraph() {
		vv.repaint();
	}

	public void setMouseModePick() {
		graphMouse.setAnimated(animatedPicking);
		vv.setCursor(new Cursor(Cursor.HAND_CURSOR));
		graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
		vv.setGraphMouse(graphMouse);
		// vv.setFocusable(true);
		// vv.requestFocus();
		// vv.setFocusable(false);

		// Event e = new MouseEvent(vv, arg1, arg2, arg3, arg4, arg5, arg6,
		// arg7, arg8, arg9, arg10)
	}

	public void setMouseModeTransform() {
		stateV.clear();
		// stateV.clearPickedEdges();
		// stateV.clearPickedVertices();
		vv.setCursor(new Cursor(Cursor.MOVE_CURSOR));
		graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		vv.setGraphMouse(graphMouse);
	}

	public void setMouseModeEditing() {
		stateV.clear();
		// stateV.clearPickedEdges();
		// stateV.clearPickedVertices();
		vv.setCursor(new Cursor(Cursor.HAND_CURSOR));
		graphMouse.setMode(ModalGraphMouse.Mode.EDITING);
		vv.setGraphMouse(graphMouse);
	}

	public void setMouseModeSelectRange() {
		stateV.clear();
		// stateV.clearPickedEdges();
		// stateV.clearPickedVertices();
		vv.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		graphMouse.setMode(null);
		graphMouse.disableAll();
	}

	public void zoomIn() {
		Runnable animator = new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < 5; i++) {
					scaler.scale(vv, 1.1f, vv.getCenter());
					try {
						Thread.sleep(100);
					} catch (InterruptedException ex) {
					}
				}
			}
		};
		Thread thread = new Thread(animator);
		thread.start();
	}

	public void zoomOut() {

		Runnable animator = new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < 5; i++) {
					scaler.scale(vv, 1 / 1.1f, vv.getCenter());
					try {
						Thread.sleep(100);
					} catch (InterruptedException ex) {
					}
				}
			}
		};
		Thread thread = new Thread(animator);
		thread.start();
	}

	public MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> getVisualizationViewer() {
		vv.setDoubleBuffered(true);
		return vv;
	}

	/** picks all elements in the graph */
	public void pickAllElements() {
		Iterator<BiologicalNodeAbstract> it = this.getAllVertices().iterator();
		BiologicalNodeAbstract bna;
		while (it.hasNext()) {
			bna = it.next();

			// vv.getPickedVertexState().pick(bna, true);
			stateV.pick(bna, true);
		}

		Iterator<BiologicalEdgeAbstract> it2 = this.getAllEdges().iterator();
		BiologicalEdgeAbstract bea;
		while (it2.hasNext()) {
			bea = it2.next();
			stateE.pick(bea, true);
		}
	}

	public void clearPickedElements() {
		// vv.getPickedState().clearPickedEdges();
		// vv.getPickedState().clearPickedVertices();
		stateV.clear();
		stateE.clear();
		updateGraph();
	}

	public void clearPickedEdges() {
		// stateV.clearPickedEdges();
		stateE.clear();
	}

	// public Vector<Object> copySelection() {
	//
	// Vector<Object> ve = new Vector<Object>();
	// for (Vertex v : (Set<Vertex>) vv.getPickedState().getPickedVertices()) {
	// try {
	// ve.add(v.clone());
	// } catch (CloneNotSupportedException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// for (Edge e : (Set<Edge>) vv.getPickedState().getPickedEdges()) {
	// try {
	// ve.add(e.clone());
	// } catch (CloneNotSupportedException e1) {
	// e1.printStackTrace();
	// }
	// }
	// return ve;
	// }

	private void setVertexStateDeleted(BiologicalNodeAbstract vertex) {
		vertex.setNodeStateChanged(NodeStateChanged.DELETED);
		for (BiologicalNodeAbstract child : vertex.getAllGraphNodes()) {
			if (!vertex.getEnvironment().contains(child)) {
				if (child.getAllGraphNodes().isEmpty()) {
					child.setNodeStateChanged(NodeStateChanged.DELETED);
				} else {
					setVertexStateDeleted(child);
				}
			}
		}
	}

	public void removeVertex(BiologicalNodeAbstract v) {
		g.removeVertex(v);
	}

	public void removeEdge(BiologicalEdgeAbstract bea) {
		// graphInstance.getPathway().removeElement(bea);
		g.removeEdge(bea);
	}

	public void removeAllVertices() {
		Set<BiologicalNodeAbstract> nodes = new HashSet<BiologicalNodeAbstract>();
		nodes.addAll(getAllVertices());
		for (BiologicalNodeAbstract n : nodes) {
			removeVertex(n);
		}
	}

	public void removeAllEdges() {
		Set<BiologicalEdgeAbstract> edges = new HashSet<BiologicalEdgeAbstract>();
		edges.addAll(getAllEdges());
		for (BiologicalEdgeAbstract e : edges) {
			removeEdge(e);
		}
	}

	public void removeAllElements() {
		removeAllEdges();
		removeAllVertices();
	}

	public Collection<BiologicalNodeAbstract> getAllVertices() {
		return g.getVertices();
	}

	public Collection<BiologicalEdgeAbstract> getAllEdges() {
		return g.getEdges();
	}

	// public void fillNodePositions() {
	// Iterator it = getAllvertices().iterator();
	// nodePositions.clear();
	// while (it.hasNext()) {
	// Vertex v = (Vertex) it.next();
	// Point2D pos = clusteringLayout.getLocation(v);
	// nodePositions.put(v, pos);
	// }
	// }
	//
	// public void changeToLastPositions() {
	// Iterator it = getAllvertices().iterator();
	// while (it.hasNext()) {
	// Vertex v = (Vertex) it.next();
	// moveVertex(v, nodePositions.get(v).getX(), nodePositions.get(v)
	// .getY());
	// }
	// }

	public VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> getVisualizationPaneCopy(
			Dimension size) {

		AggregateLayout<BiologicalNodeAbstract, BiologicalEdgeAbstract> clusteringLayout2 = new AggregateLayout<>(
				vv.getGraphLayout());

		VisualizationModel<BiologicalNodeAbstract, BiologicalEdgeAbstract> copyModel = new DefaultVisualizationModel<>(
				clusteringLayout2, size);
		VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> copyVV = new VisualizationViewer<>(
				copyModel, size);
		pr_compare = copyVV.getRenderContext();
		Function<BiologicalNodeAbstract, Shape> vertexPaint = new Function<>() {
			@Override
			public Shape apply(BiologicalNodeAbstract bna) {
				// System.out.println(bna.getClass().getName());
				// System.out.println(bna.getShape());
				return bna.getShape();
			}
		};

		pr_compare.setVertexStrokeTransformer(vsh);
		pr_compare.setVertexLabelTransformer(vertexStringer);
		pr_compare.setVertexShapeTransformer(vertexPaint);
		pr_compare.setEdgeLabelTransformer(this.edgeStringer);
		pr_compare.setVertexDrawPaintTransformer(vdpf);
		pr_compare.setVertexFillPaintTransformer(vfpf);
		pr_compare.setVertexIconTransformer(vit);
		pr_compare.setEdgeStrokeTransformer(esh);
		pr_compare.setEdgeDrawPaintTransformer(edpf);
		pr_compare.setEdgeFillPaintTransformer(efpf);
		pr_compare.setEdgeShapeTransformer(esf);
		pr_compare.setVertexLabelRenderer(vlr);
		pr_compare.setEdgeLabelRenderer(elr);
		pr_compare.setEdgeArrowTransformer(eaf);
		copyVV.setGraphMouse(graphMouse);
		// PickSupport copyPick = new ShapePickSupport();

		copyVV.setPickSupport(vv.getPickSupport());

		return copyVV;
	}

	public void changeToCircleLayout() {

		if (stateV.getPicked().isEmpty() || stateV.getPicked().size() == 0) {
			changeToLayout(new CircleLayout<BiologicalNodeAbstract, BiologicalEdgeAbstract>(g));
			// System.out.println("v: "+g.getVertexCount());
		} else {
			// this.clusteringLayout.addSubLayout(new CircularSubLayout(stateV
			// .getPicked(), clusteringLayout));
		}
	}

	public void changeToStaticLayout() {
		HashMap<BiologicalNodeAbstract, Point2D> map = new HashMap<BiologicalNodeAbstract, Point2D>();
		Iterator<BiologicalNodeAbstract> it = this.getAllVertices().iterator();
		BiologicalNodeAbstract bna;

		Point2D p;
		while (it.hasNext()) {
			bna = it.next();
			p = this.getVertexLocation(bna);
			map.put(bna, p);
		}

		// shifting to 100,100
		GraphCenter gc = new GraphCenter(this);
		double minX = gc.getMinX();
		double minY = gc.getMinY();
		double offsetX = 0;
		double offsetY = 0;
		if (minX < 0) {
			offsetX = Math.abs(minX) + 100;
		} else if (minX < 100) {
			offsetX = 100 - minX;
		}

		if (minY < 0) {
			offsetY = Math.abs(minY) + 100;
		} else if (minY < 100) {
			offsetY = 100 - minY;
		}

		changeToLayout(new StaticLayout<BiologicalNodeAbstract, BiologicalEdgeAbstract>(g));
		it = this.getAllVertices().iterator();
		while (it.hasNext()) {
			bna = it.next();
			this.moveVertex(bna, map.get(bna).getX() + offsetX, map.get(bna).getY() + offsetY);
		}
		// MyAnnotationManager am =
		// graphInstance.getPathway().getGraph().getAnnotationManager();

		annotationManager.moveAllAnnotation(offsetX, offsetY);
	}

	public void changeToGEMLayout() {
		Collection<BiologicalNodeAbstract> nodes = getVisualizationViewer().getPickedVertexState().getPicked();
		if (nodes.size() > 0) {
			Map<BiologicalNodeAbstract, Point2D> map = new HashMap<>();
			// put unpicked nodes to static
			for (BiologicalNodeAbstract n : getAllVertices()) {
				if (!nodes.contains(n)) {
					map.put(n, getVertexLocation(n));
				}
			}
			changeToLayout(new GEMLayout<>(g, map));
			PopUpDialog.getInstance().show("GEMLayout", "GEMLayout was applied on picked nodes only!");
		} else {
			changeToLayout(new GEMLayout<BiologicalNodeAbstract, BiologicalEdgeAbstract>(g));
		}
	}

	public void changeToHEBLayout() {
		if (layout instanceof HEBLayout && !((HEBLayout) layout).getConfig().resetLayout()) {
			if (((HEBLayout) layout).getConfig().getAutoRelayout()) {
				changeToLayout(new HEBLayout(g, ((HEBLayout) layout).getOrder()));
			} else {
				((HEBLayout) layout).saveCurrentOrder();
			}
			return;
		}
		changeToLayout(new HEBLayout(g));
	}

	public void changeToHCTLayout() {
		changeToLayout(new HCTLayout(g, pathway.getRootNode()));
	}

	public void changeToISOMLayout() {
		changeToLayout(new ISOMLayout<>(g));
	}

	public void changeToFRLayout() {
		changeToLayout(new FRLayout<>(g));
	}

	public void changeToKKLayout() {
		changeToLayout(new KKLayout<>(g));
	}

	public void changeToSpringLayout() {
		changeToLayout(new SpringLayout<>(g));
	}

	public void updateLayout() {
		if (layout instanceof HEBLayout) {
			changeToHEBLayout();
		} else if (layout instanceof HCTLayout) {
			changeToHCTLayout();
		} else if (layout instanceof GEMLayout) {
			// changeToGEMLayout();
		} else if (layout instanceof StaticLayout) {
			// changeToStaticLayout();
		} else if (layout instanceof CircleLayout) {
			changeToCircleLayout();
		} else if (layout instanceof SpringLayout) {
			// changeToSpringLayout();
		} else if (layout instanceof ISOMLayout) {
			// changeToISOMLayout();
		} else if (layout instanceof FRLayout) {
			// changeToFRLayout();
		} else if (layout instanceof KKLayout) {
			// changeToKKLayout();
		}
	}

	public AbstractLayout<BiologicalNodeAbstract, BiologicalEdgeAbstract> getLayout() {
		return layout;
	}

	private void changeToLayout(AbstractLayout<BiologicalNodeAbstract, BiologicalEdgeAbstract> layout) {
		// System.out.println(getAnnotationManager().getAnnotations().size());
		// System.out.println(vv.getRenderContext());
		makeDefaultObjectVisualization();
		// System.out.println(getAnnotationManager().getAnnotations().size());
		// System.out.println(vv.getRenderContext());
		if (this.getAllVertices().size() == 0) {
			return;
		}

		this.layout = layout;
		// this.clusteringLayout.removeAllSubLayouts();
		// Dimension oldDim = clusteringLayout.getSize();// getCurrentSize();
		// vv.setLayout(new BorderLayout());

		// vv.setGraphLayout(layout);
		// vv2.setGraphLayout(layout);

		// vv.setModel(visualizationModel);
		// vv2.setModel(visualizationModel);

		// normalCentering();
		// this.clusteringLayout.setDelegate(layout);
		// this.clusteringLayout.initialize();//initialize(oldDim);

		/*
		 * if (layout.isIncremental()) { vv.restart(); }
		 */

		// normalCentering(vv2);
		// normalCentering(vv);
		// normalCentering();

		visualizationModel.setGraphLayout(layout);

		Runnable center = new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(500);

					// fitScaleOfViewer(vv2);
					// fitScaleOfViewer(vv);

					normalCentering();
					// fitScaleOfSatellitView();
					// normalCentering();
					// System.out.println("test");
				} catch (InterruptedException ex) {
				}

			}
		};
		Thread thread = new Thread(center);
		thread.start();

		// this.normalCentering(vv);
		pathway.saveVertexLocations();
	}

	public Point2D findNearestFreeVertexPosition(double startSearchCoordinatesX, double startSearchCoordinatesY,
			double minDistance) {
		double radiusIncrease = 5;
		double degreeIncrease = 5;

		double radius = 0;
		while (true) {
			for (int i = 0; i < 365; i += degreeIncrease) {
				Point2D coords = new Point();
				coords.setLocation(startSearchCoordinatesX + radius * Math.sin(Math.toRadians(i)),
						startSearchCoordinatesY + radius * Math.cos(Math.toRadians(i)));
				boolean positionOK = true;

				Iterator<BiologicalNodeAbstract> it = pathway.getAllGraphNodes().iterator();
				while (it.hasNext()) {
					BiologicalNodeAbstract bna = it.next();
					Point2D p = getVertexLocation(bna);
					if (coords.distance(p) < minDistance)
						positionOK = false;
				}
				if (positionOK)
					return coords;
			}
			radius += radiusIncrease;
		}

	}

	public void fitScaleOfViewer(VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> viewer) {

		viewer.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).setScale(1, 1,
				viewer.getCenter());
		viewer.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).setScale(1, 1,
				viewer.getCenter());

		GraphCenter gc = new GraphCenter(this);

		double width = gc.getWidth() + 100;
		double height = gc.getHeight() + 100;
		// System.out.println("dim: " + width + " " + height);

		Dimension viewSize = viewer.getSize();
		// System.out.println(viewSize);
		// System.out.println(viewSize);
		// GraphCenter graphCenter = new GraphCenter(this,
		// viewer.getGraphLayout());
		// System.out.println("w: "+viewer.getWidth());
		// System.out.println(":
		// "+this.visualizationModel.getGraphLayout().getSize());
		// GraphCenter graphCenter = new GraphCenter(viewer.getGraphLayout()
		// .getGraph(), viewer.getGraphLayout());

		// System.out.println("w: "+ new Point((int)graphCenter.getWidth(),
		// (int)graphCenter.getHeight()));
		// System.out.println("h: "+graphCenter.getHeight());
		// System.out.println("w: "+viewSize.width);
		// System.out.println("h: "+viewSize.height);
		float scalex = (float) viewSize.width / ((float) width);
		float scaley = (float) viewSize.height / ((float) height);
		float scale = 1;
		// System.out.println("x: " + scalex);
		// System.out.println("y: " + scaley);

		if (scalex < scaley) {
			scale = scalex;
		} else {
			scale = scaley;
		}
		scaler.scale(viewer, scale, viewer.getCenter());
		// System.out.println(scale);
		// System.out.println("scale: "+scale);
		// viewer.getViewTransformer().setScale(scale, scale, new
		// Point2D.Float());
		// new LayoutScalingControl().scale(viewer, scale, new Point2D.Float());
		// GraphCenter graphCenter2 = new GraphCenter(this,
		// viewer.getGraphLayout());
		// System.out.println("w: "+ new Point((int)graphCenter2.getWidth(),
		// (int)graphCenter2.getHeight()));
		// System.out.println("w: "+viewer.getWidth());
		// System.out.println(":
		// "+this.visualizationModel.getGraphLayout().getSize());
		// GraphCenter graphCenter = new GraphCenter(viewer.getGraphLayout()
		// .getGraph(), viewer.getGraphLayout());

		// System.out.println("w: "+graphCenter2.getWidth());
		// System.out.println("h: "+graphCenter2.getHeight());

	}

	public void normalCentering() {

		if (!getAllVertices().isEmpty()) {
			fitScaleOfViewer(this.vv);
			fitScaleOfViewer(this.vv2);
			normalCentering(this.vv);
			normalCentering(this.vv2);

			// vv.repaint();
			// vv.restart();

			// vv2.repaint();
			// vv2.restart();
		}

	}

	public void normalCentering(VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> viewer) {
		// System.out.println("drin");
		// System.out.println("drin");
		// GraphCenter graphCenter = new GraphCenter(viewer.getGraphLayout()
		// .getGraph(), viewer.getGraphLayout());
		GraphCenter graphCenter = new GraphCenter(this);

		// System.out.println("drin");
		// Layout layout = viewer.getGraphLayout();
		Point2D q = graphCenter.getCenter();
		// Point2D q = viewer.getCenter();
		// System.out.println("r: "+r);
		// System.out.println("q: "+q);
		Point2D lvc = viewer.getRenderContext().getMultiLayerTransformer().inverseTransform(viewer.getCenter());
		// System.out.println("q: "+viewer.getCenter());
		// System.out.println(lvc);
		final double dx = (lvc.getX() - q.getX());
		final double dy = (lvc.getY() - q.getY());
		// System.out.println(viewer.getClass()+" "+dx+" "+dy);
		viewer.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).translate(dx, dy);

		// viewer.getLayoutTransformer().translate(dx, dy);

	}

	public void animatedCentering() {

		// this.fitScaleOfViewer(vv);
		// System.out.println("bla");
		// System.out.println(vv.getSize());

		// System.out.println("animated centring");
		// vv.stop();

		GraphCenter graphCenter = new GraphCenter(this);
		Point2D q = graphCenter.getCenter(); // Point2D lvc =
		// vv.inverseTransform(vv.getCenter());
		Point2D lvc = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(vv.getCenter());

		final double dx = (lvc.getX() - q.getX()) / 10;
		final double dy = (lvc.getY() - q.getY()) / 10; // System.out.println(dx+"
														// "+dy);

		// System.out.println("nodes: "+g.getVertexCount()); //
		// System.out.println(g.getEdgeCount()); //
		// System.out.println(dx + " " + dy);
		Runnable animator = new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < 10; i++) { //
					// vv.getLayoutTransformer().translate(dx, dy);
					vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).translate(dx, dy);

					try {
						Thread.sleep(100);
					} catch (InterruptedException ex) {
					}
				}
			}
		};
		Thread thread = new Thread(animator);
		thread.start();

		// fitScaleOfViewer(this.vv2);
		// normalCentering(this.vv2);

	}

	// public void updateElementLabel(Object element) {
	//
	// GraphElementAbstract geb = (GraphElementAbstract) graphInstance
	// .getPathwayElement(element);
	// if (geb.isVertex()) {
	// vertexStringer.renameVertex(graphInstance
	// .getPathwayElement(element));
	// } else if (geb.isEdge()) {
	// // getEdgeStringer().renameEdge(
	// // graphInstance.getPathwayElement(element));
	// }
	// MainWindowSingelton.getInstance().updateElementTree();
	// }

	/*
	 * public void updateAllNodeLabels() {
	 * 
	 * vertexStringer.renameAllVertexNodes();
	 * 
	 * }
	 */

	/*
	 * public void updateAllEdgeLabels() {
	 * 
	 * getEdgeStringer().renameAllEdgeNodes();
	 * 
	 * }
	 */

	// public boolean areGraphElementsSelected() {
	// if (vv.getPickedState().getSelectedObjects() != null) {
	// return true;
	// }
	// return false;
	// }

	public void enableGraphTheory() {

		// stateV.clearPickedEdges();
		// stateV.clearPickedVertices();
		stateV.clear();
		stateE.clear();
		vdpf.setGraphTheory(true);
		vfpf.setGraphTheory(true);
		vsh.setGraphTheory(true);
		esh.setGraphTheory(true);
		edpf.setGraphTheory(true);
		efpf.setGraphTheory(true);
	}

	public void disableGraphTheory() {

		vdpf.setGraphTheory(false);
		vfpf.setGraphTheory(false);
		vsh.setGraphTheory(false);
		esh.setGraphTheory(false);
		edpf.setGraphTheory(false);
		efpf.setGraphTheory(false);
		RangeSelector.getInstance().setEnabled(false);
	}

	public Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> getJungGraph() {
		return g;
	}

	// public void setJungGraph(Graph graph) {
	// g = graph;
	// // }
	//
	// public HashMap getVertexLocations() {
	// return this.nodePositions;
	// }

	public void restartVis() {
		vv.revalidate();
		vv.repaint();
	}

	// public void setVertexShapeSize(VertexShapeSize vss) {
	//
	// this.vssa = vss;
	//
	// }
	//
	// /** sucht eine Verbindungskante zwischen zwei Elementen */
	// public Object getEdgeBetween(Vertex start, Vertex end) {
	// for (Object obj : g.getEdges()) {
	// Edge e = (Edge) obj;
	//
	// if (((e.getEndpoints().getFirst().equals(start)) && (e
	// .getEndpoints().getSecond().equals(end))) || // oder
	// // andersrum
	// ((e.getEndpoints().getFirst().equals(end)) && (e
	// .getEndpoints().getSecond().equals(start)))) {
	// return e;
	// }
	// }
	// return null;
	// }

	public MyEdgeStringer getEdgeStringer() {
		return edgeStringer;
	}

	public MyAnnotationManager getAnnotationManager() {
		return this.annotationManager;
	}

	public boolean isAnimatedPicking() {
		return animatedPicking;
	}

	public void setAnimatedPicking(boolean animatedPicking) {
		this.animatedPicking = animatedPicking;
		this.setMouseModePick();
	}

	public void setMouseModeHierarchy() {
		stateV.clear();
		// stateV.clearPickedEdges();
		// stateV.clearPickedVertices();
		vv.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		graphMouse.setMode(ModalGraphMouse.Mode.ANNOTATING);
		vv.setGraphMouse(graphMouse);

	}

	public MyEdgeDrawPaintFunction getEdgeDrawPaintFunction() {
		return edpf;
	}
}
