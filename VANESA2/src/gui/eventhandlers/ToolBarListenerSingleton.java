package gui.eventhandlers;

public class ToolBarListenerSingleton {

private static ToolBarListener instance = null;
	
	/** synchronized needed for thread-safety */
	public static synchronized ToolBarListener getInstance(){
		if(instance ==null){
			instance = new ToolBarListener();
		}
		return instance;
	}
	
	/** synchronized needed for thread-safety */
	public static synchronized void setInstance(ToolBarListener submitted){
		instance=submitted;
	
	}
}
