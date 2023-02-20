package database.Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private Connection con;
    private String user;
    private String password;
    private String database;
    private String server;

    private String dawisDBName = "";
    private String ppiDBName = "";
    private String mirnaDBName = "";
    private String mirnaNewDBName = "";

    public DBConnection(String user, String password, String database, String server) {
        this.user = user;
        this.password = password;
        this.database = database;
        this.server = server;
    }

    public void checkConnection() throws Exception {
        connect2mysql(server, user, password);
        useDatabase(database);
        if (con == null) {
            connect2mysql(server, user, password);
            useDatabase(database);

        } else if (con.isClosed()) {
            connect2mysql(server, user, password);
            useDatabase(database);
        }
    }

    public void useDatabase(String database) {
        try {
            con.setCatalog(database);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void connect2mysql(String server, String user, String password) throws SQLException {
        String url = "jdbc:mysql://" + server + "/";
        con = DriverManager.getConnection(url, user, password);
    }

    public ResultSet selectQuery(String query) throws Exception {
        Statement stmt = con.createStatement();
        return stmt.executeQuery(query);
    }

    public ResultSet selectQuery(String preparedStatement, String[] inserts) throws Exception {
        PreparedStatement ps = con.prepareStatement(preparedStatement);
        for (int i = 0; i < inserts.length; i++) {
            ps.setString((i + 1), inserts[i]);
        }
        return ps.executeQuery();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getDawisDBName() {
        return dawisDBName;
    }

    public void setDawisDBName(String davisDBName) {
        this.dawisDBName = davisDBName;
    }

    public String getPpiDBName() {
        return ppiDBName;
    }

    public void setPpiDBName(String ppiDBName) {
        this.ppiDBName = ppiDBName;
    }

    public String getMirnaDBName() {
        return mirnaDBName;
    }

    public void setMirnaDBName(String mirnaDBName) {
        this.mirnaDBName = mirnaDBName;
    }

    public String getMirnaNewDBName() {
        return mirnaNewDBName;
    }

    public void setMirnaNewDBName(String mirnaNewDBName) {
        this.mirnaNewDBName = mirnaNewDBName;
    }
}
