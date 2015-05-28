package petriNet;

import java.util.List;

import util.DoubleHashMap;
import biologicalElements.GraphElementAbstract;

public class SimulationResult {

	private DoubleHashMap<GraphElementAbstract, Integer, Series> result; 
	private DoubleHashMap<GraphElementAbstract, Integer, TimeSeries> resultFiltered; 
	//private HashMap<GraphElementAbstract, HashMap<Integer, Series>> result;
	//private HashMap<GraphElementAbstract, HashMap<Integer, TimeSeries>> resultFiltered;
	private Series time;

	public SimulationResult(boolean filtered) {
		result = new DoubleHashMap<GraphElementAbstract, Integer, Series>();
		time = new Series();
		if (filtered) {
			resultFiltered = new DoubleHashMap<GraphElementAbstract, Integer, TimeSeries>();
		}
	}
	
	public void addTime(double time){
		this.time.add(time);
	}
	
	public void addValue(GraphElementAbstract gea, int type, double value){
		
		if(!result.contains(gea, type)){
			result.put(gea, type, new Series());
		}
		result.get(gea, type).add(value);
	}

	public Series get(GraphElementAbstract gea, int type) {
		return this.result.get(gea, type);
	}
	
	public List<Double> getValues(GraphElementAbstract gea, int type) {
		return this.result.get(gea, type).getAll();
	}

	public Double getValue(GraphElementAbstract gea, int type, int pos) {
		return this.result.get(gea, type).get(pos);
	}

	public List<Double> getFiltered(GraphElementAbstract gea, int type) {
		return resultFiltered.get(gea, type).getValues().getAll();
	}

	public Double getFiltered(GraphElementAbstract gea, int type, int pos) {
		return resultFiltered.get(gea, type).getValues().get(pos);
	}
	
	public Series getTime(){
		return this.time;
	}
	
	public List<Double> getTimeValues(){
		return this.time.getAll();
	}
	
	public List<Double> getTime(GraphElementAbstract gea, int type, boolean filtered){
		if(filtered){
			return resultFiltered.get(gea, type).getTime().getAll();
		}else{
			return this.time.getAll();
		}
	}
	
	public void refreshFilter(){
		resultFiltered = new DoubleHashMap<GraphElementAbstract, Integer, TimeSeries>();
	}

}
