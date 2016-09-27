package gui;

public class MyPopUpSingleton {
	
private static MyPopUp instance = null;
	
	/** synchronized needed for thread-safety */
	public static synchronized MyPopUp getInstance(){
		if(instance ==null){
			instance = new MyPopUp();
		}
		return instance;
	}
	
	/** synchronized needed for thread-safety */
	public static synchronized void setInstance(MyPopUp submitted){
		instance=submitted;
	
	}

}
