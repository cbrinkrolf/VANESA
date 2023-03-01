package api.payloads.dbBrenda;

import api.payloads.Payload;

import java.util.Arrays;

public class ReactionSearchResponsePayload extends Payload {
    public DBBrendaReaction[] results;

    @Override
    public String toString() {
        return "ReactionSearchResponsePayload{results=" + Arrays.toString(results) + '}';
    }
}
