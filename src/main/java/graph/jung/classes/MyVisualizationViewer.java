package graph.jung.classes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import biologicalElements.Pathway;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import graph.GraphContainer;

public class MyVisualizationViewer<V, E> extends VisualizationViewer<V, E> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Pathway pw;
	private Point2D mousePoint = new Point2D.Double(0, 0);
	private int fpsCounter = 0;
	private long start = 0;
	private boolean printFPS = !true;

	private Font fontBold = new Font("default", Font.BOLD, 12);
	private Font fontPlain = new Font("default", Font.PLAIN, 12);

	public MyVisualizationViewer(VisualizationModel<V, E> arg0, Dimension arg2, Pathway pw) {
		super(arg0, arg2);
		this.pw = pw;
	}

	// private long lastTimeCompartmentRendered = 0;
	// private double scaleLastCompartmentRendered = 0;

	// private long[] relaxTimes = new long[5];

	// private long[] paintTimes = new long[5];

	// private int relaxIndex = 0;

	// private int paintIndex = 0;

	// private double paintfps, relaxfps;

	/**
	 * a collection of user-implementable functions to render under the topology
	 * (before the graph is rendered)
	 */
	// protected List<Paintable> preRenderers = new ArrayList<Paintable>();

	/**
	 * a collection of user-implementable functions to render over the topology
	 * (after the graph is rendered)
	 */
	// protected List<Paintable> postRenderers = new ArrayList<Paintable>();
	protected void renderGraph(Graphics2D g2d) {
		super.renderGraph(g2d);

		g2d.setFont(fontBold);
		g2d.setColor(Color.red);

		if (GraphContainer.getInstance().isPetriView()) {
			g2d.drawString("P: " + pw.getPlaceCount() + " T: " + pw.getTransitionCount() + " Edges: "
					+ pw.getGraph().getAllEdges().size(), 1, 11);
		} else {
			g2d.drawString(
					"Nodes: " + pw.getGraph().getAllVertices().size() + " Edges: " + pw.getGraph().getAllEdges().size(),
					1, 11);
		}
		g2d.drawString("Picked nodes: " + getPickedVertexState().getPicked().size(), 1, 23);

		double scale = ((int) (getScale() * 100)) / 100.0;
		g2d.drawString("Zoom: " + scale + "x", getWidth() - 75, 11);

		drawMousePoint(g2d);

		// g2d.drawString("x", 580, 533);
		// ContainerSingelton.getInstance().setPetriView(true);
		if (printFPS) {
			fpsCounter++;
			printFPS();
		}
	}

	public List<VisualizationServer.Paintable> getPreRenderers() {
		return super.preRenderers;
	}

	public List<VisualizationServer.Paintable> getPostRenderers() {
		return super.postRenderers;
	}

	public Pathway getPathway() {
		return pw;
	}

	public double getScale() {
		double scaleV = getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
		double scaleL = getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).getScale();
		return scaleV < 1 ? scaleV : scaleL;
	}

	public void setMousePoint(Point2D point) {
		this.mousePoint = point;
		this.repaint(getWidth() - 75, 0, 75, 40);
	}

	private void drawMousePoint(Graphics2D g2d) {
		g2d.setFont(fontPlain);
		g2d.setColor(Color.black);
		String text = (int) mousePoint.getX() + ", " + (int) mousePoint.getY();
		Rectangle2D bounds = g2d.getFont().getStringBounds(text, g2d.getFontRenderContext());
		g2d.drawString(text, (int) (getWidth() - bounds.getWidth()) - 10, 22);
	}

	private void printFPS() {
		long stop = System.currentTimeMillis();
		if (stop - start > 1000) {
			System.out.println("FPS: " + fpsCounter);
			fpsCounter = 0;
			start = stop;
		}
	}
}
