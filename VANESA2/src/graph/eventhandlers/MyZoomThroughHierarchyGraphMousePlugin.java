/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 */
package graph.eventhandlers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationServer.Paintable;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractGraphMousePlugin;
import edu.uci.ics.jung.visualization.util.ArrowFactory;
import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.GraphInstance;

/**
 * A plugin that zooms throgh the hierarchies without changing the saved Pathway.
 * 
 * @author Tobias Loka
 * 
 */
public class MyZoomThroughHierarchyGraphMousePlugin extends AbstractGraphMousePlugin
		implements MouseListener, MouseMotionListener {

	private GraphInstance graphInstance = new GraphInstance();
	private Pathway pw;
	private HierarchyMenu menu;

	public MyZoomThroughHierarchyGraphMousePlugin() {
		this(InputEvent.BUTTON1_MASK);
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
		return (e.getModifiers() & modifiers) != 0;
	}

	/**
	 * If the mouse is pressed on an existing vertex, show (if coarse node) or 
	 * hide (is no coarse node) the subpathway.
	 */
	public void mousePressed(MouseEvent e) {
		if(menu!=null){
			menu = null;
			return;
		}
		pw = graphInstance.getPathway();
		if (checkModifiers(e)) {
			
			final VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = (VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract>) e
					.getSource();

			GraphElementAccessor<BiologicalNodeAbstract, BiologicalEdgeAbstract> pickSupport = vv
					.getPickSupport();

			BiologicalNodeAbstract vertex = null;
			pw.getGraph().lockVertices();
			
			vertex = (BiologicalNodeAbstract) pickSupport
					.getVertex(vv.getGraphLayout(), e.getPoint().getX(), e
							.getPoint().getY());
			if(pw.isBNA() && ((BiologicalNodeAbstract) pw).getEnvironment().contains(vertex)){
				openMenu(e,null);
			}
			openMenu(e,vertex);
		}
	}

	public void mouseReleased(MouseEvent e) {
		
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}
	
	private void openMenu(MouseEvent e, BiologicalNodeAbstract n){
        menu = new HierarchyMenu(n);
        menu.show(e.getComponent(), e.getX(), e.getY());
    }
	
	class HierarchyMenu extends JPopupMenu {
		private static final long serialVersionUID = 153465734562L;
		JMenuItem openNode = new JMenuItem("Open subpathway");
	    JMenuItem closeNode = new JMenuItem("Close subpathway");
	    JMenuItem openAllNodes = new JMenuItem("Open all subpathways");
	    JMenuItem openNetwork = new JMenuItem("Open full network");
	    JMenuItem closeNetwork = new JMenuItem("close full network");
	    BiologicalNodeAbstract node;
	    ActionListener listener;
	    
	    public HierarchyMenu(BiologicalNodeAbstract n){
	    	node = n;
	    		listener = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						HashSet<BiologicalNodeAbstract> set = new HashSet<BiologicalNodeAbstract>();
						if(e.getSource()==openNode){
							pw.openSubPathway(node);
						} else if(e.getSource()==closeNode){
							pw.closeSubPathway(node.getParentNode());
						} else if(e.getSource()==openAllNodes){
							set.addAll(GraphInstance.getMyGraph().getAllVertices());
							for(BiologicalNodeAbstract n : set){
								if(n.isCoarseNode()){
									pw.openSubPathway(n);
								}
							}
						}else if(e.getSource()==openNetwork){
							pw.openAllSubPathways();
						} else if(e.getSource()==closeNetwork){
							pw.closeAllSubPathways();
						}
						GraphInstance.getMyGraph().updateLayout();
						GraphInstance.getMyGraph().getVisualizationViewer().repaint();
						menu = null;
					}
	    	    };
	    	if(n==null){
	    		add(openAllNodes);
	    		openAllNodes.addActionListener(listener);
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
	    		if(n.getParentNode() == pw || n.getParentNode() == null){
	    			closeNode.setEnabled(false);
	    		}
	    		if(!n.isCoarseNode()){
	    			openNode.setEnabled(false);
	    		}
	    	}
	    }
	}
	
}
