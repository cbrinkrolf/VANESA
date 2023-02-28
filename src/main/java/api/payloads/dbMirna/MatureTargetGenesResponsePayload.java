package api.payloads.dbMirna;

import api.payloads.Payload;

import java.util.Arrays;

public class MatureTargetGenesResponsePayload extends Payload {
    public DBMirnaTargetGene[] results;

    @Override
    public String toString() {
        return "MatureTargetGenesResponsePayload{results=" + Arrays.toString(results) + '}';
    }
}
