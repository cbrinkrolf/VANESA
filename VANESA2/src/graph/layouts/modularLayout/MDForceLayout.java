/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph.layouts.modularLayout;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
//import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Graph;
/*import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.VertexFontFunction;
import edu.uci.ics.jung.utils.UserData;
import edu.uci.ics.jung.visualization.AbstractLayout;
import edu.uci.ics.jung.visualization.Coordinates;
import edu.uci.ics.jung.visualization.subLayout.SubLayout;
import edu.uci.ics.jung.visualization.subLayout.SubLayoutDecorator;*/

/**
 *
 * @author test
 */
public class MDForceLayout extends AbstractLayout implements SubLayout {

    private MDNode root;
    private Point center = new Point();
    private int side_width = 50;
    private Set<Vertex> checkList;
    private Pathway pathway;
    private VertexFontFunction vf;
    private Graphics g;
    private List<ForceDirectedLayout> subLayouts = new ArrayList();
    private int width;
//    private int height;
    public MDForceLayout(Graph graph, Pathway pw, VertexFontFunction _vf, Graphics _g) {
        super(graph);
        this.pathway = pw;
        this.vf = _vf;
        this.g = _g;
    }

    public MDForceLayout(Graph graph, Set<Vertex> vertices, SubLayoutDecorator layout, Pathway pw, VertexFontFunction _vf, Graphics _g) {
        this(graph, pw, _vf, _g);
        this.checkList = new HashSet(vertices);
        this.initialize_local(vertices, layout);
        this.initializeLocations();
    }

    private void layoutParalleNodes() {
        if (root.getChildren() != null) {
            List<MDNode> nodes = new ArrayList(root.getChildren());
            Collections.sort(nodes, new Comparator<MDNode>() {

                public int compare(MDNode o1, MDNode o2) {
                    int h1 = o1.getBoundary() == null ? 0 : o1.getBoundary().height;
                    int h2 = o2.getBoundary() == null ? 0 : o2.getBoundary().height;
                    return h1 - h2;
                }
            });
            float startX = 0, startY = 0, maxY = 0;
            for (MDNode node : nodes) {
                Rectangle rect = node.getBoundary();
                int w = rect == null ? 0 : rect.width;
                int h = rect == null ? 0 : rect.height;
                if (startX + w > this.width) {
                    startX = 0;
                    startY = (maxY + 10 + MDForceLayoutConfig.params[MDForceLayoutConfig.REJECT_DISTANCE].floatValue());
                }
                maxY = Math.max(maxY, startY + h);
                float newCx = startX + w / 2f,
                        newCy = startY + h / 2f;
                float dx = newCx - node.getCenter().x;
                float dy = newCy - node.getCenter().y;
                node.move(dx, dy);
                startX += w + MDForceLayoutConfig.params[MDForceLayoutConfig.REJECT_DISTANCE].floatValue();
                node.calcCoordinates();
            }

        }
    }

    @Override
	public void initializeLocations() {
        Dimension size = this.getCurrentSize();
        this.subLayouts.clear();
        if (size != null) {
            this.center.setLocation(size.width / 2, size.height / 2);
            this.width = size.width;
        }
        Collection<MDNode> children = root.getChildren();
        if (children != null) {
            if (root.getNodeType() == MDNode.PARALLEL) {
                for (MDNode c : children) {
                    if (c.getChildren() != null) {
                        this.subLayouts.add(new ForceDirectedLayout(c, MDForceLayoutConfig.RUNONCE, MDForceLayoutConfig.params[MDForceLayoutConfig.MAX_ITERATIONS].intValue()));
                    }
                }
            } else {
                this.subLayouts.add(new ForceDirectedLayout(root, MDForceLayoutConfig.RUNONCE, MDForceLayoutConfig.params[MDForceLayoutConfig.MAX_ITERATIONS].intValue()));
            }
        }
        this.advancePositions();

//        this.setInitialized(true);
        resizeDim();
    }

    private void resizeDim() {
        root.reCalcBounds();
        float dx = center.x - root.getCenter().x;
        float dy = center.y - root.getCenter().y;
        root.move(dx, dy);
        root.calcCoordinates();
        if (this.getCurrentSize() != null) {
            this.resize(new Dimension(
                    root.getWidth() + this.side_width,
                    root.getHeight() + this.side_width));
        }
    }

    public MDNode getRoot() {
        return root;
    }

    /**
     * This method calls <tt>initialize_local_vertex</tt> for each vertex,
     * and also adds initial coordinate information for each vertex. (The
     * vertex's initial location is set by calling <tt>initializeLocation</tt>.
     */
    @Override
    protected void initialize_local() {
        for (Object o : this.getGraph().getVertices()) {
            Vertex v = (Vertex) o;
            initialize_local_vertex(v);
        }
        decomposition(this.getVisibleVertices());
    }

    protected void initialize_local(Set<Vertex> selected, SubLayoutDecorator decorator) {
        double minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE,
                maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        for (Object o : selected) {
            Vertex v = (Vertex) o;
//            initialize_local_vertex(v);
            if (v.getUserDatum(ModularDecomposition.MD_KEY) == null) {
                this.initialize_local_vertex(v);
            }
            double x = decorator.getX(v);
            double y = decorator.getY(v);
            MDNode node = (MDNode) v.getUserDatum(ModularDecomposition.MD_KEY);
            double w=0,h=0,dx=0,dy=0;
            Rectangle rect=node.getBoundary();
            Point p=node.getPaintOffset();
            if(rect!=null && p!=null){
                w=rect.width;
                h=rect.height;
                dx=p.x;
                dy=p.y;
            }
            minX = Math.min(minX, x-dx);
            minY = Math.min(minY, y-dy);
            maxX = Math.max(x-dx+w, maxX);
            maxY = Math.max(y-dy+h, maxY);
        }
        decomposition(selected);
        this.width=(int) Math.round(maxX-minX);
        this.center.setLocation(minX + (maxX - minX) / 2.0, minY + (maxY - minY) / 2.0);
//        this.area = (int) ((maxX - minX) * (maxY - minY));
    }

    private void decomposition(Set<Vertex> vertices) {
        try {
            root = ClusterDecomposition.decomposition(vertices, this.getVisibleEdges());
        } catch (ConcurrentModificationException cme) {
            decomposition(vertices);
        }
    }

    /**
     * Returns the Coordinates object that stores the vertex' x and y location.
     *
     * @param v
     *            A Vertex that is a part of the Graph being visualized.
     * @return A Coordinates object with x and y locations.
     */
    @Override
    public Coordinates getCoordinates(ArchetypeVertex v) {
        try {
            if (this.checkList == null || this.checkList.contains(v)) {
                MDNode node = (MDNode) v.getUserDatum(ModularDecomposition.MD_KEY);
                return node.getCoordinates();////                }
            } else {
                return null;
            }
        } catch (Exception e) {
        }
        return new Coordinates();
    }

    /**
     * Returns the x coordinate of the vertex from the Coordinates object.
     * in most cases you will be better off calling getLocation(Vertex v);
     * @see edu.uci.ics.jung.visualization.Layout#getX(edu.uci.ics.jung.graph.Vertex)
     */
    @Override
    public double getX(Vertex v) {
        Coordinates coords = getCoordinates(v);
        return coords.getX();
    }

    /**
     * Returns the y coordinate of the vertex from the Coordinates object.
     * In most cases you will be better off calling getLocation(Vertex v)
     * @see edu.uci.ics.jung.visualization.Layout#getX(edu.uci.ics.jung.graph.Vertex)
     */
    @Override
    public double getY(Vertex v) {
        Coordinates coords = getCoordinates(v);
        return coords.getY();
    }


    @Override
    protected void initialize_local_vertex(Vertex arg0) {
        MDNode node = new MDNode(arg0);
        node.setVertex(true);
        arg0.setUserDatum(ModularDecomposition.MD_KEY, node, UserData.REMOVE);
        if (MDForceLayoutConfig.CONCERN_VERTEX_BOUNDS) {
            BiologicalNodeAbstract ba =
                    (BiologicalNodeAbstract) pathway.getElement(arg0);
            String compartment = ba.getCompartment();
            node.setCompartment(compartment);
//        if (MDLayoutConfig.concernVertexBound) {
            String label = ba.getLabel();
            Rectangle bound = ba.getShape().getBounds();
            FontMetrics metrics = this.g.getFontMetrics(this.vf.getFont(arg0));
            Rectangle2D labelBound = metrics.getStringBounds(label, g);
            int w = (int) (bound.getWidth() + labelBound.getWidth()) + 5;
            int h = (int) Math.max(bound.getHeight(), labelBound.getHeight()) + 5;
            node.setBoundary(new Rectangle(w, h));
            node.setPaintOffset(new Point(bound.width / 2, bound.height / 2));
        }
//        Coordinates coords = (Coordinates) arg0.getUserDatum("coord");
//        node.setLocation(coords.x, coords.y);
//        }
    }

    @Override
    public void advancePositions() {
        for (ForceDirectedLayout layout : this.subLayouts) {
            layout.advancePositions();
        }
        if (root.getNodeType() == MDNode.PARALLEL) {
            this.layoutParalleNodes();
        } else {
            root.setLocation(center);
            root.calcCoordinates();
        }
    }

    public boolean isIncremental() {
        return !MDForceLayoutConfig.RUNONCE;
    }

    public boolean incrementsAreDone() {
        if (!MDForceLayoutConfig.RUNONCE) {
            boolean done = true;
            for (ForceDirectedLayout l : this.subLayouts) {
                done &= l.incrementsAreDone();
            }
            return done;
        } else {
            return true;
        }
    }
//
//    @Override
//    public void lockVertex(Vertex v) {
//        super.lockVertex(v);
//        MDNode node = (MDNode) v.getUserDatum(ModularDecomposition.MD_KEY);
//        ForceItem item = node.getForceItem();
//        item.locked = true;
//    }
//
//    @Override
//    public void unlockVertex(Vertex v) {
//        super.unlockVertex(v);
//        MDNode node = (MDNode) v.getUserDatum(ModularDecomposition.MD_KEY);
//        ForceItem item = node.getForceItem();
//        item.locked = false;
//    }

    @Override
	protected void offsetVertex(Vertex v, double xOffset, double yOffset) {
    }

    /**
     * Forcibly moves a vertex to the (x,y) location by setting its x and y
     * locations to the inputted location. Does not add the vertex to the
     * "dontmove" list, and (in the default implementation) does not make any
     * adjustments to the rest of the graph.
     */
    @Override
	public void forceMove(Vertex picked, double x, double y) {
        MDNode node = (MDNode) picked.getUserDatum(ModularDecomposition.MD_KEY);
        ForceItem item = node.getForceItem();
        Coordinates coord = getCoordinates(picked);
        double dx = x - coord.x, dy = y - coord.y;
        item.location[0] += dx;
        item.location[1] += dy;
        node.translate((float) dx, (float) dy);
        node.calcCoordinates();
        fireStateChanged();
    }

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
}
