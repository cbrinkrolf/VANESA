package api.payloads.dbMirna;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DBMirnaSourceGene {
    public String name;
    @JsonProperty("ensembl_transcript_id")
    public String ensemblTranscriptId;
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
        if (ensemblGeneId != null) {
            return ensemblGeneId;
        }
        return ensemblTranscriptId;
    }
}
