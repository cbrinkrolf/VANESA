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
	void testAllOperators() throws EvaluationException, ParseException {
		assertNumberExpression("4", "+4");
		assertNumberExpression("-4", "-4");
		assertNumberExpression("5", "1+4");
		assertNumberExpression("-3", "1-4");
		assertNumberExpression("12", "4*3");
		assertNumberExpression("3", "12/4");
		assertNumberExpression("8", "2^3");
		assertNumberExpression("2", "10%4");
		assertTrue(evaluateExpression("4==1+3").getBooleanValue());
		assertTrue(evaluateExpression("4!=6").getBooleanValue());
		assertFalse(evaluateExpression("4!=4").getBooleanValue());
		assertTrue(evaluateExpression("4>3").getBooleanValue());
		assertTrue(evaluateExpression("4>=3").getBooleanValue());
		assertTrue(evaluateExpression("3<4").getBooleanValue());
		assertTrue(evaluateExpression("3<=4").getBooleanValue());
		assertTrue(evaluateExpression("true && 4==1+3").getBooleanValue());
		assertFalse(evaluateExpression("false && 4==1+3").getBooleanValue());
		assertTrue(evaluateExpression("true || 4==1+3").getBooleanValue());
		assertTrue(evaluateExpression("false || 4==1+3").getBooleanValue());
	}

	@Test
	void testAllConstants() throws EvaluationException, ParseException {
		// E
		assertNumberExpression("2.71828182845904523536028747135266249775", "E");
		assertNumberExpression("2.71828182845904523536028747135266249775", "e");
		// PI
		assertNumberExpression("3.14159265358979323846264338327950288419", "PI");
		assertNumberExpression("3.14159265358979323846264338327950288419", "pi");
		// TRUE
		assertTrue(evaluateExpression("true").getBooleanValue());
		assertTrue(evaluateExpression("TRUE").getBooleanValue());
		// FALSE
		assertFalse(evaluateExpression("false").getBooleanValue());
		assertFalse(evaluateExpression("FALSE").getBooleanValue());
	}

	@Test
	void testAllFunctions() throws EvaluationException, ParseException {
		// ABS
		assertNumberExpression("10.1", "ABS(10.1)");
		assertNumberExpression("10.1", "abs(-10.1)");
		// ACOS
		assertNumberExpression("0", "ACOS(1)");
		assertNumberExpression("90", "acos(0)");
		// ACOSH
		assertNumberExpression("0", "ACOSH(1)");
		assertNumberExpression("1", "acosh(1.54308063481524377847790562075706168260)");
		// ACOT
		assertNumberExpression("135", "ACOT(-1)");
		assertNumberExpression("45", "acot(1)");
		// ACOTH
		assertNumberExpression("0.5", "ACOTH(2.16395341373865284877000401021802311709)");
		assertNumberExpression("3", "acoth(1.00496982331368917109315124282800285381)");
		// AND
		assertFalse(evaluateExpression("AND(false, false)").getBooleanValue());
		assertFalse(evaluateExpression("AND(true, false)").getBooleanValue());
		assertFalse(evaluateExpression("and(false, true)").getBooleanValue());
		assertTrue(evaluateExpression("and(true, true)").getBooleanValue());
		// ASIN
		assertNumberExpression("90", "ASIN(1)");
		assertNumberExpression("0", "asin(0)");
		// ASINH
		assertNumberExpression("0", "SINH(0)");
		assertNumberExpression("2.12927945509481749683438749467763164883", "sinh(1.5)");
		// ATAN
		assertNumberExpression("0", "ATAN(0)");
		assertNumberExpression("45", "atan(1)");
		// ATAN2
		assertNumberExpression("0", "ATAN2(0, 1)");
		assertNumberExpression("90", "atan2(1, 0)");
		assertNumberExpression("180", "ATAN2(0, -1)");
		assertNumberExpression("-90", "atan2(-1, 0)");
		// ATANH
		assertNumberExpression("0", "ATANH(0)");
		assertNumberExpression("3.80020116725020003177596726780900446226", "atanh(0.999)");
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
		assertNumberExpression("1", "COSH(0)");
		assertNumberExpression("1.54308063481524377847790562075706168260", "cosh(1)");
		// COT
		assertNumberExpression("0", "COT(90)");
		assertNumberExpression("1", "cot(45)");
		// COTH
		assertNumberExpression("2.16395341373865284877000401021802311709", "COTH(0.5)");
		assertNumberExpression("1.00496982331368917109315124282800285381", "coth(3)");
		// CSC
		assertNumberExpression("1", "CSC(90)");
		assertNumberExpression("-1", "csc(270)");
		// CSCH
		assertNumberExpression("2.00543269634446402200795198610047307206", "CSCH(0.48)");
		assertNumberExpression("0.27572056477178320775835148216302712124", "csch(2)");
		// DEG
		assertNumberExpression("90", "DEG(PI/2)");
		assertNumberExpression("180", "deg(PI)");
		assertNumberExpression("0", "deg(0)");
		// EXP
		assertNumberExpression("1", "EXP(0)");
		assertNumberExpression("2.71828182845904523536028747135266249775", "exp(1)");
		// FACT
		assertNumberExpression("1", "FACT(1)");
		assertNumberExpression("720", "fact(6)");
		// FLOOR
		assertNumberExpression("10", "FLOOR(10.1)");
		assertNumberExpression("-11", "floor(-10.1)");
		assertNumberExpression("11", "floor(11)");
		// FRACTIONALPART
		assertNumberExpression("0.1", "FRACTIONALPART(10.1)");
		assertNumberExpression("0.12358", "fractionalpart(0.12358)");
		// GAMMA
		assertNumberExpression("720", "GAMMA(7)");
		assertNumberExpression("1", "gamma(2)");
		// IF
		assertNumberExpression("4", "IF(TRUE, 4, 7)");
		assertNumberExpression("7", "IF(FALSE, 4, 7)");
		assertNumberExpression("4", "if(true, 4, 7)");
		assertNumberExpression("7", "if(false, 4, 6+1)");
		// INTEGRALPART
		assertNumberExpression("10", "INTEGRALPART(10.1)");
		assertNumberExpression("0", "integralpart(0.12358)");
		// LOG
		assertNumberExpression("4", "LOG(e^4)");
		assertNumberExpression("1", "log(e)");
		// LOG10
		assertNumberExpression("1", "LOG10(10)");
		assertNumberExpression("3", "log10(1000)");
		// LOG2
		assertNumberExpression("2", "LOG2(4)");
		assertNumberExpression("4", "log2(16)");
		// MAX
		assertNumberExpression("5", "MAX(4, 5)");
		assertNumberExpression("-1", "max(-1, -5)");
		// MIN
		assertNumberExpression("4", "MIN(4, 5)");
		assertNumberExpression("-5", "min(-1, -5)");
		// NOT
		assertTrue(evaluateExpression("NOT(false)").getBooleanValue());
		assertFalse(evaluateExpression("not(true)").getBooleanValue());
		// OR
		assertFalse(evaluateExpression("OR(false, false)").getBooleanValue());
		assertTrue(evaluateExpression("OR(true, false)").getBooleanValue());
		assertTrue(evaluateExpression("or(false, true)").getBooleanValue());
		assertTrue(evaluateExpression("or(true, true)").getBooleanValue());
		// RAD
		assertNumberExpression("1.57079632679489661923132169163975144209", "RAD(90)");
		assertNumberExpression("3.14159265358979323846264338327950288419", "rad(180)");
		assertNumberExpression("0", "rad(0)");
		// RECIPROCAL
		assertNumberExpression("0.2", "RECIPROCAL(5)");
		assertNumberExpression("0.25", "reciprocal(4)");
		// ROOT
		assertNumberExpression("4", "ROOT(16, 2)");
		assertNumberExpression("4", "root(64, 3)");
		// ROUND
		assertNumberExpression("0", "ROUND(0.4)");
		assertNumberExpression("1", "round(0.5)");
		assertNumberExpression("1", "round(0.51)");
		assertNumberExpression("0", "ROUND(0.04, 1)");
		assertNumberExpression("0.1", "round(0.051, 1)");
		// SEC
		assertNumberExpression("1.41421356237309504880168872420969807856", "SEC(45)");
		assertNumberExpression("1.08239220029239396879944641073277884012", "sec(22.5)");
		// SECH
		assertNumberExpression("0.99999999999950000000000020833333333324", "SECH(0.000001)");
		assertNumberExpression("0.26580222883407969212086273981988897153", "sech(2)");
		// SIN
		assertNumberExpression("1", "SIN(90)");
		assertNumberExpression("0", "sin(0)");
		assertNumberExpression("-1", "sin(270)");
		// SINH
		assertNumberExpression("0", "SINH(0)");
		assertNumberExpression("2.12927945509481749683438749467763164883", "sinh(1.5)");
		// SQRT
		assertNumberExpression("3", "SQRT(9)");
		assertNumberExpression("5", "sqrt(25)");
		// TAN
		assertNumberExpression("0", "TAN(0)");
		assertNumberExpression("1", "tan(45)");
		// TANH
		assertNumberExpression("0", "TANH(0)");
		assertNumberExpression("0.999", "tanh(3.80020116725020003177596726780900446226)");
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