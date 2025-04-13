package simulation;

import biologicalElements.Elementdeclerations;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.petriNet.DiscretePlace;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import graph.gui.Parameter;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiscreteSimulatorTest {
	/**
	 * Equivalent to <a href=
	 * "https://github.com/AMIT-HSBI/PNlib/blob/master/PNlib/Examples/DisTest/SinglePD.mo">SinglePD.mo</a>
	 */
	@Test
	void pnlibSinglePD() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		final var simulator = new DiscreteSimulator(List.of(p1), new ArrayList<>());
		for (int i = 0; i < 10; i++) {
			simulator.step();
		}
		final var markingTimeline = simulator.getMarkingTimeline();
		assertEquals(1, markingTimeline.length);
		assertEquals(0, simulator.getMaxTime().intValue());
	}

	/**
	 * Equivalent to <a href=
	 * "https://github.com/AMIT-HSBI/PNlib/blob/master/PNlib/Examples/DisTest/SingleTD.mo">SingleTD.mo</a>
	 */
	@Test
	void pnlibSingleTD() throws SimulationException {
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var simulator = new DiscreteSimulator(List.of(t1), new ArrayList<>());
		for (int i = 0; i < 10; i++) {
			simulator.step();
		}
		final var markingTimeline = simulator.getMarkingTimeline();
		assertEquals(11, markingTimeline.length);
		assertEquals(10, simulator.getMaxTime().intValue());
	}

	/**
	 * Equivalent to <a href=
	 * "https://github.com/AMIT-HSBI/PNlib/blob/master/PNlib/Examples/DisTest/PDtoTD.mo">PDtoTD.mo</a>
	 */
	@Test
	void pnlibPDtoTD() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenStart(10);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var arc1 = new PNArc(p1, t1, "arc1", "arc1");
		final var simulator = new DiscreteSimulator(List.of(p1, t1), List.of(arc1));
		for (int i = 0; i < 10; i++) {
			simulator.step();
		}
		final var markingTimeline = simulator.getMarkingTimeline();
		assertEquals(11, markingTimeline.length);
		assertEquals(10, simulator.getMaxTime().intValue());
		assertPlaceTokensTimeline(simulator, markingTimeline, p1, new int[] { 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 });
	}

	/**
	 * Equivalent to <a href=
	 * "https://github.com/AMIT-HSBI/PNlib/blob/master/PNlib/Examples/DisTest/PDtoTDfunction.mo">PDtoTDfunction.mo</a>
	 */
	@Test
	void pnlibPDtoTDfunction() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenStart(10);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var arc1 = new PNArc(p1, t1, "arc1", "arc1", Elementdeclerations.pnArc, "p1");
		final var simulator = new DiscreteSimulator(List.of(p1, t1), List.of(arc1));
		for (int i = 0; i < 4; i++) {
			simulator.step();
		}
		final var markingTimeline = simulator.getMarkingTimeline();
		assertPlaceTokensTimeline(simulator, markingTimeline, p1, new int[] { 10, 0, 0, 0, 0 });
	}

	/**
	 * Equivalent to <a href=
	 * "https://github.com/AMIT-HSBI/PNlib/blob/master/PNlib/Examples/DisTest/TDtoPD.mo">TDtoPD.mo</a>
	 */
	@Test
	void pnlibTDtoPD() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var arc1 = new PNArc(t1, p1, "arc1", "arc1");
		final var simulator = new DiscreteSimulator(List.of(p1, t1), List.of(arc1));
		for (int i = 0; i < 10; i++) {
			simulator.step();
		}
		final var markingTimeline = simulator.getMarkingTimeline();
		assertEquals(11, markingTimeline.length);
		assertEquals(10, simulator.getMaxTime().intValue());
		assertPlaceTokensTimeline(simulator, markingTimeline, p1, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
	}

	/**
	 * Equivalent to <a href=
	 * "https://github.com/AMIT-HSBI/PNlib/blob/master/PNlib/Examples/DisTest/TDtoPDfunction.mo">TDtoPDfunction.mo</a>
	 */
	@Test
	void pnlibTDtoPDfunction() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenStart(1);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var arc1 = new PNArc(t1, p1, "arc1", "arc1", Elementdeclerations.pnArc, "p1");
		final var simulator = new DiscreteSimulator(List.of(p1, t1), List.of(arc1));
		for (int i = 0; i < 4; i++) {
			simulator.step();
		}
		final var markingTimeline = simulator.getMarkingTimeline();
		assertPlaceTokensTimeline(simulator, markingTimeline, p1, new int[] { 1, 2, 4, 8, 16 });
	}

	/**
	 * Equivalent to <a href=
	 * "https://github.com/AMIT-HSBI/PNlib/blob/master/PNlib/Examples/DisTest/DisLoopAndArcweight.mo">DisLoopAndArcweight.mo</a>
	 */
	@Test
	void pnlibDisLoopAndArcweight() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenStart(2);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var arc1 = new PNArc(p1, t1, "arc1", "arc1", Elementdeclerations.pnArc, "2");
		final var arc2 = new PNArc(t1, p1, "arc2", "arc2", Elementdeclerations.pnArc, "3");
		final var simulator = new DiscreteSimulator(List.of(p1, t1), List.of(arc1, arc2));
		for (int i = 0; i < 4; i++) {
			simulator.step();
		}
		final var markingTimeline = simulator.getMarkingTimeline();
		assertPlaceTokensTimeline(simulator, markingTimeline, p1, new int[] { 2, 3, 4, 5, 6 });
	}

	/**
	 * Equivalent to <a href=
	 * "https://github.com/AMIT-HSBI/PNlib/blob/master/PNlib/Examples/DisTest/NoInputConflict.mo">NoInputConflict.mo</a>
	 */
	@Test
	void pnlibNoInputConflict() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenMax(1);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var t2 = new DiscreteTransition("t2", "t2", null);
		t2.setDelay("2");
		final var arc1 = new PNArc(t1, p1, "arc1", "arc1");
		arc1.setPriority(2);
		final var arc2 = new PNArc(t2, p1, "arc2", "arc2");
		arc2.setPriority(1);
		final var simulator = new DiscreteSimulator(List.of(p1, t1, t2), List.of(arc1, arc2));
		while (!simulator.isDead()) {
			simulator.step();
		}
		final var markingTimeline = simulator.getMarkingTimeline();
		assertEquals(2, markingTimeline.length);
		assertEquals(BigInteger.valueOf(0), simulator.getTokens(markingTimeline[0], p1));
		assertEquals(BigInteger.valueOf(1), simulator.getTokens(markingTimeline[1], p1));
	}

	/**
	 * Equivalent to <a href=
	 * "https://github.com/AMIT-HSBI/PNlib/blob/master/PNlib/Examples/DisTest/PrioTest.mo">PrioTest.mo</a>
	 */
	@Test
	void pnlibPrioTest() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenStart(1);
		p1.setConflictStrategy(ConflictHandling.Priority);
		final var p2 = new DiscretePlace("p2", "p2", null);
		p2.setTokenMax(1);
		final var p3 = new DiscretePlace("p3", "p3", null);
		p3.setTokenMax(1);
		final var p4 = new DiscretePlace("p4", "p4", null);
		p4.setTokenMax(1);
		final var p5 = new DiscretePlace("p5", "p5", null);
		p5.setTokenMax(1);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var t2 = new DiscreteTransition("t2", "t2", null);
		final var t3 = new DiscreteTransition("t3", "t3", null);
		final var t4 = new DiscreteTransition("t4", "t4", null);
		final var t5 = new DiscreteTransition("t5", "t5", null);
		final var arc1 = new PNArc(t1, p1, "arc1", "arc1", Elementdeclerations.pnArc, "1");
		final var arc2 = new PNArc(p1, t2, "arc2", "arc2", Elementdeclerations.pnArc, "1");
		arc2.setPriority(4);
		final var arc3 = new PNArc(p1, t3, "arc3", "arc3", Elementdeclerations.pnArc, "1");
		arc3.setPriority(3);
		final var arc4 = new PNArc(p1, t4, "arc4", "arc4", Elementdeclerations.pnArc, "1");
		arc4.setPriority(2);
		final var arc5 = new PNArc(p1, t5, "arc5", "arc5", Elementdeclerations.pnArc, "1");
		arc5.setPriority(1);
		final var arc6 = new PNArc(t2, p2, "arc6", "arc6", Elementdeclerations.pnArc, "1");
		final var arc7 = new PNArc(t3, p3, "arc7", "arc7", Elementdeclerations.pnArc, "1");
		final var arc8 = new PNArc(t4, p4, "arc8", "arc8", Elementdeclerations.pnArc, "1");
		final var arc9 = new PNArc(t5, p5, "arc9", "arc9", Elementdeclerations.pnArc, "1");
		final var simulator = new DiscreteSimulator(List.of(t1, p1, t2, p2, t3, p3, t4, p4, t5, p5),
				List.of(arc1, arc2, arc3, arc4, arc5, arc6, arc7, arc8, arc9));
		for (int i = 0; i < 5; i++) {
			simulator.step();
		}
		final var markingTimeline = simulator.getMarkingTimeline();
		assertEquals(BigInteger.valueOf(1), simulator.getTokens(markingTimeline[1], p5));
		assertEquals(BigInteger.valueOf(1), simulator.getTokens(markingTimeline[2], p4));
		assertEquals(BigInteger.valueOf(1), simulator.getTokens(markingTimeline[3], p3));
		assertEquals(BigInteger.valueOf(1), simulator.getTokens(markingTimeline[4], p2));
		assertEquals(BigInteger.valueOf(2), simulator.getTokens(markingTimeline[5], p1));
	}

	/**
	 * Equivalent to <a href=
	 * "https://github.com/AMIT-HSBI/PNlib/blob/master/PNlib/Examples/DisTest/ConflictPrio.mo">ConflictPrio.mo</a>
	 */
	@Test
	void pnlibConflictPrio() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenStart(2);
		p1.setConflictStrategy(ConflictHandling.Priority);
		final var p2 = new DiscretePlace("p2", "p2", null);
		final var p3 = new DiscretePlace("p3", "p3", null);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var t2 = new DiscreteTransition("t2", "t2", null);
		final var t3 = new DiscreteTransition("t3", "t3", null);
		final var arc1 = new PNArc(t3, p1, "arc1", "arc1");
		final var arc2 = new PNArc(p1, t1, "arc2", "arc2");
		arc2.setPriority(1);
		final var arc3 = new PNArc(p1, t2, "arc3", "arc3");
		arc3.setPriority(2);
		final var arc4 = new PNArc(t1, p2, "arc4", "arc4");
		final var arc5 = new PNArc(t2, p3, "arc5", "arc5");
		final var simulator = new DiscreteSimulator(List.of(t1, p1, t2, p2, t3, p3),
				List.of(arc1, arc2, arc3, arc4, arc5));
		for (int i = 0; i < 9; i++) {
			simulator.step();
		}
		final var markingTimeline = simulator.getMarkingTimeline();
		assertPlaceTokensTimeline(simulator, markingTimeline, p1, new int[] { 2, 1, 1, 1, 1, 1, 1, 1, 1, 1 });
		assertPlaceTokensTimeline(simulator, markingTimeline, p2, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
		assertPlaceTokensTimeline(simulator, markingTimeline, p3, new int[] { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 });
	}

	/**
	 * Equivalent to <a href=
	 * "https://github.com/AMIT-HSBI/PNlib/blob/master/PNlib/Examples/DisTest/OutputConflictPrio.mo">OutputConflictPrio.mo</a>
	 */
	@Test
	void pnlibOutputConflictPrio() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenStart(2);
		p1.setTokenMin(2);
		p1.setConflictStrategy(ConflictHandling.Priority);
		final var p2 = new DiscretePlace("p2", "p2", null);
		final var p3 = new DiscretePlace("p3", "p3", null);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var t2 = new DiscreteTransition("t2", "t2", null);
		final var t3 = new DiscreteTransition("t3", "t3", null);
		final var arc1 = new PNArc(t3, p1, "arc1", "arc1");
		final var arc2 = new PNArc(p1, t1, "arc2", "arc2");
		arc2.setPriority(1);
		final var arc3 = new PNArc(p1, t2, "arc3", "arc3");
		arc3.setPriority(2);
		final var arc4 = new PNArc(t1, p2, "arc4", "arc4");
		final var arc5 = new PNArc(t2, p3, "arc5", "arc5");
		final var simulator = new DiscreteSimulator(List.of(t1, p1, t2, p2, t3, p3),
				List.of(arc1, arc2, arc3, arc4, arc5));
		for (int i = 0; i < 9; i++) {
			simulator.step();
		}
		final var markingTimeline = simulator.getMarkingTimeline();
		assertPlaceTokensTimeline(simulator, markingTimeline, p1, new int[] { 2, 3, 3, 3, 3, 3, 3, 3, 3, 3 });
		assertPlaceTokensTimeline(simulator, markingTimeline, p2, new int[] { 0, 0, 1, 2, 3, 4, 5, 6, 7, 8 });
		assertPlaceTokensTimeline(simulator, markingTimeline, p3, new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
	}

	/**
	 * Equivalent to <a href=
	 * "https://github.com/AMIT-HSBI/PNlib/blob/master/PNlib/Examples/DisTest/InputConflictPrio.mo">InputConflictPrio.mo</a>
	 */
	@Test
	void pnlibInputConflictPrio() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenMax(2);
		p1.setConflictStrategy(ConflictHandling.Priority);
		final var p2 = new DiscretePlace("p2", "p2", null);
		p2.setTokenStart(5);
		final var p3 = new DiscretePlace("p3", "p3", null);
		p3.setTokenStart(5);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var t2 = new DiscreteTransition("t2", "t2", null);
		final var t3 = new DiscreteTransition("t3", "t3", null);
		final var arc1 = new PNArc(p1, t1, "arc1", "arc1");
		final var arc2 = new PNArc(p2, t2, "arc2", "arc2");
		final var arc3 = new PNArc(p3, t3, "arc3", "arc3");
		final var arc4 = new PNArc(t2, p1, "arc4", "arc4");
		arc4.setPriority(1);
		final var arc5 = new PNArc(t3, p1, "arc5", "arc5");
		arc5.setPriority(2);
		final var simulator = new DiscreteSimulator(List.of(t1, p1, t2, p2, t3, p3),
				List.of(arc1, arc2, arc3, arc4, arc5));
		for (int i = 0; i < 9; i++) {
			simulator.step();
		}
		final var markingTimeline = simulator.getMarkingTimeline();
		assertPlaceTokensTimeline(simulator, markingTimeline, p1, new int[] { 0, 2, 1, 1, 1, 1, 1, 1, 1, 1 });
		assertPlaceTokensTimeline(simulator, markingTimeline, p2, new int[] { 5, 4, 4, 3, 2, 1, 0, 0, 0, 0 });
		assertPlaceTokensTimeline(simulator, markingTimeline, p3, new int[] { 5, 4, 4, 4, 4, 4, 4, 3, 2, 1 });
	}

	// TODO:
	// ConflictBeneBaB.mo
	// ConflictBeneGreedy.mo
	// ConflictBeneQuotient.mo
	// ConflictProb.mo
	// EightConflictProb.mo
	// InputConflictBeneBaB.mo
	// InputConflictBeneGreedy.mo
	// InputConflictBeneQuotient.mo
	// InputConflictProb.mo
	// OutputConflictBeneBaB.mo
	// OutputConflictBeneGreedy.mo
	// OutputConflictBeneQuotient.mo
	// OutputConflictProb.mo
	// SixConflictProb.mo

	@Test
	void singleTransition() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenStart(1);
		final var p2 = new DiscretePlace("p2", "p2", null);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var arc1 = new PNArc(p1, t1, "arc1", "arc1", Elementdeclerations.pnArc, "1");
		final var arc2 = new PNArc(t1, p2, "arc2", "arc2", Elementdeclerations.pnArc, "1");
		final var simulator = new DiscreteSimulator(List.of(p1, p2, t1), List.of(arc1, arc2));
		assertEquals(1, simulator.getMarkings().size());
		assertEquals(0, simulator.getEdges().size());
		simulator.step();
		assertEquals(2, simulator.getMarkings().size());
		assertEquals(1, simulator.getEdges().size());
		assertEquals(t1, simulator.getEdges().iterator().next().transitions[0].transition);
		final var markingTimeline = simulator.getMarkingTimeline();
		assertFalse(markingTimeline[0].isDead());
		assertEquals(BigInteger.ONE, simulator.getTokens(markingTimeline[0], p1));
		assertEquals(BigInteger.ZERO, simulator.getTokens(markingTimeline[0], p2));
		assertTrue(markingTimeline[1].isDead());
		assertEquals(BigInteger.ZERO, simulator.getTokens(markingTimeline[1], p1));
		assertEquals(BigInteger.ONE, simulator.getTokens(markingTimeline[1], p2));
	}

	@Test
	void singleTransitionRepeatedFire() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenStart(10);
		final var p2 = new DiscretePlace("p2", "p2", null);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var arc1 = new PNArc(p1, t1, "arc1", "arc1", Elementdeclerations.pnArc, "1");
		final var arc2 = new PNArc(t1, p2, "arc2", "arc2", Elementdeclerations.pnArc, "2");
		final var simulator = new DiscreteSimulator(List.of(p1, p2, t1), List.of(arc1, arc2));
		for (int i = 0; i < 10; i++) {
			simulator.step();
		}
		assertEquals(11, simulator.getMarkings().size());
		assertEquals(10, simulator.getEdges().size());
		final var markingTimeline = simulator.getMarkingTimeline();
		assertFalse(markingTimeline[0].isDead());
		assertEquals(BigInteger.valueOf(10), simulator.getTokens(markingTimeline[0], p1));
		assertEquals(BigInteger.ZERO, simulator.getTokens(markingTimeline[0], p2));
		assertTrue(markingTimeline[markingTimeline.length - 1].isDead());
		assertEquals(BigInteger.ZERO, simulator.getTokens(markingTimeline[markingTimeline.length - 1], p1));
		assertEquals(BigInteger.valueOf(20), simulator.getTokens(markingTimeline[markingTimeline.length - 1], p2));
	}

	@Test
	void singleTransitionWithArcFunctions() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenStart(10);
		final var p2 = new DiscretePlace("p2", "p2", null);
		p2.setTokenStart(0);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var arc1 = new PNArc(p1, t1, "arc1", "arc1", Elementdeclerations.pnArc, "1 + 2");
		final var arc2 = new PNArc(t1, p2, "arc2", "arc2", Elementdeclerations.pnArc, "2 + 3");
		final var simulator = new DiscreteSimulator(List.of(p1, p2, t1), List.of(arc1, arc2));
		simulator.step();
		final var markingTimeline = simulator.getMarkingTimeline();
		assertTrue(markingTimeline.length > 1);
		assertFalse(markingTimeline[1].isDead());
		assertEquals(BigInteger.valueOf(10), simulator.getTokens(markingTimeline[0], p1));
		assertEquals(BigInteger.valueOf(7), simulator.getTokens(markingTimeline[1], p1));
		assertEquals(BigInteger.valueOf(0), simulator.getTokens(markingTimeline[0], p2));
		assertEquals(BigInteger.valueOf(5), simulator.getTokens(markingTimeline[1], p2));
	}

	@Test
	void singleTransitionWithArcFunctionsAndParameter() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenStart(10);
		final var p2 = new DiscretePlace("p2", "p2", null);
		p2.setTokenStart(0);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var arc1 = new PNArc(p1, t1, "arc1", "arc1", Elementdeclerations.pnArc, "1 + k");
		arc1.getParameters().add(new Parameter("k", BigDecimal.valueOf(2), ""));
		final var arc2 = new PNArc(t1, p2, "arc2", "arc2", Elementdeclerations.pnArc, "m + 3");
		arc2.getParameters().add(new Parameter("m", BigDecimal.valueOf(2), ""));
		final var simulator = new DiscreteSimulator(List.of(p1, p2, t1), List.of(arc1, arc2));
		simulator.step();
		final var markingTimeline = simulator.getMarkingTimeline();
		assertTrue(markingTimeline.length > 1);
		assertFalse(markingTimeline[1].isDead());
		assertEquals(BigInteger.valueOf(10), simulator.getTokens(markingTimeline[0], p1));
		assertEquals(BigInteger.valueOf(7), simulator.getTokens(markingTimeline[1], p1));
		assertEquals(BigInteger.valueOf(0), simulator.getTokens(markingTimeline[0], p2));
		assertEquals(BigInteger.valueOf(5), simulator.getTokens(markingTimeline[1], p2));
	}

	@Test
	void singleTransitionWithArcFunctionsAndVariable() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenStart(10);
		final var p2 = new DiscretePlace("p2", "p2", null);
		p2.setTokenStart(0);
		final var p3 = new DiscretePlace("p3", "p3", null);
		p3.setTokenStart(2);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var arc1 = new PNArc(p1, t1, "arc1", "arc1", Elementdeclerations.pnArc, "1 + p3");
		final var arc2 = new PNArc(t1, p2, "arc2", "arc2", Elementdeclerations.pnArc, "p3 + 3");
		final var simulator = new DiscreteSimulator(List.of(p1, p2, p3, t1), List.of(arc1, arc2));
		simulator.step();
		final var markingTimeline = simulator.getMarkingTimeline();
		assertTrue(markingTimeline.length > 1);
		assertFalse(markingTimeline[1].isDead());
		assertEquals(BigInteger.valueOf(10), simulator.getTokens(markingTimeline[0], p1));
		assertEquals(BigInteger.valueOf(7), simulator.getTokens(markingTimeline[1], p1));
		assertEquals(BigInteger.valueOf(0), simulator.getTokens(markingTimeline[0], p2));
		assertEquals(BigInteger.valueOf(5), simulator.getTokens(markingTimeline[1], p2));
	}

	@Test
	void singleTransitionWithDelayFunction() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenStart(10);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		t1.setDelay("1 + 2");
		final var arc1 = new PNArc(p1, t1, "arc1", "arc1", Elementdeclerations.pnArc, "1");
		final var simulator = new DiscreteSimulator(List.of(p1, t1), List.of(arc1));
		simulator.step();
		simulator.step();
		final var markingTimeline = simulator.getMarkingTimeline();
		assertTrue(markingTimeline.length > 1);
		assertEquals(BigDecimal.valueOf(0), markingTimeline[0].time);
		assertEquals(BigDecimal.valueOf(3), markingTimeline[1].time);
		assertEquals(BigDecimal.valueOf(6), markingTimeline[2].time);
	}

	@Test
	void singleTransitionAssuringNonNegativeArcWeights() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenStart(4);
		final var p2 = new DiscretePlace("p2", "p2", null);
		p2.setTokenStart(0);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var arc1 = new PNArc(p1, t1, "arc1", "arc1", Elementdeclerations.pnArc, "1 - p2");
		final var arc2 = new PNArc(t1, p2, "arc2", "arc2", Elementdeclerations.pnArc, "1");
		final var simulator = new DiscreteSimulator(List.of(p1, p2, t1), List.of(arc1, arc2));
		simulator.step();
		simulator.step();
		simulator.step();
		final var markingTimeline = simulator.getMarkingTimeline();
		assertEquals(3, markingTimeline.length);
		assertFalse(markingTimeline[1].isDead());
		assertTrue(markingTimeline[2].isDead());
		assertEquals(BigInteger.valueOf(3), simulator.getTokens(markingTimeline[2], p1));
		assertEquals(BigInteger.valueOf(2), simulator.getTokens(markingTimeline[2], p2));
	}

	@Test
	void activatingSingleTransitionNoPrePlace() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenStart(0);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var arc1 = new PNArc(t1, p1, "arc1", "arc1", Elementdeclerations.pnArc, "1");
		final var simulator = new DiscreteSimulator(List.of(p1, t1), List.of(arc1));
		simulator.step();
		simulator.step();
		simulator.step();
		final var markingTimeline = simulator.getMarkingTimeline();
		assertTrue(markingTimeline.length > 1);
		assertFalse(markingTimeline[3].isDead());
		assertEquals(BigInteger.valueOf(0), simulator.getTokens(markingTimeline[0], p1));
		assertEquals(BigInteger.valueOf(3), simulator.getTokens(markingTimeline[3], p1));
	}

	@Test
	void divisionByZeroThrowsException() {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenStart(0);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var arc1 = new PNArc(t1, p1, "arc1", "arc1", Elementdeclerations.pnArc, "1/0");
		assertThrows(SimulationException.class, () -> new DiscreteSimulator(List.of(p1, t1), List.of(arc1)));
	}

	@Test
	void singleTransitionWithMinCapacity() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenStart(10);
		p1.setTokenMin(8);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var arc1 = new PNArc(p1, t1, "arc1", "arc1", Elementdeclerations.pnArc, "1");
		final var simulator = new DiscreteSimulator(List.of(p1, t1), List.of(arc1));
		for (int i = 0; i < 2; i++) {
			simulator.step();
		}
		final var markingTimeline = simulator.getMarkingTimeline();
		assertFalse(markingTimeline[0].isDead());
		assertFalse(markingTimeline[1].isDead());
		assertTrue(markingTimeline[2].isDead());
		assertEquals(BigInteger.valueOf(9), simulator.getTokens(markingTimeline[1], p1));
		assertEquals(BigInteger.valueOf(8), simulator.getTokens(markingTimeline[2], p1));
	}

	@Test
	void singleTransitionWithMaxCapacity() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenMax(2);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var arc1 = new PNArc(t1, p1, "arc1", "arc1", Elementdeclerations.pnArc, "1");
		final var simulator = new DiscreteSimulator(List.of(p1, t1), List.of(arc1));
		for (int i = 0; i < 2; i++) {
			simulator.step();
		}
		final var markingTimeline = simulator.getMarkingTimeline();
		assertFalse(markingTimeline[0].isDead());
		assertFalse(markingTimeline[1].isDead());
		assertTrue(markingTimeline[2].isDead());
		assertEquals(BigInteger.valueOf(1), simulator.getTokens(markingTimeline[1], p1));
		assertEquals(BigInteger.valueOf(2), simulator.getTokens(markingTimeline[2], p1));
	}

	@Test
	void singleTransitionWithConstantPlaces() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenStart(10);
		p1.setConstant(true);
		final var p2 = new DiscretePlace("p2", "p2", null);
		p2.setTokenStart(4);
		p2.setConstant(true);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var arc1 = new PNArc(p1, t1, "arc1", "arc1", Elementdeclerations.pnArc, "1");
		final var arc2 = new PNArc(t1, p2, "arc2", "arc2", Elementdeclerations.pnArc, "1");
		final var simulator = new DiscreteSimulator(List.of(p1, p2, t1), List.of(arc1, arc2));
		for (int i = 0; i < 10; i++) {
			simulator.step();
		}
		final var markingTimeline = simulator.getMarkingTimeline();
		assertEquals(11, markingTimeline.length);
		for (final var marking : markingTimeline) {
			assertFalse(marking.isDead());
			assertEquals(BigInteger.valueOf(10), simulator.getTokens(marking, p1));
			assertEquals(BigInteger.valueOf(4), simulator.getTokens(marking, p2));
		}
	}

	@Test
	void singleTransitionWithTestArcTooFewTokens() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenStart(9);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var arc1 = new PNArc(p1, t1, "arc1", "arc1", Elementdeclerations.pnTestArc, "10");
		final var simulator = new DiscreteSimulator(List.of(p1, t1), List.of(arc1));
		assertTrue(simulator.getStartMarking().isDead());
	}

	@Test
	void singleTransitionWithTestArcEnoughTokens() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenStart(10);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var arc1 = new PNArc(p1, t1, "arc1", "arc1", Elementdeclerations.pnTestArc, "10");
		final var simulator = new DiscreteSimulator(List.of(p1, t1), List.of(arc1));
		simulator.step();
		simulator.step();
		final var markingTimeline = simulator.getMarkingTimeline();
		assertFalse(markingTimeline[0].isDead());
		assertFalse(markingTimeline[1].isDead());
		assertFalse(markingTimeline[2].isDead());
	}

	@Test
	void singleTransitionWithInhibitionArcTooFewTokens() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenStart(9);
		final var p2 = new DiscretePlace("p2", "p2", null);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var arc1 = new PNArc(p1, t1, "arc1", "arc1", Elementdeclerations.pnInhibitorArc, "10");
		final var arc2 = new PNArc(t1, p2, "arc2", "arc2", Elementdeclerations.pnArc, "1");
		final var simulator = new DiscreteSimulator(List.of(p1, p2, t1), List.of(arc1, arc2));
		simulator.step();
		simulator.step();
		final var markingTimeline = simulator.getMarkingTimeline();
		assertFalse(markingTimeline[0].isDead());
		assertFalse(markingTimeline[1].isDead());
		assertFalse(markingTimeline[2].isDead());
		assertEquals(BigInteger.valueOf(9), simulator.getTokens(markingTimeline[1], p1));
		assertEquals(BigInteger.valueOf(1), simulator.getTokens(markingTimeline[1], p2));
		assertEquals(BigInteger.valueOf(9), simulator.getTokens(markingTimeline[2], p1));
		assertEquals(BigInteger.valueOf(2), simulator.getTokens(markingTimeline[2], p2));
	}

	@Test
	void singleTransitionWithInhibitionArcEnoughTokens() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenStart(10);
		final var p2 = new DiscretePlace("p2", "p2", null);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var arc1 = new PNArc(p1, t1, "arc1", "arc1", Elementdeclerations.pnInhibitorArc, "10");
		final var arc2 = new PNArc(t1, p2, "arc2", "arc2", Elementdeclerations.pnArc, "1");
		final var simulator = new DiscreteSimulator(List.of(p1, p2, t1), List.of(arc1, arc2));
		assertTrue(simulator.getStartMarking().isDead());
	}

	/**
	 * The idea of this test is that four transitions all gain concession at time=0 with a delay of 1 each.
	 * As they retain concession at each step, all four transitions must have fired after four simulation steps and
	 * p2-p5 must have gained one token each.
	 */
	@Test
	void transitionsWithSameDelayAllFire() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenStart(4);
		final var p2 = new DiscretePlace("p2", "p2", null);
		final var p3 = new DiscretePlace("p3", "p3", null);
		final var p4 = new DiscretePlace("p4", "p4", null);
		final var p5 = new DiscretePlace("p5", "p5", null);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var t2 = new DiscreteTransition("t2", "t2", null);
		final var t3 = new DiscreteTransition("t3", "t3", null);
		final var t4 = new DiscreteTransition("t4", "t4", null);
		final var arc1 = new PNArc(p1, t1, "arc1", "arc1", Elementdeclerations.pnArc, "1");
		final var arc2 = new PNArc(p1, t2, "arc2", "arc2", Elementdeclerations.pnArc, "1");
		final var arc3 = new PNArc(p1, t3, "arc3", "arc3", Elementdeclerations.pnArc, "1");
		final var arc4 = new PNArc(p1, t4, "arc4", "arc4", Elementdeclerations.pnArc, "1");
		final var arc5 = new PNArc(t1, p2, "arc5", "arc5", Elementdeclerations.pnArc, "1");
		final var arc6 = new PNArc(t2, p3, "arc6", "arc6", Elementdeclerations.pnArc, "1");
		final var arc7 = new PNArc(t3, p4, "arc7", "arc7", Elementdeclerations.pnArc, "1");
		final var arc8 = new PNArc(t4, p5, "arc8", "arc8", Elementdeclerations.pnArc, "1");
		final var simulator = new DiscreteSimulator(List.of(p1, p2, p3, p4, p5, t1, t2, t3, t4),
				List.of(arc1, arc2, arc3, arc4, arc5, arc6, arc7, arc8));
		simulator.step();
		final var markingTimeline = simulator.getMarkingTimeline();
		assertFalse(markingTimeline[0].isDead());
		assertTrue(markingTimeline[1].isDead());
		assertEquals(BigInteger.valueOf(0), simulator.getTokens(markingTimeline[1], p1));
		assertEquals(BigInteger.valueOf(1), simulator.getTokens(markingTimeline[1], p2));
		assertEquals(BigInteger.valueOf(1), simulator.getTokens(markingTimeline[1], p3));
		assertEquals(BigInteger.valueOf(1), simulator.getTokens(markingTimeline[1], p4));
		assertEquals(BigInteger.valueOf(1), simulator.getTokens(markingTimeline[1], p5));
	}

	@Test
	void arcsRetainWeightsWithSameDelay() throws SimulationException {
		final var p1 = new DiscretePlace("p1", "p1", null);
		p1.setTokenStart(4);
		final var p2 = new DiscretePlace("p2", "p2", null);
		p2.setTokenStart(1);
		final var t1 = new DiscreteTransition("t1", "t1", null);
		final var t2 = new DiscreteTransition("t2", "t2", null);
		final var t3 = new DiscreteTransition("t3", "t3", null);
		final var arc1 = new PNArc(p1, t1, "arc1", "arc1", Elementdeclerations.pnArc, "p2");
		final var arc2 = new PNArc(p1, t2, "arc2", "arc2", Elementdeclerations.pnArc, "p2");
		final var arc3 = new PNArc(p1, t3, "arc3", "arc3", Elementdeclerations.pnArc, "p2");
		final var arc4 = new PNArc(t1, p2, "arc5", "arc5", Elementdeclerations.pnArc, "1");
		final var arc5 = new PNArc(t2, p2, "arc6", "arc6", Elementdeclerations.pnArc, "1");
		final var arc6 = new PNArc(t3, p2, "arc7", "arc7", Elementdeclerations.pnArc, "1");
		final var simulator = new DiscreteSimulator(List.of(p1, p2, t1, t2, t3),
				List.of(arc1, arc2, arc3, arc4, arc5, arc6));
		simulator.step();
		final var markingTimeline = simulator.getMarkingTimeline();
		assertEquals(BigInteger.valueOf(4), simulator.getTokens(markingTimeline[0], p1));
		assertEquals(BigInteger.valueOf(1), simulator.getTokens(markingTimeline[1], p1));
	}

	private static void assertPlaceTokensTimeline(final DiscreteSimulator simulator,
			final DiscreteSimulator.Marking[] markingTimeline, final DiscretePlace p, final int[] expectedTokens) {
		for (int i = 0; i < markingTimeline.length; i++) {
			assertEquals(BigInteger.valueOf(expectedTokens[i]), simulator.getTokens(markingTimeline[i], p));
		}
	}
}