package graph.layouts.hebLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.GradientEdgePaintTransformer;
import graph.GraphInstance;
import graph.VanesaGraph;
import graph.layouts.HierarchicalCircleLayout;

public class HEBLayout extends HierarchicalCircleLayout {
	protected HashMap<Integer, List<BiologicalNodeAbstract>> bnaGroups;
	protected List<Integer> groupKeys;

	public HEBLayout(Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g) {
		super(g);
	}

	public HEBLayout(Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> g, List<BiologicalNodeAbstract> order) {
		super(g, order);
	}

	@Override
	public HEBLayoutConfig getConfig() {
		return HEBLayoutConfig.getInstance();
	}

	@Override
	public void initialize() {
		Dimension d = getSize();
		if (d != null) {
			if (bnaGroups == null) {
				groupNodes();
			}
			computeCircleNumbers();
			computeCircleData(d);
			int vertexIndex = 0;
			// larger circle for a larger number of nodes on the outer circle.
			setRadius(getRadius() * Math.log10(graphNodes.size()));
			// distance between two nodes of the same group
			final double nodeDistance = HEBLayoutConfig.nodeDistance(bnaGroups.size(), graphNodes.size());
			// distance between two groups (added to small distance between two nodes)
			final double groupDistance = HEBLayoutConfig.groupDistance(nodeDistance);
			final VanesaGraph g = GraphInstance.getGraph();
			// Move nodes on their circle position
			for (int groupIndex = 0; groupIndex < groupKeys.size(); groupIndex++) {
				for (final BiologicalNodeAbstract v : bnaGroups.get(groupKeys.get(groupIndex))) {
					final double angle = groupIndex * groupDistance + vertexIndex * nodeDistance;
					g.setNodePosition(v, Math.cos(angle) * getRadius() + centerPoint.getX(),
							Math.sin(angle) * getRadius() + centerPoint.getY());
					addCircleData(v);
					CircleVertexData data = getCircleData(v);
					data.setVertexAngle(angle);
					// All nodes on the outer circle
					data.setCircleNumber(1);
					vertexIndex++;
				}
			}
		}
		// compute the edge shapes
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
		int c;
		for (BiologicalNodeAbstract node : getGraph().getVertices()) {
			c = 0;
			BiologicalNodeAbstract p = node;
			while (p != null) {
				if (circles.containsKey(p)) {
					circles.put(p, Math.max(c, circles.get(p)));
				} else {
					circles.put(p, c);
				}
				maxCircle = Math.max(c, maxCircle);
				p = p.getParentNode();
				c += 1;
			}
		}
	}

	/**
	 * Build groups with nodes of the same parent node in the given depth.
	 *
	 * @author tloka
	 */
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
		BiologicalNodeAbstract currentNode;
		BiologicalNodeAbstract referenceParent;

		for (BiologicalNodeAbstract node : order) {
			currentNode = node.getCurrentShownParentNode(myGraph);
			if (addedNodes.contains(currentNode)) {
				continue;
			}

			referenceParent = HEBLayoutConfig.GROUP_DEPTH == HEBLayoutConfig.FINEST_LEVEL
					? currentNode.getParentNode()
					: currentNode.getLastParentNode();
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
	public void setEdgeShapes() {
		Function<BiologicalEdgeAbstract, Shape> est = new HEBEdgeShape.HEBCurve<>(getCenterPoint(), circles,
				GraphInstance.getPathway().getGraph().getJungGraph());
		GraphInstance.getPathway().getGraph().getVisualizationViewer().getRenderContext().setEdgeShapeTransformer(est);

		HEBEdgePaintTransformer ptrans = new HEBEdgePaintTransformer(HEBLayoutConfig.EDGE_OUTCOLOR,
				HEBLayoutConfig.EDGE_INCOLOR, GraphInstance.getPathway().getGraph().getVisualizationViewer());
		GraphInstance.getPathway().getGraph().getVisualizationViewer().getRenderContext().setEdgeDrawPaintTransformer(ptrans);

		GraphInstance.getPathway().getGraph().getVisualizationViewer().getRenderContext().setEdgeArrowTransformer(
				new ShowEdgeArrowsTransformer<BiologicalNodeAbstract, BiologicalEdgeAbstract>());
	}

	/**
	 * Get the group of a node.
	 *
	 * @param node The node.
	 * @return The group of the node.
	 * @author tloka
	 */
	public List<BiologicalNodeAbstract> getNodesGroup(BiologicalNodeAbstract node) {
		for (List<BiologicalNodeAbstract> group : bnaGroups.values()) {
			if (group.contains(node)) {
				return group;
			}
		}
		return new ArrayList<>();
	}

	/**
	 * Creates a color gradient for directed edges in HEBLayout.
	 *
	 * @author tobias
	 */
	private static class HEBEdgePaintTransformer
			extends GradientEdgePaintTransformer<BiologicalNodeAbstract, BiologicalEdgeAbstract> {
		public HEBEdgePaintTransformer(Color c1, Color c2,
				VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv) {
			super(c1, c2, vv);
		}

		@Override
		public Paint apply(BiologicalEdgeAbstract e) {
			if (e.isDirected()) {
				return super.apply(e);
			}
			Color oldc2 = c2;
			c2 = c1;
			Paint p = super.apply(e);
			c2 = oldc2;
			return p;
		}
	}
}
