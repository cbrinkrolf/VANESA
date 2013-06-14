/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package configurations.asyncWebservice;

import java.util.ArrayList;

/**
 *
 * @author mwesterm
 */
public abstract class AbstractWebServiceObservable {

    protected ArrayList<WebServiceListener> listenerList = null;

    public AbstractWebServiceObservable(){
        this.listenerList = new ArrayList<WebServiceListener>();
    }

    public void addListener(WebServiceListener newListener){
        // a unique listener will be added
        if(newListener != null && !(this.listenerList.contains(newListener))){
            this.listenerList.add(newListener);
        }
    }


    public void removeListener(WebServiceListener oldListener){
        if(oldListener != null && (this.listenerList.contains(oldListener))){
            this.listenerList.remove(oldListener);
        }
    }

    public abstract void fireEvent(WebServiceEvent event);
}
