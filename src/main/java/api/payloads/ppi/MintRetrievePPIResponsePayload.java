package api.payloads.ppi;

import api.payloads.Payload;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MintRetrievePPIResponsePayload extends Payload {
    public MintEntry[] entries;
    @JsonProperty("binary_interactions")
    public int[][] binaryInteractions;
    @JsonProperty("complex_interactions")
    public int[][] complexInteractions;
}
