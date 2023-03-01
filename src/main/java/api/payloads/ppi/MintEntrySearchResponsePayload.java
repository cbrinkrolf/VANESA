package api.payloads.ppi;

import api.payloads.Payload;

import java.util.Arrays;

public class MintEntrySearchResponsePayload extends Payload {
    public MintEntry[] results;

    @Override
    public String toString() {
        return "MintEntrySearchResponsePayload{results=" + Arrays.toString(results) + '}';
    }
}
