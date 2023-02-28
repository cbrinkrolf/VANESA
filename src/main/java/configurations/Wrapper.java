package configurations;

import pojos.DBColumn;

import java.util.ArrayList;

public class Wrapper {
    public ArrayList<DBColumn> requestDbContent(int database, String query) {
        return requestDbContent(database, query, null);
    }

    public ArrayList<DBColumn> requestDbContent(int database, String query, String[] attributes) {
        return new ArrayList<>();
    }
}
