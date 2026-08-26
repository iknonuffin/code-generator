package org.codegen.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codegen.exception.GenerationException;
import org.codegen.spec.ProjectSpecification;
import org.codegen.spec.MicroserviceDefiniton;
import org.codegen.template.TemplateProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Slf4j
public class ProjectGenerator {
    private final TemplateProcessor templateProcessor;
    private final MicroserviceGenerator microserviceGenerator;

    private final static String DOCKER_COMPOSE_TEMPLATE_FILE_NAME = "compose.ftl";

    public void generate(ProjectSpecification spec, Path outputDirectory) throws InterruptedException {
        log.debug("Output directory: {}", outputDirectory);

        Path projectDir = outputDirectory.resolve(spec.project().name());
        try {
            log.debug("Creating project directory: {}", projectDir);
            createProjectDirectory(projectDir);

            log.debug("Generating Docker Compose file to {}", projectDir);
            generateDockerCompose(spec.services(), projectDir);

            log.debug("Generating {} services", spec.services().size());
            for (MicroserviceDefiniton service : spec.services()) {
                log.debug("Generating service: {}", service.name());

                microserviceGenerator.generate(service, projectDir, spec.project().basePackage());
            }
        } catch (InterruptedException e) {
            deleteProjectAfterFailure(projectDir, e);

            Thread.currentThread().interrupt();
            throw e;
        } catch (GenerationException e) {
            deleteProjectAfterFailure(projectDir, e);

            throw e;
        } catch (IOException e) {
            deleteProjectAfterFailure(projectDir, e);

            throw new GenerationException(
                    "Failed to generate project '"
                            + spec.project().name()
                            + "'",
                    e
            );
        }
    }

    private void createProjectDirectory(Path projectDirectory) throws IOException {
        if (Files.exists(projectDirectory)) {
            throw new GenerationException(
                    "Project directory already exists: " + projectDirectory
            );
        }

        Files.createDirectories(projectDirectory);
    }

    private void generateDockerCompose(List<MicroserviceDefiniton> services, Path projectDir) {
        Path dockerComposePath = projectDir.resolve(Path.of("compose.yaml"));

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("services", services);

        templateProcessor.process(
                DOCKER_COMPOSE_TEMPLATE_FILE_NAME,
                dataModel,
                dockerComposePath
        );
    }

    private void deleteProjectAfterFailure(
            Path projectDirectory,
            Exception originalException
    ) {
        try {
            deleteRecursively(projectDirectory);
        } catch (IOException cleanupException) {
            originalException.addSuppressed(cleanupException);
        }
    }

    private void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }

        try (Stream<Path> paths = Files.walk(path)) {
            for (Path current : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(current);
            }
        }
    }
}
