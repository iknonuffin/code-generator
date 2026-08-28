package org.codegen.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codegen.exception.GenerationException;
import org.codegen.initializr.InitializrRequest;
import org.codegen.initializr.SpringInitializrClient;
import org.codegen.spec.MicroserviceDefiniton;
import org.codegen.zip.ZipExtractor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
@Slf4j
public class MicroserviceGenerator {
    private final SpringInitializrClient initializrClient;

    private final ZipExtractor zipExtractor;

    private final EntityGenerator entityGenerator;

    public void generate(MicroserviceDefiniton microserviceDefiniton,
                         Path location,
                         String projectBasePackage) throws InterruptedException {

        InitializrRequest initializrRequest = new InitializrRequest(
                projectBasePackage,
                microserviceDefiniton.name(),
                projectBasePackage + "." + microserviceDefiniton.packageName()
        );

        try {
            Path projectZip = Files.createTempFile(microserviceDefiniton.name(), ".zip");

            log.debug("Downloading Spring Initializr starter for '{}'", microserviceDefiniton.name());

            initializrClient.downloadProject(initializrRequest, projectZip);

            log.debug("Extracting starter into {}", location);

            zipExtractor.extract(projectZip, location);

            Files.deleteIfExists(projectZip);
        } catch (IOException e) {
            throw new GenerationException(
                    "Failed to generate service '" +
                            microserviceDefiniton.name()
                            + "'",
                    e
            );
        }

        Path microserviceProjectPath = location.resolve(microserviceDefiniton.name());
        String microserviceProjectBasePackage = initializrRequest.packageName()
                        .replace("-", "_");

        entityGenerator.generate(
                microserviceDefiniton.entities(),
                microserviceProjectPath,
                microserviceProjectBasePackage
        );
    }
}
