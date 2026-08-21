package org.codegen.generator;

import lombok.RequiredArgsConstructor;
import org.codegen.initializr.InitializrRequest;
import org.codegen.initializr.SpringInitializrClient;
import org.codegen.spec.MicroserviceDefiniton;
import org.codegen.template.TemplateProcessor;

import java.io.IOException;
import java.nio.file.Path;

@RequiredArgsConstructor
public class MicroserviceGenerator {
    private final TemplateProcessor templateProcessor;

    private final SpringInitializrClient initializrClient;

    private final EntityGenerator entityGenerator;

    public void generate(MicroserviceDefiniton microserviceDefiniton,
                         Path location,
                         String projectBasePackage) throws IOException, InterruptedException {
        InitializrRequest initializrRequest = new InitializrRequest(
                microserviceDefiniton.name(),
                projectBasePackage,
                microserviceDefiniton.name()
        );

        Path projectZip = location.resolve(microserviceDefiniton.name() + ".zip");

        initializrClient.downloadProject(initializrRequest, projectZip);

        unzip(projectZip, location);
    }

    private void unzip(Path zipFile, Path destination) {
        Path target = destination.toAbsolutePath().normalize();


    }
}
