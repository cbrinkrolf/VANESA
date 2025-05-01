package simulation;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class VanesaExpressionConfigurationTest {
	@Test
	void testAllFunctions() throws EvaluationException, ParseException {
		// ABS
		assertNumberExpression("10.1", "ABS(10.1)");
		assertNumberExpression("10.1", "abs(-10.1)");
		// ACOS
		assertNumberExpression("0", "ACOS(1)");
		assertNumberExpression("90", "acos(0)");
		// ACOSH
		// ACOT
		// ACOTH
		// ASIN
		// ASINH
		// ATAN
		// ATAN2
		// ATANH
		// BN
		// CEIL/CEILING
		assertNumberExpression("11", "CEIL(10.1)");
		assertNumberExpression("-10", "ceil(-10.1)");
		assertNumberExpression("11", "ceil(11)");
		assertNumberExpression("11", "CEILING(10.1)");
		assertNumberExpression("-10", "ceiling(-10.1)");
		assertNumberExpression("11", "ceiling(11)");
		// COS
		// COSH
		// COT
		// COTH
		// CSC
		// CSCH
		// DEG
		// E
		// EXP
		// FACT
		// FLOOR
		assertNumberExpression("10", "FLOOR(10.1)");
		assertNumberExpression("-11", "floor(-10.1)");
		assertNumberExpression("11", "floor(11)");
		// FRACTIONALPART
		// GAMMA
		// IF
		// INTEGRALPART
		// LOG
		// LOG10
		// LOG2
		// MAX
		// MIN
		// PI
		// RAD
		// RECIPROCAL
		// ROOT
		// ROUND
		// SEC
		// SECH
		// SIN
		assertNumberExpression("1", "SIN(90)");
		assertNumberExpression("0", "sin(0)");
		assertNumberExpression("-1", "sin(270)");
		// SINH
		// SQRT
		// TAN
		// TANH
	}

	@Test
	void testAnd() throws EvaluationException, ParseException {
		assertFalse(evaluateExpression("AND(false, false)").getBooleanValue());
		assertFalse(evaluateExpression("AND(true, false)").getBooleanValue());
		assertFalse(evaluateExpression("AND(false, true)").getBooleanValue());
		assertTrue(evaluateExpression("AND(true, true)").getBooleanValue());
	}

	@Test
	void testOr() throws EvaluationException, ParseException {
		assertFalse(evaluateExpression("OR(false, false)").getBooleanValue());
		assertTrue(evaluateExpression("OR(true, false)").getBooleanValue());
		assertTrue(evaluateExpression("OR(false, true)").getBooleanValue());
		assertTrue(evaluateExpression("OR(true, true)").getBooleanValue());
	}

	@Test
	void testNot() throws EvaluationException, ParseException {
		assertTrue(evaluateExpression("NOT(false)").getBooleanValue());
		assertFalse(evaluateExpression("NOT(true)").getBooleanValue());
	}

	private EvaluationValue evaluateExpression(final String formula) throws EvaluationException, ParseException {
		return createExpression(formula).evaluate();
	}

	private Expression createExpression(final String formula) {
		return new Expression(formula, VanesaExpressionConfiguration.EXPRESSION_CONFIGURATION);
	}

	private void assertNumberExpression(final String expected, final String formula)
			throws EvaluationException, ParseException {
		assertEquals(new BigDecimal(expected).toPlainString(), evaluateExpression(formula).getNumberValue()
				.toPlainString());
	}
}