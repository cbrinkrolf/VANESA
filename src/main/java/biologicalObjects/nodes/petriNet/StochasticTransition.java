package biologicalObjects.nodes.petriNet;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import biologicalElements.Elementdeclerations;
import biologicalElements.Pathway;
import graph.jung.graphDrawing.VertexShapes;
import gui.PopUpDialog;
import util.StochasticDistribution;

public class StochasticTransition extends Transition {
	private String distribution = StochasticDistribution.distributionExponential;
	private double h = 1.0; // probability density
	private double a = 0; // min
	private double b = 1; // max
	private double c = 0.5; // most likely value
	private double mu = 0.5; // expected value
	private double sigma = 1.0 / 4; // standard deviation
	private List<Integer> events = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
	private List<Double> probabilities = new ArrayList<>(Arrays.asList(0.25, 0.25, 0.25, 0.25));

	public StochasticTransition(final String label, final String name, final Pathway parent) {
		super(label, name, Elementdeclerations.stochasticTransition, parent);
		setDefaultShape(VertexShapes.getDiscreteTransitionShape());
		setDefaultColor(Color.DARK_GRAY);
	}

	public String getDistribution() {
		return distribution;
	}

	public void setDistribution(String distribution) {
		if (StochasticDistribution.distributionSet.contains(distribution)) {
			this.distribution = distribution;
		} else {
			System.err.println("Given distribution: \"" + distribution + "\" is not supported!)");
			PopUpDialog.getInstance().show("Error setting distribution",
					"Setting distribution of transition: " + getName() + "\nGiven distribution: \"" + distribution
							+ "\" is not supported!");
		}
	}

	public double getH() {
		return h;
	}

	public void setH(double h) {
		this.h = h;
	}

	public double getA() {
		return a;
	}

	public void setA(double a) {
		this.a = a;
	}

	public double getB() {
		return b;
	}

	public void setB(double b) {
		this.b = b;
	}

	public double getC() {
		return c;
	}

	public void setC(double c) {
		this.c = c;
	}

	public double getMu() {
		return mu;
	}

	public void setMu(double mu) {
		this.mu = mu;
	}

	public double getSigma() {
		return sigma;
	}

	public void setSigma(double sigma) {
		this.sigma = sigma;
	}

	public List<Integer> getEvents() {
		return events;
	}

	public void setEvents(List<Integer> events) {
		this.events = events;
	}

	public List<Double> getProbabilities() {
		return probabilities;
	}

	public void setProbabilities(List<Double> probabilities) {
		this.probabilities = probabilities;
	}

	// CHRIS add parameters for stochastic transition
	@Override
	public List<String> getTransformationParameters() {
		List<String> list = super.getTransformationParameters();
		list.add("distribution");
		return list;
	}
}
