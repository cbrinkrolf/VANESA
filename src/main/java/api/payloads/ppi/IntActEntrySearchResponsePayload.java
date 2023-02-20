package api.payloads.ppi;

import api.payloads.Payload;

import java.util.Arrays;

public class IntActEntrySearchResponsePayload extends Payload {
    public IntActEntry[] results;

    @Override
    public String toString() {
        return "IntActEntrySearchResponsePayload{results=" + Arrays.toString(results) + '}';
    }
}
