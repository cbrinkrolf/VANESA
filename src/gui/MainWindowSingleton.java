package gui;

public class MainWindowSingleton {

	private static MainWindow instance = null;
	
	/** synchronized needed for thread-safety */
	public static synchronized MainWindow getInstance(){
		if(instance ==null){
			instance = new MainWindow();
		}
		return instance;
	}
	
	/** synchronized needed for thread-safety */
	public static synchronized void setInstance(MainWindow submitted){
		instance=submitted;
	
	}
}

