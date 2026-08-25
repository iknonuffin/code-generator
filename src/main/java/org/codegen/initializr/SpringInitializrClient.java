package org.codegen.initializr;

import lombok.RequiredArgsConstructor;
import org.codegen.exception.InitializrException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

@RequiredArgsConstructor
public class SpringInitializrClient {
    private static final Logger log = LoggerFactory.getLogger(SpringInitializrClient.class);

    private final HttpClient httpClient;
    private final InitializrUriBuilder initializrUriBuilder;

    public void downloadProject(InitializrRequest initializrRequest, Path output)
            throws InterruptedException {
        URI uri = initializrUriBuilder.build(initializrRequest);

        log.debug(
                "Requesting Spring Initializr for artifact '{}'",
                initializrRequest.artifactId()
        );

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();

            HttpResponse<Path> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofFile(output)
            );

            if (response.statusCode() != 200) {
                throw new InitializrException("Spring Initializr returned HTTP " + response.statusCode());
            }

        } catch (IOException e) {
            throw new InitializrException("Failed to download project from Spring Initializr", e);
        }
    }
}
