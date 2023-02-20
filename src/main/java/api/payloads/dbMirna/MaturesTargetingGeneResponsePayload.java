package api.payloads.dbMirna;

import api.payloads.Payload;

import java.util.Arrays;

public class MaturesTargetingGeneResponsePayload extends Payload {
    public DBMirnaMature[] results;

    @Override
    public String toString() {
        return "MaturesTargetingGeneResponsePayload{results=" + Arrays.toString(results) + '}';
    }
}
