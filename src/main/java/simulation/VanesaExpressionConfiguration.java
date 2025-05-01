package simulation;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.bigmath.functions.bigdecimalmath.*;
import com.ezylang.evalex.bigmath.operators.bigdecimalmath.BigMathInfixPowerOfOperator;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.config.MapBasedFunctionDictionary;
import com.ezylang.evalex.config.MapBasedOperatorDictionary;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.functions.basic.*;
import com.ezylang.evalex.operators.arithmetic.*;
import com.ezylang.evalex.operators.booleans.*;
import com.ezylang.evalex.parser.Token;

import java.util.Locale;
import java.util.Map;

public class VanesaExpressionConfiguration {
	public static final ExpressionConfiguration EXPRESSION_CONFIGURATION;

	static {
		final var builder = ExpressionConfiguration.builder().locale(Locale.US).arraysAllowed(false).binaryAllowed(
				false).structuresAllowed(false);
		// Explicitly define available operators
		builder.operatorDictionary(MapBasedOperatorDictionary.ofOperators(
				// Disabled operators
				// Map.entry("!", new PrefixNotOperator()),
				// Disabled operators replaced by BigMath operators
				// Map.entry("^", new InfixPowerOfOperator()),
				// Disabled alternative (in-)equality operators
				// Map.entry("=", new InfixEqualsOperator()),
				// Map.entry("<>", new InfixNotEqualsOperator()),
				Map.entry("+", new PrefixPlusOperator()), Map.entry("-", new PrefixMinusOperator()),
				Map.entry("+", new InfixPlusOperator()), Map.entry("-", new InfixMinusOperator()),
				Map.entry("*", new InfixMultiplicationOperator()), Map.entry("/", new InfixDivisionOperator()),
				Map.entry("%", new InfixModuloOperator()), Map.entry("==", new InfixEqualsOperator()),
				Map.entry("!=", new InfixNotEqualsOperator()), Map.entry(">", new InfixGreaterOperator()),
				Map.entry(">=", new InfixGreaterEqualsOperator()), Map.entry("<", new InfixLessOperator()),
				Map.entry("<=", new InfixLessEqualsOperator()), Map.entry("&&", new InfixAndOperator()),
				Map.entry("||", new InfixOrOperator()),
				// BigMath operators
				Map.entry("^", new BigMathInfixPowerOfOperator())));

		// Explicitly define available functions
		builder.functionDictionary(MapBasedFunctionDictionary.ofFunctions(
				// Disabled functions
				// Map.entry("RANDOM", new RandomFunction()),
				// Map.entry("AVERAGE", new AverageFunction()),
				// Map.entry("COALESCE", new CoalesceFunction()),
				// Map.entry("SUM", new SumFunction()),
				// Map.entry("EXPONENT", new BigMathExponentFunction()),
				// Map.entry("MANTISSA", new BigMathMantissaFunction()),
				// Map.entry("SIGNIFICANTDIGITS", new BigMathSignificantDigitsFunction()),
				// Map.entry("SWITCH", new SwitchFunction()),
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
				Map.entry("ABS", new AbsFunction()), Map.entry("CEILING", new CeilingFunction()),
				Map.entry("FLOOR", new FloorFunction()), Map.entry("IF", new IfFunction()),
				Map.entry("MAX", new MaxFunction()), Map.entry("MIN", new MinFunction()),
				Map.entry("NOT", new NotFunction()), Map.entry("ROUND", new RoundFunction()),
				// Custom functions
				Map.entry("CEIL", new CeilingFunction()), Map.entry("AND", new AndFunction()),
				Map.entry("OR", new OrFunction()),
				// BigMath functions
				Map.entry("ACOS", new BigMathAcosFunction()), Map.entry("ACOSH", new BigMathAcosHFunction()),
				Map.entry("ACOT", new BigMathAcotFunction()), Map.entry("ACOTH", new BigMathAcotHFunction()),
				Map.entry("ASIN", new BigMathAsinFunction()), Map.entry("ASINH", new BigMathAsinHFunction()),
				Map.entry("ATAN2", new BigMathAtan2Function()), Map.entry("ATAN", new BigMathAtanFunction()),
				Map.entry("ATANH", new BigMathAtanHFunction()), Map.entry("BN", new BigMathBernoulliFunction()),
				Map.entry("COS", new BigMathCosFunction()), Map.entry("COSH", new BigMathCosHFunction()),
				Map.entry("COT", new BigMathCotFunction()), Map.entry("COTH", new BigMathCotHFunction()),
				Map.entry("CSC", new BigMathCscFunction()), Map.entry("CSCH", new BigMathCscHFunction()),
				Map.entry("DEG", new BigMathDegFunction()), Map.entry("E", new BigMathEFunction()),
				Map.entry("EXP", new BigMathExpFunction()), Map.entry("FACT", new BigMathFactorialFunction()),
				Map.entry("FRACTIONALPART", new BigMathFractionalPartFunction()),
				Map.entry("GAMMA", new BigMathGammaFunction()),
				Map.entry("INTEGRALPART", new BigMathIntegralPartFunction()),
				Map.entry("LOG", new BigMathLogFunction()), Map.entry("LOG10", new BigMathLog10Function()),
				Map.entry("LOG2", new BigMathLog2Function()), Map.entry("PI", new BigMathPiFunction()),
				Map.entry("RAD", new BigMathRadFunction()), Map.entry("RECIPROCAL", new BigMathReciprocalFunction()),
				Map.entry("ROOT", new BigMathRootFunction()), Map.entry("SEC", new BigMathSecFunction()),
				Map.entry("SECH", new BigMathSecHFunction()), Map.entry("SIN", new BigMathSinFunction()),
				Map.entry("SINH", new BigMathSinHFunction()), Map.entry("SQRT", new BigMathSqrtFunction()),
				Map.entry("TAN", new BigMathTanFunction()), Map.entry("TANH", new BigMathTanHFunction())));
		EXPRESSION_CONFIGURATION = builder.build();
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
