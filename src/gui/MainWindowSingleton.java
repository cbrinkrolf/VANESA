package gui;


public class MainWindowSingleton extends MainWindow {

	private static final long serialVersionUID = 1L;
	private static MainWindow instance = null;
	
	/** synchronized needed for thread-safety */
	public static synchronized MainWindow getInstance(){
		if(instance ==null){
			instance = new MainWindowSingleton();
		}
		return instance;
	}
	
	/** synchronized needed for thread-safety */
	public static synchronized void setInstance(MainWindow submitted){
		instance=submitted;
	
	}

	protected MainWindowSingleton(){
		
	}	
}

