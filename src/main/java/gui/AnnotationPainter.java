package gui;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.annotations.AnnotatingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import graph.GraphInstance;
import util.MyColorChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class AnnotationPainter {
	private static AnnotationPainter instance;
	public static final int RECTANGLE = 0;
	public static final int ELLIPSE = 1;
	public static final int POLYGON = 3;
	public static final int TEXT = 4;
	// private int currentRangeType = RECTANGLE;
	// private boolean enabled;
	private final List<Action> selectShapeActions = new ArrayList<>();
	private final List<Action> selectColorActions = new ArrayList<>();
	private final ImagePath imagePath = ImagePath.getInstance();
	// private JMenuItem dropRange;
	private Color fillColor = Color.cyan;
	private Color textColor = Color.black;
	private MyAnnotatingGraphMousePlugin<BiologicalNodeAbstract, BiologicalEdgeAbstract> annotatingPlugin;

	public static AnnotationPainter getInstance() {
		if (instance == null) {
			instance = new AnnotationPainter();
		}
		return instance;
	}

	public AnnotationPainter() {
		initShapeActions();
		initColorActions();
	}

	private void initShapeActions() {
		Action a = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				setCurrentRangeType(RECTANGLE);
			}
		};
		initAction(a, "rectangle.png", "rectangle");
		selectShapeActions.add(a);
		a = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				setCurrentRangeType(ELLIPSE);
			}
		};
		initAction(a, "ellipse.png", "ellipse");
		selectShapeActions.add(a);

		a = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				setCurrentRangeType(TEXT);
			}
		};
		initAction(a, "text.png", "text");
		selectShapeActions.add(a);
	}

	private void initColorActions() {
		Action a = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				// AnnotationPainter.this.setFillColor(getColor(fillColor));
				fillColor = getColor(fillColor);
				if (annotatingPlugin != null) {
					annotatingPlugin.setAnnotationColor(fillColor);
				}
			}
		};
		initAction(a, "comparison.png", "select range color");
		selectColorActions.add(a);
		a = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				textColor = getColor(textColor);
				if (annotatingPlugin != null) {
					annotatingPlugin.setAnnotationColor(textColor);
				}
			}
		};
		initAction(a, "font.png", "select text color");
		selectColorActions.add(a);
	}

	public List<Action> getSelectShapeActions() {
		return selectShapeActions;
	}

	public List<Action> getSelectColorActions() {
		return selectColorActions;
	}

	public void setCurrentRangeType(int currentRangeType) {
		// this.currentRangeType = currentRangeType;
		setEnabled(true);
		// GraphInstance.getMyGraph().setMouseModeSelectRange();
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
		final AnnotatingModalGraphMouse<BiologicalNodeAbstract, BiologicalEdgeAbstract> graphMouse = new AnnotatingModalGraphMouse<BiologicalNodeAbstract, BiologicalEdgeAbstract>(
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

	public void setEnabled(boolean enabled) {
		// this.enabled = enabled;
		// rangeShapeEditor.enabled = enabled;
		try {
			// GraphInstance.getMyGraph().getVisualizationViewer().getComponentPopupMenu().remove(dropRange);
		} catch (Exception e) {
		}
	}

	private void initAction(Action a, String image, String desc) {
		a.putValue(Action.SMALL_ICON, imagePath.getImageIcon(image));
		a.putValue(Action.SHORT_DESCRIPTION, desc);
		selectColorActions.add(a);
	}

	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		try {
			if (!this.fillColor.equals(fillColor)) {
				int alpha = 150;
				this.fillColor = new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), alpha);
			}
		} catch (NullPointerException e) {
		}
	}

	private Color getColor(Color oldColor) {
		MyColorChooser mc = new MyColorChooser(MainWindow.getInstance().getFrame(), "Choose color", true, oldColor);
		return mc.isOkAction() ? mc.getColor() : oldColor;
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}
}
