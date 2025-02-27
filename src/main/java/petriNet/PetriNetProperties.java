package petriNet;

import biologicalElements.Pathway;

public class PetriNetProperties {
	private final Pathway pathway;
	private int currentTimeStep = 0;
	private String covGraph;
	private boolean isPetriNetSimulation = false;
	private final SimulationResultController simResController = new SimulationResultController();

	public PetriNetProperties(final Pathway pathway) {
		this.pathway = pathway;
	}

	public String getCovGraph() {
		return covGraph;
	}

	public void setCovGraph(final String covGraph) {
		this.covGraph = covGraph;
	}

	public int getCurrentTimeStep() {
		return currentTimeStep;
	}

	public void setCurrentTimeStep(final int currentTimeStep) {
		this.currentTimeStep = currentTimeStep;
	}

	public void setPetriNetSimulation(final boolean isPetriNetSimulation) {
		this.isPetriNetSimulation = isPetriNetSimulation;
	}

	public boolean isPetriNetSimulation() {
		return isPetriNetSimulation;
	}

	public SimulationResultController getSimResController() {
		return simResController;
	}

	public Pathway getPathway() {
		return pathway;
	}
}
