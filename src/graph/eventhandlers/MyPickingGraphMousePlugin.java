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
import java.util.Iterator;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.annotations.Annotation;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;
import graph.jung.classes.MyGraph;
import graph.jung.classes.MyVisualizationViewer;
import graph.layouts.Circle;
import graph.layouts.HierarchicalCircleLayout;
import gui.MainWindow;
import gui.MyAnnotation;
import gui.MyAnnotationManager;
import miscalleanous.internet.FollowLink;

public class MyPickingGraphMousePlugin extends PickingGraphMousePlugin<BiologicalNodeAbstract, BiologicalEdgeAbstract> {

	private HashMap<BiologicalNodeAbstract, Point2D> oldVertexPositions = new HashMap<BiologicalNodeAbstract, Point2D>();
	private Set<BiologicalNodeAbstract> originalSelection = new HashSet<BiologicalNodeAbstract>();
	private boolean inwindow = false;

	private boolean dragging = false;

	private Pathway pw;
	private VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv;

	// for annotations
	private MyAnnotation currentAnnotation = null;
	private MyAnnotation highlight = null;
	private Point2D pressed = null;
	private Point2D released = null;
	private boolean moved = false;
	private boolean sticky = true;

	public MyPickingGraphMousePlugin() {

	}

	public void mouseReleased(MouseEvent e) {
		moved = false;
		// vv.setFocusable(true);
		// vv.requestFocus();

		// System.out.println("released");
		pw.pickGroup();
		if (inwindow) {
			this.mouseReleasedAnnotation(e);
			// If mouse was released to change the selection, save vertex
			// positions and return.
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

			// Find coarse nodes in specified environment of the final mouse
			// position.
			GraphElementAccessor<BiologicalNodeAbstract, BiologicalEdgeAbstract> pickSupport = vv.getPickSupport();
			float width = selectedNodes.iterator().next().getShape().getBounds().width;
			float height = selectedNodes.iterator().next().getShape().getBounds().height;
			Shape shape = new Rectangle.Float(e.getX() - (width / 2), e.getY() - (height / 2), width, height);
			Collection<BiologicalNodeAbstract> vertices = pickSupport.getVertices(vv.getGraphLayout(), shape);
			vertices.removeAll(selectedNodes);

			// If exactly one node was found, take this node, otherwise return.
			BiologicalNodeAbstract vertex = null;
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
						if (pw.getVertices().keySet().contains(selectedNode)) {
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
		Set<BiologicalNodeAbstract> selection = new HashSet<BiologicalNodeAbstract>();
		selection.addAll(pw.getSelectedNodes());
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
		@SuppressWarnings("unchecked")
		final MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = (MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract>) e
				.getSource();
		pw = vv.getPathway();
		this.vv = vv;
		// vv = pw.getGraph().getVisualizationViewer();
		if (inwindow) {
			super.mousePressed(e);
			originalSelection.clear();
			originalSelection.addAll(pw.getSelectedNodes());
			
			Iterator<BiologicalNodeAbstract> it = originalSelection.iterator();
			BiologicalNodeAbstract bna;
			BiologicalNodeAbstract pick;
			MyGraph g = pw.getGraph();
			while(it.hasNext()){
				bna = it.next();

				if(bna.hasRef()){
					//g.getVisualizationViewer().getPickedVertexState().pick(bna.getRef(), true);
					Iterator<BiologicalNodeAbstract> it2 = bna.getRef().getRefs().iterator();
					while (it2.hasNext()) {
						pick = it2.next();
						// System.out.println(pick.getLabel());
						// System.out.println(pick);
						// g.getVisualizationViewer().getPickedVertexState().pick(pick, true);
					}
				}else{

				
				
				// System.out.println("c: "+g.getJungGraph().getVertexCount());
				
				Iterator<BiologicalNodeAbstract> it2 = bna.getRefs().iterator();
				// System.out.println("size: "+bna.getRefs().size());
				while (it2.hasNext()) {
					pick = it2.next();
					// System.out.println(pick.getLabel());
					// System.out.println(pick);
					//g.getVisualizationViewer().getPickedVertexState().pick(pick, true);

				}
				}
			}
			
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
			if (pw.getSelectedNodes().size() == 0 && pw.getSelectedEdges().size() == 0
					&& SwingUtilities.isLeftMouseButton(e)) {
				this.mousePressedAnnotation(e);
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
		// System.out.println("clicked");

		if (inwindow) {
			if (dragging) {// && SwingUtilities.isRightMouseButton(e)){
				this.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
				// System.out.println("dragging false");
				dragging = false;
				// vv.setComponentPopupMenu(new GraphPopUp().returnPopUp());

				MainWindow.getInstance().getFrame().setCursor(cursor);
				vv.setCursor(cursor);
				vv.getComponentPopupMenu().show();
			}

			if (e.getClickCount() == 1) {
				super.mouseClicked(e);
			} else {

				Iterator<BiologicalNodeAbstract> it = pw.getSelectedNodes().iterator();
				BiologicalNodeAbstract bna;
				String urlString;

				while (it.hasNext()) {
					bna = it.next();
					if (bna.getBiologicalElement().equals(Elementdeclerations.protein)
							|| bna.getBiologicalElement().equals(Elementdeclerations.inhibitor)
							|| bna.getBiologicalElement().equals(Elementdeclerations.factor)
							|| bna.getBiologicalElement().equals(Elementdeclerations.smallMolecule)) {
						urlString = "https://agbi.techfak.uni-bielefeld.de/DAWISMD/jsp/result/protein_result.jsp?Protein_Id="
								+ bna.getLabel();
						FollowLink.openURL(urlString);
					} else if (bna.getBiologicalElement().equals(Elementdeclerations.enzyme)) {
						urlString = "https://agbi.techfak.uni-bielefeld.de/DAWISMD/jsp/result/enzyme_result.jsp?Enzyme_Id="
								+ bna.getLabel();
						FollowLink.openURL(urlString);
					}
				}
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (inwindow) {
			if (this.currentAnnotation != null && SwingUtilities.isLeftMouseButton(e)) {
				this.mouseDraggedAnnotation(e);
			} else {
				// System.out.println("d");
				if (!(pw.getGraph().getLayout() instanceof HierarchicalCircleLayout)) {

					if (SwingUtilities.isRightMouseButton(e)) {
						if (!dragging) {
							dragging = true;
							// System.out.println("dragged");

							vv.getComponentPopupMenu().hide();
							// System.out.println("paintes");
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
						if (moved || !sticky) {
							super.mouseDragged(e);
						}
					}
				} else {
					if (locked == false) {
						if (vertex != null) {
							HierarchicalCircleLayout hcLayout = (HierarchicalCircleLayout) pw.getGraph().getLayout();
							// mouse position
							Point p = e.getPoint();

							// move nodes in layout
							hcLayout.moveOnCircle(p, down, vv);
							down = p;
						} else {
							Point2D out = e.getPoint();
							if (e.getModifiersEx() == this.addToSelectionModifiers || e.getModifiersEx() == modifiers) {
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
		@SuppressWarnings("unchecked")
		final MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = (MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract>) e
				.getSource();
		pw = vv.getPathway();
		this.vv = vv;
		if (e.getComponent().toString().contains("MyVisualizationViewer")) {

			inwindow = true;
			// this.cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
			vv.setCursor(cursor);
			MainWindow.getInstance().getFrame().setCursor(cursor);
			vv.revalidate();
			vv.repaint();
		}
	}

	public void mouseExited(MouseEvent e) {
		if (e.getComponent().toString().contains("MyVisualizationViewer")) {
			inwindow = false;
			MainWindow.getInstance().getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	public void mouseMoved(MouseEvent e) {
	}

	private void mousePressedAnnotation(MouseEvent e) {
		pressed = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint());
		MyAnnotationManager am = pw.getGraph().getAnnotationManager();
		MyAnnotation ma = am.getMyAnnotations(e.getPoint());
		if (ma != null) {
			if (ma == currentAnnotation) {
				this.removeHighlight();
				this.currentAnnotation = null;
				return;
			}
			this.currentAnnotation = ma;
			Annotation<?> a2;
			RectangularShape s = new Rectangle();
			RectangularShape shape = ma.getShape();
			if (ma.getText().length() == 0) {
				// System.out.println("in");
				int offset = 5;
				s.setFrameFromDiagonal(shape.getMinX() - offset, shape.getMinY() - offset, shape.getMaxX() + offset,
						shape.getMaxY() + offset);
				a2 = new Annotation<>(s, Annotation.Layer.LOWER, Color.BLUE, true, new Point2D.Double(0, 0));
				highlight = new MyAnnotation(a2, s, ma.getText());
				highlight.setAnnotation(a2);

				am.add(Annotation.Layer.LOWER, highlight);

				am.updateMyAnnotation(ma);
			}
		} else {
			this.currentAnnotation = null;
			this.removeHighlight();
		}
		vv.repaint();
	}

	private void mouseDraggedAnnotation(MouseEvent e) {
		if (currentAnnotation != null) {
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
		released = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint());
		if (currentAnnotation != null) {
			if (pressed.getX() != released.getX() || pressed.getY() != released.getY()) {
				double xOffset = released.getX() - pressed.getX();
				double yOffset = released.getY() - pressed.getY();
				pw.getGraph().getAnnotationManager().moveAnnotation(currentAnnotation, xOffset, yOffset);
			}
		}
		this.currentAnnotation = null;
		this.removeHighlight();
		vv.repaint();
	}

	private void removeHighlight() {
		if (this.highlight != null) {
			MyAnnotationManager am = pw.getGraph().getAnnotationManager();
			am.remove(highlight);
			this.highlight = null;
			vv.repaint();
		}
	}
}
