package api.payloads.kegg;

public class KeggPathway {
    public String id;
    public String name;
    public String organism;

    @Override
    public String toString() {
        return "KeggPathway{id='" + id + "', name='" + name + "', organism='" + organism + "'}";
    }
}
