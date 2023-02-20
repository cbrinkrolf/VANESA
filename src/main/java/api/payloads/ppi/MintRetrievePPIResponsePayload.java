package api.payloads.ppi;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MintRetrievePPIResponsePayload {
    public MintEntry[] entries;
    @JsonProperty("binary_interactions")
    public int[][] binaryInteractions;
    @JsonProperty("complex_interactions")
    public int[][] complexInteractions;
}
