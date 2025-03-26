package simulation;

import com.ezylang.evalex.bigmath.functions.bigdecimalmath.*;
import com.ezylang.evalex.bigmath.operators.bigdecimalmath.BigDecimalMathOperators;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.functions.basic.CeilingFunction;

import java.util.Locale;
import java.util.Map;

public class VanesaExpressionConfiguration {
	public static final ExpressionConfiguration EXPRESSION_CONFIGURATION = ExpressionConfiguration.builder().locale(
			Locale.US).build();

	static {
		EXPRESSION_CONFIGURATION.withAdditionalFunctions(BigDecimalMathFunctions.allFunctions());
		EXPRESSION_CONFIGURATION.withAdditionalFunctions(Map.entry("CEIL", new CeilingFunction()));
		EXPRESSION_CONFIGURATION.withAdditionalOperators(BigDecimalMathOperators.allOperators());
	}
}
