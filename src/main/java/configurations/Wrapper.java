package configurations;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;

import configurations.asyncWebservice.AsynchroneWebServiceWrapper;
import configurations.asyncWebservice.WebServiceEvent;
import configurations.asyncWebservice.WebServiceListener;
import database.Connection.DBConnection;
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

    private final DBConnection db;

    private final AsynchroneWebServiceWrapper web_service;
    private final ArrayList<UUID> serviceRequestIDs;
    private ArrayList<DBColumn> dbResults = new ArrayList<>();

    private boolean isSingleRequestFinished = false;

    private final String QUESTION_MARK = "\\?";

    public Wrapper() {
        web_service = AsynchroneWebServiceWrapper.getInstance();
        web_service.addListener(this);
        serviceRequestIDs = new ArrayList<>();
        db = ConnectionSettings.getInstance().getDBConnection();
    }

    public ArrayList<DBColumn> requestDbContent(int database, String query, String[] attributes) {
        if (!ConnectionSettings.getInstance().useInternetConnection()) {
            if (dbtype_MiRNA == database && ConnectionSettings.getInstance().isLocalMiRNA()) {
                return getLocalRequestDbContent(database, query, attributes);
            } else if (dbtype_KEGG == database && ConnectionSettings.getInstance().isLocalKegg()) {
                return getLocalRequestDbContent(database, query, attributes);
            }
        }
        return getOnlineRequestDbContent(database, query, attributes);
    }

    private ArrayList<DBColumn> getOnlineRequestDbContent(int database, String query, String[] attributes) {
        query = buildQueryWithAttributes(query, attributes);
        if (dbtype_PPI == database) {
            getWebserviceResult(ConnectionSettings.getInstance().getDBConnection().getPpiDBName(), query);
        } else if (dbtype_MiRNA == database) {
            getWebserviceResult(ConnectionSettings.getInstance().getDBConnection().getMirnaNewDBName(), query);
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
        return getDBResult(query, attributes);
    }

    public ArrayList<DBColumn> requestDbContent(int database, String query) {
        return this.requestDbContent(database, query, null);
    }

    private void getWebserviceResult(String database, String query) {
        if (web_service.isWithAddressing()) {
            UUID requestID = web_service.callWebserviceWithQuery(database, query);
            serviceRequestIDs.add(requestID);
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
        ArrayList<DBColumn> data = new ArrayList<>();
        try {
            ResultSet rs = attributes != null ? db.selectQuery(query, attributes) : db.selectQuery(query);
            int col = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                String[] column = new String[col];
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
        if (web_service.isWithAddressing()) {
            if (this.serviceRequestIDs.contains(event.getWebServiceIdent())) {
                dbResults = VanesaUtility.createResultList(event);
                isSingleRequestFinished = true;
            }
        } else {
            dbResults = VanesaUtility.createResultList(event);
        }
    }
}
