package biologicalElements;

import graph.GraphInstance;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import petriNet.PNResultInputReader;
import petriNet.Place;
import petriNet.SimulationResult;
import petriNet.SimulationResultController;
import petriNet.Transition;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class PetriNet {
	private GraphInstance graphInstance = null;
	private Pathway pw;
	private String petriNetSimulationFile = null;
	private PNResultInputReader pnrir = new PNResultInputReader();
	private int places = 0;
	private int transitions = 0;
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

	public PetriNet() {
		// TODO Auto-generated constructor stub
	}

	public String getPetriNetSimulationFile() {
		return petriNetSimulationFile;
	}

	public void setPetriNetSimulationFile(String petriNetSimulationFile) {
		this.petriNetSimulationFile = petriNetSimulationFile;
	}


	public void loadVanesaSimulationResult(File resFile){
		
		try {
			
			HashMap<String, List<Double>> result = pnrir.readResult(resFile);
			
			
			//System.out.println(result.keySet());
			
			graphInstance = new GraphInstance();
			pw = graphInstance.getPathway();
			
			BiologicalNodeAbstract bna;
			Iterator<BiologicalNodeAbstract> it = this.pw.getAllGraphNodes().iterator();
			SimulationResult simRes = this.simResController.get("test");
			
			for(int i = 0; i<result.get("Time").size(); i++){
				simRes.addTime(result.get("Time").get(i));
			}
			
			while(it.hasNext()){
				bna = it.next();
				if(bna instanceof Place){
					if(result.containsKey(bna.getName())){
						for(int i = 0; i<result.get(bna.getName()).size(); i++){
							simRes.addValue(bna, SimulationResultController.SIM_TOKEN, result.get(bna.getName()).get(i));
						}
					}
				}else if(bna instanceof Transition){
					if(result.containsKey(bna.getName()+"-fire")){
						for(int i = 0; i<result.get(bna.getName()+"-fire").size(); i++){
							simRes.addValue(bna, SimulationResultController.SIM_FIRE, result.get(bna.getName()+"-fire").get(i));
						}
					}
					if(result.containsKey(bna.getName()+"-speed")){
						for(int i = 0; i<result.get(bna.getName()+"-speed").size(); i++){
							simRes.addValue(bna, SimulationResultController.SIM_ACTUAL_FIRING_SPEED, result.get(bna.getName()+"-speed").get(i));
						}
					}
				}
			}
			
			Iterator<BiologicalEdgeAbstract> it2 = this.pw.getAllEdges().iterator();
			BiologicalEdgeAbstract bea;
			String name;
			while(it2.hasNext()){
				bea = it2.next();
				name = bea.getFrom().getName()+"-"+bea.getTo().getName();
				//System.out.println(name);
				if(result.containsKey(name+"-tokenSum")){
					for(int i = 0; i<result.get(name+"-tokenSum").size(); i++){
						simRes.addValue(bea, SimulationResultController.SIM_SUM_OF_TOKEN, result.get(name+"-tokenSum").get(i));
					}
				}
				if(result.containsKey(name+"-token")){
					for(int i = 0; i<result.get(name+"-token").size(); i++){
						simRes.addValue(bea, SimulationResultController.SIM_ACTUAL_TOKEN_FLOW, result.get(name+"-token").get(i));
					}
				}
				
			}
			this.setPetriNetSimulation(true);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public int getNumberOfPlaces() {
		return this.places;
	}

	public int getPlaces() {
		return places;
	}

	public void setPlaces(int places) {
		this.places = places;
	}

	public int getTransitions() {
		return transitions;
	}

	public void setTransitions(int transitions) {
		this.transitions = transitions;
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
