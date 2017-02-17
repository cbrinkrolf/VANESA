package petriNet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import graph.GraphInstance;
import gui.MainWindow;

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
		if (!simNames.contains(simulation)) {
			series.put(simulation, new SimulationResult(simulation, filteredDefault));
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
			// System.out.println("returned: "+simNames.get(simNames.size()-1));
			return series.get(simNames.get(simNames.size() - 1));
		} else {
			return null;
		}
	}

	public List<SimulationResult> getAll() {
		List<SimulationResult> list = new ArrayList<SimulationResult>();
		for (int i = 0; i < this.simNames.size(); i++) {
			list.add(this.series.get(this.simNames.get(i)));
		}

		return list;
	}

	public List<SimulationResult> getAllActive() {
		List<SimulationResult> list = new ArrayList<SimulationResult>();
		for (int i = 0; i < this.simNames.size(); i++) {
			if (this.series.get(this.simNames.get(i)).isActive()) {
				list.add(this.series.get(this.simNames.get(i)));
			}
		}

		return list;
	}

	public void setAllActive(boolean active) {
		Iterator<SimulationResult> it = series.values().iterator();
		SimulationResult simRes;
		while (it.hasNext()) {
			simRes = it.next();
			simRes.setActive(active);
		}
	}
	
	public void remove(int i){
		this.series.remove(this.simNames.get(i));
		this.simNames.remove(i);
		if(simNames.size() < 1){
			GraphInstance graphInstance = new GraphInstance();
			graphInstance.getPathway().getPetriNet().setPetriNetSimulation(false);
			MainWindow w = MainWindow.getInstance();
			w.updateAllGuiElements();
		}
	}
	
	public List<String> getSimNames(){
		return this.simNames;
	}
}
