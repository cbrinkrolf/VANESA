package api.payloads.ppi;

import api.payloads.Payload;

import java.util.Arrays;

public class HPRDEntrySearchResponsePayload extends Payload {
    public HPRDEntry[] results;

    @Override
    public String toString() {
        return "HPRDEntrySearchResponsePayload{results=" + Arrays.toString(results) + '}';
    }
}
