package org.codegen.app;

import lombok.RequiredArgsConstructor;
import org.codegen.cli.Cli;
import org.codegen.generator.EntityGenerator;
import org.codegen.generator.MicroserviceGenerator;
import org.codegen.generator.ProjectGenerator;
import org.codegen.initializr.InitializrUriBuilder;
import org.codegen.initializr.SpringInitializrClient;
import org.codegen.parser.YamlParser;
import org.codegen.template.TemplateProcessor;
import org.codegen.zip.ZipExtractor;

import java.net.http.HttpClient;

@RequiredArgsConstructor
public class Application {
    private final Cli cli;

    public static Application create() {
        TemplateProcessor templateProcessor = new TemplateProcessor();

        EntityGenerator entityGenerator = new EntityGenerator(templateProcessor);

        SpringInitializrClient initializr = new SpringInitializrClient(
                HttpClient.newHttpClient(),
                new InitializrUriBuilder()
        );

        ZipExtractor zipExtractor = new ZipExtractor();

        MicroserviceGenerator microserviceGenerator =
                new MicroserviceGenerator(
                        templateProcessor,
                        initializr,
                        zipExtractor,
                        entityGenerator
                );

        ProjectGenerator projectGenerator =
                new ProjectGenerator(
                        templateProcessor,
                        microserviceGenerator
                );

        YamlParser yamlParser = new YamlParser();

        Cli cli = new Cli(yamlParser, projectGenerator);

        return new Application(cli);
    }

    public Cli cli() {
        return cli;
    }
}
