package api;

import api.payloads.Payload;
import api.payloads.Request;
import api.payloads.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import configurations.SettingsManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.function.Consumer;

public class VanesaApi {
    private static final HttpClient client = HttpClient.newBuilder().build();
    private static final ObjectMapper mapper = new ObjectMapper();

    private VanesaApi() {
    }

    public static <S extends Payload, T extends Payload> void post(String path, S requestPayload,
                                                                   TypeReference<Response<T>> responseType,
                                                                   Consumer<Response<T>> callback) {
        if (!path.startsWith("/")) {
            path = '/' + path;
        }
        var requestData = new Request<>(requestPayload);
        String requestJson;
        try {
            requestJson = mapper.writeValueAsString(requestData);
        } catch (JsonProcessingException e) {
            callback.accept(createExceptionResponse(e));
            return;
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SettingsManager.getInstance().getApiUrl() + path))
                .timeout(Duration.ofMinutes(2))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson, StandardCharsets.UTF_8))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept((String body) -> {
                    try {
                        Response<T> result = mapper.readValue(body, responseType);
                        callback.accept(result);
                    } catch (JsonProcessingException e) {
                        callback.accept(createExceptionResponse(e));
                    }
                });
    }

    private static <T extends Payload> Response<T> createExceptionResponse(Exception ex) {
        Response<T> result = new Response<>();
        result.error = ex.toString();
        return result;
    }

    public static <S extends Payload, T extends Payload> Response<T> postSync(String path, S requestPayload,
                                                                              TypeReference<Response<T>> responseType) {
        if (!path.startsWith("/")) {
            path = '/' + path;
        }
        var requestData = new Request<>(requestPayload);
        String requestJson;
        try {
            requestJson = mapper.writeValueAsString(requestData);
        } catch (JsonProcessingException e) {
            return createExceptionResponse(e);
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SettingsManager.getInstance().getApiUrl() + path))
                .timeout(Duration.ofMinutes(2))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson, StandardCharsets.UTF_8))
                .build();
        try {
            String resultBody = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            return mapper.readValue(resultBody, responseType);
        } catch (IOException | InterruptedException e) {
            return createExceptionResponse(e);
        }
    }
}
