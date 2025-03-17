package graph;

import graph.rendering.shapes.EdgeTipShape;

import java.awt.*;

public interface GraphEdge<V extends GraphNode> {
	boolean isDirected();

	boolean isVisible();

	float getLineThickness();

	Color getColor();

	V getFrom();

	V getTo();

	EdgeTipShape getFromTipShape();

	EdgeTipShape getToTipShape();

	GraphEdgeLineStyle getLineStyle();

	String getNetworkLabel();
}
