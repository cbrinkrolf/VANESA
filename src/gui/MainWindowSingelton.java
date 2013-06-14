package gui;


public class MainWindowSingelton extends MainWindow {

	private static MainWindow instance = null;
	
	/** synchronized needed for thread-safety */
	public static synchronized MainWindow getInstance(){
		if(instance ==null){
			instance = new MainWindowSingelton();
		}
		return instance;
	}
	
	/** synchronized needed for thread-safety */
	public static synchronized void setInstance(MainWindow submitted){
		instance=submitted;
	
	}

	protected MainWindowSingelton(){
		
	}	
}

