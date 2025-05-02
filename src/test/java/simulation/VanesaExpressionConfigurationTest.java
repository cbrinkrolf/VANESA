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
		assertNumberExpression("90", "ASIN(1)");
		assertNumberExpression("0", "asin(0)");
		// ASINH
		// ATAN
		// ATAN2
		// ATANH
		// CEIL/CEILING
		assertNumberExpression("11", "CEIL(10.1)");
		assertNumberExpression("-10", "ceil(-10.1)");
		assertNumberExpression("11", "ceil(11)");
		assertNumberExpression("11", "CEILING(10.1)");
		assertNumberExpression("-10", "ceiling(-10.1)");
		assertNumberExpression("11", "ceiling(11)");
		// COS
		assertNumberExpression("1", "COS(0)");
		assertNumberExpression("0", "cos(90)");
		// COSH
		// COT
		// COTH
		// CSC
		// CSCH
		// DEG
		assertNumberExpression("90", "DEG(PI/2)");
		assertNumberExpression("180", "deg(PI)");
		assertNumberExpression("0", "deg(0)");
		// E
		assertNumberExpression("2.71828182845904523536028747135266249775", "E");
		assertNumberExpression("2.71828182845904523536028747135266249775", "e");
		// EXP
		// FACT
		// FLOOR
		assertNumberExpression("10", "FLOOR(10.1)");
		assertNumberExpression("-11", "floor(-10.1)");
		assertNumberExpression("11", "floor(11)");
		// FRACTIONALPART
		assertNumberExpression("0.1", "FRACTIONALPART(10.1)");
		assertNumberExpression("0.12358", "fractionalpart(0.12358)");
		// GAMMA
		// IF
		// INTEGRALPART
		assertNumberExpression("10", "INTEGRALPART(10.1)");
		assertNumberExpression("0", "integralpart(0.12358)");
		// LOG
		// LOG10
		// LOG2
		// MAX
		assertNumberExpression("5", "MAX(4, 5)");
		assertNumberExpression("-1", "max(-1, -5)");
		// MIN
		assertNumberExpression("4", "MIN(4, 5)");
		assertNumberExpression("-5", "min(-1, -5)");
		// PI
		assertNumberExpression("3.14159265358979323846264338327950288419", "PI");
		assertNumberExpression("3.14159265358979323846264338327950288419", "pi");
		// RAD
		assertNumberExpression("1.57079632679489661923132169163975144209", "RAD(90)");
		assertNumberExpression("3.14159265358979323846264338327950288419", "rad(180)");
		assertNumberExpression("0", "rad(0)");
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
		assertNumberExpression("3", "SQRT(9)");
		assertNumberExpression("5", "sqrt(25)");
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
		final var result = evaluateExpression(formula).getNumberValue();
		final var difference = new BigDecimal(expected).subtract(result).abs();
		assertTrue(difference.compareTo(new BigDecimal("0.00000000000000001")) <= 0,
				"Expected " + formula + " to evaluate to " + expected + " (Actual: " + result + ", Difference: "
						+ difference + ')');
	}
}