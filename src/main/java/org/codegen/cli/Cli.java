package org.codegen.cli;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codegen.exception.GenerationException;
import org.codegen.generator.ProjectGenerator;
import org.codegen.parser.YamlParser;
import org.codegen.spec.ProjectSpecification;

import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
@Slf4j
public class Cli {
    private final YamlParser parser;
    private final ProjectGenerator projectGenerator;

    public int run(String[] args) {
        log.debug("Received {} CLI arguments: {}", args.length, args);

        if (args.length != 1 && args.length != 3) {
            printUsage();
            return 2;
        }

        Path spec = Path.of(args[0]);

        Path outputDirectory;

        if (args.length == 1) {
            outputDirectory = defaultOutputDir();
        } else {
            if (!args[1].equals("--output") && !args[1].equals("-o")) {
                printUsage();
                return 2;
            }

            outputDirectory = Path.of(args[2])
                    .toAbsolutePath()
                    .normalize();
        }

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
            projectGenerator.generate(projSpec, outputDirectory);
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
        System.out.println("""
                Usage: codegen [FILE] [OPTION]
                
                -o, --output        specify custom generation directory
                """);
    }

    private Path defaultOutputDir() {
        return Path.of(
                System.getProperty("user.home"), "IdeaProjects"
        ).toAbsolutePath().normalize();
    }
}
