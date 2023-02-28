package api.payloads.dbMirna;

import api.payloads.Payload;

import java.util.Arrays;

public class MatureSourceGenesResponsePayload extends Payload {
    public DBMirnaSourceGene[] results;

    @Override
    public String toString() {
        return "MatureSourceGenesResponsePayload{results=" + Arrays.toString(results) + '}';
    }
}
