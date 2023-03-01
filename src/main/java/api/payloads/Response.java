package api.payloads;

import org.apache.commons.lang3.StringUtils;

public final class Response<T extends Payload> {
    public String version;
    public String error;
    public T payload;

    public Response() {
    }

    public boolean hasError() {
        return StringUtils.isNotEmpty(error);
    }
}
