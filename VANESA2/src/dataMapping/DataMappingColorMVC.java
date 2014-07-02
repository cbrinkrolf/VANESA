package dataMapping;

/**
 * This class sets up the MVC (with Observer/Observable) for the DataMapping 
 * @author dborck
 *
 */
public class DataMappingColorMVC {
	
	/**
	 * Constructor, does nothing
	 */
	public DataMappingColorMVC() {}
	
	/**
	 * instantiates and manages the building of the MVC
	 * @return - the GUI controller (DataMappingGUIController)
	 */
	public static DataMappingGUIController createDataMapping() {
		DataMappingModelController dataMappingM = new DataMappingModelController(true);
		DataMappingView dataMappingV = new DataMappingView();
	
		dataMappingM.addObserver(dataMappingV);
		
		DataMappingGUIController dataMappingC = new DataMappingGUIController();
		dataMappingC.addDataMappingModelController(dataMappingM);
		dataMappingC.addDataMappingView(dataMappingV);
		
		dataMappingV.addController(dataMappingC);
		
		return dataMappingC;

	}
	
	// only for testing
	public static void main(String[] args) {
		
		DataMappingColorMVC.createDataMapping();
	}
	
}
