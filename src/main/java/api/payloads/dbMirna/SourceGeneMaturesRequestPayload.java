package api.payloads.dbMirna;

import api.payloads.Payload;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SourceGeneMaturesRequestPayload extends Payload {
    @JsonProperty("hsa_only")
    public Boolean hsaOnly;
    public String name;
}
