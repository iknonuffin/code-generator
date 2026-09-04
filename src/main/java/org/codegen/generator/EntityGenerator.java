package org.codegen.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codegen.exception.GenerationException;
import org.codegen.generator.imports.ImportResolver;
import org.codegen.generator.imports.ResolvedImports;
import org.codegen.spec.EntityDefinition;
import org.codegen.template.TemplateProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
public class EntityGenerator {
    private static final String ENTITY_TEMPLATE_FILE_NAME = "entity.ftl";

    private final TemplateProcessor templateProcessor;
    private final ImportResolver importResolver;

    public void generate(List<EntityDefinition> entities,
                         Path projectPath,
                         String basePackage,
                         Map<String, String> typePackages) {
        if (entities.isEmpty()) {
            return;
        }

        Path generationPath = projectPath
                .resolve("src/main/java")
                .resolve(basePackage.replace(".", "/"))
                .resolve("entity");

        try {
            Files.createDirectories(generationPath);
        } catch (IOException e) {
            throw new GenerationException(
                    "Failed to create directory '" + generationPath + "' for entities",
                    e);
        }

        log.debug("Generating {} entities into package '{}'",
                entities.size(),
                basePackage + ".entity");

        for (EntityDefinition entity : entities) {
            Set<String> referencedTypes = new HashSet<>();

            referencedTypes.addAll(resolveAnnotations(entity));
            referencedTypes.addAll(entity.fields().values());

            GenerationContext context =
                    new GenerationContext(basePackage + ".entity", typePackages);

            ResolvedImports imports = importResolver.resolve(referencedTypes, context);

            Map<String, Object> data = Map.of(
                    "entity", entity,
                    "basePackage", basePackage,
                    "imports", imports
            );

            Path output = generationPath.resolve(entity.name() + ".java");

            log.debug("Generating entity '{}' to '{}'",
                    entity.name(),
                    output);

            templateProcessor.process(
                    ENTITY_TEMPLATE_FILE_NAME,
                    data,
                    output
            );
        }
    }

    private Set<String> resolveAnnotations(EntityDefinition entity) {
        Set<String> annotations = new HashSet<>();

        annotations.add("Entity");
        annotations.add("NoArgsConstructor");
        annotations.add("Getter");
        annotations.add("Setter");
        annotations.add("Id");

        String idType = entity.fields().get("id");

        if (idType.equals("Long") || idType.equals("UUID")) {
            annotations.add("GeneratedValue");
            annotations.add("GenerationType");
        }

        return annotations;
    }
}