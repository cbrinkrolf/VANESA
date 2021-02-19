package petriNet;

import java.util.List;

import biologicalElements.GraphElementAbstract;
import util.DoubleHashMap;

public class SimulationResult {

	private DoubleHashMap<GraphElementAbstract, Integer, Series> result; 
	private DoubleHashMap<GraphElementAbstract, Integer, TimeSeries> resultFiltered; 
	//private HashMap<GraphElementAbstract, HashMap<Integer, Series>> result;
	//private HashMap<GraphElementAbstract, HashMap<Integer, TimeSeries>> resultFiltered;
	private Series time;
	private boolean active = true;
	private String name;
	private String id;
	
	private StringBuilder logMessage = new StringBuilder();

	public SimulationResult(String id, String name, boolean filtered) {
		this.id = id;
		this.name = name;
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
	
	public boolean contains(GraphElementAbstract gea){
		return result.contains(gea);
	}
	
	public boolean contains(GraphElementAbstract gea, int type){
		return result.contains(gea, type);
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getId() {
		return this.id;
	}
	
	public String getName(){
		return this.name;
	}

	public void setName(String name) {
		this.name = name.trim();
	}
	
	public int size(){
		return this.time.size();
	}

	public StringBuilder getLogMessage() {
		return logMessage;
	}

}
