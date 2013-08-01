/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import database.dawis.AllElementLoader;
import database.dawis.ForwardConnector;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.VisualizationServer.Paintable;
import edu.uci.ics.jung.visualization.annotations.Annotation;
import edu.uci.ics.jung.visualization.annotations.AnnotationManager;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import gui.images.ImagePath;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import org.apache.commons.collections.Factory;
import org.apache.commons.collections.map.LazyMap;

import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.CollectorNode;
import biologicalObjects.nodes.PathwayMap;

/**
 * 
 * @author star
 */
public class RangeSelector extends MouseAdapter implements Paintable,
		ActionListener {

	public static final int RECTANGLE = 0, ELLIPSE = 1, POLYGON = 3;
	private int currentRangeType = RECTANGLE;
	private Point2D startDragging;
	private RangeInfo justCreated;
	// private Font font = new Font(Font.SERIF, Font.BOLD, 16);
	private Map<MyGraph, List<RangeInfo>> ranges = LazyMap.decorate(
			new HashMap(), new Factory() {

				public Object create() {
					return new ArrayList();
				}
			});
	private boolean enabled;
	private static RangeSelector instance;
	private Color fillColor = Color.cyan;
	private Color textColor = Color.black;
	private Color outlineColor = Color.yellow;
	private int alpha = 150;
	private List<Action> selectShapeActions = new ArrayList();
	private List<Action> selectColorActions = new ArrayList();
	private int xOffset = 0;
	private int yOffset = 0;
	private boolean showOutline;
	private RangeShapeEditor rangeShapeEditor;
	private JMenuItem dropRange;
	private JMenuItem editRange;
	private JMenuItem moveUpRange;
	private JMenuItem moveDownRange;
	private RangeSettings settings = new RangeSettings();
	private GraphInstance graphInstance;
	private ImagePath imagePath = ImagePath.getInstance();
	private MyAnnotationManager am;

	public static RangeSelector getInstance() {
		if (instance == null) {
			instance = new RangeSelector();
		}
		return instance;
	}

	public RangeSelector() {
		this.initShapeActions();
		this.initColorActions();
		this.setFillColor(fillColor);
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

	public void addRangeInfo(RangeInfo r) {
		this.getShapes().add(r);
	}

	private List<RangeInfo> getShapes(MyGraph mg) {
		// return this.ranges.get(mg);

		MyAnnotationManager am = mg.getAnnotationManager();
		List<RangeInfo> list = new ArrayList<RangeInfo>();
		Iterator<MyAnnotation> it = am.getAnnotations().iterator();
		MyAnnotation an;
		Annotation a;
		RangeInfo r;
		while (it.hasNext()) {

			an = it.next();
			a = an.getAnnotation();
			// Annotation a = ((MyAnnotationManager)
			// this.annotationManager).getCurrentAnnotation();
			// a.setPaint(new Color(255, 255, 255));
			// System.out.println(an.getShape());
			r = new RangeInfo(an.getShape(), an.getText(),
					(Color) a.getPaint(), (Color) a.getPaint(),
					(Color) a.getPaint());
			// RangeSelector.getInstance().addRangeInfo(r);
			list.add(r);
		}
		// System.out.println("g: "+list.size());
		return list;

	}

	public void addRangesInMyGraph(MyGraph graph, Map<String, String> attributes) {
		try {
			RangeInfo r = new RangeInfo(attributes, this);
			this.getShapes(graph).add(r);
			// System.out.println(r.shape.getWidth());
			// System.out.println(r.shape.getHeight());
			// System.out.println(r.fillColor);
			// System.out.println(r.shape.getX());
			// System.out.println(r.shape.getY());
			graphInstance = new GraphInstance();
			am = graphInstance.getMyGraph().getAnnotationManager();
			// am = GraphInstance.getMyGraph().getAnnotationManager();

			if (r.shape instanceof Ellipse2D) {
				// System.out.println("elli");
			} else {
				// System.out.println("rect");
			}
			Annotation a2;
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
			// Annotation a3 = new Annotation(new Rectangle(60,60),
			// Annotation.Layer.UPPER, new Color(0,255,0), true, new
			// Point2D.Double(0,0));

			// MyAnnotationManager m = new
			// MyAnnotationManager(GraphInstance.getMyGraph().getVisualizationViewer().getRenderContext());
			am.add(Annotation.Layer.LOWER, a2);
			am.add(Annotation.Layer.LOWER, new MyAnnotation(a2, r.shape, r.text));
			// System.out.println("drin");
			// System.out.println("am: "+am);
			graphInstance.getMyGraph().getVisualizationViewer()
					.addPreRenderPaintable(am.getLowerAnnotationPaintable());
			graphInstance.getMyGraph().getVisualizationViewer()
					.addPostRenderPaintable(am.getUpperAnnotationPaintable());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Map<String, String>> getRangesInMyGraph(MyGraph graph) {

		List<Map<String, String>> allRanges = new ArrayList();
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

	private Color getColor(Color oldColor) {
		System.out.println("color: " + oldColor);
		Color newColor = oldColor;
		try {
			newColor = JColorChooser.showDialog(null, "select a new color.",
					oldColor);
		} catch (Exception e) {

		}

		return newColor;
	}

	private void initColorActions() {
		Action a = new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				RangeSelector.this.setFillColor(getColor(fillColor));
			}
		};
		this.initAction(a, "comparison.png", "select range color");
		this.selectColorActions.add(a);
		a = new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				RangeSelector.this.setTextColor(getColor(textColor));
			}
		};
		this.initAction(a, "font.png", "select text color");
		this.selectColorActions.add(a);
	}

	private void initAction(Action a, String image, String desc) {

		// a.putValue(Action.SMALL_ICON, createIcon(imagePath));
		a.putValue(Action.SMALL_ICON, new ImageIcon(imagePath.getPath(image)));
		a.putValue(Action.SHORT_DESCRIPTION, desc);

	}

	private void initShapeActions() {
		Action a = new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				setCurrentRangeType(RECTANGLE);
			}
		};
		this.initAction(a, "rectangle.png", "rectangle");
		this.selectShapeActions.add(a);
		a = new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				setCurrentRangeType(ELLIPSE);
			}
		};
		this.initAction(a, "ellipse.png", "ellipse");
		this.selectShapeActions.add(a);
	}

	private Icon createIcon(String path) {
		return new ImageIcon(this.getClass().getClassLoader().getResource(path));
	}

	private String getInputText() {
		String text = JOptionPane
				.showInputDialog("Please enter a description!");
		return text;
	}

	// private Shape createTextShape(Graphics2D g2d,String text){
	// FontRenderContext frc = g2d.getFontRenderContext();
	// Font font=g2d.getFont();
	// TextLayout tl = new TextLayout(text, font, frc);
	// return tl.getOutline(null);
	// }
	public int getCurrentRangeType() {
		return currentRangeType;
	}

	public void setCurrentRangeType(int currentRangeType) {
		this.currentRangeType = currentRangeType;
		setEnabled(true);
		try {
			GraphInstance.getMyGraph().setMouseModeSelectRange();
		} catch (NullPointerException ne) {
		}
	}

	/*
	 * @Override public void mouseDragged(MouseEvent e) { super.mouseDragged(e);
	 * if (this.enabled) { if (this.startDragging != null) { if
	 * (this.justCreated != null) { if (justCreated.shape instanceof
	 * RectangularShape) { Point2D p2 = this.inverseTransform(e);
	 * (this.justCreated.shape).setFrameFromDiagonal( startDragging, p2); } } }
	 * } graphInstance = new GraphInstance();
	 * graphInstance.getPathway().getGraph().getVisualizationViewer().repaint();
	 * }
	 */

	// TODO koordinaten passen noch nicht so ganz
	/*
	 * @Override public void mousePressed(MouseEvent e) {
	 * this.checkContextMenu(e); if (this.enabled) { if (e.getButton() ==
	 * MouseEvent.BUTTON1 && this.rangeShapeEditor.selected == null) {
	 * this.startDragging = this.inverseTransform(e); this.justCreated = new
	 * RangeInfo( this.createShape(startDragging), null, this.fillColor,
	 * this.outlineColor, this.textColor); this.getShapes().add(justCreated);
	 * GraphInstance .getMyGraph() .getVisualizationViewer() .setCursor(
	 * Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	 * 
	 * } } }
	 */

	/*
	 * @Override public void mouseReleased(MouseEvent e) {
	 * 
	 * if (e.getClickCount() == 2) {
	 * 
	 * graphInstance = new GraphInstance();
	 * 
	 * Pathway pw = graphInstance.getPathway();
	 * 
	 * GraphElementAbstract gea = graphInstance.getSelectedObject(); if
	 * (gea.isVertex()) {
	 * 
	 * BiologicalNodeAbstract bna = (BiologicalNodeAbstract) gea; if (bna
	 * instanceof CollectorNode) {
	 * 
	 * CollectorNode n = (CollectorNode) bna; AllElementLoader sel = new
	 * AllElementLoader(pw, n); sel.execute();
	 * 
	 * }
	 * 
	 * // connect to kegg database for further information if (bna instanceof
	 * PathwayMap) { if (bna.getDB().equalsIgnoreCase("KEGG")) { PathwayMap
	 * pathwayNode = (PathwayMap) bna; new ForwardConnector(pathwayNode); } } }
	 * }
	 * 
	 * if (this.enabled && this.justCreated != null) { this.startDragging =
	 * null; if (this.justCreated.shape instanceof RectangularShape) {
	 * RectangularShape rect = justCreated.shape; List<RangeInfo> shapes =
	 * this.getShapes(); if (rect.getHeight() * rect.getWidth() < 10) {
	 * shapes.remove(this.justCreated); } else { String s = this.getInputText();
	 * if (s == null || s.length() == 0) { shapes.remove(this.justCreated); }
	 * else { this.justCreated.text = s; } } }
	 * graphInstance.getPathway().getGraph().getVisualizationViewer().repaint();
	 * }
	 * 
	 * this.justCreated = null;
	 * 
	 * }
	 */

	private RectangularShape createShape(Point2D p1) {
		switch (currentRangeType) {
		case RECTANGLE:
			System.out.println("drin");
			return new Rectangle2D.Double(p1.getX(), p1.getY(), 1, 1);
		case ELLIPSE:
			return new Ellipse2D.Double(p1.getX(), p1.getY(), 1, 1);
			// case POLYGON:
			// this.justCreated=new Line2D.Float(p1.x,p1.y,p1.x,p1.y);
		}
		return null;
	}

	private double calcYInEllipse(double x, Ellipse2D shape) {
		Rectangle2D bound = shape.getBounds2D();
		double rx = bound.getWidth() / 2, ry = bound.getHeight() / 2, centerX = bound
				.getCenterX();
		double coordX = x - rx;
		double y = Math.sqrt((1 - coordX * coordX / (rx * rx)) * ry * ry);
		return y;
	}

	private RectangularShape tempRect = new Rectangle2D.Double(),
			tempEllipse = new Ellipse2D.Double();
	private int inset = 5;

	public void paint(Graphics g) {
		if (justCreated != null) {
			Color oldColor = g.getColor();
			g.setColor(fillColor);
			((Graphics2D) g).draw(justCreated.shape);
			g.setColor(oldColor);

		}
		/*
		 * List<RangeInfo> shapes = this.getShapes(); for (RangeInfo info :
		 * shapes) { if (info != null) { Color oldColor = g.getColor();
		 * g.setColor(info.fillColor); ((Graphics2D)g).draw(info.shape);
		 * g.setColor(oldColor); } }
		 */
		/*
		 * if(justCreated != null){ Color oldColor = g.getColor();
		 * g.setColor(fillColor); ((Graphics2D)g).draw(justCreated.shape);
		 * g.setColor(oldColor); }
		 */
		/*
		 * //System.out.println("paint"); Graphics2D g2d = (Graphics2D) g; Color
		 * old = g2d.getColor(); // Font oldFont = g2d.getFont(); //
		 * g2d.setFont(this.font); List<RangeInfo> shapes = this.getShapes();
		 * VisualizationViewer vv = GraphInstance.getMyGraph()
		 * .getVisualizationViewer(); AffineTransform oldXform =
		 * g2d.getTransform(); AffineTransform newXform = new
		 * AffineTransform(oldXform);
		 * //newXform.concatenate(vv.getLayoutTransformer().getTransform());
		 * g2d.setTransform(newXform); for (RangeInfo info : shapes) { if (info
		 * != null) {
		 * 
		 * int drawOutline = info.outlineType; RectangularShape temp =
		 * this.tempRect; if (drawOutline == 2) { if (info.shape instanceof
		 * Ellipse2D) { temp = this.tempEllipse; }
		 * temp.setFrameFromDiagonal(info.shape.getMinX() + inset,
		 * info.shape.getMinY() + inset, info.shape.getMaxX() - inset,
		 * info.shape.getMaxY() - inset); } int rgba = info.fillColor.getRGB();
		 * rgba &= ((info.alpha << 24) + 0x00ffffff); Color c = new Color(rgba,
		 * true); g2d.setColor(c); if (drawOutline == 2) { g2d.fill(temp);
		 * g2d.setColor(info.outlineColor); g2d.draw(temp); } else {
		 * g2d.fill(info.shape); } // g2d.dra if (drawOutline > 0) {
		 * g2d.setColor(info.outlineColor); g2d.draw(info.shape); } String s =
		 * info.text; if (s != null) { Rectangle bound = info.shape.getBounds();
		 * double cx = bound.getCenterX(); double cy = bound.getCenterY();
		 * double hw = bound.getWidth() / 2.0, hh = bound.getHeight() / 2.0;
		 * double oy = hh - yOffset; int lr = (info.titlePos & 1) > 0 ? 1 : -1,
		 * tb = (info.titlePos & 2) > 0 ? 1 : -1; FontMetrics metrics =
		 * g.getFontMetrics(); Rectangle2D labelBound =
		 * metrics.getStringBounds(s, g); double sw = labelBound.getWidth(), sh
		 * = labelBound .getHeight(); if (info.shape instanceof Ellipse2D) { oy
		 * = (this.calcYInEllipse(xOffset, (Ellipse2D) info.shape)); } float x =
		 * (float) (cx + lr * hw - lr * xOffset); x += (lr > 0 ? -sw : 0); float
		 * y = (float) (cy + tb * oy); y += (tb > 0 ? 0 : sh);
		 * g2d.setColor(info.textColor); g2d.drawString(s, x, y); } } }
		 * g2d.setColor(old); // g2d.setFont(oldFont);
		 * g2d.setTransform(oldXform);
		 */
	}

	public boolean useTransform() {
		return false;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		this.rangeShapeEditor.enabled = enabled;
		try {
			GraphInstance.getMyGraph().getVisualizationViewer()
					.getComponentPopupMenu().remove(dropRange);
		} catch (Exception e) {
		}
	}

	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		try {
			if (!this.fillColor.equals(fillColor)) {
				this.fillColor = new Color(fillColor.getRed(),
						fillColor.getGreen(), fillColor.getBlue(), this.alpha);
			}
		} catch (NullPointerException e) {
		}
	}

	public List<Action> getSelectShapeActions() {
		return selectShapeActions;
	}

	public List<Action> getSelectColorActions() {
		return selectColorActions;
	}

	public Color getTextColor() {
		return textColor;
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}

	public Color getOutlineColor() {
		return outlineColor;
	}

	public void setOutlineColor(Color outlineColor) {
		this.outlineColor = outlineColor;
	}

	public RangeShapeEditor getRangeShapeEditor() {
		return rangeShapeEditor;
	}

	private Point2D inverseTransform(VisualizationViewer vv, Point p) {
		return vv.getRenderContext().getMultiLayerTransformer().transform(p);// inverseTransform(p);
	}

	private Point2D inverseTransform(MouseEvent e) {
		VisualizationViewer vv = (VisualizationViewer) e.getSource();
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

	private void checkContextMenu(MouseEvent e) {
		if (enabled && e.getButton() == MouseEvent.BUTTON3) {
			if (rangeShapeEditor.selected != null) {
				JPopupMenu menu = GraphInstance.getMyGraph()
						.getVisualizationViewer().getComponentPopupMenu();
				menu.add(editRange);
				menu.add(dropRange);
				menu.add(this.moveUpRange);
				menu.add(this.moveDownRange);
			} else {
				JPopupMenu menu = GraphInstance.getMyGraph()
						.getVisualizationViewer().getComponentPopupMenu();
				menu.remove(dropRange);
				menu.remove(editRange);
				menu.remove(this.moveUpRange);
				menu.remove(this.moveDownRange);
			}
		}
	}

	public class RangeShapeEditor extends MouseAdapter {

		private boolean enabled;
		private RangeInfo selected;
		private double oldX, oldY;
		private Cursor oldCursor;
		private boolean resizing;
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
			select(inverseTransform(e));
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (enabled && justCreated == null) {
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

		private void select(Point2D p) {
			if (enabled) {
				this.selected = null;
				if (oldCursor != null) {
					GraphInstance.getMyGraph().getVisualizationViewer()
							.setCursor(oldCursor);
				}
				List<RangeInfo> shapes = getShapes();
				for (int i = shapes.size() - 1; i > -1; i--) {
					RangeInfo info = shapes.get(i);
					int cursor = this.checkAnchor(p, info.shape);
					// if (info.shape.contains(p)) {
					if (cursor > -1) {
						selected = info;
						oldCursor = GraphInstance.getMyGraph()
								.getVisualizationViewer().getCursor();
						GraphInstance.getMyGraph().getVisualizationViewer()
								.setCursor(Cursor.getPredefinedCursor(cursor));
						return;
					}
				}
			}
		}
	}
}
