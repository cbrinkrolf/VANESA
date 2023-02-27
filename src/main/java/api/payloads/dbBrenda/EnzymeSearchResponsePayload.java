package api.payloads.dbBrenda;

import api.payloads.Payload;

import java.util.Arrays;

public class EnzymeSearchResponsePayload extends Payload {
    public DBBrendaEnzyme[] results;

    @Override
    public String toString() {
        return "EnzymeSearchResponsePayload{results=" + Arrays.toString(results) + '}';
    }
}
