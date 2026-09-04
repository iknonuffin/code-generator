package org.codegen.generator;

import org.codegen.app.Application;
import org.codegen.cli.Cli;
import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectGenerationTest {
    @TempDir// (cleanup = CleanupMode.NEVER)
    private Path tempDirectory;

    @Test
    void generatesProject() throws Exception {
//        System.out.println("Temp dir: " + tempDirectory.toAbsolutePath().normalize());

        Path spec = Path.of(
                Objects.requireNonNull(
                        getClass().getResource("/specs/example-project.yaml")
                ).toURI()
        );

//        System.out.println("Spec: " + spec.toAbsolutePath().normalize());

        Cli cli = Application.create().cli();

        String[] args = {
                spec.toString(),
                "--output",
                tempDirectory.toString()
        };
        int exitCode = cli.run(args);

        assertEquals(0, exitCode);

        Path projectDirectory = tempDirectory.resolve("marketplace");

        Path orderService = projectDirectory.resolve("order-service");

        Path productService = projectDirectory.resolve("product-service");

        Path userService = projectDirectory.resolve("user-service");

        Path orderServiceEntityDirectory = orderService
                .resolve("src/main/java/com/github/iknonuffin/marketplace/order/entity");

        Path orderEntity = orderServiceEntityDirectory.resolve("Order.java");

        assertAll(
                () -> assertTrue(
                        Files.isDirectory(projectDirectory),
                        "Project directory was not generated"
                ),
                () -> assertTrue(
                        Files.isRegularFile(projectDirectory.resolve("compose.yaml")),
                        "compose.yaml was not generated"
                ),

                // Initializr ZIP already contains the service directory,
                // so pom.xml should be directly inside order-service
                () -> assertTrue(
                        Files.isRegularFile(orderService.resolve("pom.xml"))
                ),
                () -> assertTrue(
                        Files.isRegularFile(productService.resolve("pom.xml"))
                ),
                () -> assertTrue(
                        Files.isRegularFile(userService.resolve("pom.xml"))
                ),

                () -> assertTrue(
                        Files.isDirectory(orderServiceEntityDirectory),
                        "Entity directory should use the expected Java package"
                ),
                () -> assertTrue(
                        Files.isRegularFile(orderEntity),
                        "Order.java should be generated under the expected Java package"
                ),

                () -> assertTrue(
                        Files.readString(orderEntity).contains(
                                "package com.github.iknonuffin.marketplace.order.entity;"
                        ),
                        "Order.java should declare the expected Java package"
                )
        );

        String orderEntitySource = Files.readString(orderEntity);

        String expectedImports = """
                import com.github.iknonuffin.marketplace.order.enums.OrderStatus;
                import jakarta.persistence.Entity;
                import jakarta.persistence.GeneratedValue;
                import jakarta.persistence.GenerationType;
                import jakarta.persistence.Id;
                import lombok.Getter;
                import lombok.NoArgsConstructor;
                import lombok.Setter;
                
                import java.math.BigDecimal;
                """;

        assertTrue(
                orderEntitySource.contains(expectedImports),
                "Order.java should contain imports in the expected order"
        );
    }
}
