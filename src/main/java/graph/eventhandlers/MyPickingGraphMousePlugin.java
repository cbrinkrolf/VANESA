package graph.eventhandlers;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringUtils;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import configurations.GraphSettings;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.annotations.Annotation;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import graph.jung.classes.MyVisualizationViewer;
import graph.layouts.Circle;
import graph.layouts.HierarchicalCircleLayout;
import gui.MainWindow;
import gui.annotation.MyAnnotation;
import gui.annotation.MyAnnotationEditingGraphMouse;
import gui.annotation.MyAnnotationManager;

public class MyPickingGraphMousePlugin extends PickingGraphMousePlugin<BiologicalNodeAbstract, BiologicalEdgeAbstract> {
	private final Map<BiologicalNodeAbstract, Point2D> oldVertexPositions = new HashMap<>();
	private final Set<BiologicalNodeAbstract> originalSelection = new HashSet<>();
	private boolean inWindow = false;
	private boolean dragging = false;
	private Pathway pw = null;
	private MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = null;
	// for annotations
	private MyAnnotation currentAnnotation = null;
	private MyAnnotation highlight = null;
	private Point2D pressed = null;
	private boolean moved = false;
	private GraphSettings settings = GraphSettings.getInstance();
	private boolean modifyShape = false;

	public void mouseReleased(MouseEvent e) {
		moved = false;
		// vv.setFocusable(true);
		// vv.requestFocus();
		pw.pickGroup();
		if (inWindow) {
			this.mouseReleasedAnnotation(e);
			// If mouse was released to change the selection, save vertex positions and
			// return.
			if (!oldVertexPositions.keySet().containsAll(pw.getSelectedNodes())
					|| oldVertexPositions.keySet().size() != pw.getSelectedNodes().size()) {
				saveOldVertexPositions();
				super.mouseReleased(e);
				return;
			}
			Collection<BiologicalNodeAbstract> selectedNodes = pw.getSelectedNodes();
			// If no nodes were selected, return.
			if (selectedNodes.isEmpty()) {
				super.mouseReleased(e);
				return;
			}
			if (pw.getGraph().getLayout() instanceof HierarchicalCircleLayout) {
				HierarchicalCircleLayout hclayout = (HierarchicalCircleLayout) pw.getGraph().getLayout();
				hclayout.saveCurrentOrder();
			}
			// Find coarse nodes in specified environment of the final mouse position.
			GraphElementAccessor<BiologicalNodeAbstract, BiologicalEdgeAbstract> pickSupport = vv.getPickSupport();
			float width = selectedNodes.iterator().next().getShape().getBounds().width;
			float height = selectedNodes.iterator().next().getShape().getBounds().height;
			Shape shape = new Rectangle.Float(e.getX() - (width / 2), e.getY() - (height / 2), width, height);
			Collection<BiologicalNodeAbstract> vertices = pickSupport.getVertices(vv.getGraphLayout(), shape);
			vertices.removeAll(selectedNodes);
			// If exactly one node was found, take this node, otherwise return.
			BiologicalNodeAbstract vertex;
			if (vertices.size() == 1) {
				vertex = vertices.iterator().next();
			} else {
				super.mouseReleased(e);
				Point2D movement = new Point2D.Double();
				MyGraph graph = pw.getGraph();
				for (BiologicalNodeAbstract selectedNode : selectedNodes) {
					if (selectedNode.isCoarseNode()) {
						movement.setLocation(
								graph.getVertexLocation(selectedNode).getX()
										- oldVertexPositions.get(selectedNode).getX(),
								graph.getVertexLocation(selectedNode).getY()
										- oldVertexPositions.get(selectedNode).getY());
						for (BiologicalNodeAbstract child : selectedNode.getVertices().keySet()) {
							pw.getVertices().get(child)
									.setLocation(Circle.addPoints(pw.getVertices().get(child), movement));
						}
					} else {
						if (pw.getVertices().containsKey(selectedNode)) {
							pw.getVertices().get(selectedNode).setLocation(graph.getVertexLocation(selectedNode));
						}
					}
				}
				return;
			}
			// If node is a coarse node and not contained in the selection, add
			// the selection to the coarse node.
			if (vertex.isCoarseNode() && !selectedNodes.contains(vertex)) {
				// Disallow to add selection to environment coarse nodes.
				if (pw.isBNA()) {
					if (((BiologicalNodeAbstract) pw).getEnvironment().contains(vertices)) {
						JOptionPane.showMessageDialog(MainWindow.getInstance().getFrame(),
								"Not possible to add nodes to environment nodes.", "Coarse node integration Error!",
								JOptionPane.ERROR_MESSAGE);
						oldVertexPositions.clear();
						super.mouseReleased(e);
						return;
					}
				}
				coarseNodeFusion(vertex);
			}
			oldVertexPositions.clear();
			super.mouseReleased(e);
		}
	}

	private void coarseNodeFusion(BiologicalNodeAbstract vertex) {
		Set<BiologicalNodeAbstract> selection = new HashSet<>(pw.getSelectedNodes());
		if (!vertex.addToCoarseNode(selection, oldVertexPositions)) {
			for (BiologicalNodeAbstract node : selection) {
				pw.getGraph().moveVertex(node, oldVertexPositions.get(node).getX(),
						oldVertexPositions.get(node).getY());
			}
		} else {
			pw.updateMyGraph();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.setPathway(e);
		// vv = pw.getGraph().getVisualizationViewer();
		if (inWindow) {
			super.mousePressed(e);

			originalSelection.clear();
			originalSelection.addAll(pw.getSelectedNodes());
			if (pw.getGraph().getLayout() instanceof HierarchicalCircleLayout) {
				HierarchicalCircleLayout hcLayout = (HierarchicalCircleLayout) pw.getGraph().getLayout();
				if (hcLayout.getConfig().getMoveInGroups()) {
					for (BiologicalNodeAbstract selectedNode : pw.getSelectedNodes()) {
						for (BiologicalNodeAbstract node : hcLayout.getNodesGroup(selectedNode)) {
							pw.getGraph().getVisualizationViewer().getPickedVertexState().pick(node, true);
						}
					}
				}
			}
			saveOldVertexPositions();

			// selecting edges with offset of mouse position
			if (pw.getSelectedNodes().isEmpty() && pw.getSelectedEdges().isEmpty()) {
				Point2D ip = e.getPoint();
				// System.out.println(super.edge == null);
				int counter = 1;
				double dx = ip.getX();
				double dy = ip.getY();
				// System.out.println("start x: "+dx+" y:"+dy);

				int xyEdgeSelectionOffset = settings.getPixelOffset();

				while (edge == null && counter <= xyEdgeSelectionOffset) {
					for (int i = -xyEdgeSelectionOffset; i < xyEdgeSelectionOffset; i++) {
						counter++;
						for (int j = i; j < xyEdgeSelectionOffset; j++) {
							dx = ip.getX() + i;
							dy = ip.getY() + j;
							// System.out.println("x: "+dx+" y:"+dy);
							if ((super.edge = vv.getPickSupport().getEdge(vv.getGraphLayout(), dx, dy)) != null) {
								vv.getPickedEdgeState().clear();
								vv.getPickedEdgeState().pick(super.edge, true);
							}
						}
					}
				}
			}

			if (pw.getSelectedNodes().isEmpty() && pw.getSelectedEdges().isEmpty()
					&& SwingUtilities.isLeftMouseButton(e)) {
				mousePressedAnnotation(e);
			} else {
				this.removeHighlight();
				setModifyShape(false);
			}
		}
	}

	private void saveOldVertexPositions() {
		oldVertexPositions.clear();
		for (BiologicalNodeAbstract vertex : pw.getSelectedNodes()) {
			oldVertexPositions.put(vertex, pw.getGraph().getVertexLocation(vertex));
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (inWindow) {
			this.setPathway(e);
			vv.setMousePoint(vv.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint()));
			if (dragging) {// && SwingUtilities.isRightMouseButton(e)){
				this.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
				dragging = false;
				// vv.setComponentPopupMenu(new GraphPopUp().returnPopUp());
				MainWindow.getInstance().getFrame().setCursor(cursor);
				vv.setCursor(cursor);
				vv.getComponentPopupMenu().show();
			}
			if (e.getClickCount() == 1) {
				super.mouseClicked(e);
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (inWindow && !modifyShape) {
			this.setPathway(e);
			vv.setMousePoint(vv.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint()));
			if (this.currentAnnotation != null && SwingUtilities.isLeftMouseButton(e)) {
				this.mouseDraggedAnnotation(e);
			} else {
				if (!(pw.getGraph().getLayout() instanceof HierarchicalCircleLayout)) {

					if (SwingUtilities.isRightMouseButton(e)) {
						if (!dragging) {
							dragging = true;
							vv.getComponentPopupMenu().hide();
							this.cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
							MainWindow.getInstance().getFrame().setCursor(cursor);
							vv.setCursor(cursor);
							// this.mouseClicked(e);
						}
						// vv.getComponentPopupMenu();
						// vv.getComponentPopupMenu().removeAll();
						MutableTransformer modelTransformer = vv.getRenderContext().getMultiLayerTransformer()
								.getTransformer(Layer.LAYOUT);
						try {
							Point2D q = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(down);
							Point2D p = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint());
							float dx = (float) (p.getX() - q.getX());
							float dy = (float) (p.getY() - q.getY());
							modelTransformer.translate(dx, dy);
							down.x = e.getX();
							down.y = e.getY();
						} catch (RuntimeException ex) {
							System.err.println("down = " + down + ", e = " + e);
							throw ex;
						}
						e.consume();
						vv.repaint();
					} else {
						if (!moved) {
							float dx = Math.abs((float) (e.getX() - down.getX()));
							float dy = Math.abs((float) (e.getY() - down.getY()));
							if (Math.max(dx, dy) > 10) {
								moved = true;
							}
						}
						boolean sticky = true;
						if (moved || !sticky) {
							super.mouseDragged(e);
						}
					}
				} else {
					if (!locked) {
						if (vertex != null) {
							HierarchicalCircleLayout hcLayout = (HierarchicalCircleLayout) pw.getGraph().getLayout();
							// mouse position
							Point p = e.getPoint();
							// move nodes in layout
							hcLayout.moveOnCircle(p, down, vv);
							down = p;
						} else {
							Point2D out = e.getPoint();
							if (e.getModifiersEx() == addToSelectionModifiers || e.getModifiersEx() == modifiers) {
								rect.setFrameFromDiagonal(down, out);
							}
						}
						if (vertex != null)
							e.consume();
						vv.repaint();
					}
				}
			}
		}
	}

	public void mouseEntered(MouseEvent e) {
		this.setPathway(e);
		if (e.getComponent() instanceof MyVisualizationViewer) {
			inWindow = true;
			// this.cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
			vv.setCursor(cursor);
			MainWindow.getInstance().getFrame().setCursor(cursor);
			vv.revalidate();
			vv.repaint();
		}
	}

	public void mouseExited(MouseEvent e) {
		if (e.getComponent() instanceof MyVisualizationViewer) {
			inWindow = false;
			MainWindow.getInstance().getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (inWindow) {
			setPathway(e);
			vv.setMousePoint(vv.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint()));
		}
	}

	private void mousePressedAnnotation(MouseEvent e) {
		pressed = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint());
		MyAnnotationManager am = pw.getGraph().getAnnotationManager();
		MyAnnotation ma = am.getMyAnnotations(e.getPoint());
		if (ma == null && !MyAnnotationEditingGraphMouse.getInstance().isHovering()) {
			setModifyShape(false);
			this.currentAnnotation = null;
			return;
		}
		if (ma != currentAnnotation && !MyAnnotationEditingGraphMouse.getInstance().isHovering()) {
			if (ma == MyAnnotationEditingGraphMouse.getInstance().getHighlight()) {
				setModifyShape(false);
				this.currentAnnotation = null;
				return;
			}
			setModifyShape(false);
		}
		if (!modifyShape) {
			if (ma != null) {
				if (ma == currentAnnotation || ma == highlight) {
					this.removeHighlight();
					setModifyShape(false);
					return;
				}
				this.removeHighlight();
				GraphInstance.setSelectedObject(ma);
				MainWindow.getInstance().updateElementProperties();
				this.currentAnnotation = ma;
				RectangularShape s = new Rectangle();
				RectangularShape shape = ma.getShape();
				if (StringUtils.isEmpty(ma.getText())) {
					int offset = 5;
					s.setFrameFromDiagonal(shape.getMinX() - offset, shape.getMinY() - offset, shape.getMaxX() + offset,
							shape.getMaxY() + offset);
					highlight = new MyAnnotation(s, ma.getText(), Color.BLUE, Color.BLUE, Color.BLUE);
					am.add(Annotation.Layer.LOWER, highlight);
					am.updateMyAnnotation(ma);
				}
			} else {
				removeHighlight();
			}
			vv.repaint();
		}
	}

	private void mouseDraggedAnnotation(MouseEvent e) {
		if (currentAnnotation != null && !modifyShape) {
			MyAnnotationManager am = pw.getGraph().getAnnotationManager();
			Point2D p = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint());
			double xOffset = p.getX() - pressed.getX();
			double yOffset = p.getY() - pressed.getY();
			pressed.setLocation(p.getX(), p.getY());
			if (highlight != null) {
				am.moveAnnotation(highlight, xOffset, yOffset);
			}
			am.moveAnnotation(currentAnnotation, xOffset, yOffset);
			vv.repaint();
		}
	}

	private void mouseReleasedAnnotation(MouseEvent e) {
		if (!modifyShape) {
			Point2D released = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint());
			if (currentAnnotation != null && StringUtils.isEmpty(currentAnnotation.getText())) {
				setModifyShape(true);
				if (pressed.getX() != released.getX() || pressed.getY() != released.getY()) {
					double xOffset = released.getX() - pressed.getX();
					double yOffset = released.getY() - pressed.getY();
					pw.getGraph().getAnnotationManager().moveAnnotation(currentAnnotation, xOffset, yOffset);
				}
			}
		}
	}

	private void removeHighlight() {
		if (this.highlight != null) {
			pw.getGraph().getAnnotationManager().remove(highlight);
			highlight = null;
			currentAnnotation = null;
			vv.repaint();
		}
	}

	@SuppressWarnings("unchecked")
	private void setPathway(MouseEvent e) {
		// do not use GraphInstance.getPathway because graphs for transformation rules
		// also need mouse control
		if (this.pw == null) {
			if (e.getSource() instanceof MyVisualizationViewer) {
				vv = (MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract>) e.getSource();
				pw = vv.getPathway();
			}
		}
	}

	private void setModifyShape(boolean modify) {
		this.modifyShape = modify;
		MyAnnotationEditingGraphMouse.getInstance().setEnabled(modify);
		if (modify && this.highlight != null) {
			pw.getGraph().getAnnotationManager().remove(highlight);
			highlight = null;
		}
	}
}
