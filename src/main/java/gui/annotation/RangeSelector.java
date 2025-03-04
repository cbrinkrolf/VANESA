package gui.annotation;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.visualization.VisualizationServer.Paintable;
import edu.uci.ics.jung.visualization.annotations.Annotation;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import graph.jung.classes.MyVisualizationViewer;

public class RangeSelector extends MouseAdapter implements Paintable, ActionListener {
	public static final int RECTANGLE = 0, ELLIPSE = 1, POLYGON = 3;
	// private int currentRangeType = RECTANGLE;
	// private Point2D startDragging;
	// private RangeInfo justCreated;
	// private Font font = new Font(Font.SERIF, Font.BOLD, 16);

	// private boolean enabled;
	private static RangeSelector instance;
	// private Color fillColor = Color.cyan;
	// private Color textColor = Color.black;
	// private Color outlineColor = Color.yellow;
	// private int alpha = 150;
	// private List<Action> selectShapeActions = new ArrayList<Action>();
	// private List<Action> selectColorActions = new ArrayList<Action>();
	// private int xOffset = 0;
	// private int yOffset = 0;
	// private boolean showOutline;
	private RangeShapeEditor rangeShapeEditor;
	private JMenuItem dropRange;
	private JMenuItem editRange;
	private JMenuItem moveUpRange;
	private JMenuItem moveDownRange;
	private RangeSettings settings = new RangeSettings();
	private MyAnnotationManager am;

	public static RangeSelector getInstance() {
		if (instance == null) {
			instance = new RangeSelector();
		}
		return instance;
	}

	public RangeSelector() {
		// this.initShapeActions();
		// this.initColorActions();
		// this.setFillColor(fillColor);
		rangeShapeEditor = new RangeShapeEditor();
		dropRange = new JMenuItem("remove selected range");
		dropRange.addActionListener(this);
		editRange = new JMenuItem("edit selected range");
		editRange.addActionListener(this);
		moveUpRange = new JMenuItem("move up selected range");
		moveUpRange.addActionListener(this);
		moveDownRange = new JMenuItem("move down selected range");
		moveDownRange.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		// System.out.println("event");
		if (rangeShapeEditor.selected != null) {
			am = GraphInstance.getMyGraph().getAnnotationManager();

			Object src = e.getSource();
			if (src == this.editRange) {
				int index = am.indexOf(rangeShapeEditor.selected);
				settings.loadSettings(rangeShapeEditor.selected, index, am.size());
				int option = settings.showDialog();
				if (option == JOptionPane.OK_OPTION) {
					am.changeLayer(rangeShapeEditor.selected, settings.layer);
				}
			} else if (src == this.dropRange) {
				am.remove(rangeShapeEditor.selected);
			} else if (src == this.moveUpRange) {
				int idx = am.indexOf(rangeShapeEditor.selected);
				if (idx > am.size() - 2) {
					return;
				}
				am.changeLayer(rangeShapeEditor.selected, idx + 1);
			} else if (src == this.moveDownRange) {
				int idx = am.indexOf(rangeShapeEditor.selected);
				if (idx < 1) {
					return;
				}
				am.changeLayer(rangeShapeEditor.selected, idx - 1);
			}
		}
	}

	public void addRangesInMyGraph(MyGraph graph, Map<String, String> attributes) {
		try {
			am = graph.getAnnotationManager();
			// am = GraphInstance.getMyGraph().getAnnotationManager();

			// System.out.println(r.shape);
			// System.out.println(r.shape.getMinX());
			// System.out.println(r.shape.getMinY());
			am.add(Annotation.Layer.LOWER, new MyAnnotation(attributes));
			graph.getVisualizationViewer().addPreRenderPaintable(am.getLowerAnnotationPaintable());
			graph.getVisualizationViewer().addPostRenderPaintable(am.getUpperAnnotationPaintable());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Map<String, String>> getRangesInMyGraph(MyGraph graph) {
		am = graph.getAnnotationManager();
		List<Map<String, String>> allRanges = new ArrayList<Map<String, String>>();
		for (MyAnnotation m : am.getAnnotations()) {
			try {
				allRanges.add(m.getAsPropertyMap());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// System.out.println("annos: "+allRanges.size());
		return allRanges;
	}

	public void setEnabled(boolean enabled) {
		// this.enabled = enabled;
		this.rangeShapeEditor.enabled = enabled;
		try {
			GraphInstance.getMyGraph().getVisualizationViewer().getComponentPopupMenu().remove(dropRange);
		} catch (Exception e) {
		}
	}

	public RangeShapeEditor getRangeShapeEditor() {
		return rangeShapeEditor;
	}

	@Override
	public void paint(Graphics g) {
	}

	@Override
	public boolean useTransform() {
		return false;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		@SuppressWarnings("unchecked")
		final MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = (MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract>) e
				.getSource();
		vv.requestFocus();
	}
}
