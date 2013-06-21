package graph.layouts.gemLayout;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayPriorityQueue;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

import cern.colt.list.IntArrayList;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.Graph;
/*import edu.uci.ics.jung.graph.DirectedEdge;
 import edu.uci.ics.jung.graph.Edge;
 import edu.uci.ics.jung.graph.Graph;
 import edu.uci.ics.jung.graph.Vertex;
 import edu.uci.ics.jung.utils.Pair;
 import edu.uci.ics.jung.visualization.AbstractLayout;*/
import graph.layouts.gemLayout.GEMLayoutConfig.GemP;

/**
 * Java implementation of the gem 2D layout. <br>
 * The algorithm needs to get various subgraphs and traversals. The recursive
 * nature of the algorithm is totally captured within those subgraphs and
 * traversals. The main loop of the algorithm is then expressed using the
 * iterator feature, which makes it look like a simple flat iteration over
 * nodes.
 * 
 * @author David Duke
 * @author Hacked by Eytan Adar for Guess
 * @author Hacked by taubertj for OVTK2
 */
public class GEMLayout extends
		AbstractLayout<BiologicalNodeAbstract, BiologicalEdgeAbstract> {

	// JUNG wrapped ONDEX graph
	private Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> graph;

	// all nodes in the graph
	private Collection<BiologicalNodeAbstract> nodes;

	// all edges in the graph
	private Collection<BiologicalEdgeAbstract> edges;

	/**
	 * Required for compatibility to OVTK2lite
	 * 
	 * @param aog
	 *            ONDEXGraph
	 * @param jung
	 *            ONDEXSparseGraph
	 */
	public GEMLayout(Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g) {
		super(g);
	}

	public void setGraph(Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g) {
		this.graph = g;
	}

	/**
	 * Random function returns an random int value.
	 * 
	 * @return int
	 */
	private int rand() {
		return (int) (GEMLayoutConfig.rand.nextDouble() * Integer.MAX_VALUE);
	}

	/**
	 * Randomize selection of nodes.
	 * 
	 * @return node id
	 */
	private int select() {

		int u;
		int n, v;

		if (GEMLayoutConfig.iteration == 0) {
			// System.out.print( "New map for " + nodeCount );
			GEMLayoutConfig.map = new int[GEMLayoutConfig.nodeCount];
			for (int i = 0; i < GEMLayoutConfig.nodeCount; i++)
				GEMLayoutConfig.map[i] = i;
		}
		n = (int) (GEMLayoutConfig.nodeCount - GEMLayoutConfig.iteration
				% GEMLayoutConfig.nodeCount);
		v = rand() % n; // was 1 + rand() % n due to numbering in GEM
		if (v == GEMLayoutConfig.nodeCount)
			v--;
		if (n == GEMLayoutConfig.nodeCount)
			n--;
		// System.out.println( "Access n = " + n + " v = " + v );
		u = GEMLayoutConfig.map[v];
		GEMLayoutConfig.map[v] = GEMLayoutConfig.map[n];
		GEMLayoutConfig.map[n] = u;
		return u;
	}

	/**
	 * Performs a BFS on the graph
	 * 
	 * @param root
	 *            int
	 * @return node id
	 */
	private int bfs(int root) {

		it.unimi.dsi.fastutil.ints.IntIterator nodeSet;
		int v, ui;

		if (root >= 0) {
			GEMLayoutConfig.q = new IntArrayPriorityQueue();
			if (!GEMLayoutConfig.gemProp[root].mark) { // root > 0
				for (int vi = 0; vi < GEMLayoutConfig.nodeCount; vi++) {
					GEMLayoutConfig.gemProp[vi].in = 0;
				}
			} else
				GEMLayoutConfig.gemProp[root].mark = true; // root = -root;
			GEMLayoutConfig.q.enqueue(root);
			GEMLayoutConfig.gemProp[root].in = 1;
		}
		if (GEMLayoutConfig.q.size() == 0)
			return -1; // null
		v = GEMLayoutConfig.q.dequeueInt();

		nodeSet = IntIterators.asIntIterator(GEMLayoutConfig.adjacent.get(v)
				.toList().iterator());
		while (nodeSet.hasNext()) {
			ui = nodeSet.nextInt();
			if (GEMLayoutConfig.gemProp[ui].in != 0) {
				GEMLayoutConfig.q.enqueue(ui);
				GEMLayoutConfig.gemProp[ui].in = GEMLayoutConfig.gemProp[v].in + 1;
			}
		}

		return v;
	}

	/**
	 * Returns node for the graph center.
	 * 
	 * @return int
	 */
	private int graph_center() {
		GEMLayoutConfig.GemP p;
		int c, u, v, w; // nodes
		int h;

		c = -1; // for a contented compiler.
		u = -1;

		h = GEMLayoutConfig.nodeCount + 1;

		for (w = 0; w < GEMLayoutConfig.nodeCount; w++) {
			v = bfs(w);
			while (v >= 0 && GEMLayoutConfig.gemProp[v].in < h) {
				u = v;
				v = bfs(-1); // null
			}

			if (u < 0) {
				System.err.println("THERE IS AN ERROR!! u = " + u);
				// return 0;
			}

			p = GEMLayoutConfig.gemProp[u];
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
	 * @param starttemp
	 *            given start temperature
	 */
	private void vertexdata_init(float starttemp) {

		GEMLayoutConfig.temperature = 0;
		GEMLayoutConfig.centerX = GEMLayoutConfig.centerY = 0;

		for (int v = 0; v < GEMLayoutConfig.nodeCount; v++) {
			GEMLayoutConfig.GemP p = GEMLayoutConfig.gemProp[v];
			p.heat = starttemp * GEMLayoutConfig.ELEN;
			GEMLayoutConfig.temperature += p.heat * p.heat;
			p.iX = p.iY = 0;
			p.dir = 0;
			p.mass = 1 + GEMLayoutConfig.gemProp[v].mass / 3;
			GEMLayoutConfig.centerX += p.x;
			GEMLayoutConfig.centerY += p.y;
		}
	}

	/*
	 * INSERT code from GEM
	 */
	private int[] i_impulse(int v) {

		IntIterator nodeSet;

		int iX, iY, dX, dY, pX, pY;
		int n;
		GEMLayoutConfig.GemP p, q;

		p = GEMLayoutConfig.gemProp[v];
		pX = p.x;
		pY = p.y;

		n = (int) (GEMLayoutConfig.i_shake * GEMLayoutConfig.ELEN);
		iX = rand() % (2 * n + 1) - n;
		iY = rand() % (2 * n + 1) - n;
		iX += (GEMLayoutConfig.centerX / GEMLayoutConfig.nodeCount - pX)
				* p.mass * GEMLayoutConfig.i_gravity;
		iY += (GEMLayoutConfig.centerY / GEMLayoutConfig.nodeCount - pY)
				* p.mass * GEMLayoutConfig.i_gravity;

		for (int u = 0; u < GEMLayoutConfig.nodeCount; u++) {
			q = GEMLayoutConfig.gemProp[u];
			if (q.in > 0) {
				dX = pX - q.x;
				dY = pY - q.y;
				n = dX * dX + dY * dY;
				if (n > 0) {
					iX += dX * GEMLayoutConfig.ELENSQR / n;
					iY += dY * GEMLayoutConfig.ELENSQR / n;
				}
			}
		}
		nodeSet = IntIterators.asIntIterator(GEMLayoutConfig.adjacent.get(v)
				.toList().iterator());
		int u;
		while (nodeSet.hasNext()) {
			u = nodeSet.nextInt();
			q = GEMLayoutConfig.gemProp[u];
			if (q.in > 0) {
				dX = pX - q.x;
				dY = pY - q.y;
				n = (int) ((dX * dX + dY * dY) / p.mass);
				n = Math.min(n, GEMLayoutConfig.MAXATTRACT);
				iX -= dX * n / GEMLayoutConfig.ELENSQR;
				iY -= dY * n / GEMLayoutConfig.ELENSQR;
			}
		}

		return new int[] { iX, iY };
	}

	private void insert() {

		IntIterator nodeSet2;
		GEMLayoutConfig.GemP p, q;
		int startNode;

		int v, w;

		int d;

		// System.out.println( "insert phase" );

		vertexdata_init(GEMLayoutConfig.i_starttemp);

		GEMLayoutConfig.oscillation = GEMLayoutConfig.i_oscillation;
		GEMLayoutConfig.rotation = GEMLayoutConfig.i_rotation;
		GEMLayoutConfig.maxtemp = (int) (GEMLayoutConfig.i_maxtemp * GEMLayoutConfig.ELEN);

		v = graph_center();

		for (int ui = 0; ui < GEMLayoutConfig.nodeCount; ui++) {
			GEMLayoutConfig.gemProp[ui].in = 0;
		}

		GEMLayoutConfig.gemProp[v].in = -1;

		startNode = -1;
		for (int i = 0; i < GEMLayoutConfig.nodeCount; i++) {
			d = 0;
			for (int u = 0; u < GEMLayoutConfig.nodeCount; u++) {
				if (GEMLayoutConfig.gemProp[u].in < d) {
					d = GEMLayoutConfig.gemProp[u].in;
					v = u;
				}
			}
			GEMLayoutConfig.gemProp[v].in = 1;

			nodeSet2 = IntIterators.asIntIterator(GEMLayoutConfig.adjacent
					.get(v).toList().iterator());
			int u;
			while (nodeSet2.hasNext()) {
				u = nodeSet2.nextInt();
				if (GEMLayoutConfig.gemProp[u].in <= 0)
					GEMLayoutConfig.gemProp[u].in--;
			}
			p = GEMLayoutConfig.gemProp[v];
			p.x = p.y = 0;

			if (startNode >= 0) {
				d = 0;
				p = GEMLayoutConfig.gemProp[v];
				nodeSet2 = IntIterators.asIntIterator(GEMLayoutConfig.adjacent
						.get(v).toList().iterator());
				while (nodeSet2.hasNext()) {
					w = nodeSet2.nextInt();
					q = GEMLayoutConfig.gemProp[w];
					if (q.in > 0) {
						p.x += q.x;
						p.y += q.y;
						d++;
					}
				}
				if (d > 1) {
					p.x /= d;
					p.y /= d;
				}
				d = 0;
				while ((d++ < GEMLayoutConfig.i_maxiter)
						&& (p.heat > GEMLayoutConfig.i_finaltemp
								* GEMLayoutConfig.ELEN)) {
					int[] i_impulse = i_impulse(v);
					displace(v, i_impulse[0], i_impulse[1]);
				}

			} else {
				startNode = i;
			}
		}
	}

	private void displace(int v, int iX, int iY) {

		int t;
		int n;
		GemP p;

		if (iX != 0 || iY != 0) {
			n = Math.max(Math.abs(iX), Math.abs(iY)) / 16384;
			if (n > 1) {
				iX /= n;
				iY /= n;
			}
			p = GEMLayoutConfig.gemProp[v];
			t = (int) p.heat;
			n = (int) Math.sqrt(iX * iX + iY * iY);
			iX = iX * t / n;
			iY = iY * t / n;
			p.x += iX;
			p.y += iY;
			GEMLayoutConfig.centerX += iX;
			GEMLayoutConfig.centerY += iY;
			// imp = &vi[v].imp;
			n = t * (int) Math.sqrt(p.iX * p.iX + p.iY * p.iY);
			if (n > 0) {
				GEMLayoutConfig.temperature -= t * t;
				t += t * GEMLayoutConfig.oscillation * (iX * p.iX + iY * p.iY)
						/ n;
				t = (int) Math.min(t, GEMLayoutConfig.maxtemp);
				p.dir += GEMLayoutConfig.rotation * (iX * p.iY - iY * p.iX) / n;
				t -= t * Math.abs(p.dir) / GEMLayoutConfig.nodeCount;
				t = Math.max(t, 2);
				GEMLayoutConfig.temperature += t * t;
				p.heat = t;
			}
			p.iX = iX;
			p.iY = iY;
		}
	}

	private void a_round() {

		IntIterator nodeSet;
		int v;

		int iX, iY, dX, dY;
		int n;
		int pX, pY;
		GemP p, q;

		for (int i = 0; i < GEMLayoutConfig.nodeCount; i++) {
			v = select();
			p = GEMLayoutConfig.gemProp[v];

			pX = p.x;
			pY = p.y;

			n = (int) (GEMLayoutConfig.a_shake * GEMLayoutConfig.ELEN);
			iX = rand() % (2 * n + 1) - n;
			iY = rand() % (2 * n + 1) - n;
			iX += (GEMLayoutConfig.centerX / GEMLayoutConfig.nodeCount - pX)
					* p.mass * GEMLayoutConfig.a_gravity;
			iY += (GEMLayoutConfig.centerY / GEMLayoutConfig.nodeCount - pY)
					* p.mass * GEMLayoutConfig.a_gravity;

			for (int u = 0; u < GEMLayoutConfig.nodeCount; u++) {
				q = GEMLayoutConfig.gemProp[u];
				dX = pX - q.x;
				dY = pY - q.y;
				n = dX * dX + dY * dY;
				if (n > 0) {
					iX += dX * GEMLayoutConfig.ELENSQR / n;
					iY += dY * GEMLayoutConfig.ELENSQR / n;
				}
			}
			nodeSet = IntIterators.asIntIterator(GEMLayoutConfig.adjacent
					.get(v).toList().iterator());
			int u;
			while (nodeSet.hasNext()) {
				u = nodeSet.nextInt();
				q = GEMLayoutConfig.gemProp[u];
				dX = pX - q.x;
				dY = pY - q.y;
				n = (int) ((dX * dX + dY * dY) / p.mass);
				n = Math.min(n, GEMLayoutConfig.MAXATTRACT);
				iX -= dX * n / GEMLayoutConfig.ELENSQR;
				iY -= dY * n / GEMLayoutConfig.ELENSQR;
			}
			displace(v, iX, iY);
			GEMLayoutConfig.iteration++;
		}
	}

	private void arrange() {

		long stop_temperature;
		long stop_iteration;

		vertexdata_init(GEMLayoutConfig.a_starttemp);

		GEMLayoutConfig.oscillation = GEMLayoutConfig.a_oscillation;
		GEMLayoutConfig.rotation = GEMLayoutConfig.a_rotation;
		GEMLayoutConfig.maxtemp = (int) (GEMLayoutConfig.a_maxtemp * GEMLayoutConfig.ELEN);
		stop_temperature = (int) (GEMLayoutConfig.a_finaltemp
				* GEMLayoutConfig.a_finaltemp * GEMLayoutConfig.ELENSQR * GEMLayoutConfig.nodeCount);
		stop_iteration = GEMLayoutConfig.a_maxiter * GEMLayoutConfig.nodeCount
				* GEMLayoutConfig.nodeCount;
		GEMLayoutConfig.iteration = 0;

		// System.out.print( "arrange phase -- temp " );
		// System.out.print( stop_temperature + " iter ");
		// System.out.println ( stop_iteration );

		while (GEMLayoutConfig.temperature > stop_temperature
				&& GEMLayoutConfig.iteration < stop_iteration) {
			// com.hp.hpl.guess.ui.StatusBar.setValue((int)stop_iteration,
			// (int)iteration);
			a_round();
		}
		// com.hp.hpl.guess.ui.StatusBar.setValue(100,0);
	}

	/*
	 * Optimisation Code
	 */
	private int[] EVdistance(int thisNode, int thatNode, int v) {

		GemP thisGP = GEMLayoutConfig.gemProp[thisNode];
		GemP thatGP = GEMLayoutConfig.gemProp[thatNode];
		GemP nodeGP = GEMLayoutConfig.gemProp[v];

		int aX = thisGP.x;
		int aY = thisGP.y;
		int bX = thatGP.x;
		int bY = thatGP.y;
		int cX = nodeGP.x;
		int cY = nodeGP.y;

		long m, n;

		bX -= aX;
		bY -= aY; /* b' = b - a */
		m = bX * (cX - aX) + bY * (cY - aY); /* m = <b'|c-a> = <b-a|c-a> */
		n = bX * bX + bY * bY; /* n = |b'|^2 = |b-a|^2 */
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

		Iterator<BiologicalEdgeAbstract> edgeSet;
		int u, w;
		BiologicalEdgeAbstract e;
		int iX, iY, dX, dY;
		int n;
		GemP p, up, wp;
		int pX, pY;

		p = GEMLayoutConfig.gemProp[v];
		pX = p.x;
		pY = p.y;

		n = (int) (GEMLayoutConfig.o_shake * GEMLayoutConfig.ELEN);
		iX = rand() % (2 * n + 1) - n;
		iY = rand() % (2 * n + 1) - n;
		iX += (GEMLayoutConfig.centerX / GEMLayoutConfig.nodeCount - pX)
				* p.mass * GEMLayoutConfig.o_gravity;
		iY += (GEMLayoutConfig.centerY / GEMLayoutConfig.nodeCount - pY)
				* p.mass * GEMLayoutConfig.o_gravity;

		edgeSet = edges.iterator();
		while (edgeSet.hasNext()) {
			e = edgeSet.next();
			// Pair ends = e.getEndpoints();
			u = GEMLayoutConfig.nodeNumbers.getInt(e.getFrom());
			w = GEMLayoutConfig.nodeNumbers.getInt(e.getTo());
			if (u != v && w != v) {
				up = GEMLayoutConfig.gemProp[u];
				wp = GEMLayoutConfig.gemProp[w];
				dX = (up.x + wp.x) / 2 - pX;
				dY = (up.y + wp.y) / 2 - pY;
				n = dX * dX + dY * dY;
				if (n < 8 * GEMLayoutConfig.ELENSQR) {
					int[] evdist = EVdistance(u, w, v); // source, dest, vert
					dX = evdist[0];
					dY = evdist[1];
					dX -= pX;
					dY -= pY;
					n = dX * dX + dY * dY;
				}
				if (n > 0) {
					iX -= dX * GEMLayoutConfig.ELENSQR / n;
					iY -= dY * GEMLayoutConfig.ELENSQR / n;
				}
			} else {
				if (u == v)
					u = w;
				up = GEMLayoutConfig.gemProp[u];
				dX = pX - up.x;
				dY = pY - up.y;
				n = (int) ((dX * dX + dY * dY) / p.mass);
				n = Math.min(n, GEMLayoutConfig.MAXATTRACT);
				iX -= dX * n / GEMLayoutConfig.ELENSQR;
				iY -= dY * n / GEMLayoutConfig.ELENSQR;
			}
		}
		return new int[] { iX, iY };
	}

	private void o_round() {

		int v;
		for (int i = 0; i < GEMLayoutConfig.nodeCount; i++) {
			v = select();
			int[] o_impulse = o_impulse(v);
			displace(v, o_impulse[0], o_impulse[1]);
			GEMLayoutConfig.iteration++;
		}
	}

	private void optimize() {

		long stop_temperature;
		long stop_iteration;

		vertexdata_init(GEMLayoutConfig.o_starttemp);
		GEMLayoutConfig.oscillation = GEMLayoutConfig.o_oscillation;
		GEMLayoutConfig.rotation = GEMLayoutConfig.o_rotation;
		GEMLayoutConfig.maxtemp = (int) (GEMLayoutConfig.o_maxtemp * GEMLayoutConfig.ELEN);
		stop_temperature = (int) (GEMLayoutConfig.o_finaltemp
				* GEMLayoutConfig.o_finaltemp * GEMLayoutConfig.ELENSQR * GEMLayoutConfig.nodeCount);
		stop_iteration = GEMLayoutConfig.o_maxiter * GEMLayoutConfig.nodeCount
				* GEMLayoutConfig.nodeCount;

		// System.out.print( "optimise phase -- temp " );
		// System.out.print( stop_temperature + " iter ");
		// System.out.println ( stop_iteration );

		while (GEMLayoutConfig.temperature > stop_temperature
				&& GEMLayoutConfig.iteration < stop_iteration) {
			o_round();
			if ((GEMLayoutConfig.iteration % 20000) == 0) {
				// System.out.println( iteration + "\t" + temperature );
			}
		}
	}

	/**
	 * Runs the layout.
	 */
	public void initialize() {
		long startTime, endTime;

		startTime = System.currentTimeMillis();

		// GEMLayoutConfig.clustered = GEMLayoutConfig.boxCluster.isSelected();

		// if (GEMLayoutConfig.clustered) {
		// runClustered(clusterGraph(getGraph()));
		// } else {
		graph = getGraph();

		runNormal();

		// set location of nodes in graph
		for (int i = 0; i < GEMLayoutConfig.nodeCount; i++) {
			GemP p = GEMLayoutConfig.gemProp[i];
			BiologicalNodeAbstract n = GEMLayoutConfig.invmap[i];

			Point2D coord = transform(n);// getCoordinates(n);
			coord.setLocation(p.x, p.y);
		}
		// }
		endTime = System.currentTimeMillis();

		// System.out.println("Took: " + (endTime - startTime) + "msec");
	}

	private Collection<BiologicalNodeAbstract> getNeighbours(
			BiologicalNodeAbstract n) {
		Collection<BiologicalNodeAbstract> neighbours = new ArrayList<BiologicalNodeAbstract>();
		for (BiologicalEdgeAbstract o : getGraph().getEdges()) {
			BiologicalNodeAbstract first = o.getFrom();
			BiologicalNodeAbstract second = o.getTo();
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

		nodes = graph.getVertices();
		edges = graph.getEdges();

		GEMLayoutConfig.nodeCount = nodes.size();

		GEMLayoutConfig.gemProp = new GEMLayoutConfig.GemP[GEMLayoutConfig.nodeCount];
		GEMLayoutConfig.invmap = new BiologicalNodeAbstract[GEMLayoutConfig.nodeCount];
		GEMLayoutConfig.adjacent = new Int2ObjectOpenHashMap<IntArrayList>(
				GEMLayoutConfig.nodeCount);
		GEMLayoutConfig.nodeNumbers = new Object2IntOpenHashMap<BiologicalNodeAbstract>();

		// initialize node lists and gemProp
		Iterator<BiologicalNodeAbstract> nodeSet = nodes.iterator();
		for (int i = 0; nodeSet.hasNext(); i++) {
			BiologicalNodeAbstract n = nodeSet.next();
			Collection<BiologicalEdgeAbstract> edges = new ArrayList<BiologicalEdgeAbstract>();
			for (BiologicalEdgeAbstract o : graph.getEdges()) {
				// if (o instanceof DirectedEdge) {
				// DirectedEdge e = (DirectedEdge) o;
				if (o.getFrom().equals(n)) {
					edges.add(o);
				}
				// }
			}

			// graph.getOutEdges(n)
			GEMLayoutConfig.gemProp[i] = GEMLayoutConfig.getInstance().new GemP(
					edges.size());
			GEMLayoutConfig.invmap[i] = n;
			GEMLayoutConfig.nodeNumbers.put(n, i);
		}

		// fill adjacent lists
		Collection<BiologicalNodeAbstract> neighbors;
		for (int i = 0; i < GEMLayoutConfig.nodeCount; i++) {
			neighbors = getNeighbours(GEMLayoutConfig.invmap[i]); // graph.getNeighbours(invmap[i]);
			GEMLayoutConfig.adjacent.put(i, new IntArrayList(neighbors.size()));
			for (BiologicalNodeAbstract n : neighbors) {
				GEMLayoutConfig.adjacent.get(i).add(
						GEMLayoutConfig.nodeNumbers.getInt(n));
			}
		}

		// actual layout
		if (GEMLayoutConfig.i_finaltemp < GEMLayoutConfig.i_starttemp)
			insert();
		if (GEMLayoutConfig.a_finaltemp < GEMLayoutConfig.a_starttemp)
			arrange();
		if (GEMLayoutConfig.o_finaltemp < GEMLayoutConfig.o_starttemp)
			optimize();
	}

	/**
	 * Run the layout again.
	 */
	public void reset() {
		initialize();
	}

	public boolean incrementsAreDone() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isIncremental() {
		// TODO Auto-generated method stub
		return false;
	}

}