package graph.layouts;

/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Aug 23, 2005
 */

/*import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.algorithms.shortestpath.Distance;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.visualization.Coordinates;
import edu.uci.ics.jung.visualization.subLayout.SubLayout;
import edu.uci.ics.jung.visualization.subLayout.SubLayoutDecorator;

public class KKSubLayout implements SubLayout {

    protected final Map map = new LinkedHashMap();
    private SubLayoutDecorator layout;

	private double EPSILON = 0.1d;

	private int currentIteration;
    private int maxIterations = 2000;
	
	private double L;			// the ideal length of an edge
	private double K = 1;		// arbitrary const number
	private double[][] dm;     // distance matrix

	private boolean adjustForGravity = true;
	private boolean exchangeVertices = true;

	private Vertex[] vertices;
	private Coordinates[] xydata;
	
	private SubLayoutCenter center=null;
	private int numVertices=0;*/
    
    /**
     * Retrieves graph distances between vertices of the visible graph
     */
    //protected Distance distance;

    /**
     * The diameter of the visible graph. In other words, the maximum over all pairs
     * of vertices of the length of the shortest path between a and bf the visible graph.
     */
	//protected double diameter;

    /**
     * A multiplicative factor which partly specifies the "preferred" length of an edge (L).
     */
    //private double length_factor = 0.9;

    /**
     * A multiplicative factor which specifies the fraction of the graph's diameter to be 
     * used as the inter-vertex distance between disconnected vertices.
     */
    //private double disconnected_multiplier = 0.5;
	
	
    /**
     * create an instance with passed values
     * @param vertices the collection of vertices to arrange in a circle
     * @param radius the radius of the circle
     * @param center the center of the circle
     */
	/*public KKSubLayout(Collection vertices, SubLayoutDecorator layout) {
        this.layout=layout;
        initializeLocations(vertices);
		
	}*/

	/**
	 * Map the Vertices in the passed collection to their
	 * locations, distributed about the circumference of
	 * a circle
	 * 
	 * @param vertices
	 */
	/*private void initializeLocations(Collection verticesCollection) {
		Vertex[] vertexArray =
		    (Vertex[]) verticesCollection.toArray(new Vertex[verticesCollection.size()]);
		
        // only apply sublayout if there is more than one vertex
        if (vertexArray.length > 1) {
			
        	center = new SubLayoutCenter((Set) verticesCollection,layout);			
			distance = new UnweightedShortestPath(layout.getGraph());
			
			double height = center.getHeight();
			double width = center.getWidth();		
			numVertices = vertexArray.length;
			
			dm = new double[numVertices][numVertices];
			vertices = new Vertex[numVertices];
			xydata = new Coordinates[numVertices];
			
			while(true) {
			    try {
			        int index = 0;
			        for (int i = 0; i < vertexArray.length; i++) {
			            
			        	Vertex v = vertexArray[i];
			            Coordinates xyd = new Coordinates();
			            xyd.setLocation(layout.getLocation(v)); 
			            vertices[index] = v;
			            xydata[index] = xyd;
			            index++;
			            
			        }
			        break;
			    } catch(ConcurrentModificationException cme) {}
			}
			
		   diameter = diameter(verticesCollection, distance, true);
	            
		   double L0 = Math.min(height, width);
		   L = (L0 / diameter) * length_factor;  // length_factor used to be hardcoded to 0.9
				//L = 0.75 * Math.sqrt(height * width / n);
		   
		   for (int i = 0; i < numVertices - 1; i++) 
	        {
				for (int j = i + 1; j < numVertices; j++) 
	            {
	                Number d_ij = distance.getDistance(vertices[i], vertices[j]);
	                Number d_ji = distance.getDistance(vertices[j], vertices[i]);
	                double dist = diameter * disconnected_multiplier;
	                if (d_ij != null)
	                   dist = Math.min(d_ij.doubleValue(), dist);
	                if (d_ji != null)
	                    dist = Math.min(d_ji.doubleValue(), dist);
	                dm[i][j] = dm[j][i] = dist;
				}
			}
		
		   if(currentIteration<maxIterations){
	        	 advancePositions();
	        }  
		   changePositions();
        }
       
	}
	
	private void changePositions(){
		
		for (int i = 0; i < vertices.length; i++) {
	            
	        	Vertex v = vertices[i];
	        	Coordinates c =  xydata[i];
	        	Point2D point = new Point2D.Double(c.getX(),c.getY());
	        	map.put(vertices[i], point);
				layout.forceMove(vertices[i], point.getX(), point.getY());
	        }
		
	}
	
	 public void advancePositions() {
			currentIteration++;
			double energy = calcEnergy();

			int n = numVertices;
	        if (n == 0)
	            return;

			double maxDeltaM = 0;
			int pm = -1;            // the node having max deltaM
			for (int i = 0; i < n; i++) {
	       //     if (isLocked(vertices[i]))
	        //        continue;
				double deltam = calcDeltaM(i);
			
				if (maxDeltaM < deltam) {
					maxDeltaM = deltam;
					pm = i;
				}
			}
			if (pm == -1)
	            return;

	        for (int i = 0; i < 100; i++) {
				double[] dxy = calcDeltaXY(pm);
				xydata[pm].add(dxy[0], dxy[1]);
				double deltam = calcDeltaM(pm);
	            if (deltam < EPSILON)
	                break;
	            //if (dxy[0] > 1 || dxy[1] > 1 || dxy[0] < -1 || dxy[1] < -1)
	            //    break;
			}

			if (adjustForGravity)
				adjustForGravity();

			if (exchangeVertices && maxDeltaM < EPSILON) {
	            energy = calcEnergy();
				for (int i = 0; i < n - 1; i++) {
	           //     if (isLocked(vertices[i]))
	            //        continue;
					for (int j = i + 1; j < n; j++) {
	             //       if (isLocked(vertices[j]))
	              //          continue;
						double xenergy = calcEnergyIfExchanged(i, j);
						if (energy > xenergy) {
							double sx = xydata[i].getX();
							double sy = xydata[i].getY();
							xydata[i].setX(xydata[j].getX());
							xydata[i].setY(xydata[j].getY());
							xydata[j].setX(sx);
							xydata[j].setY(sy);
							
						
							return;
						}
					}
				}
			}
		}*/

	 	/**
		 * Calculates the energy function E.
		 */
		/*private double calcEnergy() {
			double energy = 0;
			for (int i = 0; i < vertices.length - 1; i++) {
				for (int j = i + 1; j < vertices.length; j++) {
	                double dist = dm[i][j];
					double l_ij = L * dist;
					double k_ij = K / (dist * dist);
					double dx = xydata[i].getX() - xydata[j].getX();
					double dy = xydata[i].getY() - xydata[j].getY();
					double d = Math.sqrt(dx * dx + dy * dy);

					energy += k_ij / 2 * (dx * dx + dy * dy + l_ij * l_ij -
										  2 * l_ij * d);
				}
			}
			return energy;
		}*/
	
		/**
		 * Calculates the energy function E as if positions of the
		 * specified vertices are exchanged.
		 */
		/*private double calcEnergyIfExchanged(int p, int q) {
			if (p >= q)
				throw new RuntimeException("p should be < q");
			double energy = 0;		// < 0
			for (int i = 0; i < vertices.length - 1; i++) {
				for (int j = i + 1; j < vertices.length; j++) {
					int ii = i;
					int jj = j;
					if (i == p) ii = q;
					if (j == q) jj = p;

	                double dist = dm[i][j];
					double l_ij = L * dist;
					double k_ij = K / (dist * dist);
					double dx = xydata[ii].getX() - xydata[jj].getX();
					double dy = xydata[ii].getY() - xydata[jj].getY();
					double d = Math.sqrt(dx * dx + dy * dy);
					
					energy += k_ij / 2 * (dx * dx + dy * dy + l_ij * l_ij -
										  2 * l_ij * d);
				}
			}
			return energy;
		}*/
		
		/**
		 * Calculates the gradient of energy function at the vertex m.
		 */
		/*private double calcDeltaM(int m) {
			double dEdxm = 0;
			double dEdym = 0;
			for (int i = 0; i < vertices.length; i++) {
				if (i != m) {
	                double dist = dm[m][i];
					double l_mi = L * dist;
					double k_mi = K / (dist * dist);

					double dx = xydata[m].getX() - xydata[i].getX();
					double dy = xydata[m].getY() - xydata[i].getY();
					double d = Math.sqrt(dx * dx + dy * dy);

					double common = k_mi * (1 - l_mi / d);
					dEdxm += common * dx;
					dEdym += common * dy;
				}
			}
			return Math.sqrt(dEdxm * dEdxm + dEdym * dEdym);
		}*/
		
		/**
		 * Determines a step to new position of the vertex m.
		 */
		/*private double[] calcDeltaXY(int m) {
			double dE_dxm = 0;
			double dE_dym = 0;
			double d2E_d2xm = 0;
			double d2E_dxmdym = 0;
			double d2E_dymdxm = 0;
			double d2E_d2ym = 0;

			for (int i = 0; i < vertices.length; i++) {
				if (i != m) {
	                
	                double dist = dm[m][i];
					double l_mi = L * dist;
					double k_mi = K / (dist * dist);
					double dx = xydata[m].getX() - xydata[i].getX();
					double dy = xydata[m].getY() - xydata[i].getY();
					double d = Math.sqrt(dx * dx + dy * dy);
					double ddd = d * d * d;

					dE_dxm += k_mi * (1 - l_mi / d) * dx;
					dE_dym += k_mi * (1 - l_mi / d) * dy;
					d2E_d2xm += k_mi * (1 - l_mi * dy * dy / ddd);
					d2E_dxmdym += k_mi * l_mi * dx * dy / ddd;
					d2E_d2ym += k_mi * (1 - l_mi * dx * dx / ddd);
				}
			}
			// d2E_dymdxm equals to d2E_dxmdym.
			d2E_dymdxm = d2E_dxmdym;

			double denomi = d2E_d2xm * d2E_d2ym - d2E_dxmdym * d2E_dymdxm;
			double deltaX = (d2E_dxmdym * dE_dym - d2E_d2ym * dE_dxm) / denomi;
			double deltaY = (d2E_dymdxm * dE_dxm - d2E_d2xm * dE_dym) / denomi;
			return new double[]{deltaX, deltaY};
		}*/	
		
		/**
		 * Shift all vertices so that the center of gravity is located at
		 * the center of the screen.
		 */
		/*public void adjustForGravity() {
			
			double height = center.getHeight();
			double width = center.getWidth();
			double gx = 0;
			double gy = 0;
			for (int i = 0; i < xydata.length; i++) {
				gx += xydata[i].getX();
				gy += xydata[i].getY();
			}
			gx /= xydata.length;
			gy /= xydata.length;
			double diffx = width / 2 - gx;
			double diffy = height / 2 - gy;
			for (int i = 0; i < xydata.length; i++) {
				xydata[i].add(diffx, diffy);
			}
		}*/

		/**
		 * Enable or disable gravity point adjusting.
		 */
//		public void setAdjustForGravity(boolean on) {
//			adjustForGravity = on;
//		}

		/**
		 * Returns true if gravity point adjusting is enabled.
		 */
//		public boolean getAdjustForGravity() {
//			return adjustForGravity;
//		}

		/**
		 * Enable or disable the local minimum escape technique by
		 * exchanging vertices.
		 */
//		public void setExchangeVertices(boolean on) {
//			exchangeVertices = on;
//		}

		/**
		 * Returns true if the local minimum escape technique by
		 * exchanging vertices is enabled.
		 */
//		public boolean getExchangeVertices() {
//			return exchangeVertices;
//		}
		
		  /**
	     * Returns the diameter of <code>g</code> using the metric 
	     * specified by <code>d</code>.  The diameter is defined to be
	     * the maximum, over all pairs of vertices <code>u,v</code>,
	     * of the length of the shortest path from <code>u</code> to 
	     * <code>v</code>.  If the graph is disconnected (that is, not 
	     * all pairs of vertices are reachable from one another), the
	     * value returned will depend on <code>use_max</code>:  
	     * if <code>use_max == true</code>, the value returned
	     * will be the the maximum shortest path length over all pairs of <b>connected</b> 
	     * vertices; otherwise it will be <code>Double.POSITIVE_INFINITY</code>.
	     */
	    /*private double diameter(Collection verticesCollection, Distance d, boolean use_max)
	    {
	        double diameter = 0;
	        
	        for (Iterator outer = verticesCollection.iterator(); outer.hasNext(); )
	        {
	            ArchetypeVertex v = (ArchetypeVertex)outer.next();
	      
	            for (Iterator inner = verticesCollection.iterator(); inner.hasNext(); )
	            {
	                ArchetypeVertex w = (ArchetypeVertex)inner.next();
	            
	                if (v != w) // don't include self-distances
	                {
	                    Number dist = d.getDistance(v, w);
	                    if (dist == null)
	                    {
	                        if (!use_max)
	                            return Double.POSITIVE_INFINITY;
	                    }
	                    else
	                        diameter = Math.max(diameter, dist.doubleValue());
	                }
	            }
	        }
	        return diameter;
	    }
		
    public Point2D getLocation(ArchetypeVertex v) {
        return (Point2D)map.get(v);
    }
}*/
