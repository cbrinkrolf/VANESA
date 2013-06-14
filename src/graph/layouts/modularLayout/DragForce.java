package graph.layouts.modularLayout;

/**
 * Implements a viscosity/drag force to help stabilize items.
 *
 * <p>This implementation was ported from the implementation in the
 * <a href="http://jheer.org">prefuse </a> framework.</p>
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author dao@techfak.uni-bielefeld.de
 */
public class DragForce extends AbstractForce {
//    /**
//     * Create a new DragForce.
//     * @param dragCoeff the drag co-efficient
//     */
//    public DragForce(float dragCoeff) {
////        params = new float[] { dragCoeff };
////        minValues = new float[] { DEFAULT_MIN_DRAG_COEFF };
////        maxValues = new float[] { DEFAULT_MAX_DRAG_COEFF };
//    }
    /**
     * Create a new DragForce with default drag co-efficient.
     */
    public DragForce() {
//        this(DEFAULT_DRAG_COEFF);
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
     * @see prefuse.util.force.Force#getForce(prefuse.util.force.ForceItem)
     */
    @Override
    public void getForce(ForceItem item) {
        item.force[0] -= MDForceLayoutConfig.params[MDForceLayoutConfig.DRAG_COEFF].floatValue() * item.velocity[0];
        item.force[1] -= MDForceLayoutConfig.params[MDForceLayoutConfig.DRAG_COEFF].floatValue() * item.velocity[1];
    }
} // end of class DragForce
