package api.payloads.dbMirna;

public class DBMirnaTargetGene {
    public String db;
    public String accession;

    @Override
    public String toString() {
        return "DBMirnaTargetGene{db='" + db + "', accession='" + accession + "'}";
    }
}
