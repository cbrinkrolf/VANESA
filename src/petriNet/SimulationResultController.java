package petriNet;

import java.util.HashMap;

public class SimulationResultController {

	// for places
	public static int SIM_TOKEN = 1;

	// for transitions
	public static int SIM_ACTUAL_FIRING_SPEED = 0;
	public static int SIM_FIRE = 2;

	// for edges
	public static int SIM_SUM_OF_TOKEN = 3;
	public static int SIM_ACTUAL_TOKEN_FLOW = 4;

	private HashMap<String, SimulationResult> series;

	private boolean filteredDefault = false;

	public SimulationResultController() {
		series = new HashMap<String, SimulationResult>();
	}

	public SimulationResult get(String simulation) {
		if (!series.containsKey(simulation)) {
			series.put(simulation, new SimulationResult(filteredDefault));
		}
		return series.get(simulation);
	}

	public void removeSimulationResult(String result) {
		if (series.containsKey(result)) {
			series.remove(result);
		}
	}
	
	public int getSize(){
		return series.size();
	}

}
