package petriNet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
	private List<String> simNames = new ArrayList<String>();

	private boolean filteredDefault = false;

	public SimulationResultController() {
		series = new HashMap<String, SimulationResult>();
	}

	public SimulationResult get(String simulation) {
		if (!series.containsKey(simulation)) {
			series.put(simulation, new SimulationResult(filteredDefault));
			simNames.add(simulation);
		}
		return series.get(simulation);
	}

	public void removeSimulationResult(String result) {
		if (series.containsKey(result)) {
			series.remove(result);
			simNames.remove(result);
		}
	}

	public int size() {
		return simNames.size();
	}

	public SimulationResult get() {
		if (series.size() > 0) {
			//System.out.println("returned: "+simNames.get(simNames.size()-1));
			return series.get(simNames.get(simNames.size()-1));
		} else {
			return null;
		}
	}

}
