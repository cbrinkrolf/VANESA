package util;

public enum StochasticDistribution {
	/**
	 * Exponential distribution using the probability density H of a stochastic transition
	 */
	Exponential("distributionExponential", "Exponential distribution"),
	/**
	 * Triangular distribution using the most likely value C and lower/upper bounds A/B of a stochastic transition
	 */
	Triangular("distributionTriangular", "Triangular distribution"),
	/**
	 * Uniform distribution using the lower bound A and upper bound B of a stochastic transition
	 */
	Uniform("distributionUniform", "Uniform distribution"),
	/**
	 * Truncated normal distribution using the mean Mu and standard deviation Sigma of a stochastic transition
	 */
	TruncatedNormal("distributionTruncatedNormal", "Truncated normal distribution"),
	/**
	 * Discrete probability distribution using the events and event probabilities of a stochastic transition
	 * <p>
	 * Implemented in PNlib, but not considered for simulation, yet (08.2021)
	 */
	DiscreteProbability("distributionDiscreteProbability", "Discrete probability distribution");

	public static final StochasticDistribution[] DISTRIBUTIONS = new StochasticDistribution[] { Exponential, Uniform,
			Triangular, TruncatedNormal };

	private final String id;
	private final String name;

	StochasticDistribution(final String id, final String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	public static StochasticDistribution fromId(final String id) {
		switch (id) {
		case "distributionExponential":
			return Exponential;
		case "distributionTriangular":
			return Triangular;
		case "distributionUniform":
			return Uniform;
		case "distributionTruncatedNormal":
			return TruncatedNormal;
		case "distributionDiscreteProbability":
			return DiscreteProbability;
		}
		return null;
	}
}
