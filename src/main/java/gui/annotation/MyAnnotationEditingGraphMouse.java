package gui.annotation;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.visualization.annotations.Annotation;
import graph.GraphInstance;
import graph.jung.classes.MyVisualizationViewer;
import gui.MainWindow;

public class MyAnnotationEditingGraphMouse extends MouseAdapter {

	private boolean enabled = false;
	private boolean hovering = false;
	private MyAnnotation selected;
	private double oldX;
	private double oldY;
	private Cursor oldCursor;
	// private boolean resizing;
	private double[] coords = new double[4];// lefttop x,y,rightbottom x,y
	private boolean[] move = new boolean[4];// move lefttop x,y,rightbottom
	// x,y
	private double[] resizeOffset = new double[2];

	private static MyAnnotationEditingGraphMouse instance = null;
	private MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv;
	private MyAnnotation highlight = null;

	public static MyAnnotationEditingGraphMouse getInstance() {
		if (instance == null) {
			instance = new MyAnnotationEditingGraphMouse();
		}
		return instance;
	}

	private MyAnnotationEditingGraphMouse() {
	}

	public boolean isHovering() {
		return hovering;
	}

	private int checkAnchor(Point2D p, RectangularShape shape) {
		for (int i = 0; i < move.length; i++) {
			move[i] = false;
		}
		int cursor = -1;
		int tol = 3;
		if (shape.contains(p)) {
			cursor = Cursor.MOVE_CURSOR;

			double minX = shape.getMinX(), minY = shape.getMinY(), maxX = shape.getMaxX(), maxY = shape.getMaxY();
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
			shape.setFrameFromDiagonal(coords[0], coords[1], coords[2], coords[3]);
		} else {
			shape.setFrame(shape.getX() + resizeOffset[0], shape.getY() + resizeOffset[1], shape.getWidth(),
					shape.getHeight());
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (enabled && selected != null && e.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK
				&& selected.getShape() instanceof RectangularShape) {
			Point2D p = inverseTransform(e);
			// double dx = p.getX() - oldX, dy = p.getY() - oldY;
			// RectangularShape shape = selected.shape;
			// shape.setFrame(shape.getX() + dx, shape.getY() + dy,
			// shape.getWidth(), shape.getHeight());
			modifyShape(p, selected.getShape());
			if (highlight != null) {
				int offset = 5;
				RectangularShape shape = selected.getShape();
				// modifyShape(p, highlight.getShape());
				highlight.getShape().setFrameFromDiagonal(shape.getMinX() - offset, shape.getMinY() - offset,
						shape.getMaxX() + offset, shape.getMaxY() + offset);
			}
			oldX = p.getX();
			oldY = p.getY();
			vv.repaint();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void mouseMoved(MouseEvent e) {
		if (enabled && selected != null) {
			Point2D p = inverseTransform(e);

			vv = (MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract>) e.getSource();

			if (oldCursor != null) {
				vv.setCursor(oldCursor);
			}
			// MyAnnotationManager am = vv.getPathway().getGraph().getAnnotationManager();
			// for (int i = am.size() - 1; i > -1; i--) {
			// MyAnnotation info = am.get(i);
			int cursor = this.checkAnchor(p, selected.getShape());
			// if (info.shape.contains(p)) {
			// System.out.println(cursor);
			if (cursor > -1) {
				hovering = true;
				// selected = info;
				oldCursor = vv.getCursor();
				vv.setCursor(Cursor.getPredefinedCursor(cursor));
				// vv.setCursor(cursor);
				MainWindow.getInstance().getFrame().setCursor(Cursor.getPredefinedCursor(cursor));
				// RangeSettings settings = new RangeSettings();
				// settings.loadSettings(selected, 0, am.size());
				// int option = settings.showDialog();
			} else {
				hovering = false;
				if (oldCursor != null) {
					vv.setCursor(oldCursor);
					MainWindow.getInstance().getFrame().setCursor(oldCursor);
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (GraphInstance.getSelectedObject() instanceof MyAnnotation) {
			selected = (MyAnnotation) GraphInstance.getSelectedObject();
		}
		// if (enabled) {
		Point2D p = inverseTransform(e);
		// select(p);
		if (selected != null) {
			coords[0] = selected.getShape().getMinX();
			coords[1] = selected.getShape().getMinY();
			coords[2] = selected.getShape().getMaxX();
			coords[3] = selected.getShape().getMaxY();
		}
		if (e.getButton() == MouseEvent.BUTTON1) {
			oldX = p.getX();
			oldY = p.getY();
		}
		// }
	}

	@SuppressWarnings("unchecked")
	private Point2D inverseTransform(MouseEvent e) {
		vv = (MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract>) e.getSource();
		return vv.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint());
	}

	public void setEnabled(boolean enabled) {
		if (GraphInstance.getSelectedObject() instanceof MyAnnotation) {
			selected = (MyAnnotation) GraphInstance.getSelectedObject();
		} else {
			selected = null;
		}
		this.enabled = enabled;
		MyAnnotationManager am = vv.getPathway().getGraph().getAnnotationManager();
		if (enabled) {
			if (selected != null && vv != null) {
				int offset = 5;
				RectangularShape s = new Rectangle();
				RectangularShape shape = selected.getShape();

				s.setFrameFromDiagonal(shape.getMinX() - offset, shape.getMinY() - offset, shape.getMaxX() + offset,
						shape.getMaxY() + offset);
				highlight = new MyAnnotation(s, selected.getText(), Color.BLUE, Color.BLUE, Color.BLUE);
				am.add(Annotation.Layer.LOWER, highlight);
				am.updateMyAnnotation(selected);
			}
		} else {

			if (oldCursor != null && vv != null) {
				vv.setCursor(oldCursor);
				MainWindow.getInstance().getFrame().setCursor(oldCursor);
			}
			selected = null;
			if (highlight != null) {
				am.remove(highlight);
				highlight = null;
				vv.repaint();
			}
		}
	}

	public MyAnnotation getHighlight() {
		return highlight;
	}
}
