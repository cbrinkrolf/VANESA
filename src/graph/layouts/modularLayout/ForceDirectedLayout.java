package graph.layouts.modularLayout;

import edu.uci.ics.jung.visualization.Coordinates;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>Layout that positions graph elements based on a physics simulation of
 * interacting forces; by default, nodes repel each other, edges act as
 * springs, and drag forces (similar to air resistance) are applied. This
 * algorithm can be run for multiple iterations for a run-once layout
 * computation or repeatedly run in an animated fashion for a dynamic and
 * interactive layout.</p>
 *
 * <p>The running time of this layout algorithm is the greater of O(N log N)
 * and O(E), where N is the number of nodes and E the number of edges.
 * The addition of custom force calculation modules may, however, increase
 * this value.</p>
 *
 * <p>This implementation was ported from the implementation in the
 * <a href="http://jheer.org">prefuse </a> framework.</p>
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author dao@techfak.uni-bielefeld.de
 */
public class ForceDirectedLayout {

    private ForceSimulator fsim;
    private long lasttime = -1L;
    private long maxstep = 50L;
    private boolean runonce;
    private int maxiterations = 4000;
    private Point center = new Point();
    private static final String VERTEX_BOUNDS = "VERTEX_BOUNDS";
    public static final String FORCEITEM = "_forceItem";
    public static final String SPRING = "_spring";
    private Set<MDEdge> edges;
    private Set<MDNode> nodes = new HashSet();
    private MDNode parent;
    private Map<MDNode, Set<MDNode>> treeRootsInclusters = new HashMap();
    private Map<MDNode, Set<MDNode>> verticesInclusters = new HashMap();
    private List<MDNode> cuts = new ArrayList();
    private Map<MDNode, float[]> lastPositions = new HashMap();
    private double sumMove;

    private void findTreeRoots(MDNode parent) {
        if (parent.getChildren() != null) {
            for (MDNode c : parent.getChildren()) {
                if (c.getNodeType() == MDNode.CLUSTER) {
                    Set<MDNode> treeRoots = new HashSet();
                    Set<MDNode> vertices = new HashSet();
                    treeRootsInclusters.put(c, treeRoots);
                    verticesInclusters.put(c, vertices);
                    for (MDNode cc : c.getChildren()) {
                        if (cc.getNodeType() == MDNode.TREE) {
                            treeRoots.add(cc);
                            Set<MDNode> subNodes = new HashSet();
                            cc.getAllVertices(subNodes);
                            vertices.addAll(subNodes);
                        }
                        vertices.add(cc);
                    }
                }
            }
            if (treeRootsInclusters.size() == 0) {
                Set<MDNode> treeRoots = new HashSet();
                Set<MDNode> vertices = new HashSet();
                treeRootsInclusters.put(parent, treeRoots);
                verticesInclusters.put(parent, vertices);
                for (MDNode c : parent.getChildren()) {
                    if ((c.getNodeType() == MDNode.TREE)) {
                        treeRoots.add(c);
                        Set<MDNode> subNodes = new HashSet();
                        c.getAllVertices(subNodes);
                        vertices.addAll(subNodes);
                    }
                    vertices.add(c);
                }
            }

        }
    }

    /**
     * Create a new ForceDirectedLayout.
     */
    public ForceDirectedLayout(MDNode parent, boolean runOnce,int maxIterations) {
        this.runonce=runOnce;        
        this.parent = parent;
        this.maxiterations=maxIterations;
        parent.getAllVertices(nodes);
        findTreeRoots(parent);
        edges = new HashSet();
        Map<MDNode, Set<MDNode>> connections = parent.getEdges();
        for (MDNode n : connections.keySet()) {
            for (MDNode n2 : connections.get(n)) {
                if (n2.isVertex()) {
                    edges.add(new MDEdge(n, n2));
                }
            }
        }
        fsim = new ForceSimulator();
        fsim.addForce(new NBodyForceRegardsBounds());
        fsim.addForce(new SpringForceRegardsBounds());
        fsim.addForce(new DragForce());
        this.initializeLocations();
    }    

    /**
     * Get the force simulator driving this layout.
     * @return the force simulator
     */
    public ForceSimulator getForceSimulator() {
        return fsim;
    }

    /**
     * Set the force simulator driving this layout.
     * @param fsim the force simulator
     */
    public void setForceSimulator(ForceSimulator fsim) {
        this.fsim = fsim;
    }

    /**
     * Get the number of iterations to use when computing a layout in
     * run-once mode.
     * @return the number of layout iterations to run
     */
    public int getIterations() {
        return maxiterations;
    }

    /**
     * Set the number of iterations to use when computing a layout in
     * run-once mode.
     * @param iter the number of layout iterations to run
     */
    public void setIterations(int iter) {
        if (iter < 1) {
            throw new IllegalArgumentException(
                    "Iterations must be a positive number!");
        }
        maxiterations = iter;
    }

    protected ForceItem getForceItem(MDNode node, boolean forceCreate) {
        ForceItem fitem = node.getForceItem();
        if (fitem == null || forceCreate) {
            fitem = new ForceItem();
            Rectangle2D bounds = node.getBoundary();
            try {
                fitem.bounds[0] = (float) bounds.getWidth();
                fitem.bounds[1] = (float) bounds.getHeight();
            } catch (NullPointerException e) {
            }
            node.setForceItem(fitem);
        }
        return fitem;
    }

    protected Spring getSpring(MDEdge edge, boolean forceCreate) {
        Spring item = edge.getSpringItem();
        if (item == null || forceCreate) {
            ForceItem f1 = getForceItem(edge.getFirst(), false);
            ForceItem f2 = getForceItem(edge.getLast(), false);
            float coeff = getSpringCoefficient(edge);
            float slen = getSpringLength(edge);
            item = Spring.getFactory().getSpring(f1, f2, coeff, slen);
            edge.setSpringItem(item);
        }
        return item;
    }
    /**
     * This method calls <tt>initialize_local_vertex</tt> for each vertex,
     * and also adds initial coordinate information for each vertex. (The
     * vertex's initial location is set by calling <tt>initializeLocation</tt>.
     */
    protected void initializeLocations() {
        // perform different actions if this is a run-once or
        // run-continuously layout
        if (runonce) {
            for (MDNode v : this.nodes) {
                Coordinates coord = v.getCenter();
                coord.setLocation(center);
            }
            fsim.clear();
            long timestep = 1000L;
            initSimulator(fsim);
            for (int i = 0; i < maxiterations; i++) {
                // use an annealing schedule to set time step
                timestep *= (1.0 - i / (double) maxiterations);
                long step = timestep + 50;
                // run simulator
                fsim.runSimulator(step);
            }
            updateNodePositions();
        } else {
            this.advancePositions();
        }
    }
    
    private void updateNodeBounds(MDNode node) {
        if (node.getChildren() != null) {
            for (MDNode n : node.getChildren()) {
                if (n.getNodeType() == MDNode.CLUSTER) {
                    n.reCalcBounds();
                }
            }
            node.reCalcBounds();
        }
    }
    private int countChildrenInFrame(MDNode node, double x1, double y1, double w, double h) {
        int count = 0;
        if (node.getChildren() != null) {
            for (MDNode c : node.getChildren()) {
                if ((c.getNodeType() & MDNode.CUT) == 0){
                    if(c.getBoundary()==null){
                        float x=c.getCenter().x,
                                y=c.getCenter().y;
                        if(x> x1 &&x<x1+w &&y>y1 &&y<y1+h){
                            ++count;
                        }
                    }else if(c.getBoundary().intersects(x1, y1, w, h)) {
                        ++count;
                    }
                }
            }
        }
        return count;
    }

    private MDNode checkIntersections(MDNode n1,
            MDNode n2) {
        Rectangle2D r1 = n1.getBoundary(),
                r2 = n2.getBoundary();
        int a1 = n1.getChildren().size(),
                a2 = n2.getChildren().size();
        int minIntersect = Math.min(a1, a2) / 4;
        double x1 = r1.getX(), y1 = r1.getY();
        double x2 = r2.getX(), y2 = r2.getY();
        double w1 = r1.getWidth(), h1 = r1.getHeight();
        double w2 = r2.getWidth(), h2 = r2.getHeight();
        double area1 = w1 * h1, area2 = w2 * h2;
        int c1 = this.countChildrenInFrame(n1, x2, y2, w2, h2);
        int c2 = this.countChildrenInFrame(n2, x1, y1, w1, h1);
        if (c1 > minIntersect && c2 > minIntersect) {
            return a1 > a2 ? n2 : n1;
        }
        return null;
    }

    private void fixNodePositions(MDNode toFix) {   
        float zoomRate = 0.2f;
        toFix.move(zoomRate * (toFix.getCenter().x - parent.getCenter().x),
                zoomRate * (toFix.getCenter().y - parent.getCenter().y));
        this.updateNodeBounds(parent);
    }

    private void updateNodePositions() {
        // update positions
//        parent.setBoundary(null);
        this.sumMove = 0;
        for (MDNode node : nodes) {
            ForceItem fitem = this.getForceItem(node, false);
            if (node.isLocked()) {
                // clear any force computations
                fitem.force[0] = 0.0f;
                fitem.force[1] = 0.0f;
                fitem.velocity[0] = 0.0f;
                fitem.velocity[1] = 0.0f;
//                if (Double.isNaN(item.getX())) {
//                    setX(item, referrer, 0.0);
//                    setY(item, referrer, 0.0);
//                }
                continue;
            }
            double x = fitem.location[0];
            double y = fitem.location[1];
//            double dx = Math.abs(fitem.velocity[0]);
//            double dy = Math.abs(fitem.velocity[1]);
//            sumMove += (dx + dy);
            // set the actual position
            node.setLocation(x, y);
//            parent.includeNode(node);
//            node.updateCoordinatesAndBounds();            
        }
        this.updateNodeBounds(parent);
        int iter = this.fsim.getCurrentIteration();
        float cx = parent.getCenter().x,
                cy = parent.getCenter().y;
        if (iter % 5 == 0) {
            for (MDNode node : nodes) {
                float[] last = this.lastPositions.get(node);
                float nx = node.getCenter().x - cx,
                        ny = node.getCenter().y - cy;
                if (last == null) {
                    last = new float[]{nx, ny};
                    this.lastPositions.put(node, last);
                } else {
                    float dx = Math.abs(nx - last[0]);
                    float dy = Math.abs(ny - last[1]);
                    last[0] = nx;
                    last[1] = ny;
                    this.sumMove += (dx + dy);
                }
            }
//            System.out.println("iter:" + this.m_fsim.getCurrentIteration() + " avg of move:" + sumMove / nodes.size());
        }
    }

    /**
     * Loads the simulator with all relevant force items and springs.
     * @param fsim the force simulator driving this layout
     */
    protected void initSimulator(ForceSimulator fsim) {
        // make sure we have force items to work with

        float startX = this.center.x;
        float startY = this.center.y;
        startX = Float.isNaN(startX) ? 0f : startX;
        startY = Float.isNaN(startY) ? 0f : startY;
        for (MDNode node : nodes) {
            ForceItem fitem = this.getForceItem(node, false);
            fitem.mass = getMassValue(node);
            float x = node.getCenter().x;
            float y = node.getCenter().y;
            fitem.location[0] = (Float.isNaN(x) ? startX : (float) x);
            fitem.location[1] = (Float.isNaN(y) ? startY : (float) y);
            fsim.addItem(fitem);
        }
        for (MDEdge e : edges) {
            Spring s = getSpring(e, false);
            fsim.addSpring(s);
        }
    }

    /**
     * Get the mass value associated with the given node. Subclasses should
     * override this method to perform custom mass assignment.
     * @param n the node for which to compute the mass value
     * @return the mass value for the node. By default, all items are given
     * a mass value of 1.0.
     */
    protected float getMassValue(MDNode n) {
        return 1.0f;
    }

    /**
     * Get the spring length for the given edge. Subclasses should
     * override this method to perform custom spring length assignment.
     * @param e the edge for which to compute the spring length
     * @return the spring length for the edge. A return value of
     * -1 means to ignore this method and use the global default.
     */
    protected float getSpringLength(MDEdge e) {
        return MDForceLayoutConfig.params[MDForceLayoutConfig.SPRING_LENGTH].floatValue();
    }

    /**
     * Get the spring coefficient for the given edge, which controls the
     * tension or strength of the spring. Subclasses should
     * override this method to perform custom spring tension assignment.
     * @param e the edge for which to compute the spring coefficient.
     * @return the spring coefficient for the edge. A return value of
     * -1 means to ignore this method and use the global default.
     */
    protected float getSpringCoefficient(MDEdge e) {
        return MDForceLayoutConfig.params[MDForceLayoutConfig.SPRING_COEFF].floatValue();
    }

    public void advancePositions() {
        // get timestep
        if (lasttime == -1) {
            lasttime = System.currentTimeMillis() - 20;
        }
        long time = System.currentTimeMillis();
        long timestep = Math.min(maxstep, time - lasttime);
        lasttime = time;
        // run force simulator
        fsim.clear();
        initSimulator(fsim);
        fsim.runSimulator(timestep);
        updateNodePositions();
        MDNode toFix = null;
        if (fsim.getCurrentIteration() % 217 == 0 && this.verticesInclusters.keySet().size() > 1) {
            List<MDNode> clusters = new ArrayList(this.verticesInclusters.keySet());
            for (int i = 0; i < clusters.size(); i++) {
                MDNode n1 = clusters.get(i);
                for (int j = i + 1; j < clusters.size(); j++) {
                    MDNode n2 = clusters.get(j);
                    toFix = checkIntersections(
                            n1, n2);
                    if (toFix != null) {
                        break;
                    }
                }
            }
        }
        if (toFix != null) {
            this.fixNodePositions(toFix);
        }        
    }
    public boolean incrementsAreDone() {
        return this.fsim.getCurrentIteration()>this.maxiterations;
    }
} // end of class ForceDirectedLayout
