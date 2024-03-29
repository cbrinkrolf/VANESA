/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 */
package graph.eventhandlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractGraphMousePlugin;
import graph.GraphInstance;
import graph.jung.classes.MyVisualizationViewer;

/**
 * A plugin that zooms through the hierarchies without changing the saved
 * Pathway.
 * 
 * @author Tobias Loka
 * 
 */
public class MyZoomThroughHierarchyGraphMousePlugin extends AbstractGraphMousePlugin
		implements MouseListener, MouseMotionListener {

	private Pathway pw;
	private HierarchyMenu menu;
	private boolean inWindow = false;

	public MyZoomThroughHierarchyGraphMousePlugin() {
		this(InputEvent.BUTTON1_DOWN_MASK);
	}

	/**
	 * create instance and prepare shapes for visual effects
	 * 
	 * @param modifiers
	 */
	public MyZoomThroughHierarchyGraphMousePlugin(int modifiers) {
		super(modifiers);
	}

	/**
	 * sets the vertex locations. Needed to place new vertices
	 * 
	 * @param vertexLocations
	 */
	// public void setVertexLocations(HashMap vertexLocations) {
	// this.vertexLocations = vertexLocations;
	// }

	/**
	 * overrided to be more flexible, and pass events with key combinations. The
	 * default responds to both ButtonOne and ButtonOne+Shift
	 */
	@Override
	public boolean checkModifiers(MouseEvent e) {
		return (e.getModifiersEx() & modifiers) != 0;
	}

	/**
	 * If the mouse is pressed on an existing vertex, show (if coarse node) or hide
	 * (is no coarse node) the subpathway.
	 */
	public void mousePressed(MouseEvent e) {
		if (menu != null) {
			menu = null;
			return;
		}
		pw = GraphInstance.getPathway();
		if (checkModifiers(e)) {
			final VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = pw.getGraph()
					.getVisualizationViewer();
			GraphElementAccessor<BiologicalNodeAbstract, BiologicalEdgeAbstract> pickSupport = vv.getPickSupport();

			BiologicalNodeAbstract vertex = null;

			vertex = (BiologicalNodeAbstract) pickSupport.getVertex(vv.getGraphLayout(), e.getPoint().getX(),
					e.getPoint().getY());
			if (pw.isBNA() && ((BiologicalNodeAbstract) pw).getEnvironment().contains(vertex)) {
				openMenu(e, null);
			}
			openMenu(e, vertex);
		}
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		inWindow = true;
	}

	public void mouseExited(MouseEvent e) {
		inWindow = false;
	}

	public void mouseMoved(MouseEvent e) {
		if (inWindow) {
			@SuppressWarnings("unchecked")
			final MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = (MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract>) e
					.getSource();
			vv.setMousePoint(vv.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint()));
		}
	}

	private void openMenu(MouseEvent e, BiologicalNodeAbstract n) {
		menu = new HierarchyMenu(n);
		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	class HierarchyMenu extends JPopupMenu {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		JMenuItem openNode = new JMenuItem("Open subpathway");
		JMenuItem closeNode = new JMenuItem("Close subpathway");
		JMenuItem openAllNodes = new JMenuItem("Open next level");
		JMenuItem closeAllNodes = new JMenuItem("Close finest level");
		JMenuItem openNetwork = new JMenuItem("Open full network");
		JMenuItem closeNetwork = new JMenuItem("Close full network");
		JMenu marking = new JMenu("Change node type");
		JMenuItem environmentSelection = new JMenuItem("(Un)mark as environment node");
		JMenuItem coarseSelection = new JMenuItem("Convert to coarse node");
		JMenuItem setAsRootNode = new JMenuItem("(Un)mark as parent's root node");
		BiologicalNodeAbstract node;
		ActionListener listener;

		public HierarchyMenu(BiologicalNodeAbstract n) {
			node = n;
			listener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					HashSet<BiologicalNodeAbstract> set = new HashSet<BiologicalNodeAbstract>();
					if (e.getSource() == openNode) {
						pw.openSubPathway(node);
						pw.updateMyGraph();
					} else if (e.getSource() == closeNode) {
						pw.closeSubPathway(node.getParentNode());
						pw.updateMyGraph();
					} else if (e.getSource() == openAllNodes) {
						set.addAll(GraphInstance.getMyGraph().getAllVertices());
						for (BiologicalNodeAbstract n : set) {
							if (n.isCoarseNode()) {
								pw.openSubPathway(n);
							}
						}
						pw.updateMyGraph();
					} else if (e.getSource() == closeAllNodes) {
						int maxLevel = 0;
						for (BiologicalNodeAbstract n : GraphInstance.getMyGraph().getAllVertices()) {
							if (n.getAllParentNodes().size() > 0) {
								set.add(n.getParentNode());
								maxLevel = Math.max(maxLevel, n.getAllParentNodes().size() - 1);
							}
						}
						for (BiologicalNodeAbstract n : set) {
							if (n.getAllParentNodes().size() == maxLevel) {
								pw.closeSubPathway(n);
							}
						}
						pw.updateMyGraph();
					} else if (e.getSource() == openNetwork) {
						pw.openAllSubPathways();
						pw.updateMyGraph();
					} else if (e.getSource() == closeNetwork) {
						pw.closeAllSubPathways();
						pw.updateMyGraph();
					} else if (e.getSource() == coarseSelection) {
						BiologicalNodeAbstract.coarse(n);
					} else if (e.getSource() == environmentSelection) {
						n.setMarkedAsEnvironment(!n.isMarkedAsEnvironment());
					} else if (e.getSource() == setAsRootNode) {
						if (n.getParentNode() == null) {
							if (n.getRootPathway().getRootNode() == n) {
								n.getRootPathway().setRootNode(null);
							} else {
								n.getRootPathway().setRootNode(n);
							}
						} else {
							if (n.getParentNode().getRootNode() == n) {
								n.getParentNode().setRootNode(null);
							} else {
								n.getParentNode().setRootNode(n);
							}
						}
					}
					GraphInstance.getMyGraph().updateLayout();
					GraphInstance.getMyGraph().getVisualizationViewer().repaint();
					menu = null;
				}
			};
			if (n == null) {
				add(openAllNodes);
				openAllNodes.addActionListener(listener);
				add(closeAllNodes);
				closeAllNodes.addActionListener(listener);
				add(new JSeparator());
				add(openNetwork);
				openNetwork.addActionListener(listener);
				add(closeNetwork);
				closeNetwork.addActionListener(listener);
			} else {
				add(openNode);
				openNode.addActionListener(listener);
				add(closeNode);
				closeNode.addActionListener(listener);
				environmentSelection.addActionListener(listener);
				coarseSelection.addActionListener(listener);
				setAsRootNode.addActionListener(listener);
				marking.add(environmentSelection);
				marking.add(coarseSelection);
				marking.add(setAsRootNode);
				add(marking);
				if (n.getParentNode() == pw || n.getParentNode() == null) {
					closeNode.setEnabled(false);
				}
				if (!n.isCoarseNode()) {
					openNode.setEnabled(false);
				}
				if (n.isCoarseNode() || n.isMarkedAsCoarseNode()) {
					coarseSelection.setEnabled(false);
					environmentSelection.setEnabled(false);
					setAsRootNode.setEnabled(false);
				}
				if (n.isEnvironmentNodeOf(pw)) {
					coarseSelection.setEnabled(false);
					setAsRootNode.setEnabled(false);
				}
				if (n.getParentNode() != null || pw.isBNA()) {
					environmentSelection.setEnabled(false);
				}
				if (!coarseSelection.isEnabled() && !environmentSelection.isEnabled() && !setAsRootNode.isEnabled()) {
					marking.setEnabled(false);
				}

			}
		}
	}

}
