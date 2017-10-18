/***************************************************************
 * Copyright (c) Benjamin Kormeier 2006-2009.                  *
 * All rights reserved.                                        *
 ***************************************************************/
package test;

import java.util.ArrayList;
import java.util.UUID;

import configurations.asyncWebservice.AsynchroneWebServiceWrapper;
import configurations.asyncWebservice.WebServiceEvent;
import configurations.asyncWebservice.WebServiceListener;
import pojos.DBColumn;
import util.VanesaUtility;

/**
 * @author Benjamin Kormeier
 * @version 1.0 19.10.2010
 */
public class WebserviceTest implements WebServiceListener
{
	private AsynchroneWebServiceWrapper wrapper = null;
    
    private ArrayList<UUID> serviceRequestIDs = null;
    private ArrayList<DBColumn> dbResults = null;
    
    private boolean isSingleRequestFinished = false;
	    
	public WebserviceTest()
	{
		wrapper=AsynchroneWebServiceWrapper.getInstance();
		serviceRequestIDs=new ArrayList<UUID>();
		wrapper.addListener(this);
		
		getWebserviceReslult(null, null, null);
		
//		UUID requestID = this.wrapper.callWebserviceWithQuery("dawis_md","select * from brenda_enzyme limit 10");
//		
//		this.waitUntilWaitThreadFinished(new WaitOnServiceResult(), requestID);
//		
//		for (DBColumn column : dbResults)
//		{
//			System.out.println(column.getColumn()[0]);
//		}
	}
	
	private void getWebserviceReslult(String url, String database, String query) 
	{
		UUID requestID = this.wrapper.callWebserviceWithQuery("dawis_md","select * from brenda_enzyme limit 10");
		
		this.serviceRequestIDs.add(requestID);
		
		waitOnServiceResult();
		
		this.serviceRequestIDs.remove(requestID);
		
		for (DBColumn column : dbResults)
		{
			// System.out.println(column.getColumn()[0]);
		}
		
	}
	
	private void waitOnServiceResult()
	{
		while (!isSingleRequestFinished)
		{
			try
			{ Thread.sleep(100);}
			catch (InterruptedException e)
			{}
		}
	}
	
	public ArrayList<DBColumn> waitUntilWaitThreadFinished(	WaitOnServiceResult waitThread, UUID requestID)
	{
		this.serviceRequestIDs.add(requestID);
		
		waitThread.run();
		
		if (waitThread.isReady)
		{
			this.isSingleRequestFinished=false;
			waitThread.interrupt();
		}
		
		// result gained, remove the last request id
		this.serviceRequestIDs.remove(requestID);

		return this.dbResults;

	}
	
	/**
	 * Waits on a service request tot be finished up, in the case of sequential
	 * requests.
	 * 
	 * @author mwesterm
	 * 
	 */
	private class WaitOnServiceResult extends Thread
	{
		public boolean isReady=false;
		
		public void run()
		{
			while (!isSingleRequestFinished)
			{
				try
				{
					sleep(100);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			isReady=true;
			
		}
	}
	
	@Override
	public void webServiveEventReceived(WebServiceEvent event)
	{
		ArrayList<DBColumn> columnResult=null;
				
		if (this.serviceRequestIDs.contains(event.getWebServiceIdent()))
		{
			columnResult=VanesaUtility.createResultList(event);
						
			this.dbResults=columnResult;
			this.isSingleRequestFinished=true;
		}
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		new WebserviceTest();
	}

}
