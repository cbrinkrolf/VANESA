package graph.layouts.hctLayout;

import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.GradientEdgePaintTransformer;
import graph.GraphInstance;
import graph.layouts.Circle;
import graph.layouts.HierarchicalCircleLayout;

public class HCTLayout extends HierarchicalCircleLayout {

	HashMap<BiologicalNodeAbstract, HashMap<Integer, List<BiologicalNodeAbstract>>> groups = new HashMap<BiologicalNodeAbstract, HashMap<Integer, List<BiologicalNodeAbstract>>>();

	protected HashMap<Integer, List<BiologicalNodeAbstract>> bnaGroups;
	protected List<Integer> groupKeys;
	protected Set<BiologicalNodeAbstract> outterNodes = new HashSet<BiologicalNodeAbstract>();

	public HCTLayout(Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g) {
		this(g, null);
	}

	public HCTLayout(Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g, BiologicalNodeAbstract root) {
		super(g);
		if (root != null) {
			rootNode = root;
		} else {
			rootNode = computeRootNode();
		}
	}

	public HCTLayout(Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g, List<BiologicalNodeAbstract> order,
			BiologicalNodeAbstract root) {
		super(g, order);
		if (root != null) {
			rootNode = root;
		} else {
			rootNode = computeRootNode();
		}
	}

	public HCTLayoutConfig getConfig() {
		return HCTLayoutConfig.getInstance();
	}

	@Override
	public void initialize() {
		Dimension d = getSize();

		if (d != null) {
			if (bnaGroups == null) {
				computeCircleNumbers();
				groupNodes();
			}

			computeCircleData(d);

			int group_no = 0;
			int vertex_no = 0;

			// larger circle for a larger number of nodes on the outter circle.
			setRadius(getRadius() * Math.log10(graphNodes.size()));

			// distance between two ndoes of the same group
			final double nodeDistance = HCTLayoutConfig.nodeDistance(bnaGroups.size() - 1, outterNodes.size());
			// System.out.println(outterNodes.size());

			// distance between two groups (added to small distance between two nodes)
			final double groupDistance = HCTLayoutConfig.groupDistance(nodeDistance);

			Set<BiologicalNodeAbstract> rootNodes = new HashSet<BiologicalNodeAbstract>();
			CircleVertexData data;
			for (Integer i : groupKeys) {
				if (bnaGroups.get(i).get(0) == rootNode) {
					GraphInstance.getMyGraph().moveVertex(rootNode, centerPoint.getX(), centerPoint.getY());

					addCircleData(rootNode);
					data = circleVertexDataMap.get(rootNode);
					data.setCircleNumber(circles.get(rootNode));
					data.setVertexAngle(0);
					vertex_no++;
					continue;
				}
				for (BiologicalNodeAbstract v : bnaGroups.get(i)) {
					if (circles.get(v) == null) {
						continue;
					}
					if (v.getParentNode() != null && v.getParentNode().getRootNode() == v) {
						rootNodes.add(v);
						continue;
					}
					double angle = group_no * groupDistance + vertex_no * nodeDistance;
					GraphInstance.getMyGraph().moveVertex(v,
							Math.cos(angle) * getRadius() * circles.get(v) + centerPoint.getX(),
							Math.sin(angle) * getRadius() * circles.get(v) + centerPoint.getY());

					addCircleData(v);
					data = circleVertexDataMap.get(v);
					data.setCircleNumber(circles.get(v));
					data.setVertexAngle(angle);
					vertex_no++;
				}
				group_no++;
			}
			int currentCircle = maxCircle - 1;
			Set<BiologicalNodeAbstract> children;
			Set<Point2D> childrenLocations;
			double angle;
			while (currentCircle > 0) {
				for (BiologicalNodeAbstract v : rootNodes) {
					if (circles.get(v) != currentCircle) {
						continue;
					}
					children = new HashSet<BiologicalNodeAbstract>();
					children.addAll(v.getParentNode().getCurrentShownChildrenNodes(myGraph));
					children.remove(v);
					childrenLocations = new HashSet<Point2D>();
					for (BiologicalNodeAbstract child : children) {
						childrenLocations.add(Circle.getPointOnCircle(centerPoint, getRadius() * circles.get(child),
								circleVertexDataMap.get(child).getVertexAngle()));
					}
					angle = Circle.getAngle(centerPoint, Circle.averagePoint(childrenLocations));
//    		System.out.println(v.getLabel() + ": " + circles.get(v));
					GraphInstance.getMyGraph().moveVertex(v,
							Math.cos(angle) * getRadius() * circles.get(v) + centerPoint.getX(),
							Math.sin(angle) * getRadius() * circles.get(v) + centerPoint.getY());

					addCircleData(v);
					data = circleVertexDataMap.get(v);
					data.setCircleNumber(circles.get(v));
					data.setVertexAngle(angle);
				}
				currentCircle -= 1;
			}
		}
		setEdgeShapes();
	}

	/**
	 * Computes for all graph nodes and their parents the circle they are part of.
	 * 
	 * @author tloka
	 */
	public void computeCircleNumbers() {
		circles = new HashMap<BiologicalNodeAbstract, Integer>();
		maxCircle = 0;
		List<BiologicalNodeAbstract> parents;
		int circle;
		boolean isRootNode;
		for (BiologicalNodeAbstract node : myGraph.getAllVertices()) {
			parents = node.getAllParentNodesSorted();
			circle = parents.size() + 1;
			isRootNode = false;
			if (node.getParentNode() != null && node.getParentNode().getRootNode() == node) {
				isRootNode = true;
			}
			if (node == rootNode) {
				circle = 0;
			}
			maxCircle = Math.max(maxCircle, circle);
			circles.put(node, isRootNode ? circle - 1 : circle);
			isRootNode = false;
			for (BiologicalNodeAbstract p : parents) {
				circle -= 1;
				if (node.getParentNode() != null && node.getParentNode().getRootNode() == node) {
					isRootNode = true;
				}
				if (circles.containsKey(p)) {
					circles.put(p, Math.min(isRootNode ? circle - 1 : circle, circles.get(p)));
				} else {
					circles.put(p, isRootNode ? circle - 1 : circle);
				}
			}
		}
		circles.put(this.rootNode, 0);
	}

	@Override
	public void groupNodes() {
		graphNodes = new HashSet<BiologicalNodeAbstract>();
		graphNodes.addAll(graph.getVertices());

		if (graphNodes.size() < 2) {
			return;
		}

		order = computeOrder();

		bnaGroups = new HashMap<Integer, List<BiologicalNodeAbstract>>();
		Set<BiologicalNodeAbstract> addedNodes = new HashSet<BiologicalNodeAbstract>();
		groupKeys = new ArrayList<Integer>();
		BiologicalNodeAbstract currentNode;
		BiologicalNodeAbstract referenceParent;

		for (BiologicalNodeAbstract node : order) {
			currentNode = node.getCurrentShownParentNode(myGraph);
			if (addedNodes.contains(currentNode)) {
				continue;
			}
//			if(circles.get(currentNode)==maxCircle){
//				graphNodes.remove(currentNode);
//				continue;
//			}
			if (currentNode.getParentNode() != null && currentNode.getParentNode().getRootNode() != currentNode) {
				outterNodes.add(currentNode);
			} else if (currentNode.getParentNode() == null && currentNode != rootNode) {
				outterNodes.add(currentNode);
			}

			referenceParent = currentNode.getLastParentNode();
			if (referenceParent == null) {
				groupKeys.add(currentNode.getID());
				bnaGroups.put(currentNode.getID(), new ArrayList<BiologicalNodeAbstract>());
				bnaGroups.get(currentNode.getID()).add(currentNode);
				addedNodes.add(currentNode);
				continue;
			}

			if (!groupKeys.contains(referenceParent.getID())) {
				groupKeys.add(referenceParent.getID());
				bnaGroups.put(referenceParent.getID(), new ArrayList<BiologicalNodeAbstract>());
			}
			bnaGroups.get(referenceParent.getID()).add(currentNode);
			addedNodes.add(currentNode);
		}
	}

	public BiologicalNodeAbstract getGroupParent(BiologicalNodeAbstract n) {
		if (n == rootNode) {
			return n;
		}
		if (n.getLastParentNode() == null) {
			return n;
		}
		return n.getLastParentNode();
	}

	@Override
	public List<BiologicalNodeAbstract> getNodesGroup(BiologicalNodeAbstract n) {
		List<BiologicalNodeAbstract> selection = new ArrayList<BiologicalNodeAbstract>();
		BiologicalNodeAbstract parent;
		switch (HCTLayoutConfig.SELECTION) {
		case SINGLE:
			selection.add(n);
			break;
		case SUBPATH:
			parent = n.getParentNode();
			if (parent != null && parent.getRootNode() == n) {
				for (BiologicalNodeAbstract child : parent.getCurrentShownChildrenNodes(myGraph)) {
					if (!selection.contains(child)) {
						selection.add(child);
					}
				}
			} else {
				for (BiologicalNodeAbstract child : n.getCurrentShownChildrenNodes(myGraph)) {
					if (!selection.contains(child)) {
						selection.add(child);
					}
				}
			}
			break;
		case PATH:
			parent = n.getParentNode();
			if (parent != null && parent.getRootNode() == n) {
				for (BiologicalNodeAbstract child : parent.getCurrentShownChildrenNodes(myGraph)) {
					if (!selection.contains(child)) {
						selection.add(child);
					}
				}
			} else {
				for (BiologicalNodeAbstract child : n.getCurrentShownChildrenNodes(myGraph)) {
					if (!selection.contains(child)) {
						selection.add(child);
					}
				}
			}
			for (BiologicalNodeAbstract p : n.getAllParentNodes()) {
				if (p.getRootNode() != null && getGraph().getVertices().contains(p.getRootNode())) {
					selection.add(p.getRootNode());
				}
			}
			break;
		case ROUGHESTGROUP:
			parent = n.getLastParentNode();
			for (BiologicalNodeAbstract child : parent.getCurrentShownChildrenNodes(myGraph)) {
				if (!selection.contains(child)) {
					selection.add(child);
				}
			}
			break;
		default:
			selection.add(n);
			break;
		}
		return selection;
	}

	/**
	 * Computes the necessary root node for the HCT Layout.
	 * 
	 * @return The root node.
	 */
	public BiologicalNodeAbstract computeRootNode() {
		int maxNeighborNodes = 0;
		BiologicalNodeAbstract rootNode = null;
		for (BiologicalNodeAbstract n : graph.getVertices()) {
			if (n.isCoarseNode()) {
				continue;
			}
			if (n.getParentNode() != GraphInstance.getPathway() && n.getParentNode() != null) {
				continue;
			}
			if (graph.getNeighborCount(n) > maxNeighborNodes) {
				maxNeighborNodes = graph.getNeighborCount(n);
				rootNode = n;
			}
		}
		return rootNode;
	}

	@Override
	public void setEdgeShapes() {
		GraphInstance.getMyGraph().getVisualizationViewer().getRenderContext()
				.setEdgeShapeTransformer(new HctEdgeShape());
		GraphInstance.getMyGraph().getVisualizationViewer().getRenderContext().setEdgeDrawPaintTransformer(
				new HCTEdgePaintTransformer(GraphInstance.getMyGraph().getVisualizationViewer()));

		GraphInstance.getMyGraph().getVisualizationViewer().getRenderContext().setEdgeArrowTransformer(
				new ShowEdgeArrowsTransformer<BiologicalNodeAbstract, BiologicalEdgeAbstract>());

	}

	protected class HCTEdgePaintTransformer
			extends GradientEdgePaintTransformer<BiologicalNodeAbstract, BiologicalEdgeAbstract> {
		public HCTEdgePaintTransformer(VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv) {
			super(HCTLayoutConfig.INTERNAL_EDGE_COLOR, HCTLayoutConfig.EXTERNAL_EDGE_COLOR, vv);
		}

		@Override
		public Paint apply(BiologicalEdgeAbstract e) {
			super.apply(e);
			BiologicalNodeAbstract first = e.getFrom();
			BiologicalNodeAbstract second = e.getTo();
			List<BiologicalNodeAbstract> firstParentRootNodes = new ArrayList<BiologicalNodeAbstract>();
			List<BiologicalNodeAbstract> secondParentRootNodes = new ArrayList<BiologicalNodeAbstract>();
			for (BiologicalNodeAbstract n : first.getAllParentNodesSorted()) {
				if (n.getRootNode() != null) {
					firstParentRootNodes.add(n.getRootNode());
				}
			}
			for (BiologicalNodeAbstract n : second.getAllParentNodesSorted()) {
				if (n.getRootNode() != null) {
					secondParentRootNodes.add(n.getRootNode());
				}
			}
			if (!(firstParentRootNodes.contains(second) || secondParentRootNodes.contains(first)) && first != rootNode
					&& second != rootNode) {
				return new GradientPaint(0, 0, HCTLayoutConfig.EXTERNAL_EDGE_COLOR, 0, 0,
						HCTLayoutConfig.EXTERNAL_EDGE_COLOR, true);
			}
			return new GradientPaint(0, 0, HCTLayoutConfig.INTERNAL_EDGE_COLOR, 0, 0,
					HCTLayoutConfig.INTERNAL_EDGE_COLOR, true);
		}
	}

	protected class HctEdgeShape extends EdgeShape<BiologicalNodeAbstract, BiologicalEdgeAbstract>.Line {
		public HctEdgeShape() {
			new EdgeShape<BiologicalNodeAbstract, BiologicalEdgeAbstract>(graph).super();
		}

		public Shape apply(BiologicalEdgeAbstract context) {
			BiologicalEdgeAbstract edge = context;
			Pair<BiologicalNodeAbstract> endpoints = graph.getEndpoints(edge);
			BiologicalNodeAbstract first = endpoints.getFirst();
			BiologicalNodeAbstract second = endpoints.getSecond();
			List<BiologicalNodeAbstract> firstParentRootNodes = new ArrayList<BiologicalNodeAbstract>();
			List<BiologicalNodeAbstract> secondParentRootNodes = new ArrayList<BiologicalNodeAbstract>();
			for (BiologicalNodeAbstract n : first.getAllParentNodesSorted()) {
				if (n.getRootNode() != null) {
					firstParentRootNodes.add(n.getRootNode());
				}
			}
			for (BiologicalNodeAbstract n : second.getAllParentNodesSorted()) {
				if (n.getRootNode() != null) {
					secondParentRootNodes.add(n.getRootNode());
				}
			}

			if (first.getParentNode() != null && first.getParentNode().getRootNode() == second) {
				return new Line2D.Double(0.0, 0.0, 1.0, 0.0);
			}
			if (second.getParentNode() != null && second.getParentNode().getRootNode() == first) {
				return new Line2D.Double(0.0, 0.0, 1.0, 0.0);
			}
//			if(first==rootNode || second==rootNode){
//				return new Line2D.Double(0.0,0.0,1.0,0.0);
//			}

			if (!(firstParentRootNodes.contains(second) || secondParentRootNodes.contains(first))) {
				if (!(first == rootNode || second == rootNode)) {
					if (getConfig().getShowExternalEdges()) {
						return new Line2D.Double(0.0, 0.0, 1.0, 0.0);
					}
					return new Line2D.Double(0.0, 0.0, 0.0, 0.0);
				}
			}

			if (Math.abs(circles.get(first) - circles.get(second)) == 1) {
				return new Line2D.Double(0.0, 0.0, 1.0, 0.0);
			}

			Path2D path = new Path2D.Double();
			Point2D lastPoint = new Point2D.Double(0.0, 0.0);
			BiologicalNodeAbstract startNode = first;
			BiologicalNodeAbstract endNode = second;
			if (circles.get(first) < circles.get(second)) {
				startNode = second;
				endNode = first;
				lastPoint = new Point2D.Double(1.0, 0.0);
			}
			BiologicalNodeAbstract parentNode = startNode.getParentNode();
			Set<Point2D> childNodePoints;
			double angle;
			Point2D location;
			Line2D line;
			while (parentNode != null && parentNode.getRootNode() != endNode) {
				childNodePoints = new HashSet<Point2D>();
				for (BiologicalNodeAbstract child : parentNode.getCurrentShownChildrenNodes(myGraph)) {
					childNodePoints.add(myGraph.getVertexLocation(child));
				}
				angle = Circle.getAngle(getCenterPoint(), Circle.averagePoint(childNodePoints));
				location = Circle.getPointOnCircle(getCenterPoint(), getRadius() * (circles.get(parentNode)), angle);
				location = Circle.computeControlPoint(location, getCenterPoint(), myGraph.getVertexLocation(first),
						myGraph.getVertexLocation(second));
				line = new Line2D.Double(lastPoint, location);
				path.append(line, true);
				lastPoint = location;
				parentNode = parentNode.getParentNode();
			}
			if (endNode == first) {
				line = new Line2D.Double(lastPoint, new Point2D.Double(0.0, 0.0));

			} else {
				line = new Line2D.Double(lastPoint, new Point2D.Double(1.0, 0.0));
			}
			path.append(line, true);
			return path;
		}
	}
}
