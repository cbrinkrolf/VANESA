package graph.layouts.modularLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages a simulation of physical forces acting on bodies. To create a
 * custom ForceSimulator, add the desired {@link Force} functions and choose an
 * appropriate {@link Integrator}.
 *
 * <p>This implementation was ported from the implementation in the
 * <a href="http://jheer.org">prefuse </a> framework.</p>
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author dao@techfak.uni-bielefeld.de
 */
public class ForceSimulator {

    private Collection<ForceItem> items;
    private Collection<Spring> springs;
    private Force[] iforces;
    private Force[] sforces;
    private int iflen,  sflen;
    private Integrator integrator;
    private float speedLimit = 1.0f;
    private int currentIteration;
    private Map<ForceItem, Float> meanForces = new ConcurrentHashMap();

    /**
     * Create a new, empty ForceSimulator. A RungeKuttaIntegrator is used
     * by default.
     */
    public ForceSimulator() {
        this(new RungeKuttaIntegrator());
    }

    /**
     * Create a new, empty ForceSimulator.
     * @param integr the Integrator to use
     */
    public ForceSimulator(Integrator integr) {
        integrator = integr;
        iforces = new Force[5];
        sforces = new Force[5];
        iflen = 0;
        sflen = 0;
        items = Collections.synchronizedCollection(new ArrayList());
        springs = Collections.synchronizedCollection(new ArrayList());
    }

    /**
     * Get the speed limit, or maximum velocity value allowed by this
     * simulator.
     * @return the "speed limit" maximum velocity value
     */
    public float getSpeedLimit() {
        return speedLimit;
    }

    /**
     * Set the speed limit, or maximum velocity value allowed by this
     * simulator.
     * @param limit the "speed limit" maximum velocity value to use
     */
    public void setSpeedLimit(float limit) {
        speedLimit = limit;
    }

    /**
     * Get the Integrator used by this simulator.
     * @return the Integrator
     */
    public Integrator getIntegrator() {
        return integrator;
    }

    /**
     * Set the Integrator used by this simulator.
     * @param intgr the Integrator to use
     */
    public void setIntegrator(Integrator intgr) {
        integrator = intgr;
    }

    /**
     * Clear this simulator, removing all ForceItem and Spring instances
     * for the simulator.
     */
    public void clear() {
        items.clear();
//        Iterator siter = springs.iterator();
//        Spring.SpringFactory f = Spring.getFactory();
//        while (siter.hasNext()) {
//            f.reclaim((Spring) siter.next());
//        }
        springs.clear();
//        this.currentIteration=0;
    }

    /**
     * Add a new Force function to the simulator.
     * @param f the Force function to add
     */
    public void addForce(Force f) {
        if (f.isItemForce()) {
            if (iforces.length == iflen) {
                // resize necessary
                Force[] newf = new Force[iflen + 10];
                System.arraycopy(iforces, 0, newf, 0, iforces.length);
                iforces = newf;
            }
            iforces[iflen++] = f;
        }
        if (f.isSpringForce()) {
            if (sforces.length == sflen) {
                // resize necessary
                Force[] newf = new Force[sflen + 10];
                System.arraycopy(sforces, 0, newf, 0, sforces.length);
                sforces = newf;
            }
            sforces[sflen++] = f;
        }
    }

    /**
     * Get an array of all the Force functions used in this simulator.
     * @return an array of Force functions
     */
    public Force[] getForces() {
        Force[] rv = new Force[iflen + sflen];
        System.arraycopy(iforces, 0, rv, 0, iflen);
        System.arraycopy(sforces, 0, rv, iflen, sflen);
        return rv;
    }

    /**
     * Add a ForceItem to the simulation.
     * @param item the ForceItem to add
     */
    public void addItem(ForceItem item) {
        items.add(item);
    }

    /**
     * Remove a ForceItem to the simulation.
     * @param item the ForceItem to remove
     */
    public boolean removeItem(ForceItem item) {
        return items.remove(item);
    }

    /**
     * Get an iterator over all registered ForceItems.
     * @return an iterator over the ForceItems.
     */
    public Iterator getItems() {
        return items.iterator();
    }

    public Spring addSpring(Spring s) {
        springs.add(s);
        return s;
    }

    /**
     * Add a Spring to the simulation.
     * @param item1 the first endpoint of the spring
     * @param item2 the second endpoint of the spring
     * @return the Spring added to the simulation
     */
    public Spring addSpring(ForceItem item1, ForceItem item2) {
        return addSpring(item1, item2, -1.f, -1.f);
    }

    /**
     * Add a Spring to the simulation.
     * @param item1 the first endpoint of the spring
     * @param item2 the second endpoint of the spring
     * @param length the spring length
     * @return the Spring added to the simulation
     */
    public Spring addSpring(ForceItem item1, ForceItem item2, float length) {
        return addSpring(item1, item2, -1.f, length);
    }

    /**
     * Add a Spring to the simulation.
     * @param item1 the first endpoint of the spring
     * @param item2 the second endpoint of the spring
     * @param coeff the spring coefficient
     * @param length the spring length
     * @return the Spring added to the simulation
     */
    public Spring addSpring(ForceItem item1, ForceItem item2, float coeff, float length) {
        if (item1 == null || item2 == null) {
            throw new IllegalArgumentException("ForceItems must be non-null");
        }
        Spring s = Spring.getFactory().getSpring(item1, item2, coeff, length);
        springs.add(s);
        return s;
    }

    /**
     * Get an iterator over all registered Springs.
     * @return an iterator over the Springs.
     */
    public Iterator getSprings() {
        return springs.iterator();
    }

    /**
     * Run the simulator for one timestep.
     * @param timestep the span of the timestep for which to run the simulator
     */
    public void runSimulator(long timestep) {
        ++this.currentIteration;
        for (ForceItem item : this.items) {
            item.currentIteration = this.getCurrentIteration();
            item.updateBounds();
        }
        accumulate();
        integrator.integrate(this, timestep);
        checkAllSprings();
        averageIterations = 5;
        int avg = Math.min(this.averageIterations, this.currentIteration);
        for (ForceItem item : this.items) {
            item.m_force[0] = (item.m_force[0] * (avg - 1) + item.force[0]) / avg;
            item.m_force[1] = (item.m_force[1] * (avg - 1) + item.force[1]) / avg;
            item.m_velocity[0] = (item.m_velocity[0] * (avg - 1) + item.velocity[0]) / avg;
            item.m_velocity[1] = (item.m_velocity[1] * (avg - 1) + item.velocity[1]) / avg;
        }
    }
    int averageIterations = 5;
    float relaxDifference = 0.2f, tightDifference = 0.05f;

    protected void checkAllSprings() {
        relaxDifference = 0.5f;
        tightDifference = 0.2f;
        for (Spring s : this.springs) {
            ForceItem n1 = s.item1;
            ForceItem n2 = s.item2;
            float dx1 = Math.abs((n1.m_velocity[0] - n1.velocity[0]) / n1.m_velocity[0]);
            float dy1 = Math.abs((n1.m_velocity[1] - n1.velocity[1]) / n1.m_velocity[1]);
            float dx2 = Math.abs((n2.m_velocity[0] - n2.velocity[0]) / n2.m_velocity[0]);
            float dy2 = Math.abs((n2.m_velocity[1] - n2.velocity[1]) / n2.m_velocity[1]);
            if (dx1 > relaxDifference || dy1 > relaxDifference ||
                    dx2 > relaxDifference || dy2 > relaxDifference) {
                s.relax();
            } else if (dx1 < tightDifference && dy1 < tightDifference &&
                    dx2 < tightDifference && dy2 < tightDifference) {
                s.tight();
            }
        }
    }

    /**
     * Accumulate all forces acting on the items in this simulation
     */
    public void accumulate() {
        for (int i = 0; i < iflen; i++) {
            iforces[i].init(this);
        }
        for (int i = 0; i < sflen; i++) {
            sforces[i].init(this);
        }
        for (ForceItem item : items) {
            item.force[0] = 0.0f;
            item.force[1] = 0.0f;
        }
        for (ForceItem item : items) {
//            item.force[0] = 0.0f; item.force[1] = 0.0f;
            for (int i = 0; i < iflen; i++) {
                iforces[i].getForce(item);
            }
        }
        for (Spring s : this.springs) {
            for (int i = 0; i < sflen; i++) {
                sforces[i].getForce(s);
            }
        }
        for (ForceItem item : this.items) {
            if (item.locked) {
                item.force[0] = 0f;
                item.force[1] = 0f;
                item.velocity[0] = 0f;
                item.velocity[1] = 0f;
            }
//            System.out.println("lln1:"+item+" forcex="+item.force[0]);
        }
//        System.out.println("\n-----------\n");
//        System.out.println("\n-----------\n");
//        System.out.println("\n-----------\n");
    }

    /**
     * @return the currentIteration
     */
    public int getCurrentIteration() {
        return currentIteration;
    }
} // end of class ForceSimulator
