package configurations.asyncWebservice;

import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.client.async.AxisCallback;
import org.apache.axis2.databinding.utils.BeanUtil;

import configurations.ConnectionSettings;


/**
 * This class wraps the asynchrone web service invocation to all
 * databases, like BRENDA, KEGG ...
 * This is a singleton and a observable class to get informed in the case
 * of available data.
 * 
 * @author mwesterm, Benjamin Kormeier
 */
public class AsynchroneWebServiceWrapper extends AbstractWebServiceObservable {

    protected CopyOnWriteArrayList<CallbackHandler> callbackHandlerList = null;

    private static AsynchroneWebServiceWrapper singletonServiceWrapper = null;

    private ServiceClient serviceClient = null;
   
    private boolean withAddressing = false;
    
	/**
	 * private Constructor
	 */
	private AsynchroneWebServiceWrapper()
	{

		this.callbackHandlerList=new CopyOnWriteArrayList<CallbackHandler>();
		try
		{
			this.serviceClient=new ServiceClient();
		}
		catch (AxisFault ex)
		{
			Logger.getLogger(AsynchroneWebServiceWrapper.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		// -- engage WS-Adressing --
		engageAddressing();
	}

	/**
	 * Returns the singleton instance of this class.
	 * 
	 * @return AsynchroneWebServiceWrapper, the Web-Service Wrapper instance.
	 */
	public synchronized static AsynchroneWebServiceWrapper getInstance()
	{
		if (singletonServiceWrapper==null)
		{
			singletonServiceWrapper=new AsynchroneWebServiceWrapper();
		}
		
		return singletonServiceWrapper;
	}

	/**
	 * 
	 * @param url
	 * @param database
	 * @param query
	 * @return
	 */
	public UUID callWebserviceWithQuery(String database, String query)
	{
		UUID webserviceIdent=UUID.randomUUID();
		
		try
		{
			// -- default : String webserviceName="http://tunicata.techfak.uni-bielefeld.de/axis2/services/DBService"; --
			String webserviceName=ConnectionSettings.getWebServiceUrl();
			EndpointReference targetEPR=new EndpointReference(webserviceName);

			Object[] operationArguments=new Object[]{query, database};
			QName queryDB=new QName("http://axis.de", "queryDB");
			OMElement omRequest=BeanUtil.getOMElement(queryDB, operationArguments, null, false, null);

			// callback
			AxisCallback callback=new CallbackHandler(webserviceIdent);

			// set up the options
			Options option=this.createOptions(targetEPR, omRequest, callback);
			// option.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, "true");

			option.setAction("urn:queryDB");
			this.serviceClient.setOptions(option);

			// send request to web service
			if (this.withAddressing) {
				serviceClient.sendReceiveNonBlocking(omRequest, callback);
			} else {
				OMElement resp = serviceClient.sendReceive(omRequest);
				WebServiceEvent event = new WebServiceEvent(resp, webserviceIdent);
				this.fireEvent(event);
			}
		}
		catch (AxisFault axisFault)
		{
			axisFault.printStackTrace();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return webserviceIdent;
	}
	
	public UUID callWebserviceWithPreparedQuery(String url, String database, String query)
	{
		UUID webserviceIdent=UUID.randomUUID();
		try
		{
			// -- webservice name --
			String webserviceName=ConnectionSettings.getWebServiceUrl();
			EndpointReference targetEPR=new EndpointReference(webserviceName);
			
			Object[] operationArguments=new Object[]{query, database};
			QName queryDB=new QName("http://axis.de", "preparedQueryDB");
			OMElement omRequest=BeanUtil.getOMElement(queryDB,operationArguments, null, false, null);
			
			// -- callback --
			AxisCallback callback=new CallbackHandler(webserviceIdent);

			// -- set up the options --
			Options option=this.createOptions(targetEPR, omRequest, callback);
			option.setAction("urn:preparedQueryDB");
			this.serviceClient.setOptions(option);

			// -- send request to web service --
			if (this.withAddressing) {
				serviceClient.sendReceiveNonBlocking(omRequest, callback);
			} else {
				OMElement resp = serviceClient.sendReceive(omRequest);
				WebServiceEvent event = new WebServiceEvent(resp, webserviceIdent);
				this.fireEvent(event);
			}
		}
		catch (AxisFault axisFault)
		{
			axisFault.printStackTrace();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return webserviceIdent;
	}

	private Options createOptions(EndpointReference endpoint, OMElement request, AxisCallback callback) throws AxisFault
	{
		// -- set up the options --
		Options options=new Options();
		
		if (this.withAddressing) {
			options.setUseSeparateListener(true);
			options.setMessageId(UUID.randomUUID().toString());
		}
		
		options.setTo(endpoint);
		options.setTimeOutInMilliSeconds(10000);
		options.setManageSession(true);
		options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
		
		return options;
	}

	private void engageAddressing() 
	{
		if(withAddressing)
		{
			try
			{
				this.serviceClient.disengageModule("addressing");
				this.serviceClient.engageModule("addressing");
			}
			catch (AxisFault ex)
			{
				Logger.getLogger(AsynchroneWebServiceWrapper.class.getName()).log(Level.SEVERE, null, ex);
				
				this.serviceClient.disengageModule("addressing");
				this.withAddressing=false;
				
				JOptionPane.showMessageDialog(null, "Engaging addressing module failed!\nContinue without addressing.","Addressing", JOptionPane.ERROR_MESSAGE); 
			}
		}
		else
			this.serviceClient.disengageModule("addressing");
	}
	
	@Override
	public void fireEvent(WebServiceEvent event)
	{
		if (listenerList!=null)
		{
			for (WebServiceListener oneListener : listenerList)
			{
				oneListener.webServiveEventReceived(event);
			}
		}
	}
	
	public boolean isWithAddressing()
	{
		return this.withAddressing;
	}

	public void setAddressing(boolean withAddressing)
	{
		this.withAddressing=withAddressing;
		
		// -- engage WS-Adressing -
		engageAddressing();
	}
}
