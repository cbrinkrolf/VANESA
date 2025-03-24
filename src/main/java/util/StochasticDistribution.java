package util;

import org.apache.commons.lang3.ArrayUtils;

public class StochasticDistribution {
	public static final String distributionExponential = "Exponential distribution";
	public static final String distributionTriangular = "Triangular distribution";
	public static final String distributionUniform = "Uniform distribution";
	public static final String distributionTruncatedNormal = "Truncated normal distribution";
	/**
	 * Implemented in PNlib, but not considered for simulation, yet (08.2021)
	 */
	public static final String distributionDiscreteProbability = "Discrete probability distribution";
	public static final String[] distributions = new String[] { distributionExponential, distributionUniform,
			distributionTriangular, distributionTruncatedNormal };

	public static boolean isValid(final String distribution) {
		return ArrayUtils.indexOf(distributions, distribution) != -1;
	}
}
