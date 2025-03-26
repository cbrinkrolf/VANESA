package simulation;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.bigmath.functions.bigdecimalmath.*;
import com.ezylang.evalex.bigmath.operators.bigdecimalmath.BigDecimalMathOperators;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.functions.basic.CeilingFunction;
import com.ezylang.evalex.parser.Token;

import java.util.Locale;
import java.util.Map;

public class VanesaExpressionConfiguration {
	public static final ExpressionConfiguration EXPRESSION_CONFIGURATION = ExpressionConfiguration.builder().locale(
			Locale.US).build();

	static {
		EXPRESSION_CONFIGURATION.withAdditionalFunctions(BigDecimalMathFunctions.allFunctions());
		EXPRESSION_CONFIGURATION.withAdditionalFunctions(Map.entry("CEIL", new CeilingFunction()));
		EXPRESSION_CONFIGURATION.withAdditionalFunctions(Map.entry("AND", new AndFunction()));
		EXPRESSION_CONFIGURATION.withAdditionalFunctions(Map.entry("OR", new OrFunction()));
		EXPRESSION_CONFIGURATION.withAdditionalFunctions(Map.entry("NOT", new NotFunction()));
		EXPRESSION_CONFIGURATION.withAdditionalOperators(BigDecimalMathOperators.allOperators());
	}

	@FunctionParameter(name = "a")
	@FunctionParameter(name = "b")
	private static class AndFunction extends AbstractFunction {
		@Override
		public EvaluationValue evaluate(Expression expression, Token functionToken,
				EvaluationValue... parameterValues) {
			final boolean a = parameterValues[0].getBooleanValue();
			final boolean b = parameterValues[1].getBooleanValue();
			return expression.convertValue(a && b);
		}
	}

	@FunctionParameter(name = "a")
	@FunctionParameter(name = "b")
	private static class OrFunction extends AbstractFunction {
		@Override
		public EvaluationValue evaluate(Expression expression, Token functionToken,
				EvaluationValue... parameterValues) {
			final boolean a = parameterValues[0].getBooleanValue();
			final boolean b = parameterValues[1].getBooleanValue();
			return expression.convertValue(a || b);
		}
	}

	@FunctionParameter(name = "value")
	private static class NotFunction extends AbstractFunction {
		@Override
		public EvaluationValue evaluate(Expression expression, Token functionToken,
				EvaluationValue... parameterValues) {
			return expression.convertValue(!parameterValues[0].getBooleanValue());
		}
	}
}
