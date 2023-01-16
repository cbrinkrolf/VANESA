package petriNet;

import java.util.ArrayList;
import java.util.List;

public class Series {

	private List<Double> values;
	public Series(){
		values = new ArrayList<Double>();
	}
	
	public List<Double> getAll(){
		return this.values;
	}
	
	public Double get(int pos){
		return values.get(pos);
	}
	
	public void add(Double d){
		values.add(d);
	}
	
	public int size(){
		return values.size();
	}
}
