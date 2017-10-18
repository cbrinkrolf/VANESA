package graph.layouts.gemLayout;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import cern.colt.list.IntArrayList;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.Graph;
/*import edu.uci.ics.jung.graph.DirectedEdge;
 import edu.uci.ics.jung.graph.Edge;
 import edu.uci.ics.jung.graph.Graph;
 import edu.uci.ics.jung.graph.Vertex;
 import edu.uci.ics.jung.utils.Pair;
 import edu.uci.ics.jung.visualization.AbstractLayout;*/
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayPriorityQueue;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

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
public class GEMLayout<V, E> extends AbstractLayout<V, E> {

	// JUNG wrapped ONDEX graph
	private Graph<V, E> graph;

	// all nodes in the graph
	private Collection<V> nodes;

	// all edges in the graph
	private Collection<E> edges;

	private GEMLayoutConfig<V> layoutConfig;

	/**
	 * Required for compatibility to OVTK2lite
	 * 
	 * @param aog
	 *            ONDEXGraph
	 * @param jung
	 *            ONDEXSparseGraph
	 */
	public GEMLayout(Graph<V, E> g) {
		super(g);
	}

	public void setGraph(Graph<V, E> g) {
		this.graph = g;
	}

	/**
	 * Random function returns an random int value.
	 * 
	 * @return int
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

		int u;
		int n, v;

		if (layoutConfig.iteration == 0) {
			// System.out.print( "New map for " + nodeCount );
			layoutConfig.map = new int[layoutConfig.nodeCount];
			for (int i = 0; i < layoutConfig.nodeCount; i++)
				layoutConfig.map[i] = i;
		}
		n = (int) (layoutConfig.nodeCount - layoutConfig.iteration
				% layoutConfig.nodeCount);
		v = rand() % n; // was 1 + rand() % n due to numbering in GEM
		if (v == layoutConfig.nodeCount)
			v--;
		if (n == layoutConfig.nodeCount)
			n--;
		// System.out.println( "Access n = " + n + " v = " + v );
		u = layoutConfig.map[v];
		layoutConfig.map[v] = layoutConfig.map[n];
		layoutConfig.map[n] = u;
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
			layoutConfig.q = new IntArrayPriorityQueue();
			if (!layoutConfig.gemProp[root].mark) { // root > 0
				for (int vi = 0; vi < layoutConfig.nodeCount; vi++) {
					layoutConfig.gemProp[vi].in = 0;
				}
			} else
				layoutConfig.gemProp[root].mark = true; // root = -root;
			layoutConfig.q.enqueue(root);
			layoutConfig.gemProp[root].in = 1;
		}
		if (layoutConfig.q.size() == 0)
			return -1; // null
		v = layoutConfig.q.dequeueInt();

		nodeSet = IntIterators.asIntIterator(layoutConfig.adjacent.get(v)
				.toList().iterator());
		while (nodeSet.hasNext()) {
			ui = nodeSet.nextInt();
			if (layoutConfig.gemProp[ui].in != 0) {
				layoutConfig.q.enqueue(ui);
				layoutConfig.gemProp[ui].in = layoutConfig.gemProp[v].in + 1;
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
		GEMLayoutConfig<V> layoutConfig = GEMLayoutConfigSingleton.getInstance();

		GemP p;
		int c, u, v, w; // nodes
		int h;

		c = -1; // for a contented compiler.
		u = -1;

		h = layoutConfig.nodeCount + 1;

		for (w = 0; w < layoutConfig.nodeCount; w++) {
			v = bfs(w);
			while (v >= 0 && layoutConfig.gemProp[v].in < h) {
				u = v;
				v = bfs(-1); // null
			}

			if (u < 0) {
				System.err.println("THERE IS AN ERROR!! u = " + u);
				// return 0;
			}

			p = layoutConfig.gemProp[u];
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

		layoutConfig.temperature = 0;
		layoutConfig.centerX = layoutConfig.centerY = 0;

		GemP p;

		for (int v = 0; v < layoutConfig.nodeCount; v++) {
			p = layoutConfig.gemProp[v];
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

		IntIterator nodeSet;

		int iX, iY, dX, dY, pX, pY;
		int n;
		GemP p, q;

		p = layoutConfig.gemProp[v];
		pX = p.x;
		pY = p.y;

		n = (int) (layoutConfig.i_shake * layoutConfig.ELEN);
		iX = rand() % (2 * n + 1) - n;
		iY = rand() % (2 * n + 1) - n;
		iX += (layoutConfig.centerX / layoutConfig.nodeCount - pX) * p.mass
				* layoutConfig.i_gravity;
		iY += (layoutConfig.centerY / layoutConfig.nodeCount - pY) * p.mass
				* layoutConfig.i_gravity;

		for (int u = 0; u < layoutConfig.nodeCount; u++) {
			q = layoutConfig.gemProp[u];
			if (q.in > 0) {
				dX = pX - q.x;
				dY = pY - q.y;
				n = dX * dX + dY * dY;
				if (n > 0) {
					iX += dX * layoutConfig.ELENSQR / n;
					iY += dY * layoutConfig.ELENSQR / n;
				}
			}
		}
		nodeSet = IntIterators.asIntIterator(layoutConfig.adjacent.get(v)
				.toList().iterator());
		int u;
		while (nodeSet.hasNext()) {
			u = nodeSet.nextInt();
			q = layoutConfig.gemProp[u];
			if (q.in > 0) {
				dX = pX - q.x;
				dY = pY - q.y;
				n = (int) ((dX * dX + dY * dY) / p.mass);
				n = Math.min(n, layoutConfig.MAXATTRACT);
				iX -= dX * n / layoutConfig.ELENSQR;
				iY -= dY * n / layoutConfig.ELENSQR;
			}
		}

		return new int[] { iX, iY };
	}

	private void insert() {

		IntIterator nodeSet2;
		GemP p, q;
		int startNode;

		int v, w;

		int d;

		layoutConfig = GEMLayoutConfigSingleton.getInstance();
		// System.out.println( "insert phase" );

		vertexdata_init(layoutConfig.i_starttemp);

		layoutConfig.oscillation = layoutConfig.i_oscillation;
		layoutConfig.rotation = layoutConfig.i_rotation;
		layoutConfig.maxtemp = (int) (layoutConfig.i_maxtemp * layoutConfig.ELEN);

		v = graph_center();

		for (int ui = 0; ui < layoutConfig.nodeCount; ui++) {
			layoutConfig.gemProp[ui].in = 0;
		}

		layoutConfig.gemProp[v].in = -1;

		startNode = -1;
		for (int i = 0; i < layoutConfig.nodeCount; i++) {
			d = 0;
			for (int u = 0; u < layoutConfig.nodeCount; u++) {
				if (layoutConfig.gemProp[u].in < d) {
					d = layoutConfig.gemProp[u].in;
					v = u;
				}
			}
			layoutConfig.gemProp[v].in = 1;

			nodeSet2 = IntIterators.asIntIterator(layoutConfig.adjacent.get(v)
					.toList().iterator());
			int u;
			while (nodeSet2.hasNext()) {
				u = nodeSet2.nextInt();
				if (layoutConfig.gemProp[u].in <= 0)
					layoutConfig.gemProp[u].in--;
			}
			p = layoutConfig.gemProp[v];
			p.x = p.y = 0;

			if (startNode >= 0) {
				d = 0;
				p = layoutConfig.gemProp[v];
				nodeSet2 = IntIterators.asIntIterator(layoutConfig.adjacent
						.get(v).toList().iterator());
				while (nodeSet2.hasNext()) {
					w = nodeSet2.nextInt();
					q = layoutConfig.gemProp[w];
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
				while ((d++ < layoutConfig.i_maxiter)
						&& (p.heat > layoutConfig.i_finaltemp
								* layoutConfig.ELEN)) {
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
			p = layoutConfig.gemProp[v];
			t = (int) p.heat;
			n = (int) Math.sqrt(iX * iX + iY * iY);
			iX = iX * t / n;
			iY = iY * t / n;
			p.x += iX;
			p.y += iY;
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

		IntIterator nodeSet;
		int v;

		int iX, iY, dX, dY;
		int n;
		int pX, pY;
		GemP p, q;

		for (int i = 0; i < layoutConfig.nodeCount; i++) {
			v = select();
			p = layoutConfig.gemProp[v];

			pX = p.x;
			pY = p.y;

			n = (int) (layoutConfig.a_shake * layoutConfig.ELEN);
			iX = rand() % (2 * n + 1) - n;
			iY = rand() % (2 * n + 1) - n;
			iX += (layoutConfig.centerX / layoutConfig.nodeCount - pX) * p.mass
					* layoutConfig.a_gravity;
			iY += (layoutConfig.centerY / layoutConfig.nodeCount - pY) * p.mass
					* layoutConfig.a_gravity;

			for (int u = 0; u < layoutConfig.nodeCount; u++) {
				q = layoutConfig.gemProp[u];
				dX = pX - q.x;
				dY = pY - q.y;
				n = dX * dX + dY * dY;
				if (n > 0) {
					iX += dX * layoutConfig.ELENSQR / n;
					iY += dY * layoutConfig.ELENSQR / n;
				}
			}
			nodeSet = IntIterators.asIntIterator(layoutConfig.adjacent.get(v)
					.toList().iterator());
			int u;
			while (nodeSet.hasNext()) {
				u = nodeSet.nextInt();
				q = layoutConfig.gemProp[u];
				dX = pX - q.x;
				dY = pY - q.y;
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

		long stop_temperature;
		long stop_iteration;

		vertexdata_init(layoutConfig.a_starttemp);

		layoutConfig.oscillation = layoutConfig.a_oscillation;
		layoutConfig.rotation = layoutConfig.a_rotation;
		layoutConfig.maxtemp = (int) (layoutConfig.a_maxtemp * layoutConfig.ELEN);
		stop_temperature = (int) (layoutConfig.a_finaltemp
				* layoutConfig.a_finaltemp * layoutConfig.ELENSQR * layoutConfig.nodeCount);
		stop_iteration = layoutConfig.a_maxiter * layoutConfig.nodeCount
				* layoutConfig.nodeCount;
		layoutConfig.iteration = 0;

		// System.out.print( "arrange phase -- temp " );
		// System.out.print( stop_temperature + " iter ");
		// System.out.println ( stop_iteration );

		while (layoutConfig.temperature > stop_temperature
				&& layoutConfig.iteration < stop_iteration) {
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

		GemP thisGP = layoutConfig.gemProp[thisNode];
		GemP thatGP = layoutConfig.gemProp[thatNode];
		GemP nodeGP = layoutConfig.gemProp[v];

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

		Iterator<E> edgeSet;
		int u, w;
		E e;
		int iX, iY, dX, dY;
		int n;
		GemP p, up, wp;
		int pX, pY;

		p = layoutConfig.gemProp[v];
		pX = p.x;
		pY = p.y;

		n = (int) (layoutConfig.o_shake * layoutConfig.ELEN);
		iX = rand() % (2 * n + 1) - n;
		iY = rand() % (2 * n + 1) - n;
		iX += (layoutConfig.centerX / layoutConfig.nodeCount - pX) * p.mass
				* layoutConfig.o_gravity;
		iY += (layoutConfig.centerY / layoutConfig.nodeCount - pY) * p.mass
				* layoutConfig.o_gravity;

		edgeSet = edges.iterator();
		while (edgeSet.hasNext()) {
			e = edgeSet.next();
			// Pair ends = e.getEndpoints();
			u = layoutConfig.nodeNumbers.getInt(graph.getEndpoints(e)
					.getFirst());
			w = layoutConfig.nodeNumbers.getInt(graph.getEndpoints(e)
					.getSecond());
			if (u != v && w != v) {
				up = layoutConfig.gemProp[u];
				wp = layoutConfig.gemProp[w];
				dX = (up.x + wp.x) / 2 - pX;
				dY = (up.y + wp.y) / 2 - pY;
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
				up = layoutConfig.gemProp[u];
				dX = pX - up.x;
				dY = pY - up.y;
				n = (int) ((dX * dX + dY * dY) / p.mass);
				n = Math.min(n, layoutConfig.MAXATTRACT);
				iX -= dX * n / layoutConfig.ELENSQR;
				iY -= dY * n / layoutConfig.ELENSQR;
			}
		}
		return new int[] { iX, iY };
	}

	private void o_round() {

		int v;
		for (int i = 0; i < layoutConfig.nodeCount; i++) {
			v = select();
			int[] o_impulse = o_impulse(v);
			displace(v, o_impulse[0], o_impulse[1]);
			layoutConfig.iteration++;
		}
	}

	private void optimize() {

		long stop_temperature;
		long stop_iteration;

		vertexdata_init(layoutConfig.o_starttemp);
		layoutConfig.oscillation = layoutConfig.o_oscillation;
		layoutConfig.rotation = layoutConfig.o_rotation;
		layoutConfig.maxtemp = (int) (layoutConfig.o_maxtemp * layoutConfig.ELEN);
		stop_temperature = (int) (layoutConfig.o_finaltemp
				* layoutConfig.o_finaltemp * layoutConfig.ELENSQR * layoutConfig.nodeCount);
		stop_iteration = layoutConfig.o_maxiter * layoutConfig.nodeCount
				* layoutConfig.nodeCount;

		// System.out.print( "optimise phase -- temp " );
		// System.out.print( stop_temperature + " iter ");
		// System.out.println ( stop_iteration );

		while (layoutConfig.temperature > stop_temperature
				&& layoutConfig.iteration < stop_iteration) {
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
			V n = layoutConfig.invmap[i];

			Point2D coord = transform(n);// getCoordinates(n);
			coord.setLocation(p.x, p.y);
		}
		// }
		// endTime = System.currentTimeMillis();

		// System.out.println("Took: " + (endTime - startTime) + "msec");
	}

	private Collection<V> getNeighbours(V n) {
		Collection<V> neighbours = new ArrayList<V>();
		for (E o : getGraph().getEdges()) {
			V first = graph.getEndpoints(o).getFirst();
			V second = graph.getEndpoints(o).getSecond();
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
		
		layoutConfig = GEMLayoutConfigSingleton.getInstance();

		layoutConfig.nodeCount = nodes.size();

		layoutConfig.gemProp = new GemP[layoutConfig.nodeCount];
		layoutConfig.invmap = (V[]) new Object[layoutConfig.nodeCount];
		layoutConfig.adjacent = new Int2ObjectOpenHashMap<IntArrayList>(
				layoutConfig.nodeCount);
		layoutConfig.nodeNumbers = new Object2IntOpenHashMap<V>();

		// initialize node lists and gemProp
		Iterator<V> nodeSet = nodes.iterator();
		for (int i = 0; nodeSet.hasNext(); i++) {
			V n = nodeSet.next();
			Collection<E> edges = new ArrayList<E>();
			for (E o : graph.getEdges()) {
				// if (o instanceof DirectedEdge) {
				// DirectedEdge e = (DirectedEdge) o;
				if (graph.getEndpoints(o).getFirst().equals(n)) {
					edges.add(o);
				}
				// }
			}

			// graph.getOutEdges(n)
			layoutConfig.gemProp[i] = new GemP(edges.size());
			layoutConfig.invmap[i] = n;
			layoutConfig.nodeNumbers.put(n, i);
		}

		// fill adjacent lists
		Collection<V> neighbors;
		for (int i = 0; i < layoutConfig.nodeCount; i++) {
			neighbors = getNeighbours(layoutConfig.invmap[i]); // graph.getNeighbours(invmap[i]);
			layoutConfig.adjacent.put(i, new IntArrayList(neighbors.size()));
			for (V n : neighbors) {
				layoutConfig.adjacent.get(i).add(
						layoutConfig.nodeNumbers.getInt(n));
			}
		}

		// actual layout
		if (layoutConfig.i_finaltemp < layoutConfig.i_starttemp)
			insert();
		if (layoutConfig.a_finaltemp < layoutConfig.a_starttemp)
			arrange();
		if (layoutConfig.o_finaltemp < layoutConfig.o_starttemp)
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