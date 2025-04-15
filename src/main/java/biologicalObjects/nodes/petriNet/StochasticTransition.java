package biologicalObjects.nodes.petriNet;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import graph.jung.graphDrawing.VertexShapes;
import org.apache.commons.statistics.distribution.*;
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
		case Exponential:
			final var exponentialSampler = ExponentialDistribution.of(h).createSampler(random::nextLong);
			return () -> BigDecimal.valueOf(exponentialSampler.sample());
		case Triangular:
			final var triangularSampler = TriangularDistribution.of(a, c, b).createSampler(random::nextLong);
			return () -> BigDecimal.valueOf(triangularSampler.sample());
		case Uniform:
			final var uniformSampler = UniformContinuousDistribution.of(a, b).createSampler(random::nextLong);
			return () -> BigDecimal.valueOf(uniformSampler.sample());
		case TruncatedNormal:
			final var truncatedNormalSampler = TruncatedNormalDistribution.of(mu, sigma, a, b).createSampler(
					random::nextLong);
			return () -> BigDecimal.valueOf(truncatedNormalSampler.sample());
		case DiscreteProbability:
			final var events = new ArrayList<>(this.events);
			final var probabilities = new ArrayList<>(this.probabilities);
			return () -> {
				double x = random.nextDouble();
				int index = 0;
				while (index < probabilities.size() && x >= probabilities.get(index)) {
					x -= probabilities.get(index);
					index++;
				}
				return BigDecimal.valueOf(events.get(index));
			};
		}
		return null;
	}
}
