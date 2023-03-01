package api.payloads.ppi;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HPRDEntry {
    public Integer id;
    public String name;
    public String accession;
    @JsonProperty("gene_symbol")
    public String geneSymbol;

    @Override
    public String toString() {
        return "HPRDEntry{id='" + id + "', name='" + name + "', accession='" + accession + "', geneSymbol='" +
                geneSymbol + "'}";
    }
}
