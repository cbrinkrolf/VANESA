package graph.layouts.hebLayout;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeIndexFunction;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import graph.GraphInstance;
import graph.jung.classes.MyGraph;
import graph.layouts.Circle;

/**
 * All edge shapes must be defined so that their endpoints are at (0,0) and
 * (1,0). They will be scaled, rotated and translated into position by the
 * PluggableRenderer.
 * 
 * @author tloka
 */
public class HEBEdgeShape<V, E> extends EdgeShape<V, E> {
	public HEBEdgeShape(Graph<V, E> g) {
		super(g);
	}

	/**
	 * An edge shape that renders as a CubicCurve between vertex endpoints. The two
	 * control points are at (1/3*length, 2*controlY) and (2/3*length, controlY)
	 * giving a 'spiral' effect.
	 */
	public static class HEBCurve<V, E> extends EdgeShape<V, E> implements Function<E, Shape> {
		// {extends AbstractEdgeShapeTransformer<V,E> {//implements
		// IndexedRendering<V,E> {

		protected EdgeIndexFunction<V, E> parallelEdgeIndexFunction;

		private static Point2D centerPoint;

		private final HashMap<BiologicalNodeAbstract, Integer> layer;
		private Integer maxLayer;

		private final Graph<V, E> graph;

		public Point2D getCenterPoint() {
			return centerPoint;
		}

		public HEBCurve(Point2D cP, HashMap<BiologicalNodeAbstract, Integer> l, Graph<V, E> graph) {
			super(graph);
			this.graph = graph;
			centerPoint = cP;
			layer = l;
			maxLayer = -1;
			for (Integer d : l.values()) {
				if (d > maxLayer)
					maxLayer = d;
			}
		}

		public void setEdgeIndexFunction(EdgeIndexFunction<V, E> parallelEdgeIndexFunction) {
			this.parallelEdgeIndexFunction = parallelEdgeIndexFunction;
			loop.setEdgeIndexFunction(parallelEdgeIndexFunction);
		}

		/**
		 * @return the parallelEdgeIndexFunction
		 */
		public EdgeIndexFunction<V, E> getEdgeIndexFunction() {
			return parallelEdgeIndexFunction;
		}

		/**
		 * Get the shape for this edge, returning either the shared instance or, in the
		 * case of self-loop edges, the Loop shared instance.
		 */
		public Shape apply(E e) {
			// Graph<V,E> graph = context.graph;
			if (!(e instanceof BiologicalEdgeAbstract)) {
				return EdgeShape.quadCurve(graph).apply(e);
				// return new EdgeShape.CubicCurve<V, E>().apply(context);
			}
			Pair<V> endpoints = graph.getEndpoints(e);
			Pair<BiologicalNodeAbstract> endpointNodes = new Pair<BiologicalNodeAbstract>(
					(BiologicalNodeAbstract) endpoints.getFirst(), (BiologicalNodeAbstract) endpoints.getSecond());

			// current MyGraph
			MyGraph myGraph = GraphInstance.getMyGraph();

			// If Loop, draw loop.
			if (endpoints != null) {
				boolean isLoop = endpoints.getFirst().equals(endpoints.getSecond());
				if (isLoop) {
					return loop.apply(e);
				}
			}

			// Dont draw if selected group parameters fit.
			if (!HEBLayoutConfig.getInstance().getShowInternalEdges()
					&& endpointNodes.getFirst().getParentNode() != null) {
				if (HEBLayoutConfig.GROUP_DEPTH == HEBLayoutConfig.ROUGHEST_LEVEL && endpointNodes.getFirst()
						.getLastParentNode() == endpointNodes.getSecond().getLastParentNode()) {
					return new Line2D.Double(0.0, 0.0, 0.0, 0.0);
				}
				if (HEBLayoutConfig.GROUP_DEPTH == HEBLayoutConfig.FINEST_LEVEL
						&& endpointNodes.getFirst().getParentNode() == endpointNodes.getSecond().getParentNode()) {
					return new Line2D.Double(0.0, 0.0, 0.0, 0.0);
				}
			}

			// location of the startNode.
			Point2D startPoint = new Point2D.Double(myGraph.getVertexLocation(endpointNodes.getFirst()).getX(),
					myGraph.getVertexLocation(endpointNodes.getFirst()).getY());

			// location of the endNode.
			Point2D endPoint = new Point2D.Double(myGraph.getVertexLocation(endpointNodes.getSecond()).getX(),
					myGraph.getVertexLocation(endpointNodes.getSecond()).getY());

			// The circle's attributes.
			Point2D center = getCenterPoint();
			double circleRadius = Point2D.distance(centerPoint.getX(), centerPoint.getY(), startPoint.getX(),
					startPoint.getY());

			// Bundling error. Defines, how strong the edges bundle. The higher, the less
			// strong.
			double bundling_error = (100 - HEBLayoutConfig.EDGE_BUNDLING_PERCENTAGE) * 0.01;

			// Computation of control points
			double angle;
			BiologicalNodeAbstract lcp = endpointNodes.getFirst().getLastCommonParentNode(endpointNodes.getSecond());
			List<Point2D> controlPoints = new ArrayList<Point2D>();
			// Go through all parent nodes (bottom-up) of the start node until lcp
			// (included).
			Set<Point2D> childNodePoints;
			Point2D nP;
			for (BiologicalNodeAbstract node : endpointNodes.getFirst().getAllParentNodesSorted()) {
				// Compute parent node coordinates
				childNodePoints = new HashSet<Point2D>();
				for (BiologicalNodeAbstract n : node.getCurrentShownChildrenNodes(myGraph)) {
					childNodePoints.add(myGraph.getVertexLocation(n));
				}
				angle = Circle.getAngle(center, Circle.averagePoint(childNodePoints));
				nP = Circle.getPointOnCircle(center, circleRadius, angle);
				nP = Circle.moveInCenterDirection(nP, center, 100 * layer.get(node) / (maxLayer + 1));
				// Influence of bundling error
				if (node != lcp)
					nP = Circle.moveInCenterDirection(nP, startPoint, bundling_error * 100);
				// Transform coordinates in new coordinate system
				nP = Circle.computeControlPoint(nP, center, startPoint, endPoint);
				controlPoints.add(nP);
				if (node == lcp)
					break;
			}
			// Go through all parent nodes (top-down) from lcp (excluded) until direct
			// ancestor
			boolean lcpreachedflag = false;
			List<BiologicalNodeAbstract> parents = endpointNodes.getSecond().getAllParentNodesSorted();
			BiologicalNodeAbstract node;
			for (int i = parents.size() - 1; i >= 0; i--) {
				// Change lcp-flag if lcp was reached
				if (parents.get(i) == lcp) {
					lcpreachedflag = true;
					continue;
				}
				// If no lcp or lcp was reached, compute control point
				if (lcp == null || lcpreachedflag) {
					// compute parent node coordinates
					node = parents.get(i);
					childNodePoints = new HashSet<Point2D>();
					for (BiologicalNodeAbstract n : node.getCurrentShownChildrenNodes(myGraph)) {
						childNodePoints.add(myGraph.getVertexLocation(n));
					}
					angle = Circle.getAngle(center, Circle.averagePoint(childNodePoints));
					nP = Circle.getPointOnCircle(center, circleRadius, angle);
					nP = Circle.moveInCenterDirection(nP, center, 100 * layer.get(node) / (maxLayer + 1));
					// Influence of bundling error
					nP = Circle.moveInCenterDirection(nP, endPoint, bundling_error * 100);
					// Transform coordinates in new coordinate system
					nP = Circle.computeControlPoint(nP, center, startPoint, endPoint);
					controlPoints.add(nP);
				}
			}

			// if no control points exist, draw quadratic bezier with center as control
			// point
			if (controlPoints.isEmpty()) {
				Point2D centerTransform = new Point2D.Double(center.getX(), center.getY());
				centerTransform = Circle.computeControlPoint(centerTransform, center, startPoint, endPoint);
				return new QuadCurve2D.Double(0.0, 0.0, centerTransform.getX(), centerTransform.getY(), 1.0, 0.0);
			}

			// build piecewise quadratic bezier curve
			List<QuadCurve2D> lines = new ArrayList<>();
			// startPoint of the next bezier curve
			Point2D lastPoint = new Point2D.Double(0.0f, 0.0f);
			Point2D nP2;
			for (int i = 0; i < controlPoints.size(); i++) {
				// control point of the next bezier curve
				nP = controlPoints.get(i);
				// end point of the next bezier curve
				nP2 = new Point2D.Double(1.0f, 0.0f);
				if (i + 1 < controlPoints.size()) {
					nP2 = controlPoints.get(i + 1);
					nP2 = new Point2D.Double((nP.getX() + nP2.getX()) / 2, (nP.getY() + nP2.getY()) / 2);
				}
				lines.add(new QuadCurve2D.Double(lastPoint.getX(), lastPoint.getY(), nP.getX(), nP.getY(), nP2.getX(),
						nP2.getY()));
				// endpoint is the next startpoint
				lastPoint = nP2;
			}
			Path2D path = new Path2D.Double();
			for (QuadCurve2D l : lines) {
				path.append(l, true);
			}
			return path;
		}
	}
}
