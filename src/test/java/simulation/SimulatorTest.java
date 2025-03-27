package simulation;

import graph.gui.Parameter;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimulatorTest {
	@Test
	void findNextActivationTimeRange() {
		assertEquals(Simulator.TimeRange.ALWAYS, Simulator.findNextActivationTimeRange("true", null));
		assertEquals(Simulator.TimeRange.NEVER, Simulator.findNextActivationTimeRange("false", null));
		assertEquals(Simulator.TimeRange.NEVER, Simulator.findNextActivationTimeRange("1>5", null));
		assertEquals(Simulator.TimeRange.ALWAYS,
				Simulator.findNextActivationTimeRange("x>4", List.of(new Parameter("x", BigDecimal.valueOf(5), ""))));
		assertNull(Simulator.findNextActivationTimeRange("ABC<10", null));
	}
}