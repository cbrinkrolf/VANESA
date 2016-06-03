package gui.eventhandlers;

public class MenuListenerSingleton {
	
private static  MenuListener instance = null;
	
	/** synchronized needed for thread-safety */
	public static synchronized  MenuListener getInstance(){
		if(instance ==null){
			instance = new  MenuListener();
		}
		return instance;
	}
	
	/** synchronized needed for thread-safety */
	public static synchronized void setInstance(MenuListener submitted){
		instance=submitted;
	
	}
}
