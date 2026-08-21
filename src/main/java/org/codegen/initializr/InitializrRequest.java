package org.codegen.initializr;

import java.util.List;

public record InitializrRequest(
        String type,
        String language,
        String bootVersion,
        String baseDir,
        String groupId,
        String artifactId,
        String packageName,
        String packaging,
        String javaVersion,
        String configurationFileFormat,
        List<String> dependencies
) {
    public InitializrRequest(String baseDir,
                             String groupId,
                             String artifactId) {
        this(
                "maven-project",
                "java",
                "4.1.0",
                baseDir,
                groupId,
                artifactId,
                groupId + "." + artifactId,
                "jar",
                "21",
                "properties",
                List.of("web", "data-jpa", "h2", "lombok", "postgresql")
        );
    }
}
