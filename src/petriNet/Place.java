package petriNet;

import java.awt.Color;

import biologicalElements.Elementdeclerations;
//import edu.uci.ics.jung.graph.Vertex;
import graph.GraphInstance;
import graph.jung.graphDrawing.VertexShapes;

public class Place extends PNNode {

	private double token = 0;
	private double tokenMin = 0.0;
	private double tokenMax = Double.MAX_VALUE;
	private double tokenStart = 0;

	private boolean discrete = true;

	private Color plotColor;

	public boolean isDiscrete() {
		return discrete;
	}

	public void setDiscrete(boolean discrete) {
		if (discrete) {
			setBiologicalElement(Elementdeclerations.place);
		} else {
			setBiologicalElement(Elementdeclerations.s_place);
		}
		this.discrete = discrete;
	}

	public double getTokenStart() {
		return tokenStart;
	}

	public void setTokenStart(double tokenStart) {
		this.tokenStart = tokenStart;
		if (!new GraphInstance().getPathway().getPetriNet().isPetriNetSimulation())
			token = tokenStart;
	}


	// private int r;
	// private int b;
	// private int g;
	public Place(String label, String name, double token, boolean discrete){
		super(label,name);
		if (label.equals(""))
			setLabel(name);
		if (name.equals(""))
			setName(label);
		shapes = new VertexShapes();
		//System.out.println("new place");
		this.discrete = discrete;

		if (discrete) {
			setDefaultShape(shapes.getEllipse());
		} else {
			setDefaultShape(shapes.getDoubleEllipse());
		}

		if (discrete) {
			setBiologicalElement(Elementdeclerations.place);
		} else {
			setBiologicalElement(Elementdeclerations.s_place);
		}
		if (discrete) {
			// super.setColor(new Color(255,255,255));
			// System.out.println("weiss");
		} else {
			// super.setColor(new Color(125,125,125));
			// System.out.println("grau");
		}
		this.setDefaultNodesize(2);
		// Rectangle bounds = getShape().getBounds();
		// System.out.println("hoehe: "+bounds.getHeight());
		// System.out.println("weite: "+bounds.getWidth());
		// AffineTransform transform = new AffineTransform();
		// transform.translate(x2, y2 - bounds.getHeight() / 2);
		// transform.scale(bounds.getWidth()*3, bounds.getHeight());
		// setShape(transform.createTransformedShape(getShape()));
		this.token = token;

		// this.setLabel(token+" "+label);
		// this.setComments("commetnr");
		// this.set
		setDefaultColor(Color.WHITE);
	}

	@Override
	public void rebuildShape(VertexShapes vs) {
		/*
		// this.setColor(new Color(255-token,255-token,255-token));
		Shape s = null;
		// if (!discrete)s = vs.getDoubleEllipse(getVertex());
		// else s=vs.getEllipse(getVertex());

		// Rectangle bounds = s.getBounds();
		// AffineTransform transform = new AffineTransform();
		// transform.translate(1,1);
		// transform.scale(1, 2);
		// this.setColor(new Color(token,0,0));

		AffineTransform transform = new AffineTransform();
		transform.scale(2, 2);
		setShape(transform.createTransformedShape(s));
		// setShape(s);*/
	}

	public double getToken() {
		return this.token;
	}

	public double getTokenMin() {
		if(this.isConstant()){
			return 0;
		}
		return tokenMin;
	}

	public void setTokenMin(double tokenMin) {
		this.tokenMin = tokenMin;
	}

	public double getTokenMax() {
		if(this.isConstant()){
			return Double.MAX_VALUE;
		}
		return tokenMax;
	}

	public void setTokenMax(double tokenMax) {
		this.tokenMax = tokenMax;
	}

	public void setToken(double token) {
		this.token = token;
	}

	/*
	 * public void setRelativeColor(int r, int g, int b) { this.r = r; this.g =
	 * g; this.b = b; }
	 */

	public void setPlotColor(Color plotColor) {
		this.plotColor = plotColor;
	}

	public Color getPlotColor() {
		return plotColor;
	}

}
