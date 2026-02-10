package petriNet;

public class SimulationProperties {

	private boolean isServerRunning = false;
	private String simId = "";

	public boolean isServerRunning() {
		return isServerRunning;
	}

	public void setServerRunning(boolean isServerRunning) {
		this.isServerRunning = isServerRunning;
	}

	public String getSimId() {
		return simId;
	}

	public void setSimId(String simId) {
		this.simId = simId;
	}

}
