package api.payloads.dbMirna;

import api.payloads.Payload;

import java.util.Arrays;

public class TargetGeneSearchResponsePayload extends Payload {
    public DBMirnaTargetGene[] results;

    @Override
    public String toString() {
        return "TargetGeneSearchResponsePayload{results=" + Arrays.toString(results) + '}';
    }
}
