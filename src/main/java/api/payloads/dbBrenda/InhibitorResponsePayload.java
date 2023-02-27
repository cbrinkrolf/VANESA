package api.payloads.dbBrenda;

import api.payloads.Payload;

import java.util.Arrays;

public class InhibitorResponsePayload extends Payload {
    public DBBrendaInhibitor[] results;

    @Override
    public String toString() {
        return "InhibitorResponsePayload{results=" + Arrays.toString(results) + '}';
    }
}
