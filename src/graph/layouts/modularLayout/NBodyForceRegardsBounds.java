package graph.layouts.modularLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

/**
 * <p>Force function which computes an n-body force such as gravity,
 * anti-gravity, or the results of electric charges. This function implements
 * the the Barnes-Hut algorithm for efficient n-body force simulations,
 * using a quad-tree with aggregated mass values to compute the n-body
 * force in O(N log N) time, where N is the number of ForceItems.</p>
 * 
 * <p>The algorithm used is that of J. Barnes and P. Hut, in their research
 * paper <i>A Hierarchical  O(n log n) force calculation algorithm</i>, Nature, 
 *  v.324, December 1986. For more details on the algorithm, see one of
 *  the following links --
 * <ul>
 *   <li><a href="http://www.cs.berkeley.edu/~demmel/cs267/lecture26/lecture26.html">James Demmel's UC Berkeley lecture notes</a>
 *   <li><a href="http://www.physics.gmu.edu/~large/lr_forces/desc/bh/bhdesc.html">Description of the Barnes-Hut algorithm</a>
 *   <li><a href="http://www.ifa.hawaii.edu/~barnes/treecode/treeguide.html">Joshua Barnes' recent implementation</a>
 * </ul></p>
 * 
 * <p>This implementation was ported from the implementation in the
 * <a href="http://jheer.org">prefuse </a> framework.</p>
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author dao@techfak.uni-bielefeld.de
 */
public class NBodyForceRegardsBounds extends AbstractForce {

    /* 
     * The indexing scheme for quadtree child nodes goes row by row.
     *   0 | 1    0 -> top left,    1 -> top right
     *  -------
     *   2 | 3    2 -> bottom left, 3 -> bottom right
     */
    private float xMin,  xMax,  yMin,  yMax;
    private QuadTreeNodeFactory factory = new QuadTreeNodeFactory();
    private QuadTreeNode root;
    private Random rand = new Random(12345678L); // deterministic randomness
    private boolean regardBounds;

    /**
     * Create a new NBodyForce with default parameters.
     */
    public NBodyForceRegardsBounds() {
//        this(DEFAULT_GRAV_CONSTANT, DEFAULT_DISTANCE, DEFAULT_THETA);
        root = factory.getQuadTreeNode();
    }

    /**
     * Returns true.
     * @see prefuse.util.force.Force#isItemForce()
     */
    @Override
	public boolean isItemForce() {
        return true;
    }

    /**
     * @see prefuse.util.force.AbstractForce#getParameterNames()
     */
    @Override
	protected String[] getParameterNames() {
        return null;
    }

    /**
     * Set the bounds of the region for which to compute the n-body simulation
     * @param xMin the minimum x-coordinate
     * @param yMin the minimum y-coordinate
     * @param xMax the maximum x-coordinate
     * @param yMax the maximum y-coordinate
     */
    private void setBounds(float xMin, float yMin, float xMax, float yMax) {
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
    }

    /**
     * Clears the quadtree of all entries.
     */
    public void clear() {
        clearHelper(root);
        root = factory.getQuadTreeNode();
    }

    private void clearHelper(QuadTreeNode n) {
        for (int i = 0; i < n.children.length; i++) {
            if (n.children[i] != null) {
                clearHelper(n.children[i]);
            }
        }
        factory.reclaim(n);
    }

    /**
     * Initialize the simulation with the provided enclosing simulation. After
     * this call has been made, the simulation can be queried for the 
     * n-body force acting on a given item.
     * @param fsim the enclosing ForceSimulator
     */
    @Override
	public void init(ForceSimulator fsim) {
        clear(); // clear internal state
        this.regardBounds = MDForceLayoutConfig.CONCERN_VERTEX_BOUNDS && fsim.getCurrentIteration() > MDForceLayoutConfig.params[MDForceLayoutConfig.BEGIN_ENLARGE].intValue() &&
                fsim.getCurrentIteration() % 3 != 0;
        // compute and squarify bounds of quadtree
        float x1 = Float.MAX_VALUE, y1 = Float.MAX_VALUE;
        float x2 = Float.MIN_VALUE, y2 = Float.MIN_VALUE;
        Iterator itemIter = fsim.getItems();
        while (itemIter.hasNext()) {
            ForceItem item = (ForceItem) itemIter.next();
            float x = item.location[0];
            float y = item.location[1];
            float rw = 0, rh = 0;
            if (this.regardBounds) {
                rw = item.currentBounds[0] * 0.5f;
                rh = item.currentBounds[1] * 0.5f;
            }
            if (x - rw < x1) {
                x1 = x - rw;
            }
            if (y - rh < y1) {
                y1 = y - rh;
            }
            if (x + rw > x2) {
                x2 = x + rw;
            }
            if (y + rh > y2) {
                y2 = y + rh;
            }
        }
        float dx = x2 - x1, dy = y2 - y1;
        if (dx > dy) {
            y2 = y1 + dx;
        } else {
            x2 = x1 + dy;
        }
        setBounds(x1, y1, x2, y2);

        // insert items into quadtree
        itemIter = fsim.getItems();
        while (itemIter.hasNext()) {
            ForceItem item = (ForceItem) itemIter.next();
            insert(item);
        }

        // calculate magnitudes and centers of mass
        calcMass(root);
    }

    /**
     * Inserts an item into the quadtree.
     * @param item the ForceItem to add.
     * @throws IllegalStateException if the current location of the item is
     *  outside the bounds of the quadtree
     */
    public void insert(ForceItem item) {
        // insert item into the quadtrees
        try {
            insert(item, root, xMin, yMin, xMax, yMax);
        } catch (StackOverflowError e) {
            // TODO: safe to remove?
            e.printStackTrace();
        }
    }

    private void insert(ForceItem p, QuadTreeNode n,
            float x1, float y1, float x2, float y2) {
        // try to insert particle p at node n in the quadtree
        // by construction, each leaf will contain either 1 or 0 particles
        if (n.hasChildren) {
            // n contains more than 1 particle
            insertHelper(p, n, x1, y1, x2, y2);
        } else if (n.value != null) {
            // n contains 1 particle
            if (isSameLocation(n.value, p)) {
                insertHelper(p, n, x1, y1, x2, y2);
            } else {
                ForceItem v = n.value;
                n.value = null;
                insertHelper(v, n, x1, y1, x2, y2);
                insertHelper(p, n, x1, y1, x2, y2);
            }
        } else {
            // n is empty, so is a leaf
            n.value = p;
            n.x1 = x1;
            n.y1 = y1;
            n.x2 = x2;
            n.y2 = y2;
        }
        ++n.childrenCount;
    }

    private static boolean isSameLocation(ForceItem f1, ForceItem f2) {
        float dx = Math.abs(f1.location[0] - f2.location[0]);
        float dy = Math.abs(f1.location[1] - f2.location[1]);
        return (dx < 0.01 && dy < 0.01);
    }

    private void insertHelper(ForceItem p, QuadTreeNode n,
            float x1, float y1, float x2, float y2) {
        float x = p.location[0],  y = p.location[1];
        float splitx = (x1 + x2) / 2;
        float splity = (y1 + y2) / 2;
        int i = (x >= splitx ? 1 : 0) + (y >= splity ? 2 : 0);
        // create new child node, if necessary
        if (n.children[i] == null) {
            n.children[i] = factory.getQuadTreeNode();
            n.hasChildren = true;
        }
        // update bounds
        if (i == 1 || i == 3) {
            x1 = splitx;
        } else {
            x2 = splitx;
        }
        if (i > 1) {
            y1 = splity;
        } else {
            y2 = splity;
        }
        // recurse 
        insert(p, n.children[i], x1, y1, x2, y2);
    }

    private void calcMass(QuadTreeNode n) {
        float xcom = 0,  ycom = 0;
        n.mass = 0;
        if (n.hasChildren) {
            for (int i = 0; i < n.children.length; i++) {
                if (n.children[i] != null) {
                    calcMass(n.children[i]);
                    n.mass += n.children[i].mass;
                    xcom += n.children[i].mass * n.children[i].com[0];
                    ycom += n.children[i].mass * n.children[i].com[1];
                }
            }
        }
        if (n.value != null) {
            n.mass += n.value.mass;
            xcom += n.value.mass * n.value.location[0];
            ycom += n.value.mass * n.value.location[1];
        }
        n.com[0] = xcom / n.mass;
        n.com[1] = ycom / n.mass;
    }

    /**
     * Calculates the force vector acting on the given item.
     * @param item the ForceItem for which to compute the force
     */
    @Override
    public void getForce(ForceItem item) {
        try {
            forceHelper(item, root, xMin, yMin, xMax, yMax);
            if (this.regardBounds) {
                this.enhanceRepulsiveForce(item);
            }
//            System.out.println("\n\n\n\n");
        } catch (StackOverflowError e) {
            // TODO: safe to remove?
            e.printStackTrace();
        }
    }

    public void enhanceRepulsiveForce(ForceItem p) {
        Float length = (Float) MDForceLayoutConfig.params[MDForceLayoutConfig.REJECT_DISTANCE];
        float px = p.location[0],  py = p.location[1];
        float pw = p.currentBounds[0],  ph = p.currentBounds[1];
        float px2 = px + pw,  py2 = py + ph;
        float bx1 = px - length,
                 bx2 = px2 + length;
        float by1 = py - length,
                 by2 = py2 + length;
        repulsiveForceHelper(p, root, bx1, by1, bx2, by2);
    }

    private void repulsiveForceHelper(ForceItem item, QuadTreeNode n, float bx1, float by1, float bx2, float by2) {
        if (n.hasChildren) {
            float splitx = (n.x1 + n.x2) / 2;
            float splity = (n.y1 + n.y2) / 2;
            boolean touched[] = new boolean[4];
            touched[0] = bx1 <= splitx && by1 <= splity;
            touched[1] = bx2 >= splitx && by1 <= splity;
            touched[2] = bx1 <= splitx && by2 >= splity;
            touched[3] = bx2 >= splitx && by2 >= splity;
            for (int i = 0; i < touched.length; i++) {
                if (touched[i] && n.children[i] != null) {
                    repulsiveForceHelper(item, n.children[i], bx1, by1, bx2, by2);
                }
            }
        } else if (n.value != null) {
            calcRepulsiveForce(item, n.value);
        }
    }

    private void calcRepulsiveForce(ForceItem item, ForceItem n) {
        if (item == n) {
            return;
        }
        boolean intersect = item.isIntersect(n);
        if (intersect) {
            float tx1 = item.location[0],  ty1 = item.location[1];
            float rx1 = n.location[0],  ry1 = n.location[1];
            float tw = item.currentBounds[0],  th = item.currentBounds[1];
            float rw = n.currentBounds[0],  rh = n.currentBounds[1];
            float dx = rx1 - tx1,  dy = ry1 - ty1;
            float tx2 = tx1 + tw,  rx2 = rx1 + rw;
            float ty2 = ty1 + th,  ry2 = ry1 + rh;
            float[] len = new float[4];
            if (rx2 < tx2) {
                len[0] = rx2 - tx1;
                len[1] = rx1 - tx2;
            } else {
                len[0] = rx1 - tx2;
                len[1] = rx2 - tx1;
            }
            if (ry2 < ty2) {
                len[2] = ry2 - ty1;
                len[3] = ry1 - ty2;
            } else {
                len[2] = ry1 - ty2;
                len[3] = ry2 - ty1;
            }
            float sum = 0;
            float rate[] = new float[4];
            int maxIdx = 0;
            for (int i = 0; i < rate.length; i++) {
                rate[i] = Math.abs(1f / len[i]);
                if (rate[i] > rate[maxIdx]) {
                    maxIdx = i;
                }
                sum += rate[i];
            }
            Float force = (Float) MDForceLayoutConfig.params[MDForceLayoutConfig.REJECT_COEFF];
            force *= len[maxIdx];
            item.force[maxIdx / 2] += force;
            n.force[maxIdx / 2] -= force;
//            double random = Math.random();
//            for (int i = 0; i < rate.length; i++) {
//                random -= rate[i] / sum;
//                if (random <= 0) {
//                    float force = ForceDirectedConfig.params[ForceDirectedConfig.SPRING_COEFF] * len[i];
//                    int idx = i / 2;
//                    item.force[idx] += force;
//                    n.force[idx] -= force;
//                    return;
//                }
//            }

        } else {
            double r = item.getShortestDistance(n);
            Float length = (Float) MDForceLayoutConfig.params[MDForceLayoutConfig.REJECT_DISTANCE];
            if (r < length) {
                double dx = n.location[0] - item.location[0];
                double dy = n.location[1] - item.location[1];
                r = Math.max(r, 0.1);
                float d = (float) (length - r);
                Float coeff = (Float) MDForceLayoutConfig.params[MDForceLayoutConfig.REJECT_COEFF];
                coeff *= (d / length);
                double xf = coeff * dx,  yf = coeff * dy;
                if (Math.random() < (Float) MDForceLayoutConfig.params[MDForceLayoutConfig.PERTUBATE_RATE]) {
                    xf = -xf;
                    yf = -yf;
                }
//            System.out.println("n1:"+item+" old forcex:"+item.force[0]);
//            System.out.println("n2:"+n+" old forcex:"+n.force[0]);
                item.force[0] += -xf;
                item.force[1] += -yf;
                n.force[0] += xf;
                n.force[1] += yf;
            }
        }

//            }
//            System.out.println("n1:"+item+" add force x:"+(-xf)+" forcex="+item.force[0]);
//            System.out.println("n2:"+n+" add force x:"+(xf)+" forcex="+n.force[0]);
//            System.out.println("n1:"+item+" add force x:"+(-xf)+" y:"+(-yf)+" forcex="+item.force[0]+" forcey:"+item.force[1]);
//            System.out.println("n2:"+n+" add force x:"+(xf)+" y:"+(yf)+" forcex="+n.force[0]+" forcey:"+n.force[1]);
    }

    private void forceHelper(ForceItem item, QuadTreeNode n,
            float x1, float y1, float x2, float y2) {
        float dx = n.com[0] - item.location[0];
        float dy = n.com[1] - item.location[1];
        float r = (float) Math.sqrt(dx * dx + dy * dy);
        boolean same = false;
        if (r == 0.0f) {
            // if items are in the exact same place, add some noise
            dx = (rand.nextFloat() - 0.5f) / 50.0f;
            dy = (rand.nextFloat() - 0.5f) / 50.0f;
            r = (float) Math.sqrt(dx * dx + dy * dy);
            same = true;
        }
        Float mDist = (Float) MDForceLayoutConfig.params[MDForceLayoutConfig.MIN_DISTANCE];
        boolean minDist = mDist > 0f && r > mDist;

        // the Barnes-Hut approximation criteria is if the ratio of the
        // size of the quadtree box to the distance between the point and
        // the box's center of mass is beneath some threshold theta.
        if ((!n.hasChildren && n.value != item) ||
                (!same && (x2 - x1) / r < (Float) MDForceLayoutConfig.params[MDForceLayoutConfig.BARNES_HUT_THETA])) {
            double rate = Math.random();
            if (minDist) {
                return;
            }
            // either only 1 particle or we meet criteria
            // for Barnes-Hut approximation, so calc force
            float v = (Float) MDForceLayoutConfig.params[MDForceLayoutConfig.GRAVITATIONAL_CONST];
            v *= (item.mass * n.mass / (r * r * r));
//            if (Math.random() < ForceDirectedConfig.params[ForceDirectedConfig.PERTUBATE_RATE] && item.force[0] * dx > 0 && item.force[1] * dy > 0) {
//                v = -v;
//            }
//            System.out.println("vn1:"+item+" old forcex:"+item.force[0]);
            item.force[0] += v * dx;
            item.force[1] += v * dy;
//             System.out.println("vn1:"+item+" add force x:"+(v * dx)+" forcex="+item.force[0]);
        } else if (n.hasChildren) {
            // recurse for more accurate calculation
            float splitx = (x1 + x2) / 2;
            float splity = (y1 + y2) / 2;
            for (int i = 0; i < n.children.length; i++) {
                if (n.children[i] != null) {
                    forceHelper(item, n.children[i],
                            (i == 1 || i == 3 ? splitx : x1), (i > 1 ? splity : y1),
                            (i == 1 || i == 3 ? x2 : splitx), (i > 1 ? y2 : splity));
                }
            }
            if (minDist) {
                return;
            }
            if (n.value != null && n.value != item) {
                float v = (Float) MDForceLayoutConfig.params[MDForceLayoutConfig.GRAVITATIONAL_CONST];
                v *= (item.mass * n.value.mass / (r * r * r));
                if (item.force[0] * dx > 0 && item.force[1] * dy > 0 &&
                        Math.random() < (Float) MDForceLayoutConfig.params[MDForceLayoutConfig.PERTUBATE_RATE]) {
                    v = -3f * v;
                    n.value.force[0] -= v * dx;
                    n.value.force[1] -= v * dy;
                }
//                System.out.println("vn1:"+item+" old forcex:"+item.force[0]);
                item.force[0] += v * dx;
                item.force[1] += v * dy;
//                System.out.println("vn1:"+item+" add force x:"+(v * dx)+" forcex="+item.force[0]);
            }
        }
    }

    /**
     * Represents a node in the quadtree.
     */
    public static final class QuadTreeNode {

        public QuadTreeNode() {
            com = new float[]{0.0f, 0.0f};
            children = new QuadTreeNode[4];
        } //
        boolean hasChildren = false;
        float mass; // total mass held by this node
        float[] com; // center of mass of this node 
        ForceItem value; // ForceItem in this node, null if node has children
        QuadTreeNode[] children; // children nodes
        float x1,  y1,  x2,  y2;
        int childrenCount;
        int l,  r,  t,  b;
    } // end of inner class QuadTreeNode

    /**
     * Helper class to minimize number of object creations across multiple
     * uses of the quadtree.
     */
    public static final class QuadTreeNodeFactory {

        private int maxNodes = 50000;
        private ArrayList nodes = new ArrayList();

        public QuadTreeNode getQuadTreeNode() {
            if (nodes.size() > 0) {
                return (QuadTreeNode) nodes.remove(nodes.size() - 1);
            } else {
                return new QuadTreeNode();
            }
        }

        public void reclaim(QuadTreeNode n) {
            n.mass = 0;
            n.com[0] = 0.0f;
            n.com[1] = 0.0f;
            n.value = null;
            n.hasChildren = false;
            n.x1 = 0;
            n.y1 = 0;
            n.x2 = 0;
            n.y2 = 0;
            n.childrenCount = 0;
            n.l = 0;
            n.r = 0;
            n.t = 0;
            n.b = 0;
            Arrays.fill(n.children, null);
            if (nodes.size() < maxNodes) {
                nodes.add(n);
            }
        }
    } // end of inner class QuadTreeNodeFactory
} // end of class NBodyForce
