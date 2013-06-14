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
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import configurations.gui.MDLayoutConfig;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.VertexFontFunction;
import edu.uci.ics.jung.utils.UserData;
import edu.uci.ics.jung.visualization.AbstractLayout;
import edu.uci.ics.jung.visualization.Coordinates;
import edu.uci.ics.jung.visualization.subLayout.SubLayout;
import edu.uci.ics.jung.visualization.subLayout.SubLayoutDecorator;

/**
 *
 * @author test
 */
public class MDLayout extends AbstractLayout implements SubLayout {

    private MDNode root;
    private int area;
    private boolean initialized;
    private double preferedEdgeLength;
    private int nodeCount;
    private Point center = new Point();
    private int side_width = 50;
    Map<Integer, Set<MDNode>> levels = new HashMap();
    private Set<Vertex> checkList;
    private Pathway pathway;
    private VertexFontFunction vf;
    private Graphics g;

    public MDLayout(Graph graph, Pathway pw, VertexFontFunction _vf, Graphics _g) {
        super(graph);
        this.pathway = pw;
        this.vf = _vf;
        this.g = _g;
    }

    public MDLayout(Graph graph, Set<Vertex> vertices, SubLayoutDecorator layout, Pathway pw, VertexFontFunction _vf, Graphics _g) {
        this(graph, pw, _vf, _g);
        this.checkList = new HashSet(vertices);
        this.initialize_local(vertices, layout);
        this.initializeLocations();
    }

    @Override
    public void initializeLocations() {
//        logger.info("k:" + this.preferedEdgeLength);
        this.initialized = false;
        for (Set<MDNode> v : levels.values()) {
            for (MDNode m : v) {
                m.getCenter().setLocation(0, 0);
                if (m.getChildren() != null) {
                    m.setBoundary(null);
                }
            }
        }
        Dimension size = this.getCurrentSize();
        if (size != null) {
            this.center.setLocation(size.width / 2, size.height / 2);
            this.area = (size.height - this.side_width) * (size.width - this.side_width);
        }
        area = Math.max(0, area);
//        preferedEdgeLength=10;
//        preferedEdgeLength = Math.sqrt(area / this.nodeCount);


        int lsize = levels.size();
        if (lsize < 2) {
            return;
        }
        double k = MDLayoutConfig.minEdgeLength;
        for (int i = lsize - 2; i > -1; i--) {
//            double k = this.preferedEdgeLength / (lsize - 1);
//            int l = (levels.size() - 1 - i);
//            k = l * k;
//            k=Math.pow(Math.sqrt(4.0/7.0), i)*preferedEdgeLength;
            k *= 1.7;
            Set<MDNode> nodes = levels.get(i);
            int childrenCount = levels.get(i + 1).size();
            for (MDNode node : nodes) {
                ModularLayout layout = null;
                if (node.getChildren() != null) {
                    switch (node.getNodeType()) {
                        case MDNode.PARALLEL:
                            layout = new ModularGridLayout(node, k);
//                            layout = new ModularFRLayout2(node, k );
//                            layout = new ModularFRLayout2(node, k );
                            break;
                        case MDNode.SERIES:
                            layout = new ModularCircleLayout(node, k);
//                            layout = new ModularFRLayout2(node, k );
                            break;
                        case MDNode.PRIME:
//                            layout = new ModularGridLayout(node, k);
                            layout = new ModularFRLayout(node, k);
                            break;
                        default:
                            throw new IllegalArgumentException("no such node type!" + node);
                    }
                    layout.doLayout();
                    for (MDNode vertex : node.getChildren()) {
                        node.includeNode(vertex);
                    }
                    node.reCalcBounds();
                }
            }
        }
//        root.reCalcBounds();
        root.setLocation(center);
        root.calcCoordinates();
        this.setInitialized(true);
        this.resize(new Dimension(
                root.getWidth() + this.side_width,
                root.getHeight() + this.side_width));
//        printNode(root,true);
    }

    @Override
	protected void offsetVertex(Vertex v, double xOffset, double yOffset) {
    }

    private void findNodeLevels(Map<Integer, Set<MDNode>> levels,
            MDNode node, int level) {
        Set<MDNode> siblings = levels.get(level);
        if (siblings == null) {
            siblings = new LinkedHashSet();
            levels.put(level, siblings);
        }
        siblings.add(node);
        if (node.getChildren() != null) {
            for (MDNode c : node.getChildren()) {
                findNodeLevels(levels, c, level + 1);
            }
        }
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
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
        double minX = 1000000, minY = 1000000, maxX = -1000000, maxY = -1000000;
        for (Object o : selected) {
            Vertex v = (Vertex) o;
//            initialize_local_vertex(v);
            if (v.getUserDatum(ModularDecomposition.MD_KEY) == null) {
                this.initialize_local_vertex(v);
            }
            double x = decorator.getX(v);
            double y = decorator.getY(v);
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(x, maxX);
            maxY = Math.max(y, maxY);
        }
        decomposition(selected);
        this.center.setLocation(minX + (maxX - minX) / 2.0, minY + (maxY - minY) / 2.0);
        this.area = (int) ((maxX - minX) * (maxY - minY));
    }

    private void decomposition(Set<Vertex> vertices) {
//        if (true) {
//            decomposition2(vertices);
//            return;
//        }
        try {
            ModularDecomposition md = new ModularDecomposition();
            long start = System.currentTimeMillis();
            root = md.decomposition(vertices, this.getVisibleEdges());
//        System.out.println("time:"+(System.currentTimeMillis()-start));
//        System.out.println("root:"+root);
            levels.clear();
            findNodeLevels(levels, root, 0);
//            locations.clear();
            Collection<MDNode> allLeaves = root.getAllLeaves();
//            for (MDNode node : allLeaves) {
//                locations.put(node.getId(), node);
//            }
            nodeCount = allLeaves.size();


        } catch (ConcurrentModificationException cme) {
            decomposition(vertices);
        }

    }

    private void decomposition2(Set<Vertex> vertices) {
        try {
            CompartmentDecomposition md = new CompartmentDecomposition();
            long start = System.currentTimeMillis();
            root = md.decomposition(vertices, this.getVisibleEdges());
            levels.clear();
            findNodeLevels(levels, root, 0);
            Collection<MDNode> allLeaves = root.getAllLeaves();
            nodeCount = allLeaves.size();
        } catch (ConcurrentModificationException cme) {
            decomposition2(vertices);
        }

    }
//    @Override
//    public void restart() {
//        initialize_local();
//        initializeLocations();
//    }
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
                if (MDLayoutConfig.concernVertexBound) {
                    Coordinates cord = new Coordinates(node.getCoordinates());
                    cord.add(-node.getWidth() / 2.0, -node.getHeight() / 2.0);
                    cord.add(node.getPaintOffset().getX(), node.getPaintOffset().getY());
                    return cord;
                } else {
                    return node.getCoordinates();
                }
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
        arg0.setUserDatum(ModularDecomposition.MD_KEY, node, UserData.REMOVE);
        BiologicalNodeAbstract ba =
                (BiologicalNodeAbstract) pathway.getElement(arg0);
        String compartment = ba.getCompartment();
        node.setCompartment(compartment);
        if (MDLayoutConfig.concernVertexBound) {
            String label = ba.getLabel();
            Rectangle bound = ba.getShape().getBounds();
            FontMetrics metrics = this.g.getFontMetrics(this.vf.getFont(arg0));
            Rectangle2D labelBound = metrics.getStringBounds(label, g);
            int w = (int) (bound.getWidth() + labelBound.getWidth()) + 5;
            int h = (int) Math.max(bound.getHeight(), labelBound.getHeight()) + 5;
            node.setBoundary(new Rectangle(w, h));
            node.setPaintOffset(new Point(bound.width / 2, bound.height / 2));
        }
    }

    @Override
    public void advancePositions() {
    }

    public boolean isIncremental() {
        return false;
    }

    public boolean incrementsAreDone() {
        return true;
    }
}
