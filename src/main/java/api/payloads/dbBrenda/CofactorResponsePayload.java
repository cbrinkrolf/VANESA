package api.payloads.dbBrenda;

import api.payloads.Payload;

import java.util.Arrays;

public class CofactorResponsePayload extends Payload {
    public DBBrendaCofactor[] results;

    @Override
    public String toString() {
        return "CofactorResponsePayload{results=" + Arrays.toString(results) + '}';
    }
}
