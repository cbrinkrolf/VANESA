package biologicalObjects.nodes.petriNet;

import biologicalElements.Pathway;

import java.util.List;

public abstract class Transition extends PNNode {
	private boolean simulationActive;
	private boolean simulationFire;
	private String firingCondition = "true";// "time>9.8";
	private boolean knockedOut = false;

	protected Transition(final String label, final String name, final String biologicalElement, final Pathway parent) {
		super(label, name, biologicalElement, parent);
		if (label.isEmpty()) {
			setLabel(name);
		}
		if (name.isEmpty()) {
			setName(label);
		}
	}

	public boolean isSimulationActive() {
		return simulationActive;
	}

	public void setSimulationActive(final boolean simulationActive) {
		this.simulationActive = simulationActive;
	}

	public boolean isSimulationFire() {
		return simulationFire;
	}

	public void setSimulationFire(final boolean simulationFire) {
		this.simulationFire = simulationFire;
	}

	public String getFiringCondition() {
		return firingCondition;
	}

	public void setFiringCondition(final String firingCondition) {
		this.firingCondition = firingCondition.trim();
	}

	public boolean isKnockedOut() {
		return knockedOut;
	}

	public void setKnockedOut(final boolean knockedOut) {
		this.knockedOut = knockedOut;
	}

	@Override
	public List<String> getTransformationParameters() {
		List<String> list = super.getTransformationParameters();
		list.add("firingCondition");
		list.add("isKnockedOut");
		return list;
	}
}
