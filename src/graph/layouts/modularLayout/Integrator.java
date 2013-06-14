package graph.layouts.modularLayout;

/**
 * Interface for numerical integration routines. These routines are used
 * to update the position and velocity of items in response to forces
 * over a given time step.
 *
 * <p>This implementation was ported from the implementation in the
 * <a href="http://jheer.org">prefuse </a> framework.</p>
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author dao@techfak.uni-bielefeld.de
 */
public interface Integrator {

    public void integrate(ForceSimulator sim, long timestep);
} // end of interface Integrator
