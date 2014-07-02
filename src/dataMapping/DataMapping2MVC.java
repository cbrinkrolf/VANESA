package dataMapping;

/**
 * This class sets up the MVC (with Observer/Observable) for the DataMapping 
 * @author dborck
 *
 */
public class DataMapping2MVC {
	
	/**
	 * Constructor, does nothing
	 */
	public DataMapping2MVC() {}
	
	/**
	 * instantiates and manages the building of the MVC
	 * @return - the GUI controller (DataMappingGUIController)
	 */
	public static DataMapping2GUIController createDataMapping() {
		DataMappingModelController dataMappingM = new DataMappingModelController(false);
		DataMapping2View dataMappingV = new DataMapping2View();
	
		dataMappingM.addObserver(dataMappingV);
		
		DataMapping2GUIController dataMappingC = new DataMapping2GUIController();
		dataMappingC.addDataMappingModelController(dataMappingM);
		dataMappingC.addDataMappingView(dataMappingV);
		
		dataMappingV.addController(dataMappingC);
		
		return dataMappingC;

	}
	
	// only for testing
	public static void main(String[] args) {
		
		DataMapping2MVC.createDataMapping();
	}
	
}
