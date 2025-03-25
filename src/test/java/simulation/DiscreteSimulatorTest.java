package simulation;

import biologicalElements.Elementdeclerations;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.petriNet.DiscretePlace;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class DiscreteSimulatorTest {
	@Test
	void simpleTransition() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenStart(1);
		final var p2 = new DiscretePlace("p2", "p2", null);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var arc1 = new PNArc(p1, t1, "arc1", "arc1", Elementdeclerations.pnArc, "1");
		final var arc2 = new PNArc(t1, p2, "arc2", "arc2", Elementdeclerations.pnArc, "1");
		final var simulator = new DiscreteSimulator(Arrays.asList(p1, p2, t1), Arrays.asList(arc1, arc2));
		assertEquals(1, simulator.getMarkings().size());
		assertEquals(0, simulator.getEdges().size());
		simulator.step();
		assertEquals(2, simulator.getMarkings().size());
		assertEquals(1, simulator.getEdges().size());
		assertEquals(t1, simulator.getEdges().iterator().next().transition);
		final var markingIterator = simulator.getMarkings().iterator();
		final DiscreteSimulator.Marking startMarking = markingIterator.next();
		final DiscreteSimulator.Marking endMarking = markingIterator.next();
		assertFalse(startMarking.isDead());
		assertEquals(BigInteger.ONE, simulator.getTokens(startMarking, p1));
		assertEquals(BigInteger.ZERO, simulator.getTokens(startMarking, p2));
		assertTrue(endMarking.isDead());
		assertEquals(BigInteger.ZERO, simulator.getTokens(endMarking, p1));
		assertEquals(BigInteger.ONE, simulator.getTokens(endMarking, p2));
	}

	@Test
	void simpleTransitionRepeatedFire() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenStart(10);
		final var p2 = new DiscretePlace("p2", "p2", null);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var arc1 = new PNArc(p1, t1, "arc1", "arc1", Elementdeclerations.pnArc, "1");
		final var arc2 = new PNArc(t1, p2, "arc2", "arc2", Elementdeclerations.pnArc, "2");
		final var simulator = new DiscreteSimulator(Arrays.asList(p1, p2, t1), Arrays.asList(arc1, arc2));
		for (int i = 0; i < 10; i++) {
			simulator.step();
		}
		assertEquals(11, simulator.getMarkings().size());
		assertEquals(10, simulator.getEdges().size());
		final var markingIterator = simulator.getMarkings().iterator();
		final DiscreteSimulator.Marking startMarking = markingIterator.next();
		DiscreteSimulator.Marking endMarking = markingIterator.next();
		while (markingIterator.hasNext()) {
			endMarking = markingIterator.next();
		}
		assertFalse(startMarking.isDead());
		assertEquals(BigInteger.valueOf(10), simulator.getTokens(startMarking, p1));
		assertEquals(BigInteger.ZERO, simulator.getTokens(startMarking, p2));
		assertTrue(endMarking.isDead());
		assertEquals(BigInteger.ZERO, simulator.getTokens(endMarking, p1));
		assertEquals(BigInteger.valueOf(20), simulator.getTokens(endMarking, p2));
	}
}