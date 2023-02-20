package api.payloads.dbMirna;

public class DBMirnaMature {
    public String name;
    public String sequence;
    public String accession;

    @Override
    public String toString() {
        return "DBMirnaMature{name='" + name + "', sequence='" + sequence + "', accession='" + accession + "'}";
    }
}
