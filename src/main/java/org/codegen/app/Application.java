package org.codegen.app;

import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.codegen.generator.EntityGenerator;
import org.codegen.generator.MicroserviceGenerator;
import org.codegen.generator.ProjectGenerator;
import org.codegen.initializr.InitializrUriBuilder;
import org.codegen.initializr.SpringInitializrClient;
import org.codegen.parser.YamlParser;
import org.codegen.spec.ProjectSpecification;
import org.codegen.template.TemplateProcessor;

import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
public class Application {
    private final YamlParser parser;
    private final ProjectGenerator projectGenerator;

    public static Application create() {
        TemplateProcessor templateProcessor = new TemplateProcessor();

        EntityGenerator entityGenerator = new EntityGenerator(templateProcessor);

        SpringInitializrClient initializr = new SpringInitializrClient(
                HttpClient.newHttpClient(),
                new InitializrUriBuilder()
        );

        MicroserviceGenerator microserviceGenerator =
                new MicroserviceGenerator(
                        templateProcessor,
                        initializr,
                        entityGenerator
                );

        ProjectGenerator projectGenerator =
                new ProjectGenerator(
                        templateProcessor,
                        microserviceGenerator
                );

        YamlParser yamlParser = new YamlParser();

        return new Application(
                yamlParser,
                projectGenerator
        );
    }

    public int run(String[] args)
            throws IOException,
            TemplateException,
            InterruptedException {
        if (args.length != 1) {
            System.out.println("Usage: codegen <spec.yaml>");
            return 2;
        }

        Path spec = Path.of(args[0]);

        if (!Files.isRegularFile(spec)) {
            System.out.println("Not a file");
            return 2;
        }

        String fileName = spec.getFileName().toString();
        if (!fileName.endsWith(".yaml") || !fileName.endsWith(".yml")) {
            System.out.println("Not a YAML file");
            return 2;
        }

        ProjectSpecification projSpec = parser.parse(spec);

        projectGenerator.generate(projSpec);

        return 0;
    }
}
