package petriNet;

import java.util.List;
import java.util.Set;

import biologicalElements.GraphElementAbstract;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.StochasticTransition;
import biologicalObjects.nodes.petriNet.Transition;
import util.DoubleHashMap;

public class SimulationResult {
	private final DoubleHashMap<GraphElementAbstract, Integer, Series> result = new DoubleHashMap<>();
	private DoubleHashMap<GraphElementAbstract, Integer, TimeSeries> resultFiltered;
	private final Series time;
	private boolean active = true;
	private String name;
	private final String id;
	private final StringBuilder logMessage = new StringBuilder();

	public SimulationResult(final String id, final String name, final boolean filtered) {
		this.id = id;
		this.name = name;
		time = new Series();
		if (filtered) {
			resultFiltered = new DoubleHashMap<>();
		}
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(final boolean active) {
		this.active = active;
	}

	public String getId() {
		return id;
	}

	public StringBuilder getLogMessage() {
		return logMessage;
	}

	public Series getTime() {
		return time;
	}

	public void addTime(final double time) {
		this.time.add(time);
	}

	public void addValue(final GraphElementAbstract gea, final int type, final Double value) {
		if (!result.containsKey(gea, type)) {
			result.put(gea, type, new Series());
		}
		result.get(gea, type).add(value);
	}

	public Series get(final GraphElementAbstract gea, final int type) {
		return result.get(gea, type);
	}

	public List<Double> getValues(final GraphElementAbstract gea, final int type) {
		return result.get(gea, type).getAll();
	}

	public Double getValue(final GraphElementAbstract gea, final int type, final int pos) {
		return result.get(gea, type).get(pos);
	}

	public List<Double> getFiltered(final GraphElementAbstract gea, final int type) {
		return resultFiltered.get(gea, type).getValues().getAll();
	}

	public Double getFiltered(final GraphElementAbstract gea, final int type, final int pos) {
		return resultFiltered.get(gea, type).getValues().get(pos);
	}

	public List<Double> getTimeValues() {
		return time.getAll();
	}

	public List<Double> getTime(final GraphElementAbstract gea, final int type, final boolean filtered) {
		if (filtered) {
			return resultFiltered.get(gea, type).getTime().getAll();
		} else {
			return time.getAll();
		}
	}

	public void refreshFilter() {
		resultFiltered = new DoubleHashMap<>();
	}

	public boolean contains(final GraphElementAbstract gea) {
		return result.containsKey(gea);
	}

	public boolean contains(final GraphElementAbstract gea, final int type) {
		return result.containsKey(gea, type);
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name.trim();
	}

	public int size() {
		return this.time.size();
	}

	public void refineEdgeFlow(final Set<PNArc> arcs) {
		for (final PNArc arc : arcs) {
			final Series delayS;
			if (arc.getFrom() instanceof DiscreteTransition || arc.getFrom() instanceof StochasticTransition) {
				delayS = get(arc.getFrom(), SimulationResultController.SIM_DELAY);
			} else if (arc.getTo() instanceof Transition || arc.getTo() instanceof StochasticTransition) {
				delayS = get(arc.getTo(), SimulationResultController.SIM_DELAY);
			} else {
				continue;
			}
			if (result.containsKey(arc, SimulationResultController.SIM_SUM_OF_TOKEN)
					&& result.get(arc, SimulationResultController.SIM_SUM_OF_TOKEN).size() > 0
					&& !result.containsKey(arc, SimulationResultController.SIM_ACTUAL_TOKEN_FLOW) && delayS != null
					&& delayS.size() > 0) {

				Series sumS = result.get(arc, SimulationResultController.SIM_SUM_OF_TOKEN);
				double lastToken = sumS.get(0);
				int revisedTime = 0;
				for (int i = 0; i < time.size(); i++) {
					double currentToken = sumS.get(i);
					// fired
					if (currentToken > lastToken) {
						double delay = delayS.get(i - 2);
						for (int j = revisedTime; j <= i; j++) {
							if (time.get(j) + delay >= time.get(i)) {
								addValue(arc, SimulationResultController.SIM_ACTUAL_TOKEN_FLOW,
										currentToken - lastToken);
							} else {
								addValue(arc, SimulationResultController.SIM_ACTUAL_TOKEN_FLOW, 0d);
							}
							revisedTime = j;
						}
						lastToken = currentToken;
						revisedTime++;
					}
				}
				for (int i = revisedTime; i < time.size(); i++) {
					addValue(arc, SimulationResultController.SIM_ACTUAL_TOKEN_FLOW, 0d);
				}
			}
		}
	}

	public void refineDiscreteTransitionIsFiring(final Set<DiscreteTransition> transitions) {
		for (final DiscreteTransition t : transitions) {
			for (int i = 0; i < getTime().size(); i++) {
				addValue(t, SimulationResultController.SIM_FIRE, 0.0);
			}
			final Series fireTimeS = get(t, SimulationResultController.SIM_FIRE_TIME);
			final Series delayS = get(t, SimulationResultController.SIM_DELAY);
			final Series fireS = get(t, SimulationResultController.SIM_FIRE);
			if (fireTimeS != null && delayS != null && fireS != null && !t.isKnockedOut()) {
				// TODO dirty hack to exclude knocked out transitions, needs to be improved
				Double fireTime = fireTimeS.get(0);
				Double delay = delayS.get(0);
				int lastIdx = 0;
				for (int index = 0; index < getTime().size(); index++) {
					// fire event occurred
					if (fireTimeS.get(index) - fireTime > 0.0) {
						for (int j = lastIdx; j <= index; j++) {
							if (getTime().get(j) >= getTime().get(index) - delay) {
								fireS.setValue(Math.max(j - 1, 0), 1.0);
							} else {
								delayS.setValue(Math.max(j - 1, 0), 0.0);
							}
						}
						fireS.setValue(index - 1, 1.0);
						lastIdx = index + 1;
					}
					fireTime = get(t, SimulationResultController.SIM_FIRE_TIME).get(index);
					delay = delayS.get(index);
				}
				if (getTime().size() > 1) {
					delayS.setValue(0, delayS.get(1));
				}
				for (int index = 0; index < getTime().size(); index++) {
					if (fireS.get(index) == 0) {
						delayS.setValue(index, 0.0);
					}
				}
			}
		}
	}

	public void refineStochasticTransitionIsFiring(final Set<StochasticTransition> transitions) {
		for (final StochasticTransition t : transitions) {
			for (int i = 0; i < getTime().size(); i++) {
				addValue(t, SimulationResultController.SIM_DELAY, 0.0);
				addValue(t, SimulationResultController.SIM_FIRE, 0.0);
			}
			final Series putDelayS = result.get(t, SimulationResultController.SIM_PUT_DELAY);
			final Series fireTimeS = get(t, SimulationResultController.SIM_FIRE_TIME);
			final Series delayS = get(t, SimulationResultController.SIM_DELAY);
			final Series fireS = get(t, SimulationResultController.SIM_FIRE);
			if (putDelayS != null && fireTimeS != null && delayS != null && fireS != null) {
				Double fireTime = fireTimeS.get(0);
				Double putDelay = putDelayS.get(0);
				int lastIdx = 0;
				for (int index = 1; index < getTime().size(); index++) {
					// fire event occurred
					if (fireTimeS.get(index) - fireTime > 0.0) {
						for (int j = lastIdx; j < index; j++) {
							if (getTime().get(j) >= getTime().get(index) - putDelay) {
								delayS.setValue(j - 1, putDelay);
								fireS.setValue(j - 1, 1.0);
							}
						}
						delayS.setValue(index - 1, putDelay);
						fireS.setValue(index - 1, 1.0);
						lastIdx = index;
					}
					fireTime = get(t, SimulationResultController.SIM_FIRE_TIME).get(index);
					putDelay = putDelayS.get(index);
				}
			}
		}
	}
}
