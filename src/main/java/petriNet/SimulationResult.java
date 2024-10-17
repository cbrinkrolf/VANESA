package petriNet;

import java.sql.ResultSet;
import java.util.List;
import java.util.Set;

import biologicalElements.GraphElementAbstract;
import biologicalObjects.edges.petriNet.PNArc;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
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

	public SimulationResult(String id, String name, boolean filtered) {
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

	public void setActive(boolean active) {
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

	public void addTime(double time) {
		this.time.add(time);
	}

	public void addValue(GraphElementAbstract gea, int type, double value) {
		if (!result.contains(gea, type)) {
			result.put(gea, type, new Series());
		}
		result.get(gea, type).add(value);
	}

	public Series get(GraphElementAbstract gea, int type) {
		return result.get(gea, type);
	}

	public List<Double> getValues(GraphElementAbstract gea, int type) {
		return result.get(gea, type).getAll();
	}

	public Double getValue(GraphElementAbstract gea, int type, int pos) {
		return result.get(gea, type).get(pos);
	}

	public List<Double> getFiltered(GraphElementAbstract gea, int type) {
		return resultFiltered.get(gea, type).getValues().getAll();
	}

	public Double getFiltered(GraphElementAbstract gea, int type, int pos) {
		return resultFiltered.get(gea, type).getValues().get(pos);
	}

	public List<Double> getTimeValues() {
		return time.getAll();
	}

	public List<Double> getTime(GraphElementAbstract gea, int type, boolean filtered) {
		if (filtered) {
			return resultFiltered.get(gea, type).getTime().getAll();
		} else {
			return time.getAll();
		}
	}

	public void refreshFilter() {
		resultFiltered = new DoubleHashMap<>();
	}

	public boolean contains(GraphElementAbstract gea) {
		return result.contains(gea);
	}

	public boolean contains(GraphElementAbstract gea, int type) {
		return result.contains(gea, type);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.trim();
	}

	public int size() {
		return this.time.size();
	}

	public void refineEdgeFlow(Set<PNArc> arcs) {
		Series delayS;
		
		double lastToken;
		int revisedTime ;
		double currentToken;
		for (PNArc arc : arcs) {
			double delay;
			if (arc.getFrom() instanceof DiscreteTransition || arc.getFrom() instanceof StochasticTransition) {
				delayS = get(arc.getFrom(), SimulationResultController.SIM_DELAY);
			} else if (arc.getTo() instanceof Transition || arc.getTo() instanceof StochasticTransition) {
				delayS = get(arc.getTo(), SimulationResultController.SIM_DELAY);
			} else {
				System.out.println("cont");
				continue;
			}

			if (result.contains(arc, SimulationResultController.SIM_SUM_OF_TOKEN)
					&& result.get(arc, SimulationResultController.SIM_SUM_OF_TOKEN).size() > 0
					&& !result.contains(arc, SimulationResultController.SIM_ACTUAL_TOKEN_FLOW) && delayS != null
					&& delayS.size() > 0) {

				Series sumS = result.get(arc, SimulationResultController.SIM_SUM_OF_TOKEN);

				lastToken = sumS.get(0);
				revisedTime = 0;
				for (int i = 0; i < time.size(); i++) {
					currentToken = sumS.get(i);

					// fired
					if (currentToken > lastToken) {
						//System.out.println("fired");
						delay = delayS.get(i - 2);
						//System.out.println(arc.getFrom().getName() + " -> " + arc.getTo().getName() + " t: "
							//	+ time.get(i) + " d: " + delay);
						for (int j = revisedTime; j <= i; j++) {
							if (time.get(j) + delay >= time.get(i)) {
								addValue(arc, SimulationResultController.SIM_ACTUAL_TOKEN_FLOW,
										currentToken - lastToken);
							} else {
								addValue(arc, SimulationResultController.SIM_ACTUAL_TOKEN_FLOW, 0);
							}
							revisedTime = j;
						}
						lastToken = currentToken;
						revisedTime++;
					}
				}
				for (int i = revisedTime; i < time.size(); i++) {
					addValue(arc, SimulationResultController.SIM_ACTUAL_TOKEN_FLOW, 0);
				}
			}
		}
	}

	public void refineDiscreteTransitionIsFiring(Set<DiscreteTransition> transitions) {
		Series fireTimeS;
		Series delayS;
		Series fireS;
		Double fireTime;
		Double delay;
		int lastIdx;

		for (DiscreteTransition t : transitions) {

			for (int i = 0; i < getTime().size(); i++) {
				addValue(t, SimulationResultController.SIM_FIRE, 0.0);
			}

			fireTimeS = get(t, SimulationResultController.SIM_FIRE_TIME);
			delayS = get(t, SimulationResultController.SIM_DELAY);
			fireS = get(t, SimulationResultController.SIM_FIRE);

			fireTime = fireTimeS.get(0);
			delay = delayS.get(0);

			lastIdx = 0;
			for (int index = 0; index < getTime().size(); index++) {
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
				if (fireS.get(index) == 0)
					delayS.setValue(index, 0.0);
			}
		}
	}

	public void refineStochasticTransitionIsFiring(Set<StochasticTransition> transitions) {
		Series putDelayS;
		Series fireTimeS;
		Series delayS;
		Series fireS;
		Double fireTime;
		Double putDelay;
		int lastIdx;

		for (StochasticTransition t : transitions) {

			putDelayS = result.get(t, SimulationResultController.SIM_PUT_DELAY);

			for (int i = 0; i < getTime().size(); i++) {
				addValue(t, SimulationResultController.SIM_DELAY, 0.0);
				addValue(t, SimulationResultController.SIM_FIRE, 0.0);
			}

			fireTimeS = get(t, SimulationResultController.SIM_FIRE_TIME);
			delayS = get(t, SimulationResultController.SIM_DELAY);
			fireS = get(t, SimulationResultController.SIM_FIRE);

			fireTime = fireTimeS.get(0);
			putDelay = putDelayS.get(0);

			lastIdx = 0;
			for (int index = 1; index < getTime().size(); index++) {
				// System.out.println("t: " + res.getTime().get(index) + " delay: " +
				// delayS.get(index));
				// fire event occurred
				// System.out.println(fireTime + " " + fireTimeS.get(index));
				if (fireTimeS.get(index) - fireTime > 0.0) {
					// System.out.println("fired at time: " + getTime().get(index));
					for (int j = lastIdx; j < index; j++) {
						// System.out.println("time: "+getTime().get(index)+ " putDelay: "+putDelay);
						// System.out.println(getTime().get(j) + " >= " + (getTime().get(index) -
						// putDelay));
						if (getTime().get(j) >= getTime().get(index) - putDelay) {
							delayS.setValue(j - 1, putDelay);
							fireS.setValue(j - 1, 1.0);
							// System.out.println(
							// "t: " + res.getTime().get(j) + " put Delay of: " + delayS.get(j));
						} else {
							// System.out.println("t: " + getTime().get(j) + " kleiner: " +
							// delayS.get(j));
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
