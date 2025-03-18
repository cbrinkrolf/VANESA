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
	private HashMap<Integer, List<BiologicalNodeAbstract>> bnaGroups;
	private List<Integer> groupKeys;
	private final Set<BiologicalNodeAbstract> outerNodes = new HashSet<>();

	public HCTLayout(final Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g, BiologicalNodeAbstract root) {
		super(g);
		rootNode = root != null ? root : computeRootNode();
	}

	@Override
	public HCTLayoutConfig getConfig() {
		return HCTLayoutConfig.getInstance();
	}

	@Override
	public void initialize() {
		final Dimension d = getSize();
		if (d != null) {
			if (bnaGroups == null) {
				computeCircleNumbers();
				groupNodes();
			}
			computeCircleData(d);
			int group_no = 0;
			int vertex_no = 0;
			// larger circle for a larger number of nodes on the outer circle.
			setRadius(getRadius() * Math.log10(graphNodes.size()));
			// distance between two nodes of the same group
			final double nodeDistance = HCTLayoutConfig.nodeDistance(bnaGroups.size() - 1, outerNodes.size());
			// distance between two groups (added to small distance between two nodes)
			final double groupDistance = HCTLayoutConfig.groupDistance(nodeDistance);

			final Set<BiologicalNodeAbstract> rootNodes = new HashSet<>();
			for (final Integer i : groupKeys) {
				if (bnaGroups.get(i).get(0) == rootNode) {
					GraphInstance.getGraph().setNodePosition(rootNode, centerPoint.getX(), centerPoint.getY());
					addCircleData(rootNode);
					final CircleVertexData data = circleVertexDataMap.get(rootNode);
					data.setCircleNumber(circles.get(rootNode));
					data.setVertexAngle(0);
					vertex_no++;
					continue;
				}
				for (final BiologicalNodeAbstract v : bnaGroups.get(i)) {
					if (circles.get(v) == null) {
						continue;
					}
					if (v.getParentNode() != null && v.getParentNode().getRootNode() == v) {
						rootNodes.add(v);
						continue;
					}
					final double angle = group_no * groupDistance + vertex_no * nodeDistance;
					GraphInstance.getGraph().setNodePosition(v,
							Math.cos(angle) * getRadius() * circles.get(v) + centerPoint.getX(),
							Math.sin(angle) * getRadius() * circles.get(v) + centerPoint.getY());

					addCircleData(v);
					final CircleVertexData data = circleVertexDataMap.get(v);
					data.setCircleNumber(circles.get(v));
					data.setVertexAngle(angle);
					vertex_no++;
				}
				group_no++;
			}
			int currentCircle = maxCircle - 1;
			while (currentCircle > 0) {
				for (final BiologicalNodeAbstract v : rootNodes) {
					if (circles.get(v) != currentCircle) {
						continue;
					}
					final Set<BiologicalNodeAbstract> children = new HashSet<>(
							v.getParentNode().getCurrentShownChildrenNodes(myGraph));
					children.remove(v);
					final Set<Point2D> childrenLocations = new HashSet<>();
					for (final BiologicalNodeAbstract child : children) {
						childrenLocations.add(Circle.getPointOnCircle(centerPoint, getRadius() * circles.get(child),
								circleVertexDataMap.get(child).getVertexAngle()));
					}
					final double angle = Circle.getAngle(centerPoint, Circle.averagePoint(childrenLocations));
					GraphInstance.getGraph().setNodePosition(v,
							Math.cos(angle) * getRadius() * circles.get(v) + centerPoint.getX(),
							Math.sin(angle) * getRadius() * circles.get(v) + centerPoint.getY());

					addCircleData(v);
					final CircleVertexData data = circleVertexDataMap.get(v);
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
		circles = new HashMap<>();
		maxCircle = 0;
		for (BiologicalNodeAbstract node : myGraph.getNodes()) {
			List<BiologicalNodeAbstract> parents = node.getAllParentNodesSorted();
			int circle = parents.size() + 1;
			boolean isRootNode = node.getParentNode() != null && node.getParentNode().getRootNode() == node;
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
		graphNodes = new HashSet<>();
		graphNodes.addAll(graph.getVertices());
		if (graphNodes.size() < 2) {
			return;
		}
		order = computeOrder();
		bnaGroups = new HashMap<>();
		Set<BiologicalNodeAbstract> addedNodes = new HashSet<>();
		groupKeys = new ArrayList<>();
		for (BiologicalNodeAbstract node : order) {
			BiologicalNodeAbstract currentNode = node.getCurrentShownParentNode(myGraph);
			if (addedNodes.contains(currentNode)) {
				continue;
			}
			if (currentNode.getParentNode() != null && currentNode.getParentNode().getRootNode() != currentNode) {
				outerNodes.add(currentNode);
			} else if (currentNode.getParentNode() == null && currentNode != rootNode) {
				outerNodes.add(currentNode);
			}
			BiologicalNodeAbstract referenceParent = currentNode.getLastParentNode();
			if (referenceParent == null) {
				groupKeys.add(currentNode.getID());
				bnaGroups.put(currentNode.getID(), new ArrayList<>());
				bnaGroups.get(currentNode.getID()).add(currentNode);
				addedNodes.add(currentNode);
				continue;
			}
			if (!groupKeys.contains(referenceParent.getID())) {
				groupKeys.add(referenceParent.getID());
				bnaGroups.put(referenceParent.getID(), new ArrayList<>());
			}
			bnaGroups.get(referenceParent.getID()).add(currentNode);
			addedNodes.add(currentNode);
		}
	}

	@Override
	public List<BiologicalNodeAbstract> getNodesGroup(final BiologicalNodeAbstract n) {
		final List<BiologicalNodeAbstract> selection = new ArrayList<>();
		switch (HCTLayoutConfig.SELECTION) {
		case SUBPATH: {
			final BiologicalNodeAbstract parent = n.getParentNode();
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
		}
		break;
		case PATH: {
			final BiologicalNodeAbstract parent = n.getParentNode();
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
		}
		break;
		case ROUGHESTGROUP: {
			final BiologicalNodeAbstract parent = n.getLastParentNode();
			for (BiologicalNodeAbstract child : parent.getCurrentShownChildrenNodes(myGraph)) {
				if (!selection.contains(child)) {
					selection.add(child);
				}
			}
		}
		break;
		case SINGLE:
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
		GraphInstance.getPathway().getGraph().getVisualizationViewer().getRenderContext().setEdgeShapeTransformer(
				new HctEdgeShape());
		GraphInstance.getPathway().getGraph().getVisualizationViewer().getRenderContext().setEdgeDrawPaintTransformer(
				new HCTEdgePaintTransformer(GraphInstance.getPathway().getGraph().getVisualizationViewer()));

		GraphInstance.getPathway().getGraph().getVisualizationViewer().getRenderContext().setEdgeArrowTransformer(
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
			List<BiologicalNodeAbstract> firstParentRootNodes = new ArrayList<>();
			List<BiologicalNodeAbstract> secondParentRootNodes = new ArrayList<>();
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
			new EdgeShape<>(graph).super();
		}

		public Shape apply(final BiologicalEdgeAbstract edge) {
			Pair<BiologicalNodeAbstract> endpoints = graph.getEndpoints(edge);
			BiologicalNodeAbstract first = endpoints.getFirst();
			BiologicalNodeAbstract second = endpoints.getSecond();
			List<BiologicalNodeAbstract> firstParentRootNodes = new ArrayList<>();
			List<BiologicalNodeAbstract> secondParentRootNodes = new ArrayList<>();
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
				return new Line2D.Double(0, 0, 1, 0);
			}
			if (second.getParentNode() != null && second.getParentNode().getRootNode() == first) {
				return new Line2D.Double(0, 0, 1, 0);
			}
			if (!(firstParentRootNodes.contains(second) || secondParentRootNodes.contains(first))) {
				if (!(first == rootNode || second == rootNode)) {
					if (getConfig().getShowExternalEdges()) {
						return new Line2D.Double(0, 0, 1, 0);
					}
					return new Line2D.Double();
				}
			}
			if (Math.abs(circles.get(first) - circles.get(second)) == 1) {
				return new Line2D.Double(0, 0, 1, 0);
			}
			Path2D path = new Path2D.Double();
			Point2D lastPoint = new Point2D.Double();
			BiologicalNodeAbstract startNode = first;
			BiologicalNodeAbstract endNode = second;
			if (circles.get(first) < circles.get(second)) {
				startNode = second;
				endNode = first;
				lastPoint = new Point2D.Double(1, 0);
			}
			BiologicalNodeAbstract parentNode = startNode.getParentNode();
			Line2D line;
			while (parentNode != null && parentNode.getRootNode() != endNode) {
				Set<Point2D> childNodePoints = new HashSet<>();
				for (BiologicalNodeAbstract child : parentNode.getCurrentShownChildrenNodes(myGraph)) {
					childNodePoints.add(myGraph.getNodePosition(child));
				}
				double angle = Circle.getAngle(getCenterPoint(), Circle.averagePoint(childNodePoints));
				Point2D location = Circle.getPointOnCircle(getCenterPoint(), getRadius() * (circles.get(parentNode)),
						angle);
				location = Circle.computeControlPoint(location, getCenterPoint(), myGraph.getNodePosition(first),
						myGraph.getNodePosition(second));
				line = new Line2D.Double(lastPoint, location);
				path.append(line, true);
				lastPoint = location;
				parentNode = parentNode.getParentNode();
			}
			line = new Line2D.Double(lastPoint, new Point2D.Double(endNode == first ? 0 : 1, 0));
			path.append(line, true);
			return path;
		}
	}
}
