/***************************************************************
 * Copyright (c) Benjamin Kormeier 2006-2010.                  * 
 * All rights reserved.                                        *
 ***************************************************************/
package database.dawis.webstart;

import graph.CreatePathway;
import gui.MainWindow;
import gui.MainWindowSingelton;
import gui.ProgressBar;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import pojos.DBColumn;
import biologicalElements.Pathway;
import configurations.Wrapper;
import database.dawis.CombinerSwingWorker;

/**
 * @author Benjamin Kormeier
 * @version 1.0 23.11.2010
 */
public class DAWISBridge extends SwingWorker<Object,Object>
{
	private Pathway pw=null;
	private ProgressBar bar;

	private RemoteData remoteData=new RemoteData();

	/** database-wrapper to connect to the database over the internet */
	private Wrapper wrapper;

	/**
	 * the sessionID used to identify the user, who has started the network
	 * editor with Webstart the ID is needed to get the right date from the
	 * remote_control table
	 */
	private String sessionID;
	private Timestamp lasttimestamp;
	
	public DAWISBridge(String sessionID)
	{
		this.sessionID=sessionID;
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
				}
			}

			CombinerSwingWorker csw=new CombinerSwingWorker(pw, remotecontroldata, remoteData);
			csw.execute();
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
			}
		};

		SwingUtilities.invokeLater(run);
	}
	
	@Override
	protected Object doInBackground()
	{
		Runnable run=new Runnable()
		{
			public void run()
			{
				bar=new ProgressBar();
				bar.init(100, "   Loading Data from DAWIS ", true);
				bar.setProgressBarString("Querying Database");
			}
		};
		SwingUtilities.invokeLater(run);
		return null;
	}

	@Override
	public void done()
	{
		this.lasttimestamp=new Timestamp(0);
		this.wrapper=new Wrapper();
		
		pw=new CreatePathway("DAWIS Network").getPathway();
		pw.setDAWISProject();
		
		try
		{
			// TODO Timestamp loading?
			loadData();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
