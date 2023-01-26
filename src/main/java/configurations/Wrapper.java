package configurations;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;

import configurations.asyncWebservice.AsynchroneWebServiceWrapper;
import configurations.asyncWebservice.WebServiceEvent;
import configurations.asyncWebservice.WebServiceListener;
import database.Connection.DBconnection;
import pojos.DBColumn;
import util.VanesaUtility;

public class Wrapper implements WebServiceListener {

	// -- contants to use in getResults --
	public static final int dbtype_BRENDA = 1;
	public static final int dbtype_KEGG = 2;
	public static final int dbtype_DAWIS = 3;
	public static final int dbtype_PPI = 4;
	public static final int dbtype_Cardio = 5;
	public static final int dbtype_MiRNA = 6;
	public static final int dbtype_BRENDA2 = 7;

	private DBconnection db;

	private AsynchroneWebServiceWrapper web_service = null;
	private ArrayList<UUID> serviceRequestIDs = null;
	private ArrayList<DBColumn> dbResults = new ArrayList<DBColumn>();

	private boolean isSingleRequestFinished = false;

	private final String QUESTION_MARK = new String("\\?");

	public Wrapper() {
		// -- webservice settings --
		web_service = AsynchroneWebServiceWrapper.getInstance();
		web_service.addListener(this);
		serviceRequestIDs = new ArrayList<UUID>();

		// -- database settings --
		db = ConnectionSettings.getInstance().getDBConnection();
	}

	/**
	 * 
	 * @param database
	 * @param query
	 * @param attributes
	 * @return
	 */
	public ArrayList<DBColumn> requestDbContent(int database, String query, String[] attributes) {
		return requestDbContent(database, query, attributes, false);
	}

	public ArrayList<DBColumn> requestDbContent(int database, String query, String[] attributes, boolean mirnaNew) {
		// System.out.println(database);
		if (!ConnectionSettings.getInstance().useInternetConnection()) {
			// -- use database --
			if (dbtype_MiRNA == database && ConnectionSettings.getInstance().isLocalMiRNA()) {
				return getLocalRequestDbContent(database, query, attributes);
			} else if (dbtype_KEGG == database && ConnectionSettings.getInstance().isLocalKegg()) {
				return getLocalRequestDbContent(database, query, attributes);
			}
		}
		return getOnlineRequestDbContent(database, query, attributes, mirnaNew);

	}

	private ArrayList<DBColumn> getOnlineRequestDbContent(int database, String query, String[] attributes,
			boolean mirnaNew) {
		// -- build a query string with attributes --
		query = buildQueryWithAttributes(query, attributes);
		// System.out.println(query);
		// -- use webservice --

		if (dbtype_PPI == database) {
			getWebserviceResult(ConnectionSettings.getInstance().getDBConnection().getPpiDBName(), query);
		}

		else if (dbtype_MiRNA == database) {
			if (mirnaNew) {
				getWebserviceResult(ConnectionSettings.getInstance().getDBConnection().getMirnaNewDBName(), query);
			} else {
				getWebserviceResult(ConnectionSettings.getInstance().getDBConnection().getMirnaDBName(), query);
			}
		} else
			getWebserviceResult(ConnectionSettings.getInstance().getDBConnection().getDawisDBName(), query);

		return dbResults;
	}

	private ArrayList<DBColumn> getLocalRequestDbContent(int database, String query, String[] attributes) {
		if (dbtype_PPI == database)
			ConnectionSettings.getInstance().getDBConnection()
					.useDatabase(ConnectionSettings.getInstance().getDBConnection().getPpiDBName());
		else if (dbtype_MiRNA == database) {
			ConnectionSettings.getInstance().getDBConnection()
					.useDatabase(ConnectionSettings.getInstance().getDBConnection().getMirnaDBName());
		} else
			ConnectionSettings.getInstance().getDBConnection()
					.useDatabase(ConnectionSettings.getInstance().getDBConnection().getDawisDBName());
		// System.out.println("local");
		return getDBResult(query, attributes);
	}

	public ArrayList<DBColumn> requestDbContent(int database, String query) {
		return this.requestDbContent(database, query, null, false);
	}

	public ArrayList<DBColumn> requestDbContent(int database, String query, boolean mirnaNew) {
		return this.requestDbContent(database, query, null, mirnaNew);
	}

	private void getWebserviceResult(String database, String query) {
		if (web_service.isWithAddressing()) {
			UUID requestID = web_service.callWebserviceWithQuery(database, query);

			serviceRequestIDs.add(requestID);

			// -- wait for result --
			waitOnServiceResult();

			serviceRequestIDs.remove(requestID);
		} else {
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

	public String buildQueryWithAttributes(String workingQuery, String[] attributes) {
		String finalQuery = workingQuery;

		if (attributes == null) {
			return finalQuery;
		}
		for (String attribute : attributes) {
			finalQuery = finalQuery.replaceFirst(QUESTION_MARK, "\"" + attribute + "\"");
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
			e.printStackTrace();
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