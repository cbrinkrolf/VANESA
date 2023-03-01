package api.payloads.dbMirna;

import api.payloads.Payload;

import java.util.Arrays;

public class MatureSearchResponsePayload extends Payload {
    public DBMirnaMature[] results;

    @Override
    public String toString() {
        return "MatureSearchResponsePayload{results=" + Arrays.toString(results) + '}';
    }
}
