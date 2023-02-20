package api.payloads.dbMirna;

import api.payloads.Payload;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MaturesTargetingGeneRequestPayload extends Payload {
    @JsonProperty("hsa_only")
    public Boolean hsaOnly;
    public String accession;
}
