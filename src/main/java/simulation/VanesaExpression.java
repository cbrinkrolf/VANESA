package simulation;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ASTNode;
import com.ezylang.evalex.parser.ParseException;
import com.ezylang.evalex.parser.Token;
import graph.gui.Parameter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VanesaExpression extends Expression {
	public VanesaExpression(final String expressionString) {
		super(expressionString, VanesaExpressionConfiguration.EXPRESSION_CONFIGURATION);
	}

	public VanesaExpression(final VanesaExpression expression) throws ParseException {
		super(expression);
	}

	public VanesaExpression with(final List<Parameter> parameters) {
		if (parameters != null) {
			for (final var parameter : parameters) {
				with(parameter.getName(), BigDecimal.valueOf(parameter.getValue()));
			}
		}
		return this;
	}

	public String reduce(final List<Parameter> parameters) throws ParseException {
		String lastExpressionString = getExpressionString();
		boolean reduced = true;
		while (reduced) {
			reduced = false;
			final var expression = new VanesaExpression(lastExpressionString);
			if (parameters != null) {
				for (final Parameter invariant : parameters) {
					expression.with(invariant.getName(), invariant.getValue());
				}
			}
			final var reducedExpressionString = visit(expression, expression.getAbstractSyntaxTree(), 0);
			if (!reducedExpressionString.equals(lastExpressionString)) {
				lastExpressionString = reducedExpressionString;
				reduced = true;
			}
		}
		return lastExpressionString;
	}

	public String reduce(final Map.Entry<String, Object>[] invariants) throws ParseException {
		String lastExpressionString = getExpressionString();
		boolean reduced = true;
		while (reduced) {
			reduced = false;
			final var expression = new VanesaExpression(lastExpressionString);
			if (invariants != null) {
				for (final Map.Entry<String, Object> invariant : invariants) {
					expression.with(invariant.getKey(), invariant.getValue());
				}
			}
			final var reducedExpressionString = visit(expression, expression.getAbstractSyntaxTree(), 0);
			if (!reducedExpressionString.equals(lastExpressionString)) {
				lastExpressionString = reducedExpressionString;
				reduced = true;
			}
		}
		return lastExpressionString;
	}

	private static String visit(final VanesaExpression expression, final ASTNode node, final int depth) {
		try {
			final var evaluation = expression.evaluateSubtree(node);
			if (evaluation != null) {
				switch (evaluation.getDataType()) {
				case STRING:
					return "\"" + evaluation.getStringValue() + "\"";
				case NUMBER:
					return evaluation.getNumberValue().toPlainString();
				case BOOLEAN:
					return evaluation.getBooleanValue() ? "true" : "false";
				case NULL:
					return "null";
				case DATE_TIME:
				case DURATION:
				case ARRAY:
				case STRUCTURE:
				case EXPRESSION_NODE:
				case BINARY:
					// not currently reduced
					break;
				}
			}
		} catch (EvaluationException ignored) {
		}
		final Token token = node.getToken();
		switch (token.getType()) {
		case BRACE_OPEN:
			return "(";
		case BRACE_CLOSE:
			return ")";
		case COMMA:
			return ",";
		case STRING_LITERAL:
			return "\"" + token.getValue() + "\"";
		case NUMBER_LITERAL:
		case VARIABLE_OR_CONSTANT:
			return token.getValue();
		case INFIX_OPERATOR:
			if (depth > 0 && !token.getValue().equals("*")) {
				return "(" + visit(expression, node.getParameters().get(0), depth + 1) + token.getValue() + visit(
						expression, node.getParameters().get(1), depth + 1) + ")";
			}
			return visit(expression, node.getParameters().get(0), depth + 1) + token.getValue() + visit(expression,
					node.getParameters().get(1), depth + 1);
		case PREFIX_OPERATOR:
			return token.getValue() + visit(expression, node.getParameters().get(0), depth + 1);
		case POSTFIX_OPERATOR:
			return visit(expression, node.getParameters().get(0), depth + 1) + token.getValue();
		case FUNCTION:
			if (token.getFunctionDefinition() instanceof VanesaExpressionConfiguration.AndFunction) {
				final var a = visit(expression, node.getParameters().get(0), depth + 1);
				final var b = visit(expression, node.getParameters().get(1), depth + 1);
				if ("false".equals(a) || "false".equals(b)) {
					return "false";
				}
				if ("true".equals(a)) {
					return b;
				}
				if ("true".equals(b)) {
					return a;
				}
			} else if (token.getFunctionDefinition() instanceof VanesaExpressionConfiguration.OrFunction) {
				final var a = visit(expression, node.getParameters().get(0), depth + 1);
				final var b = visit(expression, node.getParameters().get(1), depth + 1);
				if ("true".equals(a) || "true".equals(b)) {
					return "true";
				}
				if ("false".equals(a)) {
					return b;
				}
				if ("false".equals(b)) {
					return a;
				}
			}
			return token.getValue() + "(" + node.getParameters().stream().map(t -> visit(expression, t, depth + 1))
					.collect(Collectors.joining(",")) + ")";
		case FUNCTION_PARAM_START:
		case ARRAY_OPEN:
		case ARRAY_CLOSE:
		case ARRAY_INDEX:
		case STRUCTURE_SEPARATOR:
			// TODO
			break;
		}
		return "";
	}
}
