package simulation;

import com.ezylang.evalex.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VanesaExpressionTest {
	@Test
	void reduce() throws ParseException {
		assertReduction("1", "1");
		assertReduction("1+2", "3");
		assertReduction("(1+2)*5", "15");
		assertReduction("(1+2.0546)*a", "3.0546*a");
		assertReduction("min(10, max(4, 2))", "4");
		assertReduction("(a + (x / (4 * (2 + 3)))) * b * (1 + (r / (2 + 2)))", "(a+(x/20))*b*(1+(r/4))");
		assertReduction("(a + (x / (4 * (2 + 3)))) * b * (1 + (r / (2 + 2)))", "(a+5)*b*(1+(r/4))",
				new Map.Entry[] { Map.entry("x", 100) });
		assertReduction("(a + (x / (4 * (2 + 3)))) * b * (1 + (r / (2 + 2)))", "11*b*(1+(r/4))",
				new Map.Entry[] { Map.entry("x", 100), Map.entry("a", 6) });
		assertReduction("true", "true");
		assertReduction("and(true, false)", "false");
		assertReduction("and(true, (1+4)>2)", "true");
		assertReduction("and(true, a>2)", "a>2");
		assertReduction("and(false, a>2)", "false");
		assertReduction("or(true, a>2)", "true");
		assertReduction("or(false, a>2)", "a>2");
	}

	private void assertReduction(final String source, final String target) throws ParseException {
		assertEquals(target, new VanesaExpression(source).reduce(null));
	}

	private void assertReduction(final String source, final String target, final Map.Entry<String, Object>[] invariants)
			throws ParseException {
		assertEquals(target, new VanesaExpression(source).reduce(invariants));
	}
}