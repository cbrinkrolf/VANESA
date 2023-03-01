package api.payloads.ppi;

import api.payloads.Payload;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HPRDRetrievePPIResponsePayload extends Payload {
    public HPRDEntry[] entries;
    @JsonProperty("binary_interactions")
    public int[][] binaryInteractions;
}
