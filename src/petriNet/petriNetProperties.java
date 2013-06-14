package petriNet;

public class petriNetProperties {

	private int delay;
	private boolean running;
	
	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public petriNetProperties(){
		this.delay = 100;
		this.running = false;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	
}
