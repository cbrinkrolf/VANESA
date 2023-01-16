package pojos;

import java.io.Serializable;

public class DBColumn implements Serializable {
    private static final long serialVersionUID = -8392278081555498586L;
    private String[] column = new String[0];

    public DBColumn() {
    }

    public DBColumn(String[] column) {
        this.column = column;
    }

    public int getLenght() {
        return this.column.length;
    }

    public String[] getColumn() {
        return this.column;
    }

    public void setColumn(String[] column) {
        this.column = column;
    }
}