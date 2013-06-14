package graph.layouts.modularLayout;

/**
 * Represents a point particle in a force simulation, maintaining values for
 * mass, forces, velocity, and position.
 * <p>This implementation was ported from the implementation in the
 * <a href="http://jheer.org">prefuse </a> framework.</p>
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author dao@techfak.uni-bielefeld.de
 */
public class ForceItem implements Cloneable {

    /**
     * Create a new ForceItem.
     */
    public ForceItem() {
        mass = 1.0f;
        force = new float[]{0.f, 0.f};
        velocity = new float[]{0.f, 0.f};
        location = new float[]{0.f, 0.f};
        plocation = new float[]{0.f, 0.f};
        currentBounds = new float[]{0.f, 0.f};
        bounds = new float[]{0.f, 0.f};
        k = new float[4][2];
        l = new float[4][2];
        m_force = new float[]{0.f, 0.f};
        m_velocity = new float[]{0.f, 0.f};
    }

    /**
     * Clone a ForceItem.
     * @see java.lang.Object#clone()
     */
    @Override
	public Object clone() {
        ForceItem item = new ForceItem();
        item.mass = this.mass;
        System.arraycopy(force, 0, item.force, 0, 2);
        System.arraycopy(velocity, 0, item.velocity, 0, 2);
        System.arraycopy(location, 0, item.location, 0, 2);
        System.arraycopy(plocation, 0, item.plocation, 0, 2);
        System.arraycopy(currentBounds, 0, item.currentBounds, 0, 2);
        for (int i = 0; i < k.length; ++i) {
            System.arraycopy(k[i], 0, item.k[i], 0, 2);
            System.arraycopy(l[i], 0, item.l[i], 0, 2);
        }
        return item;
    }
    public int currentIteration;
    /** The mass value of this ForceItem. */
    public float mass;
    /** The values of the forces acting on this ForceItem. */
    public float[] force;
    /** The velocity values of this ForceItem. */
    public float[] velocity;
    /** The mean values of force of this ForceItem. */
    public float[] m_force;
    public float[] m_velocity;
    /** The location values of this ForceItem. */
    public float[] location;
    /** The previous location values of this ForceItem. */
    public float[] plocation;
    /** Temporary variables for Runge-Kutta integration */
    public float[][] k;
    /** Temporary variables for Runge-Kutta integration */
    public float[][] l;
    /** The bounds values of this ForceItem. */
    public float[] currentBounds;
    public float[] bounds;
    public boolean locked;

    public void updateBounds() {
        if (MDForceLayoutConfig.CONCERN_VERTEX_BOUNDS) {
            int begin = MDForceLayoutConfig.params[MDForceLayoutConfig.BEGIN_ENLARGE].intValue(),
                    end = MDForceLayoutConfig.params[MDForceLayoutConfig.END_ENLARGE].intValue();
            if (this.currentIteration > begin && this.currentIteration <= end) {
                int t = this.currentIteration - begin;
                int s = end - begin;
                float rate = (float) t / s;
                currentBounds[0] = bounds[0] * rate;
                currentBounds[1] = bounds[1] * rate;
            }
        }
    }

    /**
     * Checks a ForceItem to make sure its values are all valid numbers
     * (i.e., not NaNs).
     * @param item the item to check
     * @return true if all the values are valid, false otherwise
     */
    public static final boolean isValid(ForceItem item) {
        return !(Float.isNaN(item.location[0]) || Float.isNaN(item.location[1]) ||
                Float.isNaN(item.plocation[0]) || Float.isNaN(item.plocation[1]) ||
                Float.isNaN(item.velocity[0]) || Float.isNaN(item.velocity[1]) ||
                Float.isNaN(item.force[0]) || Float.isNaN(item.force[1]));
    }

    public double getDistance(ForceItem item) {
        float x1 = location[0], y1 = location[1];
        float x2 = item.location[0], y2 = item.location[1];
        float w1 = currentBounds[0], h1 = currentBounds[1];
        float dx = x2 - x1, dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public boolean isIntersect(ForceItem item) {
        float x1 = location[0], y1 = location[1];
        float x2 = item.location[0], y2 = item.location[1];
        float w1 = currentBounds[0], h1 = currentBounds[1];
        float w2 = item.currentBounds[0], h2 = item.currentBounds[1];
        float dx = x2 - x1, dy = y2 - y1;
        if (w1 == 0 && w2 == 0 && h1 == 0 && h2 == 0) {
            return dx == 0 && dy == 0;
        }
        float xx1 = x1 + w1, xx2 = x2 + w2;
        float yy1 = y1 + h1, yy2 = y2 + h2;
        boolean intersect = !(xx1 < x2 || x1 > xx2 || yy1 < y2 || y1 > yy2);
        return intersect;
    }

    public double getShortestDistance(ForceItem item) {
        float tx1 = location[0], ty1 = location[1];
        float rx1 = item.location[0], ry1 = item.location[1];
        float tw = currentBounds[0], th = currentBounds[1];
        float rw = item.currentBounds[0], rh = item.currentBounds[1];
        float dx = rx1 - tx1, dy = ry1 - ty1;
        if (tw == 0 && rw == 0 && th == 0 && rh == 0) {
            return Math.sqrt(dx * dx + dy * dy);
        }
        float tx2 = tx1 + tw, rx2 = rx1 + rw;
        float ty2 = ty1 + th, ry2 = ry1 + rh;
        boolean intersect = !(tx2 < rx1 || tx1 > rx2 || ty2 < ry1 || ty1 > ry2);
        if (intersect) {
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
        }
        double r = 0;
        float hwt = tw / 2f, hwr = rw / 2f,
                hht = th / 2f, hhr = rh / 2f;
        dx = Math.abs(dx + hwr - hwt) - Math.abs(hwr + hwt);
        dy = Math.abs(dy + hhr - hht) - Math.abs(hhr + hht);
        if (ty2 >= ry1 && ty1 <= ry2) {
            r = dx;
        } else if (tx2 >= rx1 && tx1 <= rx2) {
            r = dy;
        } else {
            r = Math.sqrt(dx * dx + dy * dy);
        }
        return r;
    }
} // end of class ForceItem
