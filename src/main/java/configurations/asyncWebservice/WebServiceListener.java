
package configurations.asyncWebservice;

/**
 * This interface serves as a listener on web service requests. 
 * An instance of a <code>WebServiceListener</code> will be recognized 
 * after a request to an 
 * 
 * @author mwesterm
 */
public interface WebServiceListener {

    public abstract void webServiveEventReceived(WebServiceEvent event);
    
}
