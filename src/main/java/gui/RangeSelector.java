package gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.visualization.VisualizationServer.Paintable;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.annotations.Annotation;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import graph.jung.classes.MyVisualizationViewer;

public class RangeSelector extends MouseAdapter implements Paintable, ActionListener {
	public static final int RECTANGLE = 0, ELLIPSE = 1, POLYGON = 3;
	//private int currentRangeType = RECTANGLE;
	//private Point2D startDragging;
	//private RangeInfo justCreated;
	// private Font font = new Font(Font.SERIF, Font.BOLD, 16);
	
	//private boolean enabled;
	private static RangeSelector instance;
	//private Color fillColor = Color.cyan;
	//private Color textColor = Color.black;
	//private Color outlineColor = Color.yellow;
	//private int alpha = 150;
	//private List<Action> selectShapeActions = new ArrayList<Action>();
	//private List<Action> selectColorActions = new ArrayList<Action>();
	//private int xOffset = 0;
	//private int yOffset = 0;
	//private boolean showOutline;
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
		//this.initShapeActions();
		//this.initColorActions();
		//this.setFillColor(fillColor);
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

	public boolean hasRange() {
		try {
			return this.getShapes().size() > 0;
		} catch (Exception e) {
			return false;
		}
	}

	public void actionPerformed(ActionEvent e) {
		//System.out.println("event");
		if (rangeShapeEditor.selected != null) {
			Object src = e.getSource();
			if (src == this.editRange) {
				List<RangeInfo> shapes = getShapes();
				int index = shapes.indexOf(rangeShapeEditor.selected);
				settings.loadSettings(rangeShapeEditor.selected, index,
						shapes.size());
				int option = settings.showDialog();
				if (option == JOptionPane.OK_OPTION) {
					changeLayer(rangeShapeEditor.selected, settings.layer);
				}
			} else if (src == this.dropRange) {
				getShapes().remove(rangeShapeEditor.selected);
			} else if (src == this.moveUpRange) {
				int idx = this.getShapes().indexOf(rangeShapeEditor.selected);
				if (idx > this.getShapes().size() - 2) {
					return;
				}
				this.changeLayer(rangeShapeEditor.selected, idx + 1);
			} else if (src == this.moveDownRange) {
				int idx = this.getShapes().indexOf(rangeShapeEditor.selected);
				if (idx < 1) {
					return;
				}
				this.changeLayer(rangeShapeEditor.selected, idx - 1);
			}
		}
	}

	private void changeLayer(RangeInfo info, int newLayer) {
		List<RangeInfo> shapes = getShapes();
		shapes.remove(info);
		shapes.add(newLayer, info);
	}

	private List<RangeInfo> getShapes() {
		return getShapes(GraphInstance.getMyGraph());
	}

	private List<RangeInfo> getShapes(MyGraph mg) {
		// return this.ranges.get(mg);
		MyAnnotationManager am = mg.getAnnotationManager();
		List<RangeInfo> list = new ArrayList<>();
		Iterator<MyAnnotation> it = am.getAnnotations().iterator();
		MyAnnotation an;
		Annotation<?> a;
		RangeInfo r;
		while (it.hasNext()) {

			an = it.next();
			a = an.getAnnotation();
			// Annotation a = ((MyAnnotationManager)
			// this.annotationManager).getCurrentAnnotation();
			// a.setPaint(new Color(255, 255, 255));
			r = new RangeInfo(an.getShape(), an.getText(),
					(Color) a.getPaint(), (Color) a.getPaint(),
					(Color) a.getPaint());
			// RangeSelector.getInstance().addRangeInfo(r);
			list.add(r);
		}
		return list;

	}

	public void addRangesInMyGraph(MyGraph graph, Map<String, String> attributes) {
		try {
			RangeInfo r = new RangeInfo(attributes, this);
			this.getShapes(graph).add(r);
			am = graph.getAnnotationManager();
			// am = GraphInstance.getMyGraph().getAnnotationManager();
			if (r.shape instanceof Ellipse2D) {
				// System.out.println("elli");
			} else {
				// System.out.println("rect");
			}
			Annotation<Annotation.Layer> a2;
			if (r.text.length() > 0) {
				// System.out.println(r.shape);
				// System.out.println(r.shape.getMinX());
				// System.out.println(r.shape.getMinY());
				a2 = new Annotation(r.text, Annotation.Layer.LOWER,
						r.fillColor, false, new Point2D.Double(
								r.shape.getMinX(), r.shape.getMinY()));
			} else {
				a2 = new Annotation(r.shape, Annotation.Layer.LOWER,
						r.fillColor, true, new Point2D.Double(0, 0));
			}
			am.add(Annotation.Layer.LOWER, new MyAnnotation(a2, r.shape, r.text));
			graph.getVisualizationViewer()
					.addPreRenderPaintable(am.getLowerAnnotationPaintable());
			graph.getVisualizationViewer()
					.addPostRenderPaintable(am.getUpperAnnotationPaintable());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Map<String, String>> getRangesInMyGraph(MyGraph graph) {

		List<Map<String, String>> allRanges = new ArrayList<Map<String, String>>();
		for (RangeInfo info : getShapes(graph)) {
			try {
				allRanges.add(info.getProperties());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// System.out.println("annos: "+allRanges.size());
		return allRanges;
	}

	public void setEnabled(boolean enabled) {
		//this.enabled = enabled;
		this.rangeShapeEditor.enabled = enabled;
		try {
			GraphInstance.getMyGraph().getVisualizationViewer()
					.getComponentPopupMenu().remove(dropRange);
		} catch (Exception e) {
		}
	}

	public RangeShapeEditor getRangeShapeEditor() {
		return rangeShapeEditor;
	}

	private Point2D inverseTransform(MouseEvent e) {
		
		@SuppressWarnings("unchecked")
		VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = (VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract>) e.getSource();
		// System.out.println(e.getPoint());
		// System.out.println(vv.getLocation(e.getPoint()));
		// System.out.println(vv.getLocationOnScreen());
		// return
		// vv.getRenderContext().getMultiLayerTransformer().transform(e.getPoint());
		// System.out.println("ende");
		return vv.getRenderContext().getMultiLayerTransformer()
				.inverseTransform(e.getPoint());
		// return e.getPoint();
		// return vv.inverseTransform(e.getPoint());
	}

	public class RangeShapeEditor extends MouseAdapter {

		private boolean enabled;
		private RangeInfo selected;
		private double oldX, oldY;
		private Cursor oldCursor;
		//private boolean resizing;
		private double[] coords = new double[4];// lefttop x,y,rightbottom x,y
		private boolean[] move = new boolean[4];// move lefttop x,y,rightbottom
		// x,y
		private double[] resizeOffset = new double[2];

		private int checkAnchor(Point2D p, RectangularShape shape) {
			for (int i = 0; i < move.length; i++) {
				move[i] = false;
			}
			int cursor = -1;
			int tol = 3;
			if (shape.contains(p)) {
				cursor = Cursor.MOVE_CURSOR;

				double minX = shape.getMinX(), minY = shape.getMinY(), maxX = shape
						.getMaxX(), maxY = shape.getMaxY();
				double px = p.getX(), py = p.getY();
				if (Math.abs(py - minY) < tol) {
					move[1] = true;
					if (Math.abs(px - minX) < tol) {
						cursor = Cursor.NW_RESIZE_CURSOR;
						move[0] = true;
					} else if (Math.abs(px - maxX) < tol) {
						cursor = Cursor.NE_RESIZE_CURSOR;
						move[2] = true;
					} else {
						cursor = Cursor.N_RESIZE_CURSOR;
					}
				} else if (Math.abs(py - maxY) < tol) {
					move[3] = true;
					if (Math.abs(px - minX) < tol) {
						move[0] = true;
						cursor = Cursor.SW_RESIZE_CURSOR;
					} else if (Math.abs(px - maxX) < tol) {
						cursor = Cursor.SE_RESIZE_CURSOR;
						move[2] = true;
					} else {
						cursor = Cursor.S_RESIZE_CURSOR;
					}
				} else if (Math.abs(px - minX) < tol) {
					move[0] = true;
					cursor = Cursor.W_RESIZE_CURSOR;
				} else if (Math.abs(px - maxX) < tol) {
					move[2] = true;
					cursor = Cursor.E_RESIZE_CURSOR;
				}
			}
			return cursor;
		}

		private void modifyShape(Point2D p, RectangularShape shape) {
			this.resizeOffset[0] = p.getX() - oldX;
			this.resizeOffset[1] = p.getY() - oldY;
			boolean resize = false;
			for (int i = 0; i < coords.length; i++) {
				if (move[i]) {
					coords[i] += resizeOffset[i & 1];
					resize = true;
				}
			}
			if (resize) {
				shape.setFrameFromDiagonal(coords[0], coords[1], coords[2],
						coords[3]);
			} else {
				shape.setFrame(shape.getX() + resizeOffset[0], shape.getY()
						+ resizeOffset[1], shape.getWidth(), shape.getHeight());
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (enabled && selected != null
					&& e.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK
					&& selected.shape instanceof RectangularShape) {
				Point2D p = inverseTransform(e);
				// double dx = p.getX() - oldX, dy = p.getY() - oldY;
				// RectangularShape shape = selected.shape;
				// shape.setFrame(shape.getX() + dx, shape.getY() + dy,
				// shape.getWidth(), shape.getHeight());
				modifyShape(p, selected.shape);
				oldX = p.getX();
				oldY = p.getY();
			}

		}

		@Override
		public void mouseMoved(MouseEvent e) {
			Point2D p = inverseTransform(e);
			
			@SuppressWarnings("unchecked")
			final MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = (MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract>) e
					.getSource();
			if (enabled) {
				this.selected = null;
				if (oldCursor != null) {
					vv.setCursor(oldCursor);
				}
				List<RangeInfo> shapes = getShapes();
				for (int i = shapes.size() - 1; i > -1; i--) {
					RangeInfo info = shapes.get(i);
					int cursor = this.checkAnchor(p, info.shape);
					// if (info.shape.contains(p)) {
					if (cursor > -1) {
						selected = info;
						oldCursor = vv.getCursor();
						vv.setCursor(Cursor.getPredefinedCursor(cursor));
						return;
					}
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (enabled) {
				Point2D p = inverseTransform(e);
				// select(p);
				if (selected != null) {
					coords[0] = selected.shape.getMinX();
					coords[1] = selected.shape.getMinY();
					coords[2] = selected.shape.getMaxX();
					coords[3] = selected.shape.getMaxY();
				}
				if (e.getButton() == MouseEvent.BUTTON1) {
					oldX = p.getX();
					oldY = p.getY();
				}
			}

		}
	}

	@Override
	public void paint(Graphics g) {
	}

	@Override
	public boolean useTransform() {
		return false;
	}
	
	@Override
	public void mouseReleased(MouseEvent e){
		super.mouseReleased(e);
		@SuppressWarnings("unchecked")
		final MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv =
				(MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract>) e.getSource();
		vv.requestFocus();
	}
}
