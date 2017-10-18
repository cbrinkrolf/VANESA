/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package configurations.asyncWebservice;

import java.util.UUID;

import org.apache.axiom.om.OMElement;

/**
 *
 * @author mwesterm
 */
public class WebServiceEvent {

    private OMElement serviceResult = null;
    
    private UUID webServiceIdent = null;

    public WebServiceEvent(OMElement serviceResult, UUID webServiceIdent){
        this.serviceResult = serviceResult;
        this.webServiceIdent = webServiceIdent;
    }


	/**
     * @return the serviceResult
     */
    public OMElement getServiceResult() {
        return serviceResult;
    }

    /**
     * @param serviceResult the serviceResult to set
     */
    public void setServiceResult(OMElement serviceResult) {
        this.serviceResult = serviceResult;
    }

    /**
     * @return the webServiceIdent
     */
    public UUID getWebServiceIdent() {
        return webServiceIdent;
    }

    /**
     * @param webServiceIdent the webServiceIdent to set
     */
    public void setWebServiceIdent(UUID webServiceIdent) {
        this.webServiceIdent = webServiceIdent;
    }
}
