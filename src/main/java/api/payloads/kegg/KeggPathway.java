package api.payloads.kegg;

public class KeggPathway {
    public String id;
    public String name;
    public String taxonomy_id;
    public String taxonomy_name;

    @Override
    public String toString() {
        return "KeggPathway{id='" + id + "', name='" + name + "', taxonomy_id='" + taxonomy_id + "', taxonomy_name='" +
                taxonomy_name + "'}";
    }
}
