/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 */
package graph.layouts.modularLayout;

import edu.uci.ics.jung.graph.Graph;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.map.LazyMap;

import configurations.gui.MDLayoutConfig;

/**
 * Implements the Fruchterman-Reingold algorithm for node layout.
 * 
 * @author Scott White, Yan-Biao Boey, Danyel Fisher
 */
public class ModularFRLayout<V, E> implements ModularLayout {

//    private final static Logger logger =
//            Logger.getLogger(ModularFRLayout.class);
    private double forceConstant;
    private double temperature;
    private static double min_temperature = 3.0;
    private int currentIteration;
    private static int mMaxIterations = 500;
    private Graph graph;
    private static double attraction_multiplier = 0.4;
    private double attraction_constant;
    private static double repulsion_multiplier = 0.4;
    private double repulsion_constant;
    private double max_dimension;
    private Collection<MDNode> nodes;
    private Set<Set<MDNode>> edges;
    private MDNode parent;
    private Dimension size = new Dimension();
    private double average_degree;
    private double reciprocal_of_average_degree;
    private boolean sparse;
    private boolean quenching = true;
    private Map<MDNode, Double> radii = new HashMap();
    private int kaba = 5;
    private List<Double> sums = new ArrayList();
    private List<Double> means = new ArrayList();
    private Map<Object, MDNode> nodesMap = new HashMap();
    private static double preferredEdgeMultiplier = 1.0;
    private Map<MDNode, double[]> displacements = LazyMap.decorate(new HashMap(),
            new Factory() {
                public double[] create() {
                    return new double[]{0, 0};
                }
            });
    

   

    public ModularFRLayout(MDNode _parent, double _constant_k) {
        this.initConfig(_parent, _constant_k);
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.layout.AbstractLayout#setSize(java.awt.Dimension)
     */
//    @Override
    public void setSize(Dimension size) {
        this.size = size;
        max_dimension = Math.max(size.height, size.width);
    }

    public void setAttractionMultiplier(double attraction) {
        ModularFRLayout.attraction_multiplier = attraction;
    }

    public void setRepulsionMultiplier(double repulsion) {
        ModularFRLayout.repulsion_multiplier = repulsion;
    }

    private  void loadParameters() {
        attraction_multiplier =
                MDLayoutConfig.amultiplier;
        repulsion_multiplier =
                MDLayoutConfig.rmultiplier;
        preferredEdgeMultiplier =
                MDLayoutConfig.edgeMultiplier;
        min_temperature =
                MDLayoutConfig.lowTemp;
        mMaxIterations =
                MDLayoutConfig.maxIterations;
    }

    public void reset() {
        initialize();
    }

    public void initialize() {
        currentIteration = 0;
        temperature = this.forceConstant * 2;
        attraction_constant = attraction_multiplier * forceConstant;
        repulsion_constant = repulsion_multiplier * forceConstant;
        this.quenching = false;
        this.displacements.clear();
//        new ModularGridLayout(parent, this.constant_k).doLayout();
        if (this.getSize() != null) {
            Random r = new Random(System.currentTimeMillis());
            double w = this.getSize().getWidth(),
                     h = this.getSize().getHeight();
            for (MDNode n : nodes) {
                n.setLocation(r.nextDouble() * w-w/2,
                        r.nextDouble() * h-h/2);
            }
        }
//         this.resetRepulsion_multiplier();
//        this.resetAttractionMultiplier();
    }

    private void initConfig(MDNode _parent, double _constant_k) {
        loadParameters();
        this.parent = _parent;
        this.forceConstant = _constant_k * preferredEdgeMultiplier;
        nodes = parent.getChildren();
        displacements.clear();
        edges = new HashSet();
        int connections = 0;
        for (MDNode node : nodes) {
            if (node.getConnections() != null) {
                for (MDNode n : node.getConnections()) {
                    Set<MDNode> edge = new HashSet();
                    Collections.addAll(edge, node, n);
                    edges.add(edge);
                }
                connections += node.getConnections().size();
            }
            this.radii.put(node,
                    hypot(node.getWidth(), node.getHeight()) / 2.0);
        }
        this.average_degree = connections / (double) nodes.size();
        this.reciprocal_of_average_degree = 1.0 / Math.max(this.EPSILON, this.average_degree);
//            forceConstant =
//                    Math.sqrt(d.getHeight() * d.getWidth() / graph.getVertexCount());

        double r = this.forceConstant * Math.sqrt(this.nodes.size());
        size.setSize(r, r);
        this.sparse = Math.pow(edges.size(), 1.2) < nodes.size();
    }
    private double EPSILON = 0.000001D;
    /**
     * Moves the iteration forward one notch, calculation attraction and
     * repulsion between vertices and edges and cooling the temperature.
     */
    private double sum;

    public synchronized void step() {


        /**
         * Calculate repulsion
         */
        while (true) {

            try {
                for (MDNode n1 : nodes) {
//                    if (isLocked(v1)) continue;
                    calcRepulsion(n1);
                }
                break;
            } catch (ConcurrentModificationException cme) {
            }
        }

        /**
         * Calculate attraction
         */
        while (true) {
            try {
                for (Set<MDNode> edge : edges) {
                    Iterator<MDNode> it = edge.iterator();
                    calcAttraction(it.next(), it.next());
                }
                break;
            } catch (ConcurrentModificationException cme) {
            }
        }


        while (true) {
//            try {
            for (MDNode n : nodes) {
                calcPositions(n);
//                    fireStateChanged();
            }
            break;
//            } catch (ConcurrentModificationException cme) {
//            }
        }

//        System.out.println("iterator:" + this.currentIteration);
//        System.out.println("--->sum:" + sum);
//        System.out.println("----->temp:" + this.temperature);
//        System.out.println("this.sparse:" + this.sparse);
//        System.out.println("ad:" + this.average_degree);
//        System.out.println("rd:" + this.reciprocal_of_average_degree);
//        if (this.temperature < forceConstant / 2) {
//            this.quenching = false;
//        }
//        if (temperature > this.min_temperature) {
//            this.cool();
//        } else {
//            this.quenching = false;
//        }
        this.cool();
        if (this.temperature < ModularFRLayout.min_temperature) {
            this.quenching = false;
        }
        this.sums.add(sum);
        this.average();
//        System.out.println("stop:::" + this.calcStopCriterion());
//        System.out.println("averages:" + this.means);
        sum = 0;
        currentIteration++;
//        fireStateChanged();
    }

    private void average() {
        int k = 0;
        double total = 0.0;
        double a = 0.0;
        for (int i = this.currentIteration; i >= 0; i--) {
            if (i - 1 >= 0) {
                total += Math.abs(sums.get(i) - sums.get(i - 1));
                k += 1;
            }
            if (k >= kaba) {
                break;
            }
        }
        if (k == 0) {
            a = sums.get(currentIteration);
        } else {
            a = total / k;
        }
//        System.out.println("sums:"+sums);
        this.means.add(a);
    }

    private double calcStopCriterion() {
        if (currentIteration < kaba + 1) {
            return 1.0;
        }
        int it = Math.max(0, currentIteration - 1 - kaba);
        double ret= Math.abs(
                (means.get(currentIteration - 1) - means.get(it)) /
                means.get(it));
//        System.out.println("it:"+this.currentIteration+" value:"+ret);
        return ret;
    }

    public synchronized void calcPositions(MDNode n) {
        if (n == null) {
            return;
        }
        double[] disp = displacements.get(n);
        sum += (Math.abs(disp[0]) + Math.abs(disp[1]));
        Point2D xyd = n.getCenter();

        double[] offset = new double[]{
            Math.min(this.temperature, Math.abs(disp[0])),
            Math.min(this.temperature, Math.abs(disp[1]))
        };
        disp[0] = disp[0] > 0 ? offset[0] : -offset[0];
        disp[1] = disp[1] > 0 ? offset[1] : -offset[1];
//            n.translate(
//                    (int) Math.round(disp[0]),
//                    (int) Math.round(disp[1]));




//        double deltaLength = Math.max(EPSILON, Math.hypot(disp[0], disp[1]));
//        double newXDisp = disp[0] / deltaLength * Math.min(deltaLength, temperature);
        double newXDisp = disp[0];
        if (Double.isNaN(newXDisp)) {
            throw new IllegalArgumentException(
                    "Unexpected mathematical result in FRLayout:calcPositions [xdisp]");
        }

//        double newYDisp = disp[1] / deltaLength * Math.min(deltaLength, temperature);
        double newYDisp = disp[1];
        xyd.setLocation(xyd.getX() + newXDisp, xyd.getY() + newYDisp);


//        double borderWidth = getSize().getWidth() / 50.0;
//        double newXPos = xyd.getX();
////        if (newXPos < borderWidth) {
////            newXPos = borderWidth + Math.random() * borderWidth * 2.0;
////        } else if (newXPos > (getSize().getWidth() - borderWidth)) {
////            newXPos = getSize().getWidth() - borderWidth - Math.random() * borderWidth * 2.0;
////        }
//
//        double newYPos = xyd.getY();
////        if (newYPos < borderWidth) {
////            newYPos = borderWidth + Math.random() * borderWidth * 2.0;
////        } else if (newYPos > (getSize().getHeight() - borderWidth)) {
////            newYPos = getSize().getHeight() - borderWidth - Math.random() * borderWidth * 2.0;
////        }
//
//        xyd.setLocation(newXPos, newYPos);
    }

    private double hypot(double x, double y) {
        return Math.sqrt(x * x + y * y);
//        return Math.hypot(x, y);
//        return Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2));
    }

    public void calcAttraction(MDNode n1, MDNode n2) {
//        Pair<V> endpoints = getGraph().getEndpoints(e);
//        V v1 = endpoints.getFirst();
//        V v2 = endpoints.getSecond();
        Point2D p1 = n1.getCenter();
        Point2D p2 = n2.getCenter();
        if (p1 == null || p2 == null) {
            return;
        }
        double xDelta = p1.getX() - p2.getX();
        double yDelta = p1.getY() - p2.getY();
        double deltaLength = Math.max(EPSILON, hypot(xDelta, yDelta));
        double cos = xDelta / deltaLength,  sin = yDelta / deltaLength;
        if (!quenching) {
            deltaLength -= (radii.get(n1) + this.radii.get(n2));
        }
        if (deltaLength <= this.EPSILON) {
            return;
        }
        double force = (deltaLength * deltaLength) / attraction_constant;
        double w = this.reciprocal_of_average_degree;
        if (this.temperature < 1 || this.sparse) {
            w = this.average_degree;
        }
        force *= w;
        if (Double.isNaN(force)) {
            throw new IllegalArgumentException(
                    "Unexpected mathematical result in FRLayout:calcPositions [force]");
        }
        double dx = (xDelta / deltaLength) * force;
        double dy = (yDelta / deltaLength) * force;
//        MDNode n1 = this.nodesMap.get(v1);
        double[] disp1 = displacements.get(n1);
        disp1[0] -= dx;
        disp1[1] -= dy;

//        MDNode n2 = this.nodesMap.get(v2);
        double[] disp2 = displacements.get(n2);
        disp2[0] += dx;
        disp2[1] += dy;
    }

    public void calcRepulsion(MDNode n1) {
        double[] disp1 = displacements.get(n1);
        disp1[0] = disp1[1] = 0.0;

//        try {
        for (MDNode n2 : nodes) {

//                if (isLocked(v2)) continue;
            if (n1 != n2) {
                Point2D p1 = n1.getCenter();
                Point2D p2 = n2.getCenter();
                double xDelta = p1.getX() - p2.getX();
                double yDelta = p1.getY() - p2.getY();

                double deltaLength = Math.max(EPSILON, hypot(xDelta, yDelta));
                double cos = xDelta / deltaLength,  sin = yDelta / deltaLength;
                if (!quenching) {
                    deltaLength -= (radii.get(n1) + this.radii.get(n2));
                    deltaLength = deltaLength > EPSILON ? deltaLength : EPSILON;
                }
//                if(deltaLength>5.0*this.forceConstant)return;
                double force = (repulsion_constant * repulsion_constant) / deltaLength;

                if (Double.isNaN(force)) {
                    throw new RuntimeException(
                            "Unexpected mathematical result in FRLayout:calcPositions [repulsion]");
                }
                disp1[0] += cos * force;
                disp1[1] += sin * force;
            }
        }
//        } catch (ConcurrentModificationException cme) {
//            calcRepulsion(n1);
//        }
    }

    private void cool() {
//        if(temperature>this.min_temperature){
//            double m=0.8;
//            m+=this.currentIteration*0.002;
//            m=Math.min(m, 0.99);
//            temperature*=m;
//        }  
        if (temperature > 30) {
            temperature *= 0.9;
        } else if (temperature > 20) {
            temperature *= 0.95;
        } else if (temperature > 10) {
            temperature *= 0.96;
        } else if (temperature > 5) {
            temperature *= 0.97;
        } else if (temperature > 2.5) {
            temperature *= 0.98;
        } else if (temperature > this.temperature) {
            temperature *= 0.985;
        } else {
            temperature *= 0.999;
//            if (temperature > 0.5) {
//            temperature *= 0.99;
//        } else {
//            temperature *= 0.999;
////        temperature *= 0.9;//(1.0 - currentIteration / (double) mMaxIterations);
//        }
        }
    }

    public void setMaxIterations(int maxIterations) {
        mMaxIterations = maxIterations;
    }

//    public FRVertexData getFRData(V v) {
//        return frVertexData.get(v);
//    }
    /**
     * This one is an incremental visualization.
     */
    public boolean isIncremental() {
        return true;
    }

    /**
     * Returns true once the current iteration has passed the maximum count,
     * <tt>MAX_ITERATIONS</tt>.
     */
    public boolean done() {
        if (currentIteration > mMaxIterations ||
                (temperature < ModularFRLayout.min_temperature &&
                this.calcStopCriterion() < 0.000001)) {
            return true;
        }
        return false;
    }

    public Point2D transform(V v) {
        MDNode n = nodesMap.get(v);
        return n.getCenter();
    }

    public void doLayout() {
        this.initialize();
        do {
            step();
        } while (!done());
    }

    public void setConstant_k(double constant_k) {
        this.forceConstant = constant_k;
    }
    public Dimension getSize() {
        return this.size;
    }

    public void lock(V arg0, boolean arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isLocked(V arg0) {
        return false;
    }

    public void setLocation(V arg0, Point2D arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}