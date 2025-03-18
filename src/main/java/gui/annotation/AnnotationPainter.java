package gui.annotation;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.annotations.AnnotatingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import graph.GraphInstance;
import graph.eventhandlers.MyAnnotatingGraphMousePlugin;
import gui.MainWindow;
import util.MyColorChooser;

import java.awt.*;

public class AnnotationPainter {
	private static AnnotationPainter instance;
	public static final int RECTANGLE = 0;
	public static final int ELLIPSE = 1;
	public static final int POLYGON = 3;
	public static final int TEXT = 4;
	private Color fillColor = new Color(100, 149, 237);
	private Color textColor = Color.black;
	private MyAnnotatingGraphMousePlugin<BiologicalNodeAbstract, BiologicalEdgeAbstract> annotatingPlugin;

	public static AnnotationPainter getInstance() {
		if (instance == null) {
			instance = new AnnotationPainter();
		}
		return instance;
	}

	public void setCurrentRangeType(int currentRangeType) {
		// this.currentRangeType = currentRangeType;
		// GraphInstance.getPathway().getGraph().setMouseModeSelectRange();
		VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = GraphInstance.getPathway().getGraph()
				.getVisualizationViewer();
		RenderContext<BiologicalNodeAbstract, BiologicalEdgeAbstract> rc = vv.getRenderContext();
		annotatingPlugin = new MyAnnotatingGraphMousePlugin<>(rc);
		annotatingPlugin.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		annotatingPlugin.setFill(true);
		annotatingPlugin.setCurrentType(currentRangeType);
		if (currentRangeType == AnnotationPainter.TEXT) {
			annotatingPlugin.setAnnotationColor(textColor);
		} else {
			annotatingPlugin.setAnnotationColor(fillColor);
		}
		// if(currentRangeType == RECTANGLE){
		// annotatingPlugin.setRectangularShape(new Rectangle());
		// }else if(currentRangeType == ELLIPSE){
		// annotatingPlugin.setRectangularShape(new Ellipse2D.Double());
		// }

		// create a GraphMouse for the main view
		final AnnotatingModalGraphMouse<BiologicalNodeAbstract, BiologicalEdgeAbstract> graphMouse = new AnnotatingModalGraphMouse<>(
				rc, annotatingPlugin);
		// AnnotationManager m = new AnnotationManager(rc);
		// VisualizationViewer<BiologicalNodeAbstract,BiologicalEdgeAbstract> vv
		// = graphInstance.getPathway().getGraph().getVisualizationViewer();
		vv.setGraphMouse(graphMouse);
		vv.addKeyListener(graphMouse.getModeKeyListener());
		graphMouse.getModeComboBox().setSelectedItem(ModalGraphMouse.Mode.ANNOTATING);
		// vv.addMouseListener(this);
		// String text = JOptionPane.showInputDialog("Please enter a description!");

		/*
		 * JPanel annotationControlPanel = new JPanel();
		 * annotationControlPanel.setBorder(BorderFactory.
		 * createTitledBorder("Annotation Controls"));
		 * AnnotationControls<BiologicalNodeAbstract,BiologicalEdgeAbstract>
		 * annotationControls = new AnnotationControls<>(annotatingPlugin);
		 * annotationControlPanel.add(annotationControls.getAnnotationsToolBar()) ;
		 * controls.add(annotationControlPanel);
		 */
	}

	public Color chooseColor(final Color oldColor) {
		MyColorChooser mc = new MyColorChooser(MainWindow.getInstance().getFrame(), "Choose color", true, oldColor);
		return mc.isOkAction() ? mc.getColor() : oldColor;
	}

	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(final Color color) {
		if (color != null) {
			fillColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 150);
		} else {
			fillColor = Color.GRAY;
		}
		if (annotatingPlugin != null) {
			if (annotatingPlugin.getCurrentType() == TEXT) {
				annotatingPlugin.setAnnotationColor(textColor);
			} else {
				annotatingPlugin.setAnnotationColor(fillColor);
			}
		}
	}

	public Color getTextColor() {
		return textColor;
	}

	public void setTextColor(final Color color) {
		if (color != null) {
			textColor = new Color(color.getRed(), color.getGreen(), color.getBlue());
		} else {
			textColor = Color.BLACK;
		}
		if (annotatingPlugin != null) {
			if (annotatingPlugin.getCurrentType() == TEXT) {
				annotatingPlugin.setAnnotationColor(textColor);
			} else {
				annotatingPlugin.setAnnotationColor(fillColor);
			}
		}
	}
}
