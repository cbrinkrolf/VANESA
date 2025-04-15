package simulation;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.parser.ParseException;
import graph.gui.Parameter;

import java.math.BigDecimal;
import java.util.*;

public abstract class Simulator {
	protected final RandomGenerator random;
	protected final boolean allowBranching;

	protected Simulator(final RandomGenerator random, final boolean allowBranching) {
		this.random = random;
		this.allowBranching = allowBranching;
	}

	public static TimeRange findNextActivationTimeRange(final String firingCondition,
			final List<Parameter> parameters) {
		if ("true".equals(firingCondition)) {
			return TimeRange.ALWAYS;
		}
		if ("false".equals(firingCondition)) {
			return TimeRange.NEVER;
		}
		try {
			var expression = new VanesaExpression(firingCondition);
			final var invariants = new ArrayList<Map.Entry<String, Object>>();
			if (parameters != null) {
				for (final var parameter : parameters) {
					invariants.add(Map.entry(parameter.getName(), parameter.getValue()));
				}
			}
			expression = new VanesaExpression(expression.reduce(invariants.toArray(new Map.Entry[0])));
			if (parameters != null) {
				for (final var parameter : parameters) {
					expression.with(parameter.getName(), parameter.getValue());
				}
			}
			final var undefinedVariables = new HashSet<>(expression.getUndefinedVariables());
			if (undefinedVariables.isEmpty()) {
				final var evaluation = expression.evaluate();
				if (evaluation.getBooleanValue()) {
					return TimeRange.ALWAYS;
				} else {
					return TimeRange.NEVER;
				}
			}
		} catch (ParseException | EvaluationException ignored) {
		}
		return null;
	}

	public static class TimeRange {
		public static final TimeRange ALWAYS = new TimeRange(BigDecimal.ZERO, null);
		public static final TimeRange NEVER = new TimeRange(BigDecimal.ONE.negate(), BigDecimal.ONE.negate());
		public final BigDecimal start;
		public final BigDecimal end;
		public final BigDecimal duration;
		public final boolean isOpenEnded;

		public TimeRange(final BigDecimal start, final BigDecimal end) {
			this.start = start;
			this.end = end;
			isOpenEnded = end == null;
			duration = isOpenEnded ? null : end.subtract(start);
		}
	}
}
