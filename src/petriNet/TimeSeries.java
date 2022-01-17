package petriNet;


public class TimeSeries {

	private Series time;
	private Series values;
	
	public TimeSeries(){
		time = new Series();
		values = new Series();
		
	}
	
	public Series getTime(){
		return time;
	}
	
	public Series getValues(){
		return values;
	}
	
	public void add(double t, double v){
		this.time.add(t);
		values.add(v);
	}
	
	public int size(){
		return time.size();
	}
}
