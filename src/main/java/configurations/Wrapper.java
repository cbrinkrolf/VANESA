package configurations;

import pojos.DBColumn;

import java.util.ArrayList;

public class Wrapper {
    public static final int DBTYPE_KEGG = 2;
    public static final int DBTYPE_PPI = 4;
    public static final int DBTYPE_MI_RNA = 6;
    public static final int DBTYPE_BRENDA2 = 7;

    public ArrayList<DBColumn> requestDbContent(int database, String query) {
        return requestDbContent(database, query, null);
    }

    public ArrayList<DBColumn> requestDbContent(int database, String query, String[] attributes) {
        return new ArrayList<>();
    }
}
