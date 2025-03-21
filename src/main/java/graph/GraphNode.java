package graph;

import graph.rendering.nodes.NodeShape;

import java.awt.*;

public interface GraphNode {
	boolean isVisible();

	Color getColor();

	double getSize();

	NodeShape getNodeShape();

	String getNetworkLabel();
}
