package graph.jung.classes;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import configurations.GraphSettings;
import configurations.Workspace;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.annotations.Annotation;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.EditingPopupGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.picking.ShapePickSupport;
import edu.uci.ics.jung.visualization.renderers.BasicEdgeRenderer;
import edu.uci.ics.jung.visualization.renderers.EdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.VertexLabelRenderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
import graph.GraphInstance;
import graph.eventhandlers.MyEditingModalGraphMouse;
import graph.gui.GraphPopUp;
import graph.jung.graphDrawing.MyArrowDrawPaintTransformer;
import graph.jung.graphDrawing.MyArrowFillPaintTransformer;
import graph.jung.graphDrawing.MyEdgeArrowFunction;
import graph.jung.graphDrawing.MyEdgeDrawPaintFunction;
import graph.jung.graphDrawing.MyEdgeFillPaintFunction;
import graph.jung.graphDrawing.MyEdgeFontTransformer;
import graph.jung.graphDrawing.MyEdgeLabelRenderer;
import graph.jung.graphDrawing.MyEdgeShapeFunction;
import graph.jung.graphDrawing.MyEdgeStringer;
import graph.jung.graphDrawing.MyEdgeStrokeHighlighting;
import graph.jung.graphDrawing.MyVertexDrawPaintFunction;
import graph.jung.graphDrawing.MyVertexFillPaintFunction;
import graph.jung.graphDrawing.MyVertexFontTransformer;
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
import gui.PopUpDialog;
import gui.algorithms.ScreenSize;
import gui.annotation.MyAnnotation;
import gui.annotation.MyAnnotationManager;
import gui.annotation.MyAnnotationEditingGraphMouse;
import gui.visualization.TokenRenderer;
import util.VanesaUtility;

public class MyGraph {
	private Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g = new SparseMultigraph<>();
	private final MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv;
	private AbstractLayout<BiologicalNodeAbstract, BiologicalEdgeAbstract> layout;
	private final MyEditingModalGraphMouse graphMouse;
	private final ShapePickSupport<BiologicalNodeAbstract, BiologicalEdgeAbstract> pickSupport;
	private final ScalingControl scaler = new CrossoverScalingControl();
	// private final HashMap<BiologicalNodeAbstract, Point2D> vertexLocations;
	private final RenderContext<BiologicalNodeAbstract, BiologicalEdgeAbstract> pr;
	// private AbstractRenderer pr;
	private RenderContext<BiologicalNodeAbstract, BiologicalEdgeAbstract> pr_compare;
	private final MyVertexStringer vertexStringer;
	private final MyEdgeStringer edgeStringer;
	private final MyVertexShapeTransformer vertexShapeTransformer;
	private final MyVertexStrokeHighlighting vsh;
	private final MyVertexDrawPaintFunction vdpf;
	private final MyVertexFillPaintFunction vfpf;
	private final MyVertexIconTransformer vit;
	private final MyEdgeStrokeHighlighting esh;
	private final MyEdgeDrawPaintFunction edpf;
	private final MyEdgeFillPaintFunction efpf;
	private final MyEdgeShapeFunction esf;
	private final MyEdgeArrowFunction eaf;
	private final MyArrowDrawPaintTransformer adpt;
	private final MyArrowFillPaintTransformer afpt;
	private final MyVertexFontTransformer vft;
	private final MyEdgeFontTransformer eft;

	// defaults for vertices
	private VertexLabelRenderer vertexLabelRendererDefault;
	private Function<? super BiologicalNodeAbstract, Stroke> vertexStrokeTransformerDefault;
	private Function<? super BiologicalNodeAbstract, String> vertexLabelTransformerDefault;
	private Function<? super BiologicalNodeAbstract, Shape> vertexShapeTransformerDefault;
	private Function<? super BiologicalNodeAbstract, Paint> vertexDrawPaintTransformerDefault;
	private Function<? super BiologicalNodeAbstract, Paint> vertexFillPaintTransformerDefault;
	private Function<? super BiologicalNodeAbstract, Icon> vertexIconTransformerDefault;
	private Function<? super BiologicalNodeAbstract, Font> vertexFontTransformerDefault;

	// defaults for edges
	private Function<? super BiologicalEdgeAbstract, Stroke> edgeStrokeTransformerDefault;
	private Function<? super BiologicalEdgeAbstract, Paint> edgeDrawPaintTransformerDefault;
	private Function<? super BiologicalEdgeAbstract, Paint> edgeFillPaintTransformerDefault;
	private Function<? super BiologicalEdgeAbstract, Font> edgeFontTransformerDefault;
	private Function<? super BiologicalEdgeAbstract, Shape> edgeShapeTransformerDefault;
	private EdgeLabelRenderer edgeLabelRendererDefault;
	private Function<? super BiologicalEdgeAbstract, String> edgeLabelTransformerDefault;

	// defaults for arrows
	private Function<? super Context<Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract>, BiologicalEdgeAbstract>, Shape> edgeArrowTransformerDefault;
	private Function<? super BiologicalEdgeAbstract, Paint> arrowDrawPaintTransformerDefault;
	private Function<? super BiologicalEdgeAbstract, Paint> arrowFillPaintTransformerDefault;

	private final PickedState<BiologicalNodeAbstract> stateV;
	private final PickedState<BiologicalEdgeAbstract> stateE;
	private final VisualizationModel<BiologicalNodeAbstract, BiologicalEdgeAbstract> visualizationModel;
	private final MyVertexLabelRenderer vlr = new MyVertexLabelRenderer(Color.blue);
	private final MyEdgeLabelRenderer elr = new MyEdgeLabelRenderer(Color.blue);

	private final MyAnnotationManager annotationManager;

	private boolean animatedPicking = false;

	private final GraphSettings settings = GraphSettings.getInstance();

	private GraphZoomScrollPane pane = null;
	private final Pathway pathway;

	public MyGraph(Pathway pw) {
		ScreenSize screenSize = new ScreenSize();
		int visualizationViewerWidth = screenSize.width - 70;
		int visualizationViewerHeight = screenSize.height - 100;

		this.pathway = pw;
		graphMouse = new MyEditingModalGraphMouse();
		Dimension preferredSize = new Dimension(visualizationViewerWidth, visualizationViewerHeight);
		Dimension preferredSize2 = new Dimension(300, 200);

		layout = new StaticLayout<>(g);

		layout.setSize(preferredSize);

		visualizationModel = new DefaultVisualizationModel<>(new AggregateLayout<>(layout), preferredSize);
		visualizationModel.getRelaxer().setSleepTime(10);
		vv = new MyVisualizationViewer<>(visualizationModel, preferredSize, pathway);

		vv.setSize(preferredSize);
		vv.setMinimumSize(preferredSize);

		vv.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// del-key
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					pathway.removeSelection();

					if (GraphInstance.getSelectedObject() instanceof MyAnnotation) {
						MyAnnotation annotation = (MyAnnotation) GraphInstance.getSelectedObject();
						if (MyAnnotationEditingGraphMouse.getInstance().getHighlight() != null
								|| StringUtils.isNotEmpty(annotation.getText()))
							getAnnotationManager().remove(annotation);
						GraphInstance.setSelectedObject(null);
						MyAnnotationEditingGraphMouse.getInstance().setEnabled(false);
					}
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

		vv.getRenderer().setVertexRenderer(new MultiVertexRenderer<>());
		fetchDefaultTransformers(vv.getRenderContext());

		// set the heatmap-layer to be painted before the actual graph
		// vv.addPreRenderPaintable(HeatgraphLayer.getInstance());

		// set the Ranges-Layer to get notified of mouse movements and clicks
		// vv.addMouseListener(RangeSelector.getInstance());
		// vv.addMouseMotionListener(RangeSelector.getInstance());

		vv.addMouseListener(MyAnnotationEditingGraphMouse.getInstance());
		vv.addMouseMotionListener(MyAnnotationEditingGraphMouse.getInstance());

		// set the inner nodes to be painted after the actual graph
		// vv.addPostRenderPaintable(new InnerNodeRenderer(vv));
		// vv.addPostRenderPaintable(new TokenRenderer(vv));
		pickSupport = new ShapePickSupport<>(vv);

		stateV = vv.getPickedVertexState();
		stateE = vv.getPickedEdgeState();

		vsh = new MyVertexStrokeHighlighting(stateV, stateE, pathway);
		vdpf = new MyVertexDrawPaintFunction(stateV, stateE, pathway);
		vfpf = new MyVertexFillPaintFunction(stateV, stateE, pathway);
		vit = new MyVertexIconTransformer();
		esh = new MyEdgeStrokeHighlighting(stateV, stateE, pathway);
		edpf = new MyEdgeDrawPaintFunction(stateV, stateE);
		efpf = new MyEdgeFillPaintFunction(stateV, stateE);

		vertexStringer = new MyVertexStringer();
		vertexShapeTransformer = new MyVertexShapeTransformer();
		edgeStringer = new MyEdgeStringer();
		esf = new MyEdgeShapeFunction(this.g);

		eaf = new MyEdgeArrowFunction();
		adpt = new MyArrowDrawPaintTransformer();
		afpt = new MyArrowFillPaintTransformer();

		vft = new MyVertexFontTransformer();
		eft = new MyEdgeFontTransformer();

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

		// g.addListener(new GraphListener(), GraphEventType.ALL_SINGLE_EVENTS);
		// stateV.addListener(new PickListener());
		// vv.getPickedEdgeState().addItemListener(new EdgePickListener());
		stateV.addItemListener(e -> {
			final MainWindow w = MainWindow.getInstance();
			if (stateV.getSelectedObjects().length == 1) {
				GraphInstance.setSelectedObject((BiologicalNodeAbstract) stateV.getSelectedObjects()[0]);
				if (!pathway.isHeadless()) {
					w.updateElementProperties();
				}
			}
			if (pathway.isPetriNet() || pathway.getTransformationInformation() != null
					&& pathway.getTransformationInformation().getPetriNet() != null) {
				if (pathway.getPetriPropertiesNet().isPetriNetSimulation()) {
					w.updateSimulationResultView();
				}
			}
		});

		stateE.addItemListener(e -> {
			final MainWindow w = MainWindow.getInstance();
			if (stateE.getSelectedObjects().length == 1) {
				GraphInstance.setSelectedObject((BiologicalEdgeAbstract) stateE.getSelectedObjects()[0]);
				if (!pathway.isHeadless()) {
					w.updateElementProperties();
				}
			}
			if (pathway.isPetriNet() || pathway.getTransformationInformation() != null
					&& pathway.getTransformationInformation().getPetriNet() != null) {
				if (pathway.getPetriPropertiesNet().isPetriNetSimulation()) {
					w.updateSimulationResultView();
				}
			}
		});
		setMouseModePick();
		disableAntiAliasing(Workspace.getCurrentSettings().isDisabledAntiAliasing());
		improvePerformance(vv);

		vv.addMouseWheelListener(e -> updateLabelVisibilityOnZoom());
	}

	public void updateLabelVisibilityOnZoom() {
		Font vertexFont = GraphSettings.getInstance().getVertexFont();
		if (vertexFont == null) {
			vertexFont = vv.getFont();
		}
		Font edgeFont = GraphSettings.getInstance().getEdgeFont();
		if (edgeFont == null) {
			edgeFont = vv.getFont();
		}
		vlr.setDisabled(vertexFont.getSize() * vv.getScale() <= settings.getMinVertexFontSize());
		elr.setDisabled(edgeFont.getSize() * vv.getScale() <= settings.getMinEdgeFontSize());
	}

	public void makeDefaultObjectVisualization() {
		if (!GraphSettings.getInstance().isDefaultTransformers()) {
			addTransformersToVV(false);
		}
	}

	private void fetchDefaultTransformers(RenderContext<BiologicalNodeAbstract, BiologicalEdgeAbstract> rc) {
		// vertices
		vertexLabelRendererDefault = rc.getVertexLabelRenderer();
		vertexStrokeTransformerDefault = rc.getVertexStrokeTransformer();
		vertexLabelTransformerDefault = rc.getVertexLabelTransformer();
		vertexShapeTransformerDefault = rc.getVertexShapeTransformer();
		vertexDrawPaintTransformerDefault = rc.getVertexDrawPaintTransformer();
		vertexFillPaintTransformerDefault = rc.getVertexFillPaintTransformer();
		vertexIconTransformerDefault = rc.getVertexIconTransformer();
		vertexFontTransformerDefault = rc.getVertexFontTransformer();
		// edges
		edgeStrokeTransformerDefault = rc.getEdgeStrokeTransformer();
		edgeDrawPaintTransformerDefault = rc.getEdgeDrawPaintTransformer();
		edgeFillPaintTransformerDefault = rc.getEdgeFillPaintTransformer();
		edgeFontTransformerDefault = rc.getEdgeFontTransformer();
		edgeShapeTransformerDefault = rc.getEdgeShapeTransformer();
		edgeLabelRendererDefault = rc.getEdgeLabelRenderer();
		edgeLabelTransformerDefault = rc.getEdgeLabelTransformer();
		// arrows
		edgeArrowTransformerDefault = rc.getEdgeArrowTransformer();
		arrowDrawPaintTransformerDefault = rc.getArrowDrawPaintTransformer();
		arrowFillPaintTransformerDefault = rc.getArrowFillPaintTransformer();
	}

	public void addTransformersToVV(boolean repaint) {
		// vertices
		pr.setVertexDrawPaintTransformer(vdpf);
		pr.setVertexFillPaintTransformer(vfpf);
		pr.setVertexIconTransformer(vit);
		pr.setVertexFontTransformer(vft);
		pr.setVertexLabelRenderer(vlr);
		pr.setVertexStrokeTransformer(vsh);
		pr.setVertexLabelTransformer(vertexStringer);
		pr.setVertexShapeTransformer(vertexShapeTransformer);
		// edges
		pr.setEdgeLabelTransformer(edgeStringer);
		pr.setEdgeFontTransformer(eft);
		pr.setEdgeStrokeTransformer(esh);
		pr.setEdgeDrawPaintTransformer(edpf);
		pr.setEdgeFillPaintTransformer(efpf);
		pr.setEdgeShapeTransformer(esf);
		// pr.setEdgeLabelRenderer(elr);
		vv.getRenderer().setEdgeLabelRenderer(elr);
		// arrows
		pr.setEdgeArrowTransformer(eaf);
		pr.setArrowDrawPaintTransformer(adpt);
		pr.setArrowFillPaintTransformer(afpt);

		vv.addPostRenderPaintable(new TokenRenderer(pathway));
		if (repaint) {
			vv.repaint();
		}
	}

	public void dropTransformersOfVV(boolean repaint) {
		// vertices
		pr.setVertexLabelRenderer(vertexLabelRendererDefault);
		pr.setVertexStrokeTransformer(vertexStrokeTransformerDefault);
		pr.setVertexLabelTransformer(vertexLabelTransformerDefault);
		pr.setVertexShapeTransformer(vertexShapeTransformerDefault);
		pr.setVertexDrawPaintTransformer(vertexDrawPaintTransformerDefault);
		pr.setVertexFillPaintTransformer(vertexFillPaintTransformerDefault);
		pr.setVertexIconTransformer(vertexIconTransformerDefault);
		pr.setVertexFontTransformer(vertexFontTransformerDefault);
		// edges
		pr.setEdgeStrokeTransformer(edgeStrokeTransformerDefault);
		pr.setEdgeDrawPaintTransformer(edgeDrawPaintTransformerDefault);
		pr.setEdgeFillPaintTransformer(edgeFillPaintTransformerDefault);
		pr.setEdgeShapeTransformer(edgeShapeTransformerDefault);
		pr.setEdgeLabelRenderer(edgeLabelRendererDefault);
		pr.setEdgeLabelTransformer(edgeLabelTransformerDefault);
		pr.setEdgeFontTransformer(edgeFontTransformerDefault);
		// arrows
		pr.setEdgeArrowTransformer(edgeArrowTransformerDefault);
		pr.setArrowDrawPaintTransformer(arrowDrawPaintTransformerDefault);
		pr.setArrowFillPaintTransformer(arrowFillPaintTransformerDefault);

		if (repaint) {
			vv.repaint();
		}
	}

	public void restartVisualizationModel() {
		pathway.updateMyGraph();
	}

	public void moveVertex(BiologicalNodeAbstract vertex, double xPos, double yPos) {
		layout.setLocation(vertex, new Point.Double(xPos, yPos));
	}

	public GraphZoomScrollPane getGraphVisualization() {
		if (pane == null) {
			pane = new GraphZoomScrollPane(vv);
		}
		return pane;
	}

	public boolean addVertex(BiologicalNodeAbstract bna, Point2D p) {
		layout.setLocation(bna, p);
		return g.addVertex(bna);
	}

	public Point2D getVertexLocation(BiologicalNodeAbstract vertex) {
		return layout.apply(vertex);
	}

	public boolean addEdge(BiologicalEdgeAbstract bea) {
		return g.addEdge(bea, bea.getFrom(), bea.getTo(), bea.isDirected() ? EdgeType.DIRECTED : EdgeType.UNDIRECTED);
	}

	public void updateGraph() {
		vv.repaint();
	}

	public void setMouseModePick() {
		graphMouse.setAnimated(animatedPicking);
		vv.setCursor(new Cursor(Cursor.HAND_CURSOR));
		graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
		vv.setGraphMouse(graphMouse);
	}

	public void setMouseModeTransform() {
		stateV.clear();
		vv.setCursor(new Cursor(Cursor.MOVE_CURSOR));
		graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		vv.setGraphMouse(graphMouse);
	}

	public void setMouseModeEditing() {
		stateV.clear();
		vv.setCursor(new Cursor(Cursor.HAND_CURSOR));
		graphMouse.setMode(ModalGraphMouse.Mode.EDITING);
		vv.setGraphMouse(graphMouse);
	}

	public void setMouseModeSelectRange() {
		stateV.clear();
		vv.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		graphMouse.setMode(null);
		graphMouse.disableAll();
	}

	public void zoomIn() {
		Thread thread = new Thread(() -> {
			for (int i = 0; i < 5; i++) {
				scaler.scale(vv, 1.1f, vv.getCenter());
				VanesaUtility.trySleep(100);
			}
			updateLabelVisibilityOnZoom();
		});
		thread.start();
	}

	public void zoomOut() {
		Thread thread = new Thread(() -> {
			for (int i = 0; i < 5; i++) {
				scaler.scale(vv, 1 / 1.1f, vv.getCenter());
				VanesaUtility.trySleep(100);
			}
			updateLabelVisibilityOnZoom();
		});
		thread.start();
	}

	public MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> getVisualizationViewer() {
		vv.setDoubleBuffered(true);
		return vv;
	}

	/**
	 * Picks all nodes and edges in the graph
	 */
	public void pickAllElements() {
		for (final BiologicalNodeAbstract bna : g.getVertices()) {
			stateV.pick(bna, true);
		}
		for (final BiologicalEdgeAbstract bea : g.getEdges()) {
			stateE.pick(bea, true);
		}
	}

	public void clearPickedElements() {
		stateV.clear();
		stateE.clear();
		updateGraph();
	}

	public void clearPickedEdges() {
		stateE.clear();
	}

	public void removeVertex(BiologicalNodeAbstract v) {
		g.removeVertex(v);
	}

	public void removeEdge(BiologicalEdgeAbstract bea) {
		g.removeEdge(bea);
	}

	public void removeAllVertices() {
		Set<BiologicalNodeAbstract> nodes = new HashSet<>(getAllVertices());
		for (BiologicalNodeAbstract n : nodes) {
			removeVertex(n);
		}
	}

	public void removeAllEdges() {
		Set<BiologicalEdgeAbstract> edges = new HashSet<>(getAllEdges());
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

	public VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> getVisualizationPaneCopy(
			Dimension size) {

		AggregateLayout<BiologicalNodeAbstract, BiologicalEdgeAbstract> clusteringLayout2 = new AggregateLayout<>(
				vv.getGraphLayout());

		VisualizationModel<BiologicalNodeAbstract, BiologicalEdgeAbstract> copyModel = new DefaultVisualizationModel<>(
				clusteringLayout2, size);
		VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> copyVV = new VisualizationViewer<>(
				copyModel, size);
		pr_compare = copyVV.getRenderContext();
		pr_compare.setVertexStrokeTransformer(vsh);
		pr_compare.setVertexLabelTransformer(vertexStringer);
		pr_compare.setVertexShapeTransformer(BiologicalNodeAbstract::getShape);
		pr_compare.setEdgeLabelTransformer(edgeStringer);
		pr_compare.setVertexDrawPaintTransformer(vdpf);
		pr_compare.setVertexFillPaintTransformer(vfpf);
		pr_compare.setVertexIconTransformer(vit);
		pr_compare.setEdgeStrokeTransformer(esh);
		pr_compare.setEdgeDrawPaintTransformer(edpf);
		pr_compare.setEdgeFillPaintTransformer(efpf);
		pr_compare.setEdgeShapeTransformer(esf);
		pr_compare.setVertexLabelRenderer(vlr);
		// pr_compare.setEdgeLabelRenderer(elr);
		pr_compare.setEdgeArrowTransformer(eaf);
		pr_compare.setArrowDrawPaintTransformer(adpt);
		pr_compare.setArrowFillPaintTransformer(afpt);
		pr_compare.setVertexFontTransformer(vft);
		pr_compare.setEdgeFontTransformer(eft);
		copyVV.setGraphMouse(graphMouse);
		copyVV.setPickSupport(vv.getPickSupport());

		return copyVV;
	}

	public void changeToCircleLayout() {
		if (stateV.getPicked().isEmpty() || stateV.getPicked().isEmpty()) {
			changeToLayout(new CircleLayout<>(g));
		}
	}

	public void changeToStaticLayout() {
		HashMap<BiologicalNodeAbstract, Point2D> map = new HashMap<>();
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

		changeToLayout(new StaticLayout<>(g));
		it = this.getAllVertices().iterator();
		while (it.hasNext()) {
			bna = it.next();
			this.moveVertex(bna, map.get(bna).getX() + offsetX, map.get(bna).getY() + offsetY);
		}
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
			changeToLayout(new GEMLayout(g, map));
			PopUpDialog.getInstance().show("GEMLayout", "GEMLayout was applied on picked nodes only!");
		} else {
			changeToLayout(new GEMLayout(g));
		}
	}

	public void changeToGEMLayout(Map<BiologicalNodeAbstract, Point2D> mapOfStaticNodes) {
		changeToLayout(new GEMLayout(g, mapOfStaticNodes));
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
		makeDefaultObjectVisualization();
		if (g.getVertexCount() == 0) {
			return;
		}
		this.layout = layout;
		visualizationModel.setGraphLayout(layout);
		new Thread(() -> {
			VanesaUtility.trySleep(500);
			normalCentering();
		}).start();
	}

	public void fitScaleOfViewer(VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> viewer) {
		viewer.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).setScale(1, 1,
				viewer.getCenter());
		viewer.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).setScale(1, 1,
				viewer.getCenter());
		final GraphCenter gc = new GraphCenter(this);
		final double width = gc.getWidth() + 100;
		final double height = gc.getHeight() + 100;
		final Dimension viewSize = viewer.getSize();
		final double scale = Math.min(viewSize.width / width, viewSize.height / height);
		scaler.scale(viewer, (float) scale, viewer.getCenter());
	}

	public void normalCentering() {
		if (g.getVertexCount() > 0) {
			fitScaleOfViewer(vv);
			normalCentering(vv);
			updateLabelVisibilityOnZoom();
		}
	}

	public void normalCentering(VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> viewer) {
		final GraphCenter graphCenter = new GraphCenter(this);
		final Point2D q = graphCenter.getCenter();
		final Point2D lvc = viewer.getRenderContext().getMultiLayerTransformer().inverseTransform(viewer.getCenter());
		final double dx = (lvc.getX() - q.getX());
		final double dy = (lvc.getY() - q.getY());
		viewer.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).translate(dx, dy);
	}

	public void enableGraphTheory() {
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
	}

	public Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> getJungGraph() {
		return g;
	}

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
		vv.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		graphMouse.setMode(ModalGraphMouse.Mode.ANNOTATING);
		vv.setGraphMouse(graphMouse);
	}

	public MyEdgeDrawPaintFunction getEdgeDrawPaintFunction() {
		return edpf;
	}

	public void addAnnotation(Map<String, String> attributes) {
		annotationManager.add(Annotation.Layer.LOWER, new MyAnnotation(attributes));
		getVisualizationViewer().addPreRenderPaintable(annotationManager.getLowerAnnotationPaintable());
		getVisualizationViewer().addPostRenderPaintable(annotationManager.getUpperAnnotationPaintable());
	}

	public List<Map<String, String>> getAllAnnotations() {
		List<Map<String, String>> allAnnotations = new ArrayList<>();
		for (MyAnnotation m : annotationManager.getAnnotations()) {
			try {
				allAnnotations.add(m.getAsPropertyMap());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return allAnnotations;
	}

	// Probably the most important step for the pure rendering performance:
	// Disable anti-aliasing
	public void disableAntiAliasing(boolean deactivate) {
		if (deactivate) {
			vv.getRenderingHints().remove(RenderingHints.KEY_ANTIALIASING);
		} else {
			vv.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
	}

	// This method summarizes several options for improving the painting
	// performance. Enable or disable them depending on which visual features
	// you want to sacrifice for the higher performance.
	private static <V, E> void improvePerformance(VisualizationViewer<V, E> vv) {

		// Skip vertices that are not inside the visible area.
		if (Workspace.getCurrentSettings().isOmitPaintInvisibleNodes()) {
			doNotPaintInvisibleVertices(vv);
		}

		// May be helpful for performance in general, but not appropriate
		// when there are multiple edges between a pair of nodes: Draw
		// the edges not as curves, but as straight lines:
		// vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<V,E>());

		// May be helpful for painting performance: Omit the arrow heads
		// of directed edges
		// Predicate<Context<Graph<V, E>, E>> edgeArrowPredicate =
		// new Predicate<Context<Graph<V,E>,E>>()
		// {
		// @Override
		// public boolean evaluate(Context<Graph<V, E>, E> arg0)
		// {
		// return false;
		/// }
		// };
		// vv.getRenderContext().setEdgeArrowPredicate(edgeArrowPredicate);

	}

	// Skip all vertices that are not in the visible area.
	// NOTE: See notes at the end of this method!
	private static <V, E> void doNotPaintInvisibleVertices(VisualizationViewer<V, E> vv) {
		Predicate<Context<Graph<V, E>, V>> vertexIncludePredicate = new Predicate<>() {
			final Dimension size = new Dimension();

			@Override
			public boolean apply(Context<Graph<V, E>, V> c) {
				if (c == null) {
					return true;
				}
				vv.getSize(size);
				Rectangle bounds = new Rectangle();
				if (c.element instanceof BiologicalNodeAbstract) {
					final BiologicalNodeAbstract bna = (BiologicalNodeAbstract) c.element;
					final Shape shape = bna.getShape();
					if (shape != null) {
						bounds = VanesaUtility.scaleRectangle(shape.getBounds(), bna.getSize());
					}
				}
				Point2D pos = vv.getGraphLayout().apply(c.element);
				pos = vv.getRenderContext().getMultiLayerTransformer().transform(pos);
				final Point2D topLeft = new Point((int) (pos.getX() + bounds.x), (int) (pos.getY() + bounds.y));
				final Point2D bottomRight = new Point((int) (pos.getX() + bounds.x + bounds.width),
						(int) (pos.getY() + bounds.y + bounds.height));
				return bottomRight.getX() >= 0 && topLeft.getX() <= size.width && bottomRight.getY() >= 0
						&& topLeft.getY() <= size.height;
			}
		};
		vv.getRenderContext().setVertexIncludePredicate(vertexIncludePredicate);

		// NOTE: By default, edges will NOT be included in the visualization
		// when ONE of their vertices is NOT included in the visualization.
		// This may look a bit odd when zooming and panning over the graph.
		// Calling the following method will cause the edges to be skipped
		// ONLY when BOTH their vertices are NOT included in the visualization,
		// which may look nicer and more intuitive
		doPaintEdgesAtLeastOneVertexIsVisible(vv);
	}

	// See note at end of "doNotPaintInvisibleVertices"
	private static <V, E> void doPaintEdgesAtLeastOneVertexIsVisible(VisualizationViewer<V, E> vv) {
		vv.getRenderer().setEdgeRenderer(new BasicEdgeRenderer<>() {
			@Override
			public void paintEdge(RenderContext<V, E> rc, Layout<V, E> layout, E e) {
				GraphicsDecorator g2d = rc.getGraphicsContext();
				Graph<V, E> graph = layout.getGraph();
				if (!rc.getEdgeIncludePredicate().apply(Context.getInstance(graph, e))) {
					return;
				}
				if (!rc.getVertexIncludePredicate().apply(Context.getInstance(graph, graph.getEndpoints(e).getFirst()))
						&& !rc.getVertexIncludePredicate().apply(
						Context.getInstance(graph, graph.getEndpoints(e).getSecond()))) {
					return;
				}
				Stroke new_stroke = rc.getEdgeStrokeTransformer().apply(e);
				Stroke old_stroke = g2d.getStroke();
				if (new_stroke != null) {
					g2d.setStroke(new_stroke);
				}
				drawSimpleEdge(rc, layout, e);

				// restore paint and stroke
				if (new_stroke != null) {
					g2d.setStroke(old_stroke);
				}
			}
		});
	}
}
