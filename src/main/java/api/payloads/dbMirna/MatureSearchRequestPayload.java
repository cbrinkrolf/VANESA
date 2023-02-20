package api.payloads.dbMirna;

import api.payloads.Payload;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MatureSearchRequestPayload extends Payload {
    @JsonProperty("hsa_only")
    public Boolean hsaOnly;
    public String name;
    public String sequence;
    public String accession;
}
