package org.codegen.generator.imports;

import org.codegen.generator.GenerationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImportResolverTest {

    private final ImportResolver importResolver = new ImportResolver();
    private GenerationContext context;

    @BeforeEach
    void setUp() {
        context = new GenerationContext(
                "com.example.entity",
                Map.of()
        );
    }

    @Test
    void keepsJavaAndOtherImportsInSeparateGroups() {
        GenerationContext context = new GenerationContext(
                "com.example.entity",
                Map.of()
        );

        Set<String> types = Set.of(
                "Getter",
                "Setter",
                "AllArgsConstructor",
                "UUID",
                "List"
        );

        ResolvedImports imports =
                importResolver.resolve(types, context);

        assertEquals(
                List.of(
                        "lombok.AllArgsConstructor",
                        "lombok.Getter",
                        "lombok.Setter"
                ),
                imports.other()
        );

        assertEquals(
                List.of(
                        "java.util.List",
                        "java.util.UUID"
                ),
                imports.java()
        );
    }

    @Test
    void collapsesImportsWhenPackageContainsFiveOrMoreTypes() {
        Set<String> types = Set.of(
                "Entity",
                "Id",
                "GeneratedValue",
                "GenerationType",
                "Table"
        );

        ResolvedImports imports =
                importResolver.resolve(types, context);

        assertEquals(
                List.of("jakarta.persistence.*"),
                imports.other()
        );
    }

    @Test
    void doesntCollapseImportsWhenPackageContainsLessThanFiveTypes() {
        Set<String> types = Set.of(
                "Getter",
                "Setter",
                "NoArgsConstructor",
                "AllArgsConstructor"
        );

        ResolvedImports imports =
                importResolver.resolve(types, context);

        assertEquals(
                List.of(
                        "lombok.AllArgsConstructor",
                        "lombok.Getter",
                        "lombok.NoArgsConstructor",
                        "lombok.Setter"
                ),
                imports.other()
        );
    }

    @Test
    void shouldCollapsePackagesIndependentlyAndPreserveOrder() {
        Set<String> types = Set.of(
                "Getter",
                "Setter",
                "NoArgsConstructor",
                "AllArgsConstructor",
                "Builder",
                "Entity",
                "Id",
                "GeneratedValue",
                "GenerationType",
                "Table"
        );

        ResolvedImports imports =
                importResolver.resolve(types, context);

        assertEquals(
                List.of(
                        "jakarta.persistence.*",
                        "lombok.*"
                ),
                imports.other()
        );
    }
}
