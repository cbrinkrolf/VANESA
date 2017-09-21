package biologicalObjects.nodes.petriNet;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import biologicalElements.Elementdeclerations;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNEdge;
//import edu.uci.ics.jung.graph.Vertex;
import graph.GraphInstance;
import graph.jung.graphDrawing.VertexShapes;

public class Place extends PNNode {

	public static final int CONFLICTHANDLING_NONE = 0;
	public static final int CONFLICTHANDLING_PRIO = 1;
	public static final int CONFLICTHANDLING_PROB = 2;

	private int conflictStrategy = 0;

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
	public Place(String label, String name, double token, boolean discrete) {
		super(label, name);
		if (label.equals(""))
			setLabel(name);
		if (name.equals(""))
			setName(label);
		shapes = new VertexShapes();
		// System.out.println("new place");
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
		 * // this.setColor(new Color(255-token,255-token,255-token)); Shape s =
		 * null; // if (!discrete)s = vs.getDoubleEllipse(getVertex()); // else
		 * s=vs.getEllipse(getVertex());
		 * 
		 * // Rectangle bounds = s.getBounds(); // AffineTransform transform =
		 * new AffineTransform(); // transform.translate(1,1); //
		 * transform.scale(1, 2); // this.setColor(new Color(token,0,0));
		 * 
		 * AffineTransform transform = new AffineTransform(); transform.scale(2,
		 * 2); setShape(transform.createTransformedShape(s)); // setShape(s);
		 */
	}

	public double getToken() {
		return this.token;
	}

	public double getTokenMin() {
		if (this.isConstant()) {
			return 0;
		}
		return tokenMin;
	}

	public void setTokenMin(double tokenMin) {
		this.tokenMin = tokenMin;
	}

	public double getTokenMax() {
		if (this.isConstant()) {
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

	public int getConflictStrategy() {
		return conflictStrategy;
	}

	public void setConflictStrategy(int conflictStrategy) {
		this.conflictStrategy = conflictStrategy;
	}

	public Set<PNEdge> getConflictingOutEdges() {
		Collection<BiologicalEdgeAbstract> coll = GraphInstance.getMyGraph().getJungGraph().getOutEdges(this);
		Set<PNEdge> result = new HashSet<PNEdge>();
		if (coll.size() > 0) {
			Iterator<BiologicalEdgeAbstract> it = coll.iterator();
			BiologicalEdgeAbstract bea;
			while (it.hasNext()) {
				bea = it.next();
				if (bea instanceof PNEdge && !(bea.getTo() instanceof ContinuousTransition)) {
					result.add((PNEdge) bea);
				}
			}
		}
		return result;
	}

	public boolean hasConflictProperties() {
		if (this.conflictStrategy == Place.CONFLICTHANDLING_PRIO) {
			return this.hasPriorityConflicts();
		} else if (this.conflictStrategy == Place.CONFLICTHANDLING_PROB) {
			return this.hasProbabilityConflicts();
		}
		return false;
	}

	public boolean hasPriorityConflicts() {
		Collection<PNEdge> edges = this.getConflictingOutEdges();
		if (edges.size() > 1) {
			Iterator<PNEdge> it = edges.iterator();
			PNEdge bea;
			Set<Integer> set = new HashSet<Integer>();
			while (it.hasNext()) {
				bea = it.next();
				if (set.contains(bea.getPriority())) {
					return true;
				} else {
					if (bea.getPriority() > 0 && bea.getPriority() <= edges.size()) {
						set.add(bea.getPriority());
					} else {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean hasProbabilityConflicts() {
		Collection<PNEdge> edges = this.getConflictingOutEdges();
		if (edges.size() > 1) {
			double sum = 0;
			Iterator<PNEdge> it = edges.iterator();
			PNEdge bea;
			while (it.hasNext()) {
				bea = it.next();
				sum += bea.getProbability();
			}
			if (sum != 1.0) {
				return true;
			}
		}
		return false;
	}

	public void solveConflictProperties() {
		if (this.conflictStrategy == Place.CONFLICTHANDLING_PRIO && this.hasPriorityConflicts()) {
			this.solvePriorityConflicts();
		} else if (this.conflictStrategy == Place.CONFLICTHANDLING_PROB && hasProbabilityConflicts()) {
			this.solveProbabilityConflicts();
		}
	}

	public void solvePriorityConflicts() {
		Collection<PNEdge> edges = this.getConflictingOutEdges();
		if (edges.size() > 1) {
			Iterator<PNEdge> it = edges.iterator();
			PNEdge bea;

			Set<Integer> goodSet = new HashSet<Integer>();
			for (int i = 1; i <= edges.size(); i++) {
				goodSet.add(i);
			}

			Set<PNEdge> set = new HashSet<PNEdge>();
			while (it.hasNext()) {
				bea = it.next();
				if (goodSet.contains(bea.getPriority())) {
					goodSet.remove(bea.getPriority());
				} else {
					set.add(bea);
				}
			}
			it = set.iterator();
			if (set.size() == goodSet.size()) {
				int prio = 1;
				while (it.hasNext()) {
					bea = it.next();
					prio = goodSet.iterator().next();
					bea.setPriority(prio);
					goodSet.remove(prio);
				}
			}
		}
	}

	public void solveProbabilityConflicts() {
		Collection<PNEdge> edges = this.getConflictingOutEdges();
		if (edges.size() > 1) {
			double sum = 0;
			Iterator<PNEdge> it = edges.iterator();
			PNEdge bea;
			while (it.hasNext()) {
				bea = it.next();
				sum += bea.getProbability();
			}
			if (sum != 1.0) {
				it = edges.iterator();
				while (it.hasNext()) {
					bea = it.next();
					bea.setProbability(bea.getProbability() / sum);
				}
			}
		}
	}
}
