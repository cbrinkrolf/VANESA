package io;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.ContinuousTransition;
import biologicalObjects.nodes.petriNet.DiscreteTransition;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.StochasticTransition;
import biologicalObjects.nodes.petriNet.Transition;
import petriNet.SimulationResult;
import petriNet.SimulationResultController;

public class CSVWriter extends BaseWriter<Pathway> {
	private final String simId;

	public CSVWriter(File file, String simId) {
		super(file);
		this.simId = simId;
	}

	@Override
	protected void internalWrite(OutputStream outputStream, Pathway pw) throws Exception {
		SimulationResult simRes;
		if (simId == null) {
			simRes = pw.getPetriPropertiesNet().getSimResController().getLastActive();
		} else {
			simRes = pw.getPetriPropertiesNet().getSimResController().get(simId);
		}
		if (simRes != null) {
			String content = buildFileContent(pw, simRes);
			outputStream.write(content.getBytes());
		}
	}

	private static String buildFileContent(Pathway pw, SimulationResult simRes) {
		StringBuilder sb = new StringBuilder();
		sb.append("\"Time\";");
		List<Place> places = new ArrayList<>();
		List<Transition> transitions = new ArrayList<>();
		List<BiologicalNodeAbstract> nodes = pw.getAllGraphNodesSortedAlphabetically();
		for (BiologicalNodeAbstract bna : nodes) {
			if (bna instanceof Place && !bna.isLogical()) {
				places.add((Place) bna);
			} else if (bna instanceof Transition && !bna.isLogical()) {
				transitions.add((Transition) bna);
			}
		}
		List<BiologicalEdgeAbstract> edges = new ArrayList<>(pw.getAllEdgesSorted());
		for (Place item : places) {
			sb.append("\"").append(item.getName()).append("\";");
		}
		for (Transition t : transitions) {
			if (t instanceof DiscreteTransition) {
				sb.append("\"").append(t.getName()).append("-active\";");
				sb.append("\"").append(t.getName()).append("-fire\";");
				sb.append("\"").append(t.getName()).append("-delay\";");
			} else if (t instanceof StochasticTransition) {
				sb.append("\"").append(t.getName()).append("-active\";");
				sb.append("\"").append(t.getName()).append("-fire\";");
				sb.append("\"").append(t.getName()).append("-delay\";");
			} else if (t instanceof ContinuousTransition) {
				sb.append("\"").append(t.getName()).append("-fire\";");
				sb.append("\"").append(t.getName()).append("-speed\";");
			}
		}
		for (BiologicalEdgeAbstract bea : edges) {
			sb.append("\"").append(bea.getFrom().getName()).append("-").append(bea.getTo().getName())
					.append("-token\";");
			sb.append("\"").append(bea.getFrom().getName()).append("-").append(bea.getTo().getName())
					.append("-tokenSum\";");
		}
		sb.append("\r\n");
		for (int t = 0; t < simRes.getTime().size(); t++) {
			sb.append("\"").append(simRes.getTime().get(t)).append("\";");
			for (Place place : places) {
				// sb.append(places.get(i).getName() + ";");
				sb.append("\"").append(simRes.get(place, SimulationResultController.SIM_TOKEN).get(t)).append("\";");
			}
			for (Transition transition : transitions) {
				if (transition instanceof DiscreteTransition) {
					if (simRes.contains(transition, SimulationResultController.SIM_ACTIVE)) {
						sb.append("\"").append(simRes.get(transition, SimulationResultController.SIM_ACTIVE).get(t))
								.append("\";");
					} else {
						sb.append("\"\";");
					}
					if (simRes.contains(transition, SimulationResultController.SIM_FIRE)) {
						sb.append("\"").append(simRes.get(transition, SimulationResultController.SIM_FIRE).get(t))
								.append("\";");
					} else {
						sb.append("\"\";");
					}
					if (simRes.contains(transition, SimulationResultController.SIM_DELAY)) {
						sb.append("\"").append(simRes.get(transition, SimulationResultController.SIM_DELAY).get(t))
								.append("\";");
					} else {
						sb.append("\"\";");
					}
				} else if (transition instanceof StochasticTransition) {
					if (simRes.contains(transition, SimulationResultController.SIM_ACTIVE)) {
						sb.append("\"").append(simRes.get(transition, SimulationResultController.SIM_ACTIVE).get(t))
								.append("\";");
					} else {
						sb.append("\"\";");
					}
					if (simRes.contains(transition, SimulationResultController.SIM_FIRE)) {
						sb.append("\"").append(simRes.get(transition, SimulationResultController.SIM_FIRE).get(t))
								.append("\";");
					} else {
						sb.append("\"\";");
					}
					if (simRes.contains(transition, SimulationResultController.SIM_DELAY)) {
						sb.append("\"").append(simRes.get(transition, SimulationResultController.SIM_DELAY).get(t))
								.append("\";");
					} else {
						sb.append("\"\";");
					}
				} else if (transition instanceof ContinuousTransition) {
					if (simRes.contains(transition, SimulationResultController.SIM_FIRE)) {
						sb.append("\"").append(simRes.get(transition, SimulationResultController.SIM_FIRE).get(t))
								.append("\";");
					} else {
						sb.append("\"\";");
					}
					if (simRes.contains(transition, SimulationResultController.SIM_ACTUAL_FIRING_SPEED)) {
						sb.append("\"")
								.append(simRes.get(transition, SimulationResultController.SIM_ACTUAL_FIRING_SPEED).get(t))
								.append("\";");
					} else {
						sb.append("\"\";");
					}
				}
			}
			for (BiologicalEdgeAbstract edge : edges) {
				if (simRes.contains(edge, SimulationResultController.SIM_ACTUAL_TOKEN_FLOW)) {
					sb.append("\"").append(simRes.get(edge, SimulationResultController.SIM_ACTUAL_TOKEN_FLOW).get(t))
							.append("\";");
				} else {
					sb.append("\"\";");
				}
				if (simRes.contains(edge, SimulationResultController.SIM_SUM_OF_TOKEN)) {
					sb.append("\"").append(simRes.get(edge, SimulationResultController.SIM_SUM_OF_TOKEN).get(t))
							.append("\";");
				} else {
					sb.append("\"\";");
				}
			}
			sb.append("\r\n");
		}
		return sb.toString();
	}
}
