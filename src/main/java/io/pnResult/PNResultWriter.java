package io.pnResult;

import java.io.File;
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
import io.BaseWriter;
import io.csv.CSVWriter;
import petriNet.SimulationResult;
import petriNet.SimulationResultController;
import util.TableData;

public class PNResultWriter {
	private final File file;
	private final SimulationResult simRes;
	private final Pathway pw;

	public PNResultWriter(File file, SimulationResult simRes, Pathway pw) {
		this.file = file;
		this.simRes = simRes;
		this.pw = pw;
	}

	public BaseWriter<Pathway> getCSVWriter() {
		if (simRes != null) {
			TableData<String> data = getTableData();
			return new CSVWriter(file, data, ';', "\r\n");
		}
		return null;
	}

	private TableData<String> getTableData() {
		TableData<String> data = new TableData<>();

		List<String> headers = new ArrayList<>();

		headers.add("\"Time\"");
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
			headers.add("\"" + item.getName() + "\"");
		}
		for (Transition t : transitions) {
			if (t instanceof DiscreteTransition) {
				headers.add("\"" + t.getName() + "-active\"");
				headers.add("\"" + t.getName() + "-fire\"");
				headers.add("\"" + t.getName() + "-delay\"");
			} else if (t instanceof StochasticTransition) {
				headers.add("\"" + t.getName() + "-active\"");
				headers.add("\"" + t.getName() + "-fire\"");
				headers.add("\"" + t.getName() + "-delay\"");
			} else if (t instanceof ContinuousTransition) {
				headers.add("\"" + t.getName() + "-fire\"");
				headers.add("\"" + t.getName() + "-speed\"");
			}
		}
		for (BiologicalEdgeAbstract bea : edges) {
			headers.add("\"" + bea.getFrom().getName() + "-" + bea.getTo().getName() + "-token\"");
			headers.add("\"" + bea.getFrom().getName() + "-" + bea.getTo().getName() + "-tokenSum\"");
		}
		data.setHeaders(headers);

		for (int t = 0; t < simRes.getTime().size(); t++) {
			final List<String> row = new ArrayList<>();

			row.add("\"" + simRes.getTime().get(t) + "\"");
			for (Place place : places) {
				// sb.append(places.get(i).getName() + ";");
				row.add("\"" + simRes.get(place, SimulationResultController.SIM_TOKEN).get(t) + "\"");
			}
			for (Transition transition : transitions) {
				if (transition instanceof DiscreteTransition) {
					if (simRes.contains(transition, SimulationResultController.SIM_ACTIVE)) {
						row.add("\"" + simRes.get(transition, SimulationResultController.SIM_ACTIVE).get(t) + "\"");
					} else {
						row.add("\"\"");
					}
					if (simRes.contains(transition, SimulationResultController.SIM_FIRE)) {
						row.add("\"" + simRes.get(transition, SimulationResultController.SIM_FIRE).get(t) + "\"");
					} else {
						row.add("\"\"");
					}
					if (simRes.contains(transition, SimulationResultController.SIM_DELAY)) {
						row.add("\"" + simRes.get(transition, SimulationResultController.SIM_DELAY).get(t) + "\"");
					} else {
						row.add("\"\"");
					}
				} else if (transition instanceof StochasticTransition) {
					if (simRes.contains(transition, SimulationResultController.SIM_ACTIVE)) {
						row.add("\"" + simRes.get(transition, SimulationResultController.SIM_ACTIVE).get(t) + "\"");
					} else {
						row.add("\"\"");
					}
					if (simRes.contains(transition, SimulationResultController.SIM_FIRE)) {
						row.add("\"" + simRes.get(transition, SimulationResultController.SIM_FIRE).get(t) + "\"");
					} else {
						row.add("\"\"");
					}
					if (simRes.contains(transition, SimulationResultController.SIM_DELAY)) {
						row.add("\"" + simRes.get(transition, SimulationResultController.SIM_DELAY).get(t) + "\"");
					} else {
						row.add("\"\"");
					}
				} else if (transition instanceof ContinuousTransition) {
					if (simRes.contains(transition, SimulationResultController.SIM_FIRE)) {
						row.add("\"" + simRes.get(transition, SimulationResultController.SIM_FIRE).get(t) + "\"");
					} else {
						row.add("\"\"");
					}
					if (simRes.contains(transition, SimulationResultController.SIM_ACTUAL_FIRING_SPEED)) {
						row.add("\"" + simRes.get(transition, SimulationResultController.SIM_ACTUAL_FIRING_SPEED).get(t)
								+ "\"");
					} else {
						row.add("\"\"");
					}
				}
			}
			for (BiologicalEdgeAbstract edge : edges) {
				if (simRes.contains(edge, SimulationResultController.SIM_ACTUAL_TOKEN_FLOW)) {
					row.add("\"" + simRes.get(edge, SimulationResultController.SIM_ACTUAL_TOKEN_FLOW).get(t) + "\"");
				} else {
					row.add("\"\"");
				}
				if (simRes.contains(edge, SimulationResultController.SIM_SUM_OF_TOKEN)) {
					row.add("\"" + simRes.get(edge, SimulationResultController.SIM_SUM_OF_TOKEN).get(t) + "\"");
				} else {
					row.add("\"\"");
				}
			}
			data.addRow(row);
		}
		return data;
	}
}
