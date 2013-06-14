package database.dawis.webstart;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import observer.client.AxisNetObserverClient;
import observer.client.ObserverResponse;

import org.apache.axis2.AxisFault;

import pojos.DBColumn;

import configurations.Wrapper;

import database.dawis.CombinerSwingWorker;
import de.axis.gen.NetObserverCallbackHandler;
import de.axis.gen.NetObserverStub;
import de.axis.gen.NetObserverStub.Read;

import graph.CreatePathway;
import graph.jung.classes.MyGraph;
import gui.MainWindow;
import gui.MainWindowSingelton;
import gui.ProgressBar;
import biologicalElements.Pathway;

/**
 * @author Evgeny Anisiforov
 *
 */
public class DAWISWebstartConnector extends SwingWorker{
	
	private final static Long observation_timeout = 100000L; //100 seconds
	private final static Long socket_timeout = observation_timeout*2;
	
	
	private MyGraph myGraph;

	private Pathway pw = null;

	private String pathwayID = "";
	
	private ProgressBar bar;
	
	private RemoteData remoteData = new RemoteData();
	
	/** database-wrapper to connect to the database over the internet */
	private Wrapper wrapper;
	
	/** 
	 *  the sessionID used to identify the user, who has started
	 *  the network editor with Webstart
	 *  the ID is needed to get the right date from the remote_control table
	 */
	private String sessionID;
	
	private Read readRequestData;
	
	/**
	 * build simple edge between two vertices
	 * 
	 * @param one = the first Element 
	 * @param two = the second Element 
	 * @param directed = is there a directection (=arrow) 
	 */
	/*private void buildSimpleEdge(Vertex one, Vertex two, boolean directed) {
		BiologicalEdgeAbstract r = new BiologicalEdgeAbstract(myGraph
				.createEdge(one, two, false), "", "");
		r.setDirected(directed);
		r.setReference(true);
		r.setHidden(false);
		r.setVisible(true);

		pw.addElement(r);
	}*/
	
	private DAWISWebstartCombiner combiner;
	private Timestamp lasttimestamp;
	public static NetObserverStub stub;
	public DAWISWebstartConnector(/*ProgressBar b, */ String sessionID){

		this.sessionID = sessionID;
		
		//getDetails(input, null);
	}
	
	/** 
	 * prevents from repainting the graph in the time 
	 * as the graph is beeing generated
	 * to get better performance
	 */
	private void stopVisualizationModel() {
		myGraph.lockVertices();
		myGraph.stopVisualizationModel();
	}
	
	/**
	 * start painting after generating the graph is ready
	 */
	private void startVisualizationModel() {
		myGraph.restartVisualizationModel();
		myGraph.unlockVertices();
		
	}
	
	
	/** loads corresponding data from rc table */
	private ArrayList<DBColumn> getRemoteControlData()
	{
		ArrayList<DBColumn> result;
		String[] query_organism={this.lasttimestamp.toString(), this.sessionID};
		
		result=wrapper.requestDbContent(Wrapper.dbtype_DAWIS, DAWISWebstartQueries.getRCdata, query_organism);
		
		return result;
	}
	

	/**
	 * load data from dawis remote control once
	 * 
	 * @throws SQLException
	 */
	public void loadData() throws SQLException
	{
		MainWindow mainwindow=MainWindowSingelton.getInstance();
		mainwindow.setEnable(false);

		bar.setProgressBarString("Querying Database");

		ArrayList<DBColumn> remotecontroldata=this.getRemoteControlData();
		
		if (!remotecontroldata.isEmpty())
		{
			for (DBColumn o : remotecontroldata)
			{
				Timestamp t=Timestamp.valueOf(o.getColumn()[3]);
				
				if (t.after(this.lasttimestamp))
				{
					this.lasttimestamp=t;
					// System.out.println("set lastts to"+t);
				}
			}

			// combiner = new DAWISWebstartCombiner(pw, remotecontroldata,
			// remoteData);
			// combiner.execute();

			CombinerSwingWorker csw=new CombinerSwingWorker(pw, remotecontroldata, remoteData);
			csw.execute();
			// mainwindow.setEnable(false);
			// bar.init(100, "Loading data from DAWIS remote control..", true);

			// combiner.addElements(remotecontroldata);
			// bar.closeWindow();
			// mainwindow.setEnable(true);
		}
		endSearch(mainwindow, bar);

	}
	
	private void endSearch(final MainWindow w, final ProgressBar bar) 
	{
		Runnable run=new Runnable()
		{
			public void run()
			{
				bar.closeWindow();
//				w.setEnable(true);
			}
		};
		
		SwingUtilities.invokeLater(run);
	}
	
	NetObserverCallbackHandler observation_callback = new NetObserverCallbackHandler() {
		/**
        * auto generated Axis2 call back method for read method
        * override this method for handling normal response from read operation
        */
		@Override
		public void receiveResultread(de.axis.gen.NetObserverStub.ReadResponse result)
		{
			ObserverResponse observed=result.get_return();
//			System.out.println("DEBUG: "+observed.getLastUpdate()+" :::: "+observed.getLastUpdate().equals(readRequestData.getSince()));
//			System.out.println("observed and  got "+AxisNetObserverClient.printCalendar(observed.getLastUpdate())+"  "+observed.getValue());
			if ((observed.getLastUpdate()!=null)&&(!observed.getLastUpdate().equals(readRequestData.getSince())))
			{
				try
				{
					loadData();
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			readRequestData.setSince(observed.getLastUpdate());
			readRequestData.setTimeout(observation_timeout);
			observeData();

		}

      /**
       * auto generated Axis2 Error handler
       * override this method for handling error response from read operation
       */
        @Override
		public void receiveErrorread(java.lang.Exception e) {
        	e.printStackTrace();
        	observeData();
        }
};
	
	/**
	 * observe data from dawis remote control 
	 * 
	 * @throws SQLException
	 * @throws RemoteException 
	 */
	public void observeData() {
		try {
			stub.startread(this.readRequestData, observation_callback);
			
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//System.out.println("Starting new observation cycle...");
	}
	
	@Override
	protected Object doInBackground() {
		Runnable run = new Runnable() {
			public void run() {
					bar = new ProgressBar();
					bar.init(100, "   Loading Data from DAWIS ", true);
					bar.setProgressBarString("Querying Database");
			}
		};
		SwingUtilities.invokeLater(run);
		return null;
	}

	@Override
	public void done() {
		
		try {
			stub =
			    new NetObserverStub(null, "http://agbi.techfak.uni-bielefeld.de/axis2/services/NetObserver");
			stub._getServiceClient().getOptions().setTimeOutInMilliSeconds(DAWISWebstartConnector.socket_timeout);
			this.readRequestData = new Read();
			this.readRequestData.setProject("jeffsproject");
			this.readRequestData.setKey(this.sessionID);
			this.readRequestData.setSince(null);
	
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.lasttimestamp = new Timestamp(0);
		this.wrapper = new Wrapper();
		pw = new CreatePathway("DAWIS Network").getPathway();
		pw.setDAWISProject();
		observeData();
	}
}
