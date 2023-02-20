package api.payloads.dbMirna;

import api.payloads.Payload;

import java.util.Arrays;

public class GenesTargetedByMatureResponsePayload extends Payload {
    public DBMirnaTargetGene[] results;

    @Override
    public String toString() {
        return "GenesTargetedByMatureResponsePayload{results=" + Arrays.toString(results) + '}';
    }
}
