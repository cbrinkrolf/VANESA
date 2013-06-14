package graph.layouts.modularLayout;

/**
 * Force function that computes the force acting on ForceItems due to a
 * given Spring.
 *
 * <p>This implementation was ported from the implementation in the
 * <a href="http://jheer.org">prefuse </a> framework.</p>
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author dao@techfak.uni-bielefeld.de
 */
public class SpringForceRegardsBounds extends AbstractForce {

    private boolean regardBounds;

//    /**
//     * Create a new SpringForce.
//     * @param springCoeff the default spring co-efficient to use. This will
//     * be used if the spring's own co-efficient is less than zero.
//     * @param defaultLength the default spring length to use. This will
//     * be used if the spring's own length is less than zero.
//     */
//    public SpringForceRegardsBounds(float springCoeff, float defaultLength) {
//        params = new float[] { springCoeff, defaultLength };
//        minValues = new float[]
//            { DEFAULT_MIN_SPRING_COEFF, DEFAULT_MIN_SPRING_LENGTH };
//        maxValues = new float[]
//            { DEFAULT_MAX_SPRING_COEFF, DEFAULT_MAX_SPRING_LENGTH };
//    }
    /**
     * Constructs a new SpringForce instance with default parameters.
     */
    public SpringForceRegardsBounds() {
//        this(DEFAULT_SPRING_COEFF, DEFAULT_SPRING_LENGTH);
    }

    /**
     * Initialize this force function. This default implementation does nothing.
     * Subclasses should override this method with any needed initialization.
     * @param fsim the encompassing ForceSimulator
     */
    @Override
    public void init(ForceSimulator fsim) {
        this.regardBounds = MDForceLayoutConfig.CONCERN_VERTEX_BOUNDS && fsim.getCurrentIteration() >
                MDForceLayoutConfig.params[MDForceLayoutConfig.BEGIN_ENLARGE].intValue();
    }

    /**
     * Returns true.
     * @see prefuse.util.force.Force#isSpringForce()
     */
    @Override
    public boolean isSpringForce() {
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
     * Calculates the force vector acting on the items due to the given spring.
     * @param s the Spring for which to compute the force
     * @see prefuse.util.force.Force#getForce(prefuse.util.force.Spring)
     */
    @Override
    public void getForce(Spring s) {
        ForceItem item1 = s.item1;
        ForceItem item2 = s.item2;
        float length = s.length;
//        float noise=(float) ((Math.random() - 0.5f) * length * 0.1f);
//        length+=noise;       
        float x1 = item1.location[0],  y1 = item1.location[1];
        float x2 = item2.location[0],  y2 = item2.location[1];
        float dx = x2 - x1,  dy = y2 - y1;
        if (item1.isIntersect(item2)) {
            return;
        }
        float r = (float) item1.getShortestDistance(item2);
        if (this.regardBounds && r < length) {
            return;
        }
//        if ( r <= 0.0 ) {
//            dx = ((float)Math.random()-0.5f) / 50.0f;
//            dy = ((float)Math.random()-0.5f) / 50.0f;
//            r  = (float)Math.sqrt(dx*dx+dy*dy);
//        }
        float d = r - length;
        float coeff = s.coeff * d / length;
//        if(this.regardBounds){
//            coeff=0.000001f;
//        }
//        System.out.println("sn1:"+item1+" old forcex:"+item1.force[0]);
//        System.out.println("sn2:"+item2+" old forcex:"+item2.force[0]);     
        item1.force[0] += coeff * dx;
        item1.force[1] += coeff * dy;
        item2.force[0] += -coeff * dx;
        item2.force[1] += -coeff * dy;

//         System.out.println("sn1:"+item1+" add force x:"+(coeff*dx)+" forcex="+item1.force[0]);
//            System.out.println("sn2:"+item2+" add force x:"+(-coeff*dx)+" forcex="+item2.force[0]);
    }
} // end of class SpringForce
