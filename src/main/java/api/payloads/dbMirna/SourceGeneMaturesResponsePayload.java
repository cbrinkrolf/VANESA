package api.payloads.dbMirna;

import api.payloads.Payload;

import java.util.Arrays;

public class SourceGeneMaturesResponsePayload extends Payload {
    public DBMirnaMature[] results;

    @Override
    public String toString() {
        return "SourceGeneMaturesResponsePayload{results=" + Arrays.toString(results) + '}';
    }
}
