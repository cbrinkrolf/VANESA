package api.payloads;

public final class Request<T extends Payload> {
    public final String version;
    public final T payload;

    public Request(T payload) {
        version = "1.0";
        this.payload = payload;
    }
}
