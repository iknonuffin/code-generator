package org.codegen.generator;

import lombok.RequiredArgsConstructor;
import org.codegen.exception.GenerationException;
import org.codegen.initializr.InitializrRequest;
import org.codegen.initializr.SpringInitializrClient;
import org.codegen.spec.MicroserviceDefiniton;
import org.codegen.template.TemplateProcessor;
import org.codegen.zip.ZipExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
public class MicroserviceGenerator {
    private final static Logger log = LoggerFactory.getLogger(MicroserviceGenerator.class);

    private final TemplateProcessor templateProcessor;

    private final SpringInitializrClient initializrClient;

    private final ZipExtractor zipExtractor;

    private final EntityGenerator entityGenerator;

    public void generate(MicroserviceDefiniton microserviceDefiniton,
                         Path location,
                         String projectBasePackage) throws InterruptedException {
        InitializrRequest initializrRequest = new InitializrRequest(
                microserviceDefiniton.name(),
                projectBasePackage,
                microserviceDefiniton.name()
        );

        try {
            Path projectZip = Files.createTempFile(microserviceDefiniton.name(), ".zip");

            log.debug("Downloading Spring Initializr starter for '{}'", microserviceDefiniton.name());

            initializrClient.downloadProject(initializrRequest, projectZip);

            Path microserviceDir = location.resolve(microserviceDefiniton.name());

            log.debug("Extracting starter into {}", microserviceDir);

            zipExtractor.extract(projectZip, microserviceDir);

            Files.deleteIfExists(projectZip);
        } catch (IOException e) {
            throw new GenerationException(
                    "Failed to generate service '" +
                            microserviceDefiniton.name()
                            + "'",
                    e
            );
        }
    }

}
