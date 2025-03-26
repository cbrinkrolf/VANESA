package simulation;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VanesaExpressionConfigurationTest {
	@Test
	void testAnd() throws EvaluationException, ParseException {
		assertFalse(createExpression("AND(false, false)").evaluate().getBooleanValue());
		assertFalse(createExpression("AND(true, false)").evaluate().getBooleanValue());
		assertFalse(createExpression("AND(false, true)").evaluate().getBooleanValue());
		assertTrue(createExpression("AND(true, true)").evaluate().getBooleanValue());
	}

	@Test
	void testOr() throws EvaluationException, ParseException {
		assertFalse(createExpression("OR(false, false)").evaluate().getBooleanValue());
		assertTrue(createExpression("OR(true, false)").evaluate().getBooleanValue());
		assertTrue(createExpression("OR(false, true)").evaluate().getBooleanValue());
		assertTrue(createExpression("OR(true, true)").evaluate().getBooleanValue());
	}

	@Test
	void testNot() throws EvaluationException, ParseException {
		assertTrue(createExpression("NOT(false)").evaluate().getBooleanValue());
		assertFalse(createExpression("NOT(true)").evaluate().getBooleanValue());
	}

	private Expression createExpression(final String formula) {
		return new Expression(formula, VanesaExpressionConfiguration.EXPRESSION_CONFIGURATION);
	}
}