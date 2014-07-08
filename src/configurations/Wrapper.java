package configurations;

import gui.MainWindow;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;

import pojos.DBColumn;
import util.VanesaUtility;
import configurations.asyncWebservice.AsynchroneWebServiceWrapper;
import configurations.asyncWebservice.WebServiceEvent;
import configurations.asyncWebservice.WebServiceListener;
import database.Connection.DBconnection;
import database.ppi.PPIqueries;

public class Wrapper implements WebServiceListener {

	// -- contants to use in getResults --
	public static final int dbtype_BRENDA = 1;
	public static final int dbtype_KEGG = 2;
	public static final int dbtype_DAWIS = 3;
	public static final int dbtype_PPI = 4;
	public static final int dbtype_Cardio = 5;
	public static final int dbtype_MiRNA = 6;

	
	private DBconnection db;

	private AsynchroneWebServiceWrapper web_service = null;
	private ArrayList<UUID> serviceRequestIDs = null;
	private ArrayList<DBColumn> dbResults = null;

	private boolean isSingleRequestFinished = false;

	private final String QUESTION_MARK = new String("\\?");

	public Wrapper() {
		// -- webservice settings --
		web_service = AsynchroneWebServiceWrapper.getInstance();
		web_service.addListener(this);
		serviceRequestIDs = new ArrayList<UUID>();

		// -- database settings --
		db = ConnectionSettings.getDBConnection();
	}

	/**
	 * 
	 * @param database
	 * @param query
	 * @param attributes
	 * @return
	 */
	public ArrayList<DBColumn> requestDbContent(int database, String query,
			String[] attributes) {
		//System.out.println(database);
		if (!ConnectionSettings.useInternetConnection()) {
			// -- use database --
			if (dbtype_PPI == database)
				ConnectionSettings.getDBConnection().useDatabase(
						ConnectionSettings.getDBConnection().getPpiDBName());
			else if (dbtype_MiRNA == database) {
				ConnectionSettings.getDBConnection().useDatabase(
						ConnectionSettings.getDBConnection().getmirnaDBName());
		
			}
			else
				ConnectionSettings.getDBConnection().useDatabase(
						ConnectionSettings.getDBConnection().getDawisDBName());

			return getDBResult(query, attributes);
		} else {
			// -- build a query string with attributes --
			//MARTIN save old query for check
			String oldquery = query;
			query = buildQueryWithAttributes(query, attributes);

			// -- use webservice --
			
			//MARTIN HPRD in neuer Dawis, Mint und IntAct nicht
			if (dbtype_PPI == database){
				if(MainWindow.useOldDB)
					getWebserviceResult(ConnectionSettings.getDBConnection().getPpiDBName(), query);
				else{
					
					//MARTIN Mint+Intact abfangen und �ber PPI_DB abfragen
					
					if (oldquery.equals(PPIqueries.intact_resultForACnumber) || 
							oldquery.equals(PPIqueries.intact_resultForAlias) ||
							oldquery.equals(PPIqueries.intact_resultForName) ||
							oldquery.equals(PPIqueries.intact_interactionsForID) ||
							oldquery.equals(PPIqueries.mint_resultForACnumber) || 
							oldquery.equals(PPIqueries.mint_resultForAlias) ||
							oldquery.equals(PPIqueries.mint_resultForName) ||
							oldquery.equals(PPIqueries.mint_interactionsForID)){
						
						getWebserviceResult(ConnectionSettings.getDBConnection().getPpiDBName(), query);	
						
						
					}else					
						getWebserviceResult(ConnectionSettings.getDBConnection().getDawisDBName(), query);
				}
			}

			else if (dbtype_MiRNA == database) {
				getWebserviceResult(ConnectionSettings.getDBConnection()
						.getmirnaDBName(), query);
			}
			else
				getWebserviceResult(ConnectionSettings.getDBConnection()
						.getDawisDBName(), query);

			return dbResults;
		}

	}

	public ArrayList<DBColumn> requestDbContent(int database, String query) {
		if (!ConnectionSettings.useInternetConnection()) {
			// -- use database --
			
			if (dbtype_PPI == database)
				ConnectionSettings.getDBConnection().useDatabase(
						ConnectionSettings.getDBConnection().getPpiDBName());
			else if (dbtype_MiRNA == database) {
				
				ConnectionSettings.getDBConnection().useDatabase(
						ConnectionSettings.getDBConnection().getmirnaDBName());
			}
			else
				ConnectionSettings.getDBConnection().useDatabase(
						ConnectionSettings.getDBConnection().getDawisDBName());

			return getDBResult(query, null);
		} else {
			// -- use webservice --
			if (dbtype_PPI == database){
				getWebserviceResult(ConnectionSettings.getDBConnection()
						.getPpiDBName(), query);
			}
			
			else if(dbtype_MiRNA == database){
				System.out.println(ConnectionSettings.getDBConnection()
						.getmirnaDBName());
				getWebserviceResult(ConnectionSettings.getDBConnection()
						.getmirnaDBName(), query);
				
			}
				else{ getWebserviceResult(ConnectionSettings.getDBConnection()
						.getDawisDBName(), query);
			}
			return dbResults;
		}
	}

	private void getWebserviceResult(String database, String query) {
		if(web_service.isWithAddressing()) {
			UUID requestID = web_service.callWebserviceWithQuery(database, query);

			serviceRequestIDs.add(requestID);

			// -- wait for result --
			waitOnServiceResult();

			serviceRequestIDs.remove(requestID);
		}
		else {
			web_service.callWebserviceWithQuery(database, query);
		}
		
	}

	private void waitOnServiceResult() {
		isSingleRequestFinished = false;

		while (!isSingleRequestFinished) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

	public String buildQueryWithAttributes(String workingQuery,
			String[] attributes) {
		String finalQuery = workingQuery;

		for (String attribute : attributes) {
			finalQuery = finalQuery.replaceFirst(QUESTION_MARK, "\""
					+ attribute + "\"");
		}

		return finalQuery + ";";
	}

	public ArrayList<DBColumn> getDBResult(String query, String[] attributes) {
		ResultSet rs = null;
		ArrayList<DBColumn> data = new ArrayList<DBColumn>();

		try {
			if (attributes != null) {
				rs = db.selectQuery(query, attributes);
			} else
				rs = db.selectQuery(query);

			int col = rs.getMetaData().getColumnCount();

			while (rs.next()) {
				String column[] = new String[col];

				for (int i = 0; i < column.length; i++) {
					column[i] = rs.getString(i + 1);
				}

				data.add(new DBColumn(column));
			}

			rs.close();
		} catch (Exception e) {
		}

		return data;
	}

	@Override
	public void webServiveEventReceived(WebServiceEvent event) {
		ArrayList<DBColumn> columnResult = null;
		if (web_service.isWithAddressing()) {
			if (this.serviceRequestIDs.contains(event.getWebServiceIdent())) {
				columnResult = VanesaUtility.createResultList(event);

				dbResults = columnResult;
				isSingleRequestFinished = true;
			}
		} else {
			columnResult = VanesaUtility.createResultList(event);
			dbResults = columnResult;
		}
		
	}
}
