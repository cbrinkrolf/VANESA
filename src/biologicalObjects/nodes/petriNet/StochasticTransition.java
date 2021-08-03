package biologicalObjects.nodes.petriNet;

import java.awt.Color;
import java.util.ArrayList;

import biologicalElements.Elementdeclerations;
import gui.MyPopUp;
import util.StochasticDistribution;

public class StochasticTransition extends Transition {

	private String distribution = StochasticDistribution.distributionExponential;
	private double h = 1.0; // probability density
	private double a = 0; // min
	private double b = 1; // max
	private double c = 0.5; // most likely value
	private double mu = 0.5; // expected value
	private double sigma = 1.0 / 4; // standard deviation
	private ArrayList<Integer> events = new ArrayList<Integer>();
	private ArrayList<Double> probabilities = new ArrayList<Double>();

	public StochasticTransition(String label, String name) {
		super(label, name);
		setBiologicalElement(Elementdeclerations.stochasticTransition);
		this.setDefaultColor(Color.DARK_GRAY);
		events.add(1);
		events.add(2);
		events.add(3);
		events.add(4);

		probabilities.add(1.0 / 4);
		probabilities.add(1.0 / 4);
		probabilities.add(1.0 / 4);
		probabilities.add(1.0 / 4);
	}

	public String getDistribution() {
		return distribution;
	}

	public void setDistribution(String distribution) {

		if (StochasticDistribution.distributionSet.contains(distribution)) {
			this.distribution = distribution;
		} else {
			System.err.println("Given distribution: \"" + distribution + "\" is not supported!)");
			MyPopUp.getInstance().show("Error setting distribuiton", "Setting distribution of transiton: "
					+ this.getName() + "\n " + "Given distribution: \"" + distribution + "\" is not supported!");
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

	public ArrayList<Integer> getEvents() {
		return events;
	}

	public void setEvents(ArrayList<Integer> events) {
		this.events = events;
	}

	public ArrayList<Double> getProbabilities() {
		return probabilities;
	}

	public void setProbabilities(ArrayList<Double> probabilities) {
		this.probabilities = probabilities;
	}
}
