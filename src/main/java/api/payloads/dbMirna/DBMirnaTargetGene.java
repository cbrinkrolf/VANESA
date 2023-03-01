package api.payloads.dbMirna;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DBMirnaTargetGene {
    public String name;
    public String db;
    @JsonProperty("hgnc_id")
    public String hgncId;
    @JsonProperty("ensembl_gene_id")
    public String ensemblGeneId;
    @JsonProperty("entrez_gene_id")
    public Integer entrezGeneId;

    public String getAccession() {
        if (entrezGeneId != null) {
            return String.valueOf(entrezGeneId);
        }
        if (hgncId != null) {
            return hgncId;
        }
        return ensemblGeneId;
    }
}
