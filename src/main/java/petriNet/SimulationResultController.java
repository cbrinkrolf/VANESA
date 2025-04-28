package petriNet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import biologicalElements.GraphElementAbstract;
import graph.GraphInstance;
import gui.MainWindow;

public class SimulationResultController {
	/**
	 * should be concurrent, otherwise it might lead to problems if a simulation is performed and simulation runs are
	 * deleted in the sim menu. Then Ids get messed up.
	 */
	private final Map<String, SimulationResult> series = new ConcurrentHashMap<>();
	private final List<String> simIds = new ArrayList<>();

	public SimulationResult get(final String simId) {
		return simIds.contains(simId) ? series.get(simId) : add(simId);
	}

	public SimulationResult add(final String simId) {
		final SimulationResult result;
		synchronized (series) {
			result = new SimulationResult(simId, simIds.size() + "", false);
			series.put(simId, result);
			simIds.add(simId);
		}
		return result;
	}

	public void removeSimulationResult(final String simId) {
		if (series.containsKey(simId)) {
			series.remove(simId);
			simIds.remove(simId);
		}
	}

	public int size() {
		return simIds.size();
	}

	public SimulationResult getLastActive() {
		for (int i = simIds.size() - 1; i >= 0; i--) {
			final SimulationResult s = series.get(simIds.get(i));
			if (s.isActive()) {
				return s;
			}
		}
		return null;
	}

	public List<SimulationResult> getAll() {
		final List<SimulationResult> list = new ArrayList<>();
		for (final String simId : simIds) {
			list.add(series.get(simId));
		}
		return list;
	}

	public List<SimulationResult> getAllActive() {
		final List<SimulationResult> list = new ArrayList<>();
		for (final String simId : simIds) {
			final SimulationResult s = series.get(simId);
			if (s.isActive()) {
				list.add(s);
			}
		}
		return list;
	}

	public List<SimulationResult> getAllActiveWithData(final GraphElementAbstract gea,
			final SimulationResultSeriesKey simulationAttribute) {
		final List<SimulationResult> list = new ArrayList<>();
		for (final String simId : simIds) {
			final SimulationResult s = series.get(simId);
			if (s.isActive() && s.contains(gea, simulationAttribute)) {
				list.add(s);
			}
		}
		return list;
	}

	public void setAllActive(final boolean active) {
		for (final SimulationResult simRes : series.values()) {
			simRes.setActive(active);
		}
	}

	public void remove(final int i) {
		remove(series.get(simIds.get(i)));
	}

	public void remove(final SimulationResult result) {
		series.remove(result.getId());
		simIds.remove(result.getId());
		if (simIds.isEmpty()) {
			GraphInstance.getPathway().getPetriPropertiesNet().setPetriNetSimulation(false);
			MainWindow.getInstance().updateAllGuiElements();
		}
	}

	public boolean containsSimId(final String simId) {
		return simIds.contains(simId);
	}

	public List<String> getSimIds() {
		return simIds;
	}
}
