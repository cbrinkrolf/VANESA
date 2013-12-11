package graph.jung.classes;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.visualization.VisualizationModel;
/*import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.Renderer;*/
import edu.uci.ics.jung.visualization.VisualizationViewer;

public class MyVisualizationViewer extends VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Pathway pw;

	public MyVisualizationViewer(VisualizationModel<BiologicalNodeAbstract, BiologicalEdgeAbstract> arg0, Dimension arg2, Pathway pw) {
		super(arg0, arg2);
		this.pw=pw;
		// TODO Auto-generated constructor stub
	}

	long[] relaxTimes = new long[5];

	long[] paintTimes = new long[5];

	int relaxIndex = 0;

	int paintIndex = 0;

	double paintfps, relaxfps;
	
	/**
	 * a collection of user-implementable functions to render under
	 * the topology (before the graph is rendered)
	 */
	//protected List<Paintable> preRenderers = new ArrayList<Paintable>();

	/**
	 * a collection of user-implementable functions to render over the
	 * topology (after the graph is rendered)
	 */
	//protected List<Paintable> postRenderers = new ArrayList<Paintable>();

	protected void renderGraph(Graphics2D g2d){
		
		 super.renderGraph(g2d);
	}
	
	/*@Override
	protected void renderGraph(Graphics2D g2d) {
		System.out.println("render");
		
		 if(renderContext.getGraphicsContext() == null) {
		        renderContext.setGraphicsContext(new GraphicsDecorator(g2d));
	        } else {
	        	renderContext.getGraphicsContext().setDelegate(g2d);
	        }
	        renderContext.setScreenDevice(this);
		    Layout layout = model.getGraphLayout();

			g2d.setRenderingHints(renderingHints);

			// the size of the VisualizationViewer
			Dimension d = getSize();

			// clear the offscreen image
			g2d.setColor(getBackground());
			g2d.fillRect(0,0,d.width,d.height);

			AffineTransform oldXform = g2d.getTransform();
	        AffineTransform newXform = new AffineTransform(oldXform);
	        newXform.concatenate(
	        		renderContext.getMultiLayerTransformer().getTransformer(Layer.VIEW).getTransform());
//	        		viewTransformer.getTransform());

	        g2d.setTransform(newXform);

			// if there are  preRenderers set, paint them
			for(Paintable paintable : preRenderers) {

			    if(paintable.useTransform()) {
			        paintable.paint(g2d);
			    } else {
			        g2d.setTransform(oldXform);
			        paintable.paint(g2d);
	                g2d.setTransform(newXform);
			    }
			}

	        if(layout instanceof Caching) {
	        	((Caching)layout).clear();
	        }

	        renderer.render(renderContext, layout);
	        
	        

			// if there are postRenderers set, do it
			for(Paintable paintable : postRenderers) {

			    if(paintable.useTransform()) {
			        paintable.paint(g2d);
			    } else {
			        g2d.setTransform(oldXform);
			        paintable.paint(g2d);
	                g2d.setTransform(newXform);
			    }
			}
			g2d.setTransform(oldXform);
		/*Layout layout = model.getGraphLayout();
	
		g2d.setRenderingHints(renderingHints);
		long start = System.currentTimeMillis();
		// the size of the VisualizationViewer
		Dimension d = getSize();

		// clear the offscreen image
		g2d.setColor(getBackground());
		g2d.fillRect(0, 0, d.width, d.height);

		AffineTransform oldXform = g2d.getTransform();
		AffineTransform newXform = new AffineTransform(oldXform);
//		newXform.concatenate(viewTransformer.getTransform());
		
		
JScrollBar bar;
		g2d.setTransform(newXform);

		// if there are preRenderers set, paint them
		for (Iterator iterator = preRenderers.iterator(); iterator.hasNext();) {
			Paintable paintable = (Paintable) iterator.next();
			if (paintable.useTransform()) {
				paintable.paint(g2d);
			} else {
				g2d.setTransform(oldXform);
				paintable.paint(g2d);
				g2d.setTransform(newXform);
			}
		}

		locationMap.clear();

		// paint all the edges
		try {
			for (Iterator iter = layout.getGraph().getEdges().iterator(); iter
					.hasNext();) {
				Edge e = (Edge) iter.next();

				Vertex v1 = (Vertex) e.getEndpoints().getFirst();
				Vertex v2 = (Vertex) e.getEndpoints().getSecond();

				if (pw.containsElement(e)) {

					GraphElementAbstract gea = (GraphElementAbstract) pw
							.getElement(e);
					
					if (gea.isVisible()) {
						Point2D p = (Point2D) locationMap.get(v1);
						if (p == null) {

							p = layout.getLocation(v1);
							p = layoutTransformer.transform(p);
							locationMap.put(v1, p);
						}
						Point2D q = (Point2D) locationMap.get(v2);
						if (q == null) {
							q = layout.getLocation(v2);
							q = layoutTransformer.transform(q);
							locationMap.put(v2, q);
						}

						if (p != null && q != null) {
							renderer.paintEdge(g2d, e, (int) p.getX(), (int) p
									.getY(), (int) q.getX(), (int) q.getY());

						}
					}

				} else {

					Point2D p = (Point2D) locationMap.get(v1);
					if (p == null) {

						p = layout.getLocation(v1);
						p = layoutTransformer.transform(p);
						locationMap.put(v1, p);
					}
					Point2D q = (Point2D) locationMap.get(v2);
					if (q == null) {
						q = layout.getLocation(v2);
						q = layoutTransformer.transform(q);
						locationMap.put(v2, q);
					}

					if (p != null && q != null) {
						renderer.paintEdge(g2d, e, (int) p.getX(), (int) p
								.getY(), (int) q.getX(), (int) q.getY());

					}

				}
			}

		} catch (ConcurrentModificationException cme) {
			repaint();
		}

		// paint all the vertices
		try {
			for (Iterator iter = layout.getGraph().getVertices().iterator(); iter
					.hasNext();) {

				BiologicalNodeAbstract v = (BiologicalNodeAbstract) iter.next();
				
				if (pw.containsElement(v)){
					GraphElementAbstract gea = (GraphElementAbstract) pw
					.getElement(v);
					if (gea.isVisible()) {
						Point2D p = (Point2D) locationMap.get(v);
						if (p == null) {
							p = layout.getLocation(v);
							p = layoutTransformer.transform(p);
							locationMap.put(v, p);
						}
						if (p != null) {
							renderer.paintVertex(g2d, v, (int) p.getX(), (int) p
									.getY());
						}
					}

				}else{
					Point2D p = (Point2D) locationMap.get(v);
					if (p == null) {
						p = layout.getLocation(v);
						p = layoutTransformer.transform(p);
						locationMap.put(v, p);
					}
					if (p != null) {
						renderer.paintVertex(g2d, v, (int) p.getX(), (int) p
								.getY());
					}
				}		
			}
		} catch (ConcurrentModificationException cme) {
			repaint();
		}

		long delta = System.currentTimeMillis() - start;
		paintTimes[paintIndex++] = delta;
		paintIndex = paintIndex % paintTimes.length;
		paintfps = average(paintTimes);

		// if there are postRenderers set, do it
		for (Iterator iterator = postRenderers.iterator(); iterator.hasNext();) {
			Paintable paintable = (Paintable) iterator.next();
			if (paintable.useTransform()) {
				paintable.paint(g2d);
			} else {
				g2d.setTransform(oldXform);
				paintable.paint(g2d);
				g2d.setTransform(newXform);
			}
		}
		g2d.setTransform(oldXform);*/
	//}

	public void setPw(Pathway pw) {
		this.pw = pw;
	}

	public Pathway getPw() {
		return pw;
	}

}
