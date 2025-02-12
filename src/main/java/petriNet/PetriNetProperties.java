package petriNet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
import graph.GraphInstance;
import gui.SimMenu;
import io.pnResult.PNResultReader;

public class PetriNetProperties {
	private int currentTimeStep = 0;
	private String covGraph;
	private boolean isPetriNetSimulation = false;
	private SimulationResultController simResController = null;

	public String getCovGraph() {
		return covGraph;
	}

	public void setCovGraph(final String covGraph) {
		this.covGraph = covGraph;
	}

	public int getCurrentTimeStep() {
		return currentTimeStep;
	}

	public void setCurrentTimeStep(final int currentTimeStep) {
		this.currentTimeStep = currentTimeStep;
	}

	public PetriNetProperties() {
	}

	public void loadVanesaSimulationResult(final File resFile) throws IOException {
		final PNResultReader pnrir = new PNResultReader();
		final HashMap<String, List<Double>> result = pnrir.readResult(resFile);
		Pathway pw = GraphInstance.getPathway();
		// if BN holds PN
		if (!pw.isPetriNet()) {
			pw = pw.getTransformationInformation().getPetriNet();
		}
		String fileName = resFile.getName();
		if (getSimResController().getSimIds().contains(fileName)) {
			int i = 1;
			while (getSimResController().getSimIds().contains(fileName + "(" + i + ")")) {
				i++;
			}
			fileName += "(" + i + ")";
		}
		final SimulationResult simRes = getSimResController().get(fileName);
		simRes.setName(fileName);
		for (int i = 0; i < result.get("Time").size(); i++) {
			simRes.addTime(result.get("Time").get(i));
		}
		for (final BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
			if (bna instanceof Place) {
				if (result.containsKey(bna.getName())) {
					for (int i = 0; i < result.get(bna.getName()).size(); i++) {
						simRes.addValue(bna, SimulationResultController.SIM_TOKEN, result.get(bna.getName()).get(i));
					}
				}
			} else if (bna instanceof Transition) {
				if (result.containsKey(bna.getName() + "-active")) {
					for (int i = 0; i < result.get(bna.getName() + "-active").size(); i++) {
						simRes.addValue(bna, SimulationResultController.SIM_ACTIVE,
										result.get(bna.getName() + "-active").get(i));
					}
				}
				if (result.containsKey(bna.getName() + "-fire")) {
					for (int i = 0; i < result.get(bna.getName() + "-fire").size(); i++) {
						simRes.addValue(bna, SimulationResultController.SIM_FIRE,
										result.get(bna.getName() + "-fire").get(i));
					}
				}
				if (result.containsKey(bna.getName() + "-speed")) {
					for (int i = 0; i < result.get(bna.getName() + "-speed").size(); i++) {
						simRes.addValue(bna, SimulationResultController.SIM_ACTUAL_FIRING_SPEED,
										result.get(bna.getName() + "-speed").get(i));
					}
				}
				if (result.containsKey(bna.getName() + "-delay")) {
					for (int i = 0; i < result.get(bna.getName() + "-fire").size(); i++) {
						simRes.addValue(bna, SimulationResultController.SIM_DELAY,
										result.get(bna.getName() + "-delay").get(i));
					}
				}
			}
		}
		for (final BiologicalEdgeAbstract bea : pw.getAllEdges()) {
			final String name = bea.getFrom().getName() + "-" + bea.getTo().getName();
			// System.out.println(name);
			if (result.containsKey(name + "-tokenSum")) {
				for (int i = 0; i < result.get(name + "-tokenSum").size(); i++) {
					simRes.addValue(bea, SimulationResultController.SIM_SUM_OF_TOKEN,
									result.get(name + "-tokenSum").get(i));
				}
			}
			if (result.containsKey(name + "-token")) {
				for (int i = 0; i < result.get(name + "-token").size(); i++) {
					simRes.addValue(bea, SimulationResultController.SIM_ACTUAL_TOKEN_FLOW,
									result.get(name + "-token").get(i));
				}
			}
		}
		pw.setPlotColorPlacesTransitions(false);
		this.setPetriNetSimulation(true);
		SimMenu menu = pw.getPetriNetSimulation().getMenu();
		if (menu != null) {
			menu.updateSimulationResults();
		}
	}

	public void setPetriNetSimulation(boolean isPetriNetSimulation) {
		this.isPetriNetSimulation = isPetriNetSimulation;
	}

	public boolean isPetriNetSimulation() {
		return isPetriNetSimulation;
	}

	public SimulationResultController getSimResController() {
		if (simResController == null) {
			simResController = new SimulationResultController();
		}
		return simResController;
	}
}
