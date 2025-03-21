package graph.rendering;

import biologicalElements.ElementDeclarations;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.ContinuousPlace;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscretePlace;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import configurations.Workspace;
import graph.Graph;
import graph.annotations.OvalAnnotation;
import graph.annotations.RectangleAnnotation;
import graph.annotations.VanesaAnnotation;
import graph.rendering.nodes.RectangleShape;
import graph.rendering.nodes.RegularPolygonShape;
import graph.rendering.nodes.RegularStarShape;
import graph.rendering.nodes.RoundedRectangleShape;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import transformation.graphElements.ANYPlace;
import transformation.graphElements.ANYTransition;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

class GraphRendererPanelTest {
	@Test
	@Disabled
	public void test() throws InterruptedException {
		Workspace.switchToDefaultWorkspace();
		final Graph<BiologicalNodeAbstract, BiologicalEdgeAbstract, VanesaAnnotation> graph = new Graph<>();
		RectangleAnnotation a1 = new RectangleAnnotation();
		a1.setWidth(100);
		a1.setHeight(50);
		a1.setColor(Color.CYAN);
		graph.add(a1, -50, -50);
		OvalAnnotation a2 = new OvalAnnotation();
		a2.setRadiusX(20);
		a2.setRadiusY(50);
		a2.setColor(Color.DARK_GRAY);
		graph.add(a2, 50, 50);
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
		graph.add(new PNArc(t1, p1, "a1", "a1", ElementDeclarations.pnArc, "1 + sqrt(4 / 3)"));
		graph.add(new PNArc(p1, t2, "a2", "a2", ElementDeclarations.pnInhibitorArc, "2"));
		graph.add(new PNArc(p1, t3, "a3", "a3", ElementDeclarations.pnTestArc, "1 + sin(3)"));
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
		while (graph.getNodeCount() < 2000) {
			graph.add(new DiscretePlace("", "", null), new Point2D.Double());
		}

		final JFrame frame = new JFrame();
		frame.setSize(800, 600);
		frame.setContentPane(new GraphRendererPanel<>(graph));
		frame.setVisible(true);
		while (frame.isVisible()) {
			Thread.sleep(1);
		}
	}
}
