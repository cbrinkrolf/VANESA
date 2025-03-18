package graph.jung.classes;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import configurations.GraphSettings;
import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
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
import gui.MainWindow;
import gui.algorithms.ScreenSize;
import gui.annotation.MyAnnotation;
import gui.annotation.MyAnnotationManager;
import gui.annotation.MyAnnotationEditingGraphMouse;

public class MyGraph {
	private final Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g = new SparseMultigraph<>();
	private final MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv;
	private final MyEditingModalGraphMouse graphMouse;
	private final ScalingControl scaler = new CrossoverScalingControl();
	private final RenderContext<BiologicalNodeAbstract, BiologicalEdgeAbstract> pr;
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

	private final PickedState<BiologicalNodeAbstract> stateV;
	private final PickedState<BiologicalEdgeAbstract> stateE;
	private final VisualizationModel<BiologicalNodeAbstract, BiologicalEdgeAbstract> visualizationModel;
	private final MyVertexLabelRenderer vlr = new MyVertexLabelRenderer(Color.blue);
	private final MyEdgeLabelRenderer elr = new MyEdgeLabelRenderer(Color.blue);

	private final MyAnnotationManager annotationManager;

	private boolean animatedPicking = false;

	private final Pathway pathway;

	public MyGraph(Pathway pw) {
		ScreenSize screenSize = new ScreenSize();
		int visualizationViewerWidth = screenSize.width - 70;
		int visualizationViewerHeight = screenSize.height - 100;

		this.pathway = pw;
		graphMouse = new MyEditingModalGraphMouse();
		Dimension preferredSize = new Dimension(visualizationViewerWidth, visualizationViewerHeight);
		visualizationModel = new DefaultVisualizationModel<>(new AggregateLayout<>(null), preferredSize);
		visualizationModel.getRelaxer().setSleepTime(10);
		vv = new MyVisualizationViewer<>(visualizationModel, preferredSize, pathway);

		vv.setSize(preferredSize);
		vv.setMinimumSize(preferredSize);
		vv.addKeyListener(new KeyAdapter() {
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

		vv.addMouseListener(MyAnnotationEditingGraphMouse.getInstance());
		vv.addMouseMotionListener(MyAnnotationEditingGraphMouse.getInstance());

		ShapePickSupport<BiologicalNodeAbstract, BiologicalEdgeAbstract> pickSupport = new ShapePickSupport<>(vv);
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

		// 1. vertexFactor, 2. edgeFactory
		graphMouse.add(new EditingPopupGraphMousePlugin<BiologicalNodeAbstract, BiologicalEdgeAbstract>(null, null));

		graphMouse.setMode(ModalGraphMouse.Mode.EDITING);

		vv.setPickSupport(pickSupport);

		GraphSettings settings = GraphSettings.getInstance();
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

		vlr.setRotateEdgeLabels(false);
		vlr.setForeground(Color.WHITE);

		pr = vv.getRenderContext();

		annotationManager = new MyAnnotationManager(pr);

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
	}

	public boolean addEdge(BiologicalEdgeAbstract bea) {
		return g.addEdge(bea, bea.getFrom(), bea.getTo(), bea.isDirected() ? EdgeType.DIRECTED : EdgeType.UNDIRECTED);
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

	public MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> getVisualizationViewer() {
		vv.setDoubleBuffered(true);
		return vv;
	}

	public Collection<BiologicalNodeAbstract> getAllVertices() {
		return g.getVertices();
	}

	public VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> getVisualizationPaneCopy(
			Dimension size) {

		AggregateLayout<BiologicalNodeAbstract, BiologicalEdgeAbstract> clusteringLayout2 = new AggregateLayout<>(
				vv.getGraphLayout());

		VisualizationModel<BiologicalNodeAbstract, BiologicalEdgeAbstract> copyModel = new DefaultVisualizationModel<>(
				clusteringLayout2, size);
		VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> copyVV = new VisualizationViewer<>(
				copyModel, size);
		// private AbstractRenderer pr;
		RenderContext<BiologicalNodeAbstract, BiologicalEdgeAbstract> pr_compare = copyVV.getRenderContext();
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

	public MyAnnotationManager getAnnotationManager() {
		return annotationManager;
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
}
