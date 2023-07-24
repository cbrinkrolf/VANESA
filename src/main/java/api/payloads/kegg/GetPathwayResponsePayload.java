package api.payloads.kegg;

import api.payloads.Payload;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GetPathwayResponsePayload extends Payload {
    public String id;
    public String name;
    @JsonProperty("taxonomy_id")
    public String taxonomyId;
    @JsonProperty("taxonomy_name")
    public String taxonomyName;

    @Override
    public String toString() {
        return "PathwaySearchResponsePayload{id='" + id + "', name='" + name + "', taxonomy_id='" + taxonomyId +
               "', taxonomy_name='" + taxonomyName + "'}";
    }
}
