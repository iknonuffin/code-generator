package org.codegen.generator;

import org.codegen.app.Application;
import org.codegen.cli.Cli;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectGenerationTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void generatesProject() throws Exception {
        System.out.println("Temp dir: " + tempDirectory.toAbsolutePath().normalize());

        Path spec = Path.of(
                Objects.requireNonNull(
                        getClass().getResource("/specs/example-project.yaml")
                ).toURI()
        );

        System.out.println("Spec: " + spec.toAbsolutePath().normalize());

        Cli cli = Application.create().cli();

        String[] args = {
                spec.toString(),
                "--output",
                tempDirectory.toString()
        };
        int exitCode = cli.run(args);

        assertEquals(0, exitCode);

        Path projectDirectory = tempDirectory.resolve("marketplace");

        assertAll(
                () -> assertTrue(
                        Files.isDirectory(projectDirectory)
                ),
                () -> assertTrue(
                        Files.isRegularFile(projectDirectory.resolve("compose.yaml"))
                ),

                () -> assertTrue(
                        Files.isRegularFile(
                                projectDirectory
                                        .resolve("order-service")
                                        .resolve("pom.xml")
                        )
                ),
                () -> assertTrue(
                        Files.isRegularFile(
                                projectDirectory
                                        .resolve("product-service")
                                        .resolve("pom.xml")
                        )
                ),
                () -> assertTrue(
                        Files.isRegularFile(
                                projectDirectory
                                        .resolve("user-service")
                                        .resolve("pom.xml")
                        )
                )
        );
    }
}
