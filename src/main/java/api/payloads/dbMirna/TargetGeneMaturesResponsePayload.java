package api.payloads.dbMirna;

import api.payloads.Payload;

import java.util.Arrays;

public class TargetGeneMaturesResponsePayload extends Payload {
    public DBMirnaMature[] results;

    @Override
    public String toString() {
        return "TargetGeneMaturesResponsePayload{results=" + Arrays.toString(results) + '}';
    }
}
