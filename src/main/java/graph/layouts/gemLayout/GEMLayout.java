package graph.layouts.gemLayout;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.Graph;

/**
 * Java implementation of the gem 2D layout.
 * <p/>
 * The algorithm needs to get various sub-graphs and traversals. The recursive
 * nature of the algorithm is totally captured within those sub-graphs and
 * traversals. The main loop of the algorithm is then expressed using the
 * iterator feature, which makes it look like a simple flat iteration over
 * nodes.
 * 
 * @author David Duke
 * @author Hacked by Eytan Adar for Guess
 * @author Hacked by taubertj for OVTK2
 * @author extended by cbrinkro for static nodes
 */
public class GEMLayout extends AbstractLayout<BiologicalNodeAbstract, BiologicalEdgeAbstract> {
	private Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> graph;
	// all nodes in the graph
	private Collection<BiologicalNodeAbstract> nodes;
	// all edges in the graph
	private Collection<BiologicalEdgeAbstract> edges;
	private GEMLayoutConfig layoutConfig;
	// static nodes which keep position
	private Map<BiologicalNodeAbstract, Point2D> sNodes = new HashMap<>();

	/**
	 * Required for compatibility to OVTK2lite
	 */
	public GEMLayout(Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g) {
		this(g, new HashMap<>());
	}

	public GEMLayout(Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g,
			Map<BiologicalNodeAbstract, Point2D> staticNodes) {
		super(g);
		if (staticNodes != null) {
			this.sNodes = staticNodes;
		}
	}

	public void setGraph(Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g) {
		this.graph = g;
	}

	/**
	 * Random function returns a random int value.
	 */
	private int rand() {
		return (int) (layoutConfig.rand.nextDouble() * Integer.MAX_VALUE);
	}

	/**
	 * Randomize selection of nodes.
	 * 
	 * @return node id
	 */
	private int select() {
		if (layoutConfig.iteration == 0) {
			layoutConfig.map = new int[layoutConfig.nodeCount];
			for (int i = 0; i < layoutConfig.nodeCount; i++)
				layoutConfig.map[i] = i;
		}
		int n = (int) (layoutConfig.nodeCount - layoutConfig.iteration % layoutConfig.nodeCount);
		int v = rand() % n; // was 1 + rand() % n due to numbering in GEM
		if (v == layoutConfig.nodeCount)
			v--;
		if (n == layoutConfig.nodeCount)
			n--;
		int u = layoutConfig.map[v];
		layoutConfig.map[v] = layoutConfig.map[n];
		layoutConfig.map[n] = u;
		return u;
	}

	/**
	 * Performs a BFS on the graph
	 *
	 * @return node id
	 */
	private int bfs(int root) {
		if (root >= 0) {
			layoutConfig.q = new PriorityQueue<>();
			if (!layoutConfig.gemProp[root].mark) { // root > 0
				for (int vi = 0; vi < layoutConfig.nodeCount; vi++) {
					layoutConfig.gemProp[vi].in = 0;
				}
			} else
				layoutConfig.gemProp[root].mark = true; // root = -root;
			layoutConfig.q.add(root);
			layoutConfig.gemProp[root].in = 1;
		}
		if (layoutConfig.q.isEmpty())
			return -1; // null
		int v = layoutConfig.q.poll();
		for (int ui : layoutConfig.adjacent.get(v)) {
			if (layoutConfig.gemProp[ui].in != 0) {
				layoutConfig.q.add(ui);
				layoutConfig.gemProp[ui].in = layoutConfig.gemProp[v].in + 1;
			}
		}

		return v;
	}

	/**
	 * Returns node for the graph center.
	 */
	private int graph_center() {
		final GEMLayoutConfig layoutConfig = GEMLayoutConfig.getInstance();
		int c = -1;
		int u = -1;
		int h = layoutConfig.nodeCount + 1;
		for (int w = 0; w < layoutConfig.nodeCount; w++) {
			int v = bfs(w);
			while (v >= 0 && layoutConfig.gemProp[v].in < h) {
				u = v;
				v = bfs(-1); // null
			}
			if (u < 0) {
				System.err.println("THERE IS AN ERROR!! u = " + u);
				// return 0;
			}
			GemP p = layoutConfig.gemProp[u];
			if (p.in < h) {
				h = p.in;
				c = w;
			}
		}
		return c;
	}

	/**
	 * Initialize properties of nodes.
	 * 
	 * @param starttemp given start temperature
	 */
	private void vertexdata_init(float starttemp) {
		layoutConfig.temperature = 0;
		layoutConfig.centerX = layoutConfig.centerY = 0;
		for (int v = 0; v < layoutConfig.nodeCount; v++) {
			GemP p = layoutConfig.gemProp[v];
			p.heat = starttemp * layoutConfig.ELEN;
			layoutConfig.temperature += p.heat * p.heat;
			p.iX = p.iY = 0;
			p.dir = 0;
			p.mass = 1 + layoutConfig.gemProp[v].mass / 3;
			layoutConfig.centerX += p.x;
			layoutConfig.centerY += p.y;
		}
	}

	/*
	 * INSERT code from GEM
	 */
	private int[] i_impulse(int v) {
		GemP p = layoutConfig.gemProp[v];
		int pX = p.x;
		int pY = p.y;
		int n = (int) (layoutConfig.i_shake * layoutConfig.ELEN);
		int iX = rand() % (2 * n + 1) - n;
		int iY = rand() % (2 * n + 1) - n;
		iX += (layoutConfig.centerX / layoutConfig.nodeCount - pX) * p.mass * layoutConfig.i_gravity;
		iY += (layoutConfig.centerY / layoutConfig.nodeCount - pY) * p.mass * layoutConfig.i_gravity;

		for (int u = 0; u < layoutConfig.nodeCount; u++) {
			GemP q = layoutConfig.gemProp[u];
			if (q.in > 0) {
				int dX = pX - q.x;
				int dY = pY - q.y;
				n = dX * dX + dY * dY;
				if (n > 0) {
					iX += dX * layoutConfig.ELENSQR / n;
					iY += dY * layoutConfig.ELENSQR / n;
				}
			}
		}
		for (int u : layoutConfig.adjacent.get(v)) {
			GemP q = layoutConfig.gemProp[u];
			if (q.in > 0) {
				int dX = pX - q.x;
				int dY = pY - q.y;
				n = (int) ((dX * dX + dY * dY) / p.mass);
				n = Math.min(n, layoutConfig.MAXATTRACT);
				iX -= dX * n / layoutConfig.ELENSQR;
				iY -= dY * n / layoutConfig.ELENSQR;
			}
		}
		return new int[] { iX, iY };
	}

	private void insert() {
		layoutConfig = GEMLayoutConfig.getInstance();

		vertexdata_init(layoutConfig.i_starttemp);

		layoutConfig.oscillation = layoutConfig.i_oscillation;
		layoutConfig.rotation = layoutConfig.i_rotation;
		layoutConfig.maxtemp = (int) (layoutConfig.i_maxtemp * layoutConfig.ELEN);

		int v = graph_center();

		for (int ui = 0; ui < layoutConfig.nodeCount; ui++) {
			layoutConfig.gemProp[ui].in = 0;
		}

		layoutConfig.gemProp[v].in = -1;

		boolean first = true;
		for (int i = 0; i < layoutConfig.nodeCount; i++) {
			int d = 0;
			for (int u = 0; u < layoutConfig.nodeCount; u++) {
				if (layoutConfig.gemProp[u].in < d) {
					d = layoutConfig.gemProp[u].in;
					v = u;
				}
			}
			layoutConfig.gemProp[v].in = 1;

			for (int u : layoutConfig.adjacent.get(v)) {
				if (layoutConfig.gemProp[u].in <= 0)
					layoutConfig.gemProp[u].in--;
			}
			GemP p = layoutConfig.gemProp[v];
			if (!sNodes.containsKey(layoutConfig.invmap[v])) {
				p.x = p.y = 0;
			}

			if (first) {
				first = false;
			} else {
				d = 0;
				p = layoutConfig.gemProp[v];
				for (int w : layoutConfig.adjacent.get(v)) {
					GemP q = layoutConfig.gemProp[w];
					if (q.in > 0) {
						if (!sNodes.containsKey(layoutConfig.invmap[v])) {
							p.x += q.x;
							p.y += q.y;
							d++;
						}
					}
				}
				if (d > 1) {
					if (!sNodes.containsKey(layoutConfig.invmap[v])) {
						p.x /= d;
						p.y /= d;
					}
				}
				d = 0;
				while ((d++ < layoutConfig.i_maxiter) && (p.heat > layoutConfig.i_finaltemp * layoutConfig.ELEN)) {
					int[] i_impulse = i_impulse(v);
					displace(v, i_impulse[0], i_impulse[1]);
				}
			}
		}
	}

	private void displace(int v, int iX, int iY) {
		if (iX != 0 || iY != 0) {
			int n = Math.max(Math.abs(iX), Math.abs(iY)) / 16384;
			if (n > 1) {
				iX /= n;
				iY /= n;
			}
			GemP p = layoutConfig.gemProp[v];
			int t = (int) p.heat;
			n = (int) Math.sqrt(iX * iX + iY * iY);
			iX = iX * t / n;
			iY = iY * t / n;
			if (!sNodes.containsKey(layoutConfig.invmap[v])) {
				p.x += iX;
				p.y += iY;
			}
			layoutConfig.centerX += iX;
			layoutConfig.centerY += iY;
			// imp = &vi[v].imp;
			n = t * (int) Math.sqrt(p.iX * p.iX + p.iY * p.iY);
			if (n > 0) {
				layoutConfig.temperature -= t * t;
				t += t * layoutConfig.oscillation * (iX * p.iX + iY * p.iY) / n;
				t = (int) Math.min(t, layoutConfig.maxtemp);
				p.dir += layoutConfig.rotation * (iX * p.iY - iY * p.iX) / n;
				t -= t * Math.abs(p.dir) / layoutConfig.nodeCount;
				t = Math.max(t, 2);
				layoutConfig.temperature += t * t;
				p.heat = t;
			}
			p.iX = iX;
			p.iY = iY;
		}
	}

	private void a_round() {

		for (int i = 0; i < layoutConfig.nodeCount; i++) {
			int v = select();

			GemP p = layoutConfig.gemProp[v];

			int pX = p.x;
			int pY = p.y;

			int n = (int) (layoutConfig.a_shake * layoutConfig.ELEN);
			int iX = rand() % (2 * n + 1) - n;
			int iY = rand() % (2 * n + 1) - n;
			iX += (layoutConfig.centerX / layoutConfig.nodeCount - pX) * p.mass * layoutConfig.a_gravity;
			iY += (layoutConfig.centerY / layoutConfig.nodeCount - pY) * p.mass * layoutConfig.a_gravity;
			for (int u = 0; u < layoutConfig.nodeCount; u++) {
				GemP q = layoutConfig.gemProp[u];
				int dX = pX - q.x;
				int dY = pY - q.y;
				n = dX * dX + dY * dY;
				if (n > 0) {
					iX += dX * layoutConfig.ELENSQR / n;
					iY += dY * layoutConfig.ELENSQR / n;
				}
			}
			for (int u : layoutConfig.adjacent.get(v)) {
				GemP q = layoutConfig.gemProp[u];
				int dX = pX - q.x;
				int dY = pY - q.y;
				n = (int) ((dX * dX + dY * dY) / p.mass);
				n = Math.min(n, layoutConfig.MAXATTRACT);
				iX -= dX * n / layoutConfig.ELENSQR;
				iY -= dY * n / layoutConfig.ELENSQR;
			}
			displace(v, iX, iY);
			layoutConfig.iteration++;
		}
	}

	private void arrange() {
		vertexdata_init(layoutConfig.a_starttemp);

		layoutConfig.oscillation = layoutConfig.a_oscillation;
		layoutConfig.rotation = layoutConfig.a_rotation;
		layoutConfig.maxtemp = (int) (layoutConfig.a_maxtemp * layoutConfig.ELEN);
		long stop_temperature = (int) (layoutConfig.a_finaltemp * layoutConfig.a_finaltemp * layoutConfig.ELENSQR
				* layoutConfig.nodeCount);
		long stop_iteration = layoutConfig.a_maxiter * layoutConfig.nodeCount * layoutConfig.nodeCount;
		layoutConfig.iteration = 0;
		long temp1 = 0;
		long temp2 = -1;

		// cut-off only performed if node size is greater equal given min node size and
		// factor > 0
		boolean doCheck = layoutConfig.minNodesCutOff > 0 && layoutConfig.minNodesCutOff <= layoutConfig.nodeCount
				&& layoutConfig.factorCutOffCheck > 0;

		// System.out.println("min Nodes: " + layoutConfig.minNodesCutOff);
		// System.out.println("factor: " + layoutConfig.factorCutOffCheck);
		// System.out.println("do check: " + doCheck);
		int mod = (layoutConfig.nodeCount * layoutConfig.factorCutOffCheck);
		while (layoutConfig.temperature > stop_temperature && layoutConfig.iteration < stop_iteration) {

			if (doCheck) {
				if (layoutConfig.iteration % (mod * 3) == 0) {
					temp1 = layoutConfig.temperature;
				}
				if ((layoutConfig.iteration + 2 * mod) % (mod * 3) == 0) {
					temp2 = layoutConfig.temperature;
				}
				if ((layoutConfig.iteration + mod) % (mod * 3) == 0) {
					if ((temp1 == temp2) && (temp1 == layoutConfig.temperature)) {
						double d = (double) layoutConfig.iteration / (double) stop_iteration;
						double round = Math.round(d * 10000) / 100.0;
						System.out.println("GEM Layout converged after " + round + "%, " + layoutConfig.iteration
								+ " out of " + stop_iteration + " iterations!");
						break;
					}
				}
			}
			// com.hp.hpl.guess.ui.StatusBar.setValue((int)stop_iteration, (int)iteration);
			a_round();
		}
		// com.hp.hpl.guess.ui.StatusBar.setValue(100,0);
	}

	/*
	 * Optimisation Code
	 */
	private int[] EVdistance(int thisNode, int thatNode, int v) {
		GemP thisGP = layoutConfig.gemProp[thisNode];
		GemP thatGP = layoutConfig.gemProp[thatNode];
		GemP nodeGP = layoutConfig.gemProp[v];

		int aX = thisGP.x;
		int aY = thisGP.y;
		int bX = thatGP.x;
		int bY = thatGP.y;
		int cX = nodeGP.x;
		int cY = nodeGP.y;

		bX -= aX;
		bY -= aY; /* b' = b - a */
		long m = bX * (cX - aX) + bY * (cY - aY); /* m = <b'|c-a> = <b-a|c-a> */
		long n = bX * bX + bY * bY; /* n = |b'|^2 = |b-a|^2 */
		if (m < 0)
			m = 0;
		if (m > n)
			m = n = 1;
		if ((m >> 17) > 0) { /* prevent integer overflow */
			n /= m >> 16;
			m /= m >> 16;
		}
		if (n != 0) {
			aX += (int) (bX * m / n); /* a' = m/n b' = a + m/n (b-a) */
			aY += (int) (bY * m / n);
		}
		return new int[] { aX, aY };
	}

	private int[] o_impulse(int v) {
		GemP p = layoutConfig.gemProp[v];
		int pX = p.x;
		int pY = p.y;

		int n = (int) (layoutConfig.o_shake * layoutConfig.ELEN);
		int iX = rand() % (2 * n + 1) - n;
		int iY = rand() % (2 * n + 1) - n;
		iX += (layoutConfig.centerX / layoutConfig.nodeCount - pX) * p.mass * layoutConfig.o_gravity;
		iY += (layoutConfig.centerY / layoutConfig.nodeCount - pY) * p.mass * layoutConfig.o_gravity;

		for (BiologicalEdgeAbstract e : edges) {
			// Pair ends = e.getEndpoints();
			int u = layoutConfig.nodeNumbers.get(graph.getEndpoints(e).getFirst());
			int w = layoutConfig.nodeNumbers.get(graph.getEndpoints(e).getSecond());
			if (u != v && w != v) {
				GemP up = layoutConfig.gemProp[u];
				GemP wp = layoutConfig.gemProp[w];
				int dX = (up.x + wp.x) / 2 - pX;
				int dY = (up.y + wp.y) / 2 - pY;
				n = dX * dX + dY * dY;
				if (n < 8 * layoutConfig.ELENSQR) {
					int[] evdist = EVdistance(u, w, v); // source, dest, vert
					dX = evdist[0];
					dY = evdist[1];
					dX -= pX;
					dY -= pY;
					n = dX * dX + dY * dY;
				}
				if (n > 0) {
					iX -= dX * layoutConfig.ELENSQR / n;
					iY -= dY * layoutConfig.ELENSQR / n;
				}
			} else {
				if (u == v)
					u = w;
				GemP up = layoutConfig.gemProp[u];
				int dX = pX - up.x;
				int dY = pY - up.y;
				n = (int) ((dX * dX + dY * dY) / p.mass);
				n = Math.min(n, layoutConfig.MAXATTRACT);
				iX -= dX * n / layoutConfig.ELENSQR;
				iY -= dY * n / layoutConfig.ELENSQR;
			}
		}
		return new int[] { iX, iY };
	}

	private void o_round() {
		for (int i = 0; i < layoutConfig.nodeCount; i++) {
			int v = select();
			int[] o_impulse = o_impulse(v);
			displace(v, o_impulse[0], o_impulse[1]);
			layoutConfig.iteration++;
		}
	}

	private void optimize() {
		vertexdata_init(layoutConfig.o_starttemp);
		layoutConfig.oscillation = layoutConfig.o_oscillation;
		layoutConfig.rotation = layoutConfig.o_rotation;
		layoutConfig.maxtemp = (int) (layoutConfig.o_maxtemp * layoutConfig.ELEN);
		long stop_temperature = (int) (layoutConfig.o_finaltemp * layoutConfig.o_finaltemp * layoutConfig.ELENSQR
				* layoutConfig.nodeCount);
		long stop_iteration = layoutConfig.o_maxiter * layoutConfig.nodeCount * layoutConfig.nodeCount;

		// System.out.print( "optimise phase -- temp " );
		// System.out.print( stop_temperature + " iter ");
		// System.out.println ( stop_iteration );

		while (layoutConfig.temperature > stop_temperature && layoutConfig.iteration < stop_iteration) {
			o_round();
			if ((layoutConfig.iteration % 20000) == 0) {
				// System.out.println( iteration + "\t" + temperature );
			}
		}
	}

	/**
	 * Runs the layout.
	 */
	public void initialize() {
		// long startTime, endTime;

		// startTime = System.currentTimeMillis();

		// GEMLayoutConfig.clustered = GEMLayoutConfig.boxCluster.isSelected();

		// if (GEMLayoutConfig.clustered) {
		// runClustered(clusterGraph(getGraph()));
		// } else {
		graph = getGraph();

		runNormal();

		// set location of nodes in graph
		for (int i = 0; i < layoutConfig.nodeCount; i++) {
			GemP p = layoutConfig.gemProp[i];
			BiologicalNodeAbstract n = layoutConfig.invmap[i];

			// if (vNodes.contains(n)) {
			Point2D coord = apply(n);// getCoordinates(n);
			coord.setLocation(p.x, p.y);
			// }
		}
		// }
		// endTime = System.currentTimeMillis();

		// System.out.println("Took: " + (endTime - startTime) + "msec");
	}

	private Collection<BiologicalNodeAbstract> getNeighbours(BiologicalNodeAbstract n) {
		Collection<BiologicalNodeAbstract> neighbours = new ArrayList<BiologicalNodeAbstract>();
		for (BiologicalEdgeAbstract o : getGraph().getEdges()) {
			BiologicalNodeAbstract first = graph.getEndpoints(o).getFirst();
			BiologicalNodeAbstract second = graph.getEndpoints(o).getSecond();
			if (n.equals(first)) {
				neighbours.add(second);
			}
			if (n.equals(second)) {
				neighbours.add(first);
			}
		}
		return neighbours;
	}

	/**
	 * Normal bubble like GEM layout.
	 */
	private void runNormal() {
		// System.out.println("run normal");
		nodes = graph.getVertices();

		edges = graph.getEdges();

		layoutConfig = GEMLayoutConfig.getInstance();

		layoutConfig.nodeCount = nodes.size();

		layoutConfig.gemProp = new GemP[layoutConfig.nodeCount];
		layoutConfig.invmap = new BiologicalNodeAbstract[layoutConfig.nodeCount];
		layoutConfig.adjacent = new HashMap<>(layoutConfig.nodeCount);
		layoutConfig.nodeNumbers = new HashMap<>();

		// initialize node lists and gemProp
		Iterator<BiologicalNodeAbstract> nodeSet = nodes.iterator();
		for (int i = 0; nodeSet.hasNext(); i++) {
			BiologicalNodeAbstract n = nodeSet.next();
			Collection<BiologicalEdgeAbstract> edges = new ArrayList<>();
			for (BiologicalEdgeAbstract o : graph.getEdges()) {
				// if (o instanceof DirectedEdge) {
				// DirectedEdge e = (DirectedEdge) o;
				if (graph.getEndpoints(o).getFirst().equals(n)) {
					edges.add(o);
				}
				// }
			}

			// graph.getOutEdges(n)
			layoutConfig.gemProp[i] = new GemP(edges.size());
			if (sNodes.containsKey(n)) {
				layoutConfig.gemProp[i].x = (int) sNodes.get(n).getX();
				layoutConfig.gemProp[i].y = (int) sNodes.get(n).getY();
			} else {
				layoutConfig.gemProp[i].x = 0;
				layoutConfig.gemProp[i].y = 0;
			}

			layoutConfig.invmap[i] = n;
			layoutConfig.nodeNumbers.put(n, i);
		}

		// fill adjacent lists
		for (int i = 0; i < layoutConfig.nodeCount; i++) {
			// graph.getNeighbours(invmap[i]);
			Collection<BiologicalNodeAbstract> neighbors = getNeighbours(layoutConfig.invmap[i]);
			layoutConfig.adjacent.put(i, new ArrayList<>(neighbors.size()));
			for (BiologicalNodeAbstract n : neighbors) {
				layoutConfig.adjacent.get(i).add(layoutConfig.nodeNumbers.get(n));
			}
		}

		// System.out.println("run normal end");

		// actual layout
		if (layoutConfig.i_finaltemp < layoutConfig.i_starttemp)
			insert();
		// System.out.println("insert end");
		if (layoutConfig.a_finaltemp < layoutConfig.a_starttemp)
			arrange();
		// System.out.println("arrange end");
		if (layoutConfig.o_finaltemp < layoutConfig.o_starttemp)
			optimize();
		// System.out.println("optimize end");
	}

	/**
	 * Run the layout again.
	 */
	public void reset() {
		initialize();
	}

	public boolean incrementsAreDone() {
		return false;
	}

	public boolean isIncremental() {
		return false;
	}
}