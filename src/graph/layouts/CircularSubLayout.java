/*package graph.layouts;

/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Aug 23, 2005
 */


/*import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.visualization.subLayout.SubLayout;
import edu.uci.ics.jung.visualization.subLayout.SubLayoutDecorator;

public class CircularSubLayout implements SubLayout {

    protected final Map map = new LinkedHashMap();
    private SubLayoutDecorator layout;

    
    /**
     * create an instance with passed values
     * @param vertices the collection of vertices to arrange in a circle
     * @param radius the radius of the circle
     * @param center the center of the circle
     */
	/*public CircularSubLayout(Collection vertices, SubLayoutDecorator layout) {
        this.layout=layout;
        initializeLocations(vertices);
		
	}

	/**
	 * Map the Vertices in the passed collection to their
	 * locations, distributed about the circumference of
	 * a circle
	 * 
	 * @param vertices
	 */
	/*private void initializeLocations(Collection vertices) {
		Vertex[] vertexArray =
		    (Vertex[]) vertices.toArray(new Vertex[vertices.size()]);
		
        // only apply sublayout if there is more than one vertex
        if (vertexArray.length > 1) {
			SubLayoutCenter center = new SubLayoutCenter((Set) vertices,layout);
        	for (int i = 0; i < vertexArray.length; i++) {
				double angle = (2 * Math.PI * i) / vertexArray.length;
				Point2D point = new Point2D.Double(
						(Math.cos(angle) * center.getRadius() + center.getCenter().getX()), (Math
								.sin(angle)
								* center.getRadius() + center.getCenter().getY()));
				map.put(vertexArray[i], point);

				layout.forceMove(vertexArray[i], point.getX(), point.getY());
				
			}
		}
	}
	
    public Point2D getLocation(ArchetypeVertex v) {
        return (Point2D)map.get(v);
    }
}*/
