package petriNet;

import java.util.HashMap;
import java.util.List;

import biologicalElements.GraphElementAbstract;

public class SimulationResult {

	private HashMap<GraphElementAbstract, HashMap<Integer, Series>> result;
	private HashMap<GraphElementAbstract, HashMap<Integer, TimeSeries>> resultFiltered;
	private Series time;

	public SimulationResult(boolean filtered) {
		result = new HashMap<GraphElementAbstract, HashMap<Integer, Series>>();
		time = new Series();
		if (filtered) {
			resultFiltered = new HashMap<GraphElementAbstract, HashMap<Integer, TimeSeries>>();
		}
	}

	public List<Double> get(GraphElementAbstract gea, int type) {
		return this.result.get(gea).get(type).getAll();
	}

	public Double get(GraphElementAbstract gea, int type, int pos) {
		return this.result.get(gea).get(type).get(pos);
	}

	public List<Double> getFiltered(GraphElementAbstract gea, int type) {
		return resultFiltered.get(gea).get(type).getValues().getAll();
	}

	public Double getFiltered(GraphElementAbstract gea, int type, int pos) {
		return resultFiltered.get(gea).get(type).getValues().get(pos);
	}
	
	public List<Double> getTime(){
		return this.time.getAll();
	}
	
	public List<Double> getTime(GraphElementAbstract gea, int type, boolean filtered){
		if(filtered){
			return resultFiltered.get(gea).get(type).getTime().getAll();
		}else{
			return this.time.getAll();
		}
	}
	
	public void refreshFilter(){
		resultFiltered = new HashMap<GraphElementAbstract, HashMap<Integer, TimeSeries>>();
	}

}
