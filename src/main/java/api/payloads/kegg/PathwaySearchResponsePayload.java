package api.payloads.kegg;

import api.payloads.Payload;

import java.util.Arrays;

public class PathwaySearchResponsePayload extends Payload {
    public KeggPathway[] results;

    @Override
    public String toString() {
        return "KeggPathwayResponsePayload{results=" + Arrays.toString(results) + '}';
    }
}
