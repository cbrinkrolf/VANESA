package petriNet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import biologicalElements.GraphElementAbstract;
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
	private List<String> simIds = new ArrayList<String>();

	private boolean filteredDefault = false;

	public SimulationResultController() {
		series = new HashMap<String, SimulationResult>();
	}

	public SimulationResult get(String simId) {
		if (!simIds.contains(simId)) {
			series.put(simId, new SimulationResult(simId, simIds.size()+"",filteredDefault));
			simIds.add(simId);
		}
		return series.get(simId);
	}

	public void removeSimulationResult(String simId) {
		if (series.containsKey(simId)) {
			series.remove(simId);
			simIds.remove(simId);
		}
	}

	public int size() {
		return simIds.size();
	}

	public SimulationResult getLastActive() {
		List<SimulationResult> active = this.getAllActive();
		if (active.size() > 0) {
			return active.get(active.size() - 1);
		} else {
			return null;
		}
	}

	public List<SimulationResult> getAll() {
		List<SimulationResult> list = new ArrayList<SimulationResult>();
		for (int i = 0; i < this.simIds.size(); i++) {
			list.add(this.series.get(this.simIds.get(i)));
		}
		return list;
	}

	public List<SimulationResult> getAllActive() {
		List<SimulationResult> list = new ArrayList<SimulationResult>();
		for (int i = 0; i < this.simIds.size(); i++) {
			if (this.series.get(this.simIds.get(i)).isActive()) {
				list.add(this.series.get(this.simIds.get(i)));
			}
		}
		return list;
	}
	
	public List<SimulationResult> getAllActiveWithData(GraphElementAbstract gea, int simulationAttribute){
		List<SimulationResult> list = new ArrayList<SimulationResult>();
		List<SimulationResult> active = getAllActive();
		for (int i = 0; i < active.size(); i++) {
			if (active.get(i).contains(gea, simulationAttribute)) {
				list.add(active.get(i));
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
		this.series.remove(this.simIds.get(i));
		this.simIds.remove(i);
		if(simIds.size() < 1){
			GraphInstance graphInstance = new GraphInstance();
			graphInstance.getPathway().getPetriPropertiesNet().setPetriNetSimulation(false);
			MainWindow w = MainWindow.getInstance();
			w.updateAllGuiElements();
		}
	}
	
	public List<String> getSimIds(){
		return this.simIds;
	}
}
