package graph.jung.classes;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import petriNet.Place;

import biologicalElements.Elementdeclerations;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import configurations.gui.MDLayoutConfig;
import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
//import edu.uci.ics.jung.graph.decorators.VertexShapeFunction;
//import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import graph.GraphInstance;
//import edu.uci.ics.jung.visualization.subLayout.SubLayoutDecorator;
import graph.layouts.modularLayout.MDLayout;
import graph.layouts.modularLayout.MDNode;

/**
 *
 * @author Besitzer
 */
public class TokenRenderer implements VisualizationViewer.Paintable {

    private VisualizationViewer viewer;
    private static final Color PRIME_PAINT = new Color(1, 0, 0, 0.4f),  PARALLEL_PAINT = new Color(0, 1, 0, 0.4f),  SERIES_PAINT = new Color(0, 0, 1, 0.4f);
    private int vertexWidth = -1,  vertexHeight = -1;
    private static Map<String, Color> compartmentsColor = new HashMap();
    private Collection nodes;
    private GraphInstance graphInstance = new GraphInstance();
    private BiologicalNodeAbstract bna;
    private Place p;
    private Point2D point;

   

    public TokenRenderer(VisualizationViewer viewer) {
        this.viewer = viewer;
       
    }

    public void paint(Graphics arg0) {
    	this.nodes = graphInstance.getPathway().getAllNodes();
    	Iterator<BiologicalNodeAbstract> it = nodes.iterator();
    	int i = 0;
    	int j = 0;
    	while(it.hasNext()){
    		bna = it.next();
    		if(bna instanceof Place){
    			//System.out.println("paint");
    			point = graphInstance.getMyGraph().getVertexLocation(bna);
    			p = (Place) bna;
    			//arg0.drawString(p.getToken()+"", (int)point.getX(), (int)point.getY());
    			i+=100;
    			j+=100;
    			//viewer.getRenderContext().getScreenDevice();
    		}
    		//System.out.println(bna.getID());
    	}
    	
    }

	@Override
	public boolean useTransform() {
		// TODO Auto-generated method stub
		return true;
	}

}

