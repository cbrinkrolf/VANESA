package graph.jung.classes;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.Iterator;

import petriNet.Place;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;

//import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.visualization.PluggableRenderContext;
//import edu.uci.ics.jung.visualization.PluggableRenderer;
import graph.GraphInstance;
import gui.MainWindowSingelton;

public class MyPluggableRenderer extends PluggableRenderContext{

    public void paintVertex(Graphics g, Vertex v, int x, int y){
        if (!vertexIncludePredicate.evaluate(v))
            return;
        boolean vertexHit = true;
        Rectangle deviceRectangle = null;
        Graphics2D g2d = (Graphics2D)g;
        if(screenDevice != null) {
            Dimension d = screenDevice.getSize();
            if(d.width <= 0 || d.height <= 0) {
                d = screenDevice.getPreferredSize();
            }
            deviceRectangle = new Rectangle(
                    0,0,
                    d.width,d.height);
        }        
        Stroke old_stroke = g2d.getStroke();
        Stroke new_stroke = vertexStrokeFunction.getStroke(v);
        if (new_stroke != null) {
            g2d.setStroke(new_stroke);
        }
        // get the shape to be rendered
        Shape s = vertexShapeFunction.getShape(v);
        
        // create a transform that translates to the location of
        // the vertex to be rendered
        AffineTransform xform = AffineTransform.getTranslateInstance(x,y);
        // transform the vertex shape with xtransform
        s = xform.createTransformedShape(s);
        
        vertexHit = viewTransformer.transform(s).intersects(deviceRectangle);

        if (vertexHit) {

			if (vertexIconFunction != null) {
				paintIconForVertex(g2d, v, x, y);
			} else {
				paintShapeForVertex(g2d, v, s);
			}

			if (new_stroke != null) {
				g2d.setStroke(old_stroke);
			}
			String label = vertexStringer.getLabel(v);
			if (label != null) {
				labelVertex(g, v, label, x, y);
			}
		}
    }
    
    protected void labelVertex(Graphics g, Vertex v, String label, int x, int y)
    {
    	Pathway pw = new GraphInstance().getPathway();
    	boolean isPlace=false;
    	Place p=null;
    	for (Iterator i=pw.getAllNodes().iterator(); i.hasNext();){
    		BiologicalNodeAbstract bna  = (BiologicalNodeAbstract) i.next();
    		if (bna.getVertex().equals(v) && bna instanceof Place)  {
    		isPlace=true;
    		p=(Place) bna;
    		}
    	}
    
    	if (!isPlace) super.labelVertex(g, v, label, x, y);
    	else{    	
   
    	Component component1 =null;
    	if (p.isDiscrete())
        component1 = prepareRenderer(graphLabelRenderer, (int)p.getToken() , isPicked(v), v);   	
    	else component1 = prepareRenderer(graphLabelRenderer, new DecimalFormat("#0.00").format(p.getToken()), isPicked(v), v);
    	
        Dimension d1 = component1.getPreferredSize();    
        int h_offset = -d1.width / 2;
        int v_offset = -d1.height / 2;
        rendererPane.paintComponent(g, component1, screenDevice, x+h_offset, y+v_offset,
                d1.width, d1.height, true);
        
        Component component2 = prepareRenderer(graphLabelRenderer, label, isPicked(v), v);
        Dimension d2 = component2.getPreferredSize();
            Rectangle2D bounds = vertexShapeFunction.getShape(v).getBounds2D();
           int  h_offset2 = (int)(bounds.getWidth() / 2) + 5;
            int v_offset2 = (int)(bounds.getHeight() / 2) + 5 -d2.height;  
        rendererPane.paintComponent(g, component2, screenDevice, x+h_offset2, y+v_offset2,
                d2.width, d2.height, true);
   
        Component component3 = prepareRenderer(graphLabelRenderer,"P"+ p.getID(), isPicked(v), v);
        Dimension d3 = component3.getPreferredSize();
        int  h_offset3 = -(int)(bounds.getWidth() / 2) - 5- d3.width;
        int v_offset3 = -(int)(bounds.getHeight() / 2) - 5; 
        rendererPane.paintComponent(g, component3, screenDevice, x+h_offset3, y+v_offset3,
                d3.width, d3.height, true);
    	
    	}
    }
	
}
