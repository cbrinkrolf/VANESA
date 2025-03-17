package graph;

import graph.rendering.shapes.EdgeTipShape;

import java.awt.*;

public interface GraphEdge<V extends GraphNode> {
	boolean isDirected();

	boolean isVisible();

	float getThickness();

	Color getColor();

	V getFrom();

	V getTo();

	EdgeTipShape getFromTipShape();

	EdgeTipShape getToTipShape();

	String getNetworkLabel();
}
