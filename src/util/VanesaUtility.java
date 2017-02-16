package util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.databinding.utils.BeanUtil;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.engine.ObjectSupplier;

import pojos.DBColumn;
import configurations.asyncWebservice.WebServiceEvent;

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
        
        Iterator<OMElement> it = element.getChildrenWithLocalName("column");
            //Object[] javaTypes = {DBColumn.class};
            ArrayList<DBColumn> results = new ArrayList<DBColumn>();
            ObjectSupplier objectSupplier = new AxisService().getObjectSupplier();

            // get all DBColumn deserialized
            OMElement oneDBColumn;
        while(it.hasNext()){
           oneDBColumn = (it.next());

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
    
    public static Double getMean(List<Double> list){
    	double mean = 0;
    	for(int i = 0; i<list.size(); i++){
    		mean += list.get(i);
    	}
    	
    	return mean/list.size();
    }
    
    public static Double getMedian(List<Double> list){
    	double median = 0;
    		if(list.size()>0){
    			if(list.size() % 2 == 0){
    				return (list.get(list.size()/2) + list.get(list.size()/2+1))/2;
    			}else{
    				return list.get((list.size()-1)/2);
    			}
    		}
    	return median;
    }
}



