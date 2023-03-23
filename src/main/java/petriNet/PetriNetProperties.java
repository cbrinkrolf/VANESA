package petriNet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.petriNet.Place;
import biologicalObjects.nodes.petriNet.Transition;
import graph.GraphInstance;

public class PetriNetProperties {
	private Pathway pw;
	private PNResultInputReader pnrir = new PNResultInputReader();
	private int currentTimeStep = 0;
	private String covGraph;
	private boolean isPetriNetSimulation = false;
	private SimulationResultController simResController = null;

	public String getCovGraph() {
		return this.covGraph;
	}

	public void setCovGraph(String covGraph) {
		this.covGraph = covGraph;
	}

	public int getCurrentTimeStep() {
		return currentTimeStep;
	}

	public void setCurrentTimeStep(int currentTimeStep) {
		this.currentTimeStep = currentTimeStep;
	}

	public PetriNetProperties() {
	}

	public void loadVanesaSimulationResult(File resFile) {

		try {
			HashMap<String, List<Double>> result = pnrir.readResult(resFile);
			pw = GraphInstance.getPathway();
			// if BN holds PN
			if (!pw.isPetriNet()) {
				pw = pw.getTransformationInformation().getPetriNet();
			}
			BiologicalNodeAbstract bna;
			Iterator<BiologicalNodeAbstract> it = this.pw.getAllGraphNodes().iterator();

			String fileName = resFile.getName();
			// System.out.println(fileName);
			// System.out.println(this.pw.getPetriPropertiesNet().getSimResController().getSimIds().size());
			// System.out.println(this.getSimResController().getSimIds().size());
			// System.out.println("n: "+this.getSimResController().getSimIds().get(0));
			if (this.getSimResController().getSimIds().contains(fileName)) {
				int i = 1;
				while (this.getSimResController().getSimIds().contains(fileName + "(" + i + ")")) {
					i++;
				}
				fileName += "(" + i + ")";
				// System.out.println("contains");
			}
			SimulationResult simRes = this.getSimResController().get(fileName);
			simRes.setName(fileName);
			// System.out.println(fileName);
			for (int i = 0; i < result.get("Time").size(); i++) {
				simRes.addTime(result.get("Time").get(i));
			}
			while (it.hasNext()) {
				bna = it.next();
				if (bna instanceof Place) {
					if (result.containsKey(bna.getName())) {
						for (int i = 0; i < result.get(bna.getName()).size(); i++) {
							simRes.addValue(bna, SimulationResultController.SIM_TOKEN,
									result.get(bna.getName()).get(i));
						}
					}
				} else if (bna instanceof Transition) {
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
				}
			}
			Iterator<BiologicalEdgeAbstract> it2 = this.pw.getAllEdges().iterator();
			BiologicalEdgeAbstract bea;
			String name;
			while (it2.hasNext()) {
				bea = it2.next();
				name = bea.getFrom().getName() + "-" + bea.getTo().getName();
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
		} catch (IOException e) {
			e.printStackTrace();
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
