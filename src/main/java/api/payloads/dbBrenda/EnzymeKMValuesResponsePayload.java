package api.payloads.dbBrenda;

import api.payloads.Payload;

import java.util.Arrays;

public class EnzymeKMValuesResponsePayload extends Payload {
    public DBBrendaKMValue[] results;

    @Override
    public String toString() {
        return "EnzymeKMValuesResponsePayload{results=" + Arrays.toString(results) + '}';
    }
}
