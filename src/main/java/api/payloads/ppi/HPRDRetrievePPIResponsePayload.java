package api.payloads.ppi;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HPRDRetrievePPIResponsePayload {
    public HPRDEntry[] entries;
    @JsonProperty("binary_interactions")
    public int[][] binaryInteractions;
}
