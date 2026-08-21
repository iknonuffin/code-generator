package org.codegen.initializr;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class InitializrUriBuilder {
    private final static String BASE_URL = "https://start.spring.io/starter.zip";

    public URI build(InitializrRequest request) {
        String query = buildQuery(request);

        return URI.create(BASE_URL + "?" + query);
    }

    private String buildQuery(InitializrRequest request) {
        return String.join("&",
                queryParam("type", request.type()),
                queryParam("language", request.language()),
                queryParam("bootVersion", request.bootVersion()),
                queryParam("baseDir", request.baseDir()),
                queryParam("groupId", request.groupId()),
                queryParam("artifactId", request.artifactId()),
                queryParam("packageName", request.packageName()),
                queryParam("packaging", request.packaging()),
                queryParam("javaVersion", request.javaVersion()),
                queryParam("configurationFileFormat", request.configurationFileFormat()),
                queryParam("dependencies", String.join(",", request.dependencies()))
        );
    }

    private String queryParam(String name, String value) {
        return URLEncoder.encode(name, StandardCharsets.UTF_8)
                + "="
                + URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
