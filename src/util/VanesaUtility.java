package util;

import java.util.Vector;

import configurations.asyncWebservice.CallbackHandler;
import configurations.asyncWebservice.WebServiceEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.async.AxisCallback;
import org.apache.axis2.databinding.utils.BeanUtil;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.engine.ObjectSupplier;
import pojos.DBColumn;

/**
 *
 * @author mwesterm
 */

public class VanesaUtility {

	private final static String QUESTION_MARK = "\\?";
	
    /**
     * This utility method creates a vector with results, after an asynchrone web service has
     * been called. Because we need a Vector<DBColumn> to 
     * 
     * @param event  WebServiceEvent, the event which has been given after a
     * Web-Service call.
     * @return result  ArrayList<DBColumn>, the vector has no elements if no content in the
     * WebServiceEvent is given
     */
    public static ArrayList<DBColumn> createResultList(WebServiceEvent event){
    	
        // SOAP envelope -> create a readable OMElement
        OMElement element = event.getServiceResult();
        
        Iterator it = element.getChildrenWithLocalName("column");
            Object[] javaTypes = {DBColumn.class};
            ArrayList<DBColumn> results = new ArrayList<DBColumn>();
            ObjectSupplier objectSupplier = new AxisService().getObjectSupplier();

            // get all DBColumn deserialized
        while(it.hasNext()){
            OMElement oneDBColumn = (OMElement) (it.next());

            DBColumn workingColumn;
            try {
                workingColumn = (DBColumn) BeanUtil.deserialize(DBColumn.class,oneDBColumn,objectSupplier,"column");
                results.add(workingColumn);
            } catch (AxisFault ex) {
                ex.printStackTrace();
           }
        }
    return results;
}
    
    public static String buildQueryWithAttributes(String workingQuery, String[] attributes){
    	String finalQuery = workingQuery;
    	
    	int lenght = attributes.length;
    	for(int run = 0; run < lenght; run ++){
    		
    		try{
    		finalQuery = finalQuery.replaceFirst(QUESTION_MARK, "\"" + attributes[run] +"\"");
    		}
    		catch(Exception ex){
    			ex.printStackTrace();
    		}
    	}
    	return finalQuery + ";";
    }
    
}



