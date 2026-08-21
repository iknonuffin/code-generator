package org.codegen.initializr;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

@RequiredArgsConstructor
public class SpringInitializrClient {
    private final HttpClient httpClient;
    private final InitializrUriBuilder initializrUriBuilder;

    public void downloadProject(InitializrRequest initializrRequest, Path output)
            throws IOException, InterruptedException {
        URI uri = initializrUriBuilder.build(initializrRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<Path> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofFile(output)
        );

        if (response.statusCode() != 200) {
            throw new IOException(
                    "Initializr returned HTTP " + response.statusCode()
            );
        }
    }
}
