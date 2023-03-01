package api.payloads.dbBrenda;

import api.payloads.Payload;

import java.util.Arrays;

public class EnzymeTurnoverNumberValuesResponsePayload extends Payload {
    public DBBrendaTurnoverNumberValue[] results;

    @Override
    public String toString() {
        return "EnzymeTurnoverNumberValuesResponsePayload{results=" + Arrays.toString(results) + '}';
    }
}
