package graph.jung.classes;

/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/

import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;

import javax.swing.ToolTipManager;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.PluggableRenderContext;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.control.MouseListenerTranslator;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;

/**
 * Adds mouse behaviors and tooltips to the graph visualization base class
 *
 * @author Joshua O'Madadhain
 * @author Tom Nelson
 * @author Danyel Fisher
 */
@SuppressWarnings("serial")
public class MyPRC<V,E> extends PluggableRenderContext<V,E> {

	MyPRC() {
		
		// TODO Auto-generated constructor stub
	}

	
	
}
