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
    private final EnumGenerator enumGenerator;

    public void generate(
            MicroserviceDefiniton microserviceDefiniton,
            Path location,
            String projectBasePackage) throws InterruptedException {

        InitializrRequest initializrRequest =
                createInitializrRequest(
                        microserviceDefiniton, projectBasePackage
                );

        generateSpringProject(microserviceDefiniton, initializrRequest, location);

        Path microserviceProjectPath = location.resolve(microserviceDefiniton.name());
        String microserviceProjectBasePackage = initializrRequest.packageName();

        entityGenerator.generate(
                microserviceDefiniton.entities(),
                microserviceProjectPath,
                microserviceProjectBasePackage
        );

        enumGenerator.generate(
                microserviceDefiniton.enums(),
                microserviceProjectPath,
                microserviceProjectBasePackage
        );
    }

    private InitializrRequest createInitializrRequest(
            MicroserviceDefiniton microservice, String projectBasePkg
    ) {
        // Hyphens are not valid in Java package names
        String pkgName =
                projectBasePkg +  "." + microservice.packageName()
                .replace("-", "_");

        return new InitializrRequest(
                projectBasePkg,
                microservice.name(),
                pkgName
        );
    }

    private void generateSpringProject(
            MicroserviceDefiniton microservice,
            InitializrRequest request,
            Path location
    ) throws InterruptedException {

        Path projectZip = null;

        try {
            projectZip = Files.createTempFile(microservice.name(), ".zip");

            log.debug("Downloading Spring Initializr starter for '{}'", microservice.name());

            initializrClient.downloadProject(request, projectZip);

            log.debug("Extracting starter into {}", location);

            zipExtractor.extract(projectZip, location);

            Files.deleteIfExists(projectZip);
        } catch (IOException e) {
            throw new GenerationException(
                    "Failed to generate service '" +
                            microservice.name()
                            + "'",
                    e
            );
        } finally {
            if (projectZip != null) {
                try {
                    Files.delete(projectZip);
                } catch (IOException e) {
                    log.warn("Failed to delete temporary ZIP '{}'", projectZip, e);
                }
            }
        }

    }

}
