package org.codegen.cli;

import lombok.RequiredArgsConstructor;
import org.codegen.exception.GenerationException;
import org.codegen.generator.ProjectGenerator;
import org.codegen.parser.YamlParser;
import org.codegen.spec.ProjectSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
public class Cli {
    private static final Logger log = LoggerFactory.getLogger(Cli.class);
    private final YamlParser parser;
    private final ProjectGenerator projectGenerator;

    public int run(String[] args) {
        log.debug("Received {} CLI arguments", args.length);

        if (args.length != 1) {
            printUsage();
            return 2;
        }

        Path spec = Path.of(args[0]);

        log.debug("Using specification: {}", spec.toAbsolutePath().normalize());

        if (!Files.isRegularFile(spec)) {
            System.out.println("Not a file");

            log.debug("Specification file does not exist");

            return 2;
        }

        String fileName = spec.getFileName().toString();
        if (!fileName.endsWith(".yaml") && !fileName.endsWith(".yml")) {
            System.out.println("Not a YAML file");

            log.debug("File passed is not a YAML file: {}", fileName);
            return 2;
        }

        System.out.println("Reading specification...");
        ProjectSpecification projSpec = parser.parse(spec);

        System.out.println(
                "Generating project '"
                        + projSpec.project().name()
                        + "'..."
        );

        try {
            projectGenerator.generate(projSpec);
        } catch (GenerationException e) {
            System.err.println("Error: " + e.getMessage());
            log.error("Generation failed", e);
            return 1;
        }  catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            System.err.println("Generation interrupted.");
            log.debug("Generation interrupted", e);
            return 1;
        }

        System.out.println("Project generated successfully.");

        log.info("Project '{}' generated successfully", projSpec.project().name());

        return 0;
    }

    private void printUsage() {
        System.out.println("Usage: codegen <spec.yaml>");
    }
}
