/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph.jung.classes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import biologicalElements.Elementdeclerations;
import configurations.gui.MDLayoutConfig;
import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
//import edu.uci.ics.jung.graph.decorators.VertexShapeFunction;
//import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
//import edu.uci.ics.jung.visualization.subLayout.SubLayoutDecorator;
//import graph.layouts.modularLayout.MDLayout;
//import graph.layouts.modularLayout.MDNode;
import graph.layouts.modularLayout.MDLayout;
import graph.layouts.modularLayout.MDNode;

/**
 *
 * @author Besitzer
 */
public class InnerNodeRenderer implements VisualizationViewer.Paintable {

    private VisualizationViewer viewer;
    private static final Color PRIME_PAINT = new Color(1, 0, 0, 0.4f),  PARALLEL_PAINT = new Color(0, 1, 0, 0.4f),  SERIES_PAINT = new Color(0, 0, 1, 0.4f);
    private int vertexWidth = -1,  vertexHeight = -1;
    private static Map<String, Color> compartmentsColor = new HashMap();


    static {
        compartmentsColor.put(Elementdeclerations.membrane, new Color(0.8f, 0.2f, 0.2f, 0.4f));
        compartmentsColor.put(Elementdeclerations.cellInside, new Color(0.1f, 0.9f, 0.2f, 0.4f));
        compartmentsColor.put(Elementdeclerations.cellOutside, new Color(0.2f, 0.2f, 0.9f, 0.4f));
        compartmentsColor.put(Elementdeclerations.cytoplasma, new Color(0.8f, 0.2f, 0.8f, 0.4f));
        compartmentsColor.put(Elementdeclerations.nucleus, new Color(0.1f, 0.8f, 0.7f, 0.4f));
    }

    public InnerNodeRenderer(VisualizationViewer viewer) {
        this.viewer = viewer;
    }

    public void paint(Graphics arg0) {
        try {
            if (!MDLayoutConfig.showInnerNode) {
                return;
            }
            AggregateLayout decorator = (AggregateLayout)  viewer.getGraphLayout();
            MDLayout layout = (MDLayout) decorator.getDelegate();
            Renderer pr = viewer.getRenderer();
            //VertexShapeFunction sf = pr.getVertexShapeFunction();
//            Vertex v0 = (Vertex) layout.getGraph().getVertices().iterator().next();
//            Rectangle b = sf.getShape(v0).getBounds();
//            this.vertexWidth = b.width / 2;
//            this.vertexHeight = b.height / 2;
            MDNode root = layout.getRoot();
            paintGroup(arg0, root);
        } catch (ClassCastException e) {
        } catch (NullPointerException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void paintGroup(Graphics g, MDNode node) {
        Collection<MDNode> children = node.getChildren();
        if (children != null) {
            for (MDNode child : children) {
                paintGroup(g, child);
            }
        }
        if (node.isRoot()) {
            return;
        }
        boolean vertexHit = true;
        // get the shape to be rendered
        Rectangle bound = node.getBoundary();
        if (bound != null) {
            Rectangle frame = viewer.getLayoutTransformer().transform(bound).getBounds();
//            frame.grow(this.vertexWidth, this.vertexHeight);

            if (node.getChildren() != null && node.getCompartment()!=null) {
                paintShapeForVertex(g, node.getCompartment(), frame);
            }else {
                paintShapeForVertex(g, node.getNodeType(), frame);
            }
        }
    }

    public boolean useTransform() {
        return true;
    }

    protected void paintShapeForVertex(Graphics g, String compartment, Rectangle shape) {
        Color fillPaint = compartmentsColor.get(compartment);
        Color oldPaint = g.getColor();
        g.setColor(fillPaint);
        g.fillRect(shape.x, shape.y, shape.width, shape.height);
//        double cx=shape.getCenterX(),cy=shape.getCenterY();
//        g.setColor(Color.red);
//        g.fillOval((int)cx-30, (int)cy-30,60 ,60);
        g.setColor(oldPaint);
    }

    protected void paintShapeForVertex(Graphics g, int type, Rectangle shape) {
        Color fillPaint = PRIME_PAINT;
        switch (type) {
            case MDNode.PARALLEL:
                fillPaint = PARALLEL_PAINT;
                break;
            case MDNode.SERIES:
                fillPaint = SERIES_PAINT;
        }
        Color oldPaint = g.getColor();
        g.setColor(fillPaint);
        g.fillRect(shape.x, shape.y, shape.width, shape.height);
        g.setColor(oldPaint);
    }
}
