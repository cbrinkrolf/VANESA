package api.payloads.kegg;

import api.payloads.Payload;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GetPathwayRequestPayload extends Payload {
    @JsonProperty("pathway_id")
    public String pathwayId;
}
