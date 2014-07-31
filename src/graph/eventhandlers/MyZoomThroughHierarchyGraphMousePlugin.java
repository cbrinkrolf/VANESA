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
				return;
			}
			if (vertex != null) {
				if(vertex.isCoarseNode()){
					vertex.showSubPathway();
				} else if(vertex.getParentNode()!=null){
					vertex.getParentNode().hideSubPathway();
				}
			} else {
				hideAllSubpathways();
			}
			vv.repaint();
		}
	}
	
	public void hideAllSubpathways(){
		if(pw==null || pw!=new GraphInstance().getPathway()){
			return;
		}
		Set<BiologicalNodeAbstract> parentNodes = new HashSet<BiologicalNodeAbstract>();
		Set<BiologicalNodeAbstract> innerNodes = new HashSet<BiologicalNodeAbstract>();
		if(pw.isBNA()){
			innerNodes.addAll(((BiologicalNodeAbstract) pw).getInnerNodes());
		} else {
			innerNodes.addAll(pw.getAllNodes());
		}
		for(BiologicalNodeAbstract node : innerNodes){
			if(node.getParentNode()!=null){
				parentNodes.add(node.getParentNode());
			}
		}
		for(BiologicalNodeAbstract node : parentNodes){
			if(node.getParentNode()!=null && node.getParentNode().getCurrentShownParentNode(pw.getGraph())!=null){
				continue;
			}
			node.hideSubPathway();
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
}
