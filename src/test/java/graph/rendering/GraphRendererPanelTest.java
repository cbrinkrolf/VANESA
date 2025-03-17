package graph.rendering;

import biologicalElements.Elementdeclerations;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.ContinuousPlace;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscretePlace;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import graph.Graph;
import graph.rendering.shapes.RectangleShape;
import graph.rendering.shapes.RegularPolygonShape;
import graph.rendering.shapes.RegularStarShape;
import graph.rendering.shapes.RoundedRectangleShape;
import org.junit.jupiter.api.Test;
import transformation.graphElements.ANYPlace;
import transformation.graphElements.ANYTransition;

import javax.swing.*;
import java.awt.geom.Point2D;

public class GraphRendererPanelTest {
	@Test
	public void test() throws InterruptedException {
		final Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract> graph = new Graph<>();
		ContinuousTransition t1 = new ContinuousTransition("t1", "t1", null);
		DiscreteTransition t2 = new DiscreteTransition("t2", "t2", null);
		ANYTransition t3 = new ANYTransition("t3", "t3", null);
		ContinuousPlace p1 = new ContinuousPlace("p1", "p1", null);
		DiscretePlace p2 = new DiscretePlace("p2", "p2", null);
		ANYPlace p3 = new ANYPlace("p3", "p3", null);
		graph.add(t1, new Point2D.Float(0, 0));
		graph.add(t2, new Point2D.Float(200, 0));
		graph.add(t3, new Point2D.Float(200, 200));
		graph.add(p1, new Point2D.Float(0, 200));
		graph.add(p2, new Point2D.Float(-100, 100));
		graph.add(p3, new Point2D.Float(-100, -100));
		graph.add(new PNArc(t1, p1, "a1", "a1", Elementdeclerations.pnArc, "1 + sqrt(4 / 3)"));
		graph.add(new PNArc(p1, t2, "a2", "a2", Elementdeclerations.pnInhibitorArc, "2"));
		graph.add(new PNArc(p1, t3, "a3", "a3", Elementdeclerations.pnTestArc, "1 + sin(3)"));
		for (int i = 3; i <= 8; i++) {
			final var n = new DiscretePlace("poly" + i, "poly" + i, null);
			n.setNodeShape(new RegularPolygonShape(i));
			graph.add(n, new Point2D.Float(-200, -200 + i * 50));
		}
		for (int i = 4; i <= 8; i++) {
			final var n = new DiscretePlace("star" + i, "star" + i, null);
			n.setNodeShape(new RegularStarShape(i));
			graph.add(n, new Point2D.Float(-300, -200 + i * 50));
		}
		final var rect = new DiscretePlace("rect", "rect", null);
		rect.setNodeShape(new RectangleShape());
		graph.add(rect, new Point2D.Float(-200, -100));
		final var roundRect = new DiscretePlace("rrect", "rrect", null);
		roundRect.setNodeShape(new RoundedRectangleShape());
		graph.add(roundRect, new Point2D.Float(-300, -100));

		final JFrame frame = new JFrame();
		frame.setSize(800, 600);
		frame.setContentPane(new GraphRendererPanel<>(graph));
		frame.setVisible(true);
		while (frame.isVisible()) {
			Thread.sleep(1);
		}
	}
}
