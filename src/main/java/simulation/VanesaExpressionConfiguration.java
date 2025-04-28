package simulation;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.bigmath.functions.bigdecimalmath.*;
import com.ezylang.evalex.bigmath.operators.bigdecimalmath.BigDecimalMathOperators;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.config.MapBasedFunctionDictionary;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.functions.basic.*;
import com.ezylang.evalex.functions.trigonometric.*;
import com.ezylang.evalex.parser.Token;

import java.util.Locale;
import java.util.Map;

public class VanesaExpressionConfiguration {
	public static final ExpressionConfiguration EXPRESSION_CONFIGURATION;

	static {
		final var builder = ExpressionConfiguration.builder().locale(Locale.US);
		builder.functionDictionary(MapBasedFunctionDictionary.ofFunctions(Map.entry("ABS", new AbsFunction()),
				Map.entry("AVERAGE", new AverageFunction()), Map.entry("CEILING", new CeilingFunction()),
				Map.entry("COALESCE", new CoalesceFunction()), Map.entry("FACT", new FactFunction()),
				Map.entry("FLOOR", new FloorFunction()), Map.entry("IF", new IfFunction()),
				Map.entry("LOG", new LogFunction()), Map.entry("LOG10", new Log10Function()),
				Map.entry("MAX", new MaxFunction()), Map.entry("MIN", new MinFunction()),
				Map.entry("NOT", new NotFunction()),
				// Disabled random
				// Map.entry("RANDOM", new RandomFunction()),
				Map.entry("ROUND", new RoundFunction()), Map.entry("SQRT", new SqrtFunction()),
				Map.entry("SUM", new SumFunction()), Map.entry("SWITCH", new SwitchFunction()),
				Map.entry("ACOS", new AcosFunction()), Map.entry("ACOSH", new AcosHFunction()),
				Map.entry("ACOSR", new AcosRFunction()), Map.entry("ACOT", new AcotFunction()),
				Map.entry("ACOTH", new AcotHFunction()), Map.entry("ACOTR", new AcotRFunction()),
				Map.entry("ASIN", new AsinFunction()), Map.entry("ASINH", new AsinHFunction()),
				Map.entry("ASINR", new AsinRFunction()), Map.entry("ATAN", new AtanFunction()),
				Map.entry("ATAN2", new Atan2Function()), Map.entry("ATAN2R", new Atan2RFunction()),
				Map.entry("ATANH", new AtanHFunction()), Map.entry("ATANR", new AtanRFunction()),
				Map.entry("COS", new CosFunction()), Map.entry("COSH", new CosHFunction()),
				Map.entry("COSR", new CosRFunction()), Map.entry("COT", new CotFunction()),
				Map.entry("COTH", new CotHFunction()), Map.entry("COTR", new CotRFunction()),
				Map.entry("CSC", new CscFunction()), Map.entry("CSCH", new CscHFunction()),
				Map.entry("CSCR", new CscRFunction()), Map.entry("DEG", new DegFunction()),
				Map.entry("RAD", new RadFunction()), Map.entry("SIN", new SinFunction()),
				Map.entry("SINH", new SinHFunction()), Map.entry("SINR", new SinRFunction()),
				Map.entry("SEC", new SecFunction()), Map.entry("SECH", new SecHFunction()),
				Map.entry("SECR", new SecRFunction()), Map.entry("TAN", new TanFunction()),
				Map.entry("TANH", new TanHFunction()), Map.entry("TANR", new TanRFunction()),
				// Disabled string functions
				// Map.entry("STR_CONTAINS", new StringContains()),
				// Map.entry("STR_ENDS_WITH", new StringEndsWithFunction()),
				// Map.entry("STR_FORMAT", new StringFormatFunction()),
				// Map.entry("STR_LEFT", new StringLeftFunction()),
				// Map.entry("STR_LENGTH", new StringLengthFunction()),
				// Map.entry("STR_LOWER", new StringLowerFunction()),
				// Map.entry("STR_MATCHES", new StringMatchesFunction()),
				// Map.entry("STR_RIGHT", new StringRightFunction()),
				// Map.entry("STR_STARTS_WITH", new StringStartsWithFunction()),
				// Map.entry("STR_SUBSTRING", new StringSubstringFunction()),
				// Map.entry("STR_TRIM", new StringTrimFunction()),
				// Map.entry("STR_UPPER", new StringUpperFunction()),
				// Disabled datetime functions
				// Map.entry("DT_DATE_NEW", new DateTimeNewFunction()),
				// Map.entry("DT_DATE_PARSE", new DateTimeParseFunction()),
				// Map.entry("DT_DATE_FORMAT", new DateTimeFormatFunction()),
				// Map.entry("DT_DATE_TO_EPOCH", new DateTimeToEpochFunction()),
				// Map.entry("DT_DURATION_NEW", new DurationNewFunction()),
				// Map.entry("DT_DURATION_FROM_MILLIS", new DurationFromMillisFunction()),
				// Map.entry("DT_DURATION_TO_MILLIS", new DurationToMillisFunction()),
				// Map.entry("DT_DURATION_PARSE", new DurationParseFunction()),
				// Map.entry("DT_NOW", new DateTimeNowFunction()),
				// Map.entry("DT_TODAY", new DateTimeTodayFunction())
				// Custom functions
				Map.entry("CEIL", new CeilingFunction()), Map.entry("AND", new AndFunction()),
				Map.entry("OR", new OrFunction())));
		EXPRESSION_CONFIGURATION = builder.build();
		// Add BigDecimal extensions
		EXPRESSION_CONFIGURATION.withAdditionalFunctions(BigDecimalMathFunctions.allFunctions());
		EXPRESSION_CONFIGURATION.withAdditionalOperators(BigDecimalMathOperators.allOperators());
	}

	@FunctionParameter(name = "a")
	@FunctionParameter(name = "b")
	static class AndFunction extends AbstractFunction {
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
	static class OrFunction extends AbstractFunction {
		@Override
		public EvaluationValue evaluate(Expression expression, Token functionToken,
				EvaluationValue... parameterValues) {
			final boolean a = parameterValues[0].getBooleanValue();
			final boolean b = parameterValues[1].getBooleanValue();
			return expression.convertValue(a || b);
		}
	}
}
