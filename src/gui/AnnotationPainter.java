package gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.annotations.AnnotatingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import graph.GraphInstance;
import gui.images.ImagePath;
import util.MyColorChooser;

public class AnnotationPainter {

	private static AnnotationPainter instance;
	public static final int RECTANGLE = 0, ELLIPSE = 1, POLYGON = 3, TEXT = 4;
	// private int currentRangeType = RECTANGLE;
	// private boolean enabled;
	private List<Action> selectShapeActions = new ArrayList<Action>();
	private List<Action> selectColorActions = new ArrayList<Action>();
	private ImagePath imagePath = ImagePath.getInstance();
	// private JMenuItem dropRange;
	private Color fillColor = Color.cyan;
	private Color textColor = Color.black;
	private int alpha = 150;
	private GraphInstance graphInstance;
	private MyAnnotatingGraphMousePlugin<BiologicalNodeAbstract, BiologicalEdgeAbstract> annotatingPlugin;

	public static AnnotationPainter getInstance() {
		if (instance == null) {
			instance = new AnnotationPainter();
		}
		return instance;
	}

	public AnnotationPainter() {
		this.initShapeActions();
		this.initColorActions();

	}

	private void initShapeActions() {
		Action a = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				setCurrentRangeType(RECTANGLE);
				// System.out.println("set rect");
			}
		};
		this.initAction(a, "rectangle.png", "rectangle");
		this.selectShapeActions.add(a);
		a = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				setCurrentRangeType(ELLIPSE);
				// System.out.println("set ell");
			}
		};
		this.initAction(a, "ellipse.png", "ellipse");
		this.selectShapeActions.add(a);

		a = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				setCurrentRangeType(TEXT);
			}
		};
		this.initAction(a, "text.png", "text");
		this.selectShapeActions.add(a);

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
				// System.out.println("fill color: " + fillColor);
			}
		};
		this.initAction(a, "comparison.png", "select range color");
		this.selectColorActions.add(a);
		a = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				// System.out.println("drin");
				textColor = getColor(textColor);
				if (annotatingPlugin != null) {
					annotatingPlugin.setAnnotationColor(textColor);
				}
			}
		};
		this.initAction(a, "font.png", "select text color");
		this.selectColorActions.add(a);
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

		graphInstance = new GraphInstance();
		VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = graphInstance.getPathway().getGraph()
				.getVisualizationViewer();
		RenderContext<BiologicalNodeAbstract, BiologicalEdgeAbstract> rc = vv.getRenderContext();
		annotatingPlugin = new MyAnnotatingGraphMousePlugin<BiologicalNodeAbstract, BiologicalEdgeAbstract>(rc);
		annotatingPlugin.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		annotatingPlugin.setFill(true);
		annotatingPlugin.setCurrentType(currentRangeType);
		if (currentRangeType == AnnotationPainter.TEXT) {
			// System.out.println("text");
			annotatingPlugin.setAnnotationColor(textColor);
		} else {
			annotatingPlugin.setAnnotationColor(fillColor);
		}
		// System.out.println("farbe: "+fillColor);

		// if(currentRangeType == RECTANGLE){
		// annotatingPlugin.setRectangularShape(new Rectangle());
		// }else if(currentRangeType == ELLIPSE){
		// annotatingPlugin.setRectangularShape(new Ellipse2D.Double());
		// }

		// create a GraphMouse for the main view
		//

		final AnnotatingModalGraphMouse<BiologicalNodeAbstract, BiologicalEdgeAbstract> graphMouse = new AnnotatingModalGraphMouse<BiologicalNodeAbstract, BiologicalEdgeAbstract>(
				rc, annotatingPlugin);
		// AnnotationManager m = new AnnotationManager(rc);

		// VisualizationViewer<BiologicalNodeAbstract,BiologicalEdgeAbstract> vv
		// = graphInstance.getPathway().getGraph().getVisualizationViewer();
		vv.setGraphMouse(graphMouse);
		vv.addKeyListener(graphMouse.getModeKeyListener());
		graphMouse.getModeComboBox().setSelectedItem(ModalGraphMouse.Mode.ANNOTATING);
		// vv.addMouseListener(this);
		// String text = JOptionPane
		// .showInputDialog("Please enter a description!");

		/*
		 * JPanel annotationControlPanel = new JPanel();
		 * annotationControlPanel.setBorder
		 * (BorderFactory.createTitledBorder("Annotation Controls"));
		 * 
		 * AnnotationControls<BiologicalNodeAbstract,BiologicalEdgeAbstract>
		 * annotationControls = new
		 * AnnotationControls<BiologicalNodeAbstract,BiologicalEdgeAbstract
		 * >(annotatingPlugin);
		 * 
		 * annotationControlPanel.add(annotationControls.getAnnotationsToolBar()) ;
		 * controls.add(annotationControlPanel);
		 */
	}

	public void setEnabled(boolean enabled) {
		// this.enabled = enabled;
		// this.rangeShapeEditor.enabled = enabled;
		try {
			// GraphInstance.getMyGraph().getVisualizationViewer()
			// .getComponentPopupMenu().remove(dropRange);
		} catch (Exception e) {
			// System.out.println("Exception");
		}
	}

	private void initAction(Action a, String image, String desc) {

		// a.putValue(Action.SMALL_ICON, createIcon(imagePath));
		// System.out.println(imagePath.getPath(image));
		a.putValue(Action.SMALL_ICON, new ImageIcon(imagePath.getPath(image)));
		a.putValue(Action.SHORT_DESCRIPTION, desc);

	}

	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		try {
			if (!this.fillColor.equals(fillColor)) {
				this.fillColor = new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), this.alpha);
			}
		} catch (NullPointerException e) {
		}
	}

	private Color getColor(Color oldColor) {
		MyColorChooser mc = new MyColorChooser(MainWindow.getInstance().getFrame(), "Choose color", true, oldColor);
		if (mc.isOkAction()) {
			return mc.getColor();
		} else {
			return oldColor;
		}
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}

}
