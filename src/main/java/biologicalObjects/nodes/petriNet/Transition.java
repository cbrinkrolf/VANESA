package biologicalObjects.nodes.petriNet;

import java.util.List;

public abstract class Transition extends PNNode {
	private boolean simulationActive;
	private boolean simulationFire;
	private String firingCondition = "true";// "time>9.8";
	private boolean knockedOut = false;

	public Transition(String label, String name) {
		super(label, name);
		if (label.equals(""))
			setLabel(name);
		if (name.equals(""))
			setName(label);
	}

	public boolean isSimulationActive() {
		return simulationActive;
	}

	public void setSimulationActive(boolean simulationActive) {
		this.simulationActive = simulationActive;
	}
	
	public boolean isSimulationFire() {
		return simulationFire;
	}

	public void setSimulationFire(boolean simulationFire) {
		this.simulationFire = simulationFire;
	}

	public String getFiringCondition() {
		return firingCondition;
	}

	public void setFiringCondition(String firingCondition) {
		this.firingCondition = firingCondition.trim();
	}

	public boolean isKnockedOut() {
		return knockedOut;
	}

	public void setKnockedOut(boolean knockedOut) {
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
