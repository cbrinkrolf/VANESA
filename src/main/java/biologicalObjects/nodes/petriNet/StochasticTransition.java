package biologicalObjects.nodes.petriNet;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import graph.jung.graphDrawing.VertexShapes;
import org.apache.commons.math3.special.Erf;
import simulation.RandomGenerator;
import simulation.StochasticSampler;
import util.StochasticDistribution;

public class StochasticTransition extends Transition {
	private StochasticDistribution distribution = StochasticDistribution.Exponential;
	private double h = 1.0; // probability density
	private double a = 0; // min
	private double b = 1; // max
	private double c = 0.5; // most likely value
	private double mu = 0.5; // expected value
	private double sigma = 0.25; // standard deviation
	private List<Integer> events = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
	private List<Double> probabilities = new ArrayList<>(Arrays.asList(0.25, 0.25, 0.25, 0.25));

	public StochasticTransition(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.stochasticTransition, parent);
		setDefaultShape(VertexShapes.getDiscreteTransitionShape());
		setDefaultColor(Color.DARK_GRAY);
	}

	public StochasticDistribution getDistribution() {
		return distribution;
	}

	public void setDistribution(final StochasticDistribution distribution) {
		this.distribution = distribution;
	}

	public double getH() {
		return h;
	}

	public void setH(final double h) {
		this.h = h;
	}

	public double getA() {
		return a;
	}

	public void setA(final double a) {
		this.a = a;
	}

	public double getB() {
		return b;
	}

	public void setB(final double b) {
		this.b = b;
	}

	public double getC() {
		return c;
	}

	public void setC(final double c) {
		this.c = c;
	}

	public double getMu() {
		return mu;
	}

	public void setMu(final double mu) {
		this.mu = mu;
	}

	public double getSigma() {
		return sigma;
	}

	public void setSigma(final double sigma) {
		this.sigma = sigma;
	}

	public List<Integer> getEvents() {
		return events;
	}

	public void setEvents(final List<Integer> events) {
		this.events = events;
	}

	public List<Double> getProbabilities() {
		return probabilities;
	}

	public void setProbabilities(final List<Double> probabilities) {
		this.probabilities = probabilities;
	}

	// CHRIS add parameters for stochastic transition
	@Override
	public List<String> getTransformationParameters() {
		List<String> list = super.getTransformationParameters();
		list.add("distribution");
		return list;
	}

	public StochasticSampler getDistributionSampler(final RandomGenerator random) {
		switch (distribution) {
		case Exponential: {
			// Equivalent to PNlib "randomexp.mo"
			final double lambda = Math.max(1e-10, h);
			return () -> BigDecimal.valueOf(-Math.log(Math.max(1e-10, random.nextDouble()) / lambda));
			// final var exponentialSampler = ExponentialDistribution.of(h).createSampler(random::nextLong);
			// return () -> BigDecimal.valueOf(exponentialSampler.sample());
		}
		case Triangular: {
			// Equivalent to PNlib "randomtriangular.mo"
			final double a = this.a;
			final double b = this.b;
			final double c = this.c;
			return () -> {
				final double x = Math.max(1e-10, random.nextDouble());
				final double help = (c - a) / (b - a);
				if (x <= help) {
					return BigDecimal.valueOf(Math.sqrt(x * (b - a) * (c - a)) + a);
				} else {
					return BigDecimal.valueOf(b - Math.sqrt((1 - x) * (b - a) * (b - c)));
				}
			};
			// final var triangularSampler = TriangularDistribution.of(a, c, b).createSampler(random::nextLong);
			// return () -> BigDecimal.valueOf(triangularSampler.sample());
		}
		case Uniform: {
			// Equivalent to OpenModelica "Distributions.mo"
			final double a = this.a;
			final double b = this.b;
			return () -> BigDecimal.valueOf(Math.max(1e-10, random.nextDouble()) * (b - a) + a);
			// final var uniformSampler = UniformContinuousDistribution.of(a, b).createSampler(random::nextLong);
			// return () -> BigDecimal.valueOf(uniformSampler.sample());
		}
		case TruncatedNormal: {
			// Equivalent to OpenModelica "Distributions.mo"
			final double cdfMin = (1 + Erf.erf((a - mu) / (sigma * Math.sqrt(2)))) * 0.5; // normal cumulative
			final double cdfMax = (1 + Erf.erf((b - mu) / (sigma * Math.sqrt(2)))) * 0.5; // normal cumulative
			return () -> {
				final double u = cdfMin + Math.max(1e-10, random.nextDouble()) * (cdfMax - cdfMin);
				final double normalQuantile = mu + sigma * Math.sqrt(2) * Erf.erfInv(2 * u - 1);
				return BigDecimal.valueOf(Math.min(b, Math.max(a, normalQuantile)));
			};
			// final var truncatedNormalSampler = TruncatedNormalDistribution.of(mu, sigma, a, b).createSampler(
			// 		random::nextLong);
			// return () -> BigDecimal.valueOf(truncatedNormalSampler.sample());
		}
		case DiscreteProbability: {
			final var events = this.events.toArray(new Integer[0]);
			final var probabilities = this.probabilities.toArray(new Double[0]);
			// Normalize the probabilities
			double sum = 0.0;
			for (int i = 0; i < probabilities.length; i++) {
				sum += probabilities[i];
			}
			for (int i = 0; i < probabilities.length; i++) {
				probabilities[i] /= sum;
			}
			// Equivalent to PNlib "randomdis.mo"
			final var cumulativeProbabilities = new double[events.length];
			cumulativeProbabilities[0] = probabilities[0];
			for (int i = 1; i < events.length; i++) {
				cumulativeProbabilities[i] = cumulativeProbabilities[i - 1] + probabilities[i];
			}
			return () -> {
				double x = random.nextDouble();
				for (int i = 0; i < cumulativeProbabilities.length; i++) {
					if (x <= cumulativeProbabilities[i]) {
						return BigDecimal.valueOf(events[i]);
					}
				}
				return BigDecimal.valueOf(events[events.length - 1]);
			};
		}
		}
		return null;
	}
}
