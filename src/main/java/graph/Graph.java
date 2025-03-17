package graph;

import graph.operations.GraphOperation;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

public class Graph<V extends GraphNode, E extends GraphEdge<V>> {
	private final List<GraphSelectionChangedListener> selectionChangedListeners = new ArrayList<>();
	private final List<V> nodes = new ArrayList<>();
	private final List<E> edges = new ArrayList<>();
	private final Map<V, Point2D> nodePositions = new HashMap<>();
	private final Set<V> selectedNodes = new HashSet<>();
	private final Set<E> selectedEdges = new HashSet<>();

	public int getNodeCount() {
		return nodes.size();
	}

	public int getEdgeCount() {
		return edges.size();
	}

	@SafeVarargs
	public final void selectNodes(final V... nodes) {
		selectedNodes.clear();
		Collections.addAll(selectedNodes, nodes);
		invokeSelectionChangedListeners();
	}

	public void selectNodes(final Collection<V> nodes) {
		selectedNodes.clear();
		selectedNodes.addAll(nodes);
		invokeSelectionChangedListeners();
	}

	@SafeVarargs
	public final void selectNodes(final boolean additive, final V... nodes) {
		if (!additive) {
			selectedNodes.clear();
		}
		Collections.addAll(selectedNodes, nodes);
		invokeSelectionChangedListeners();
	}

	public void selectNodes(final boolean additive, final Collection<V> nodes) {
		if (!additive) {
			selectedNodes.clear();
		}
		selectedNodes.addAll(nodes);
		invokeSelectionChangedListeners();
	}

	@SafeVarargs
	public final void selectEdges(final E... edges) {
		selectedEdges.clear();
		Collections.addAll(selectedEdges, edges);
		invokeSelectionChangedListeners();
	}

	public void selectEdges(final Collection<E> edges) {
		selectedEdges.clear();
		selectedEdges.addAll(edges);
		invokeSelectionChangedListeners();
	}

	@SafeVarargs
	public final void selectEdges(final boolean additive, final E... edges) {
		if (!additive) {
			selectedEdges.clear();
		}
		Collections.addAll(selectedEdges, edges);
		invokeSelectionChangedListeners();
	}

	public final void selectEdges(final boolean additive, final Collection<E> edges) {
		if (!additive) {
			selectedEdges.clear();
		}
		selectedEdges.addAll(edges);
		invokeSelectionChangedListeners();
	}

	public void clearNodeSelection() {
		selectedNodes.clear();
		invokeSelectionChangedListeners();
	}

	public void clearEdgeSelection() {
		selectedEdges.clear();
		invokeSelectionChangedListeners();
	}

	public void clearSelection() {
		selectedNodes.clear();
		selectedEdges.clear();
		invokeSelectionChangedListeners();
	}

	public Collection<V> getSelectedNodes() {
		return selectedNodes;
	}

	public Collection<E> getSelectedEdges() {
		return selectedEdges;
	}

	public boolean isSelected(final V node) {
		return selectedNodes.contains(node);
	}

	public boolean isSelected(final E edge) {
		return selectedEdges.contains(edge);
	}

	public Point2D getNodePosition(final V node) {
		return nodePositions.get(node);
	}

	public void setNodePosition(final V node, final Point2D p) {
		nodePositions.put(node, p);
	}

	public void setNodePosition(final V node, final double x, final double y) {
		nodePositions.put(node, new Point2D.Double(x, y));
	}

	public void add(final V node) {
		if (!nodes.contains(node)) {
			nodes.add(node);
			nodePositions.put(node, new Point2D.Double());
		}
	}

	public void add(final V node, final Point2D p) {
		if (!nodes.contains(node)) {
			nodes.add(node);
			nodePositions.put(node, p);
		}
	}

	public void add(final E edge) {
		if (!edges.contains(edge)) {
			edges.add(edge);
		}
	}

	public void remove(final V node) {
		nodes.remove(node);
		nodePositions.remove(node);
	}

	public void remove(final E edge) {
		edges.remove(edge);
	}

	public void clear() {
		edges.clear();
		nodes.clear();
		nodePositions.clear();
	}

	public boolean contains(final V node) {
		return nodes.contains(node);
	}

	public boolean contains(final E edge) {
		return edges.contains(edge);
	}

	public E findEdge(final V from, final V to) {
		// TODO: replace with set intersection once edge.setTo & edge.setFrom is removed
		for (final E edge : edges) {
			if (edge.getFrom() != null && edge.getFrom().equals(from) && edge.getTo() != null && edge.getTo().equals(
					to)) {
				return edge;
			}
		}
		return null;
	}

	public Collection<V> getNodes() {
		return nodes;
	}

	public Collection<E> getEdges() {
		return edges;
	}

	public void translateSelectedNodes(final double offsetX, final double offsetY) {
		for (final V node : selectedNodes) {
			final Point2D p = nodePositions.get(node);
			nodePositions.put(node, new Point2D.Double(p.getX() + offsetX, p.getY() + offsetY));
		}
	}

	public int getNeighborCount(final V node) {
		int result = 0;
		if (node != null) {
			for (final E edge : edges) {
				if (node.equals(edge.getFrom()) || node.equals(edge.getTo())) {
					result++;
				}
			}
		}
		return result;
	}

	private void invokeSelectionChangedListeners() {
		for (final GraphSelectionChangedListener listener : selectionChangedListeners) {
			listener.onSelectionChanged();
		}
	}

	public void addSelectionChangedListener(final GraphSelectionChangedListener listener) {
		if (!selectionChangedListeners.contains(listener)) {
			selectionChangedListeners.add(listener);
		}
	}

	public void removeSelectionChangedListener(final GraphSelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	public Collection<V> getNeighbors(final V node) {
		final Set<V> result = new HashSet<>();
		if (node != null) {
			for (final E edge : edges) {
				if (node.equals(edge.getFrom())) {
					result.add(edge.getTo());
				}
				if (node.equals(edge.getTo())) {
					result.add(edge.getFrom());
				}
			}
		}
		return result;
	}

	public void apply(final GraphOperation<V, E> operation) {
		operation.apply(this);
	}
}
