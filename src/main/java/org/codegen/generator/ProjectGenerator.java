package org.codegen.generator;

import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.codegen.spec.ProjectSpecification;
import org.codegen.spec.MicroserviceDefiniton;
import org.codegen.template.TemplateProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class ProjectGenerator {
    private final TemplateProcessor templateProcessor;
    private final MicroserviceGenerator microserviceGenerator;

    private final static String DOCKER_COMPOSE_TEMPLATE_FILE_NAME = "compose.ftl";

    public void generate(ProjectSpecification spec) throws IOException, TemplateException, InterruptedException {
        Path projectDir = Path.of(System.getProperty("user.home"), "IdeaProjects")
                .resolve(spec.project().name());

        try {
            Files.createDirectory(projectDir);

            generateDockerCompose(spec.services(), projectDir);

            for (MicroserviceDefiniton service : spec.services()) {
                microserviceGenerator.generate(service, projectDir, spec.project().basePackage());
            }
        } catch (Exception e) {
            deleteRecursively(projectDir);
            throw e;
        }
    }

    private void generateDockerCompose(List<MicroserviceDefiniton> services, Path projectDir)
            throws IOException, TemplateException {
        Path dockerComposePath = projectDir.resolve(Path.of("compose.yaml"));

        templateProcessor.process(
                DOCKER_COMPOSE_TEMPLATE_FILE_NAME,
                services,
                dockerComposePath
        );
    }

    private void deleteRecursively(Path path) throws IOException {
        try (Stream<Path> walk = Files.walk(path)) {
            walk.sorted(Comparator.reverseOrder())
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        System.err.printf("Failed to delete file: %s%n", p);
                    }
                });
        }
    }
}
