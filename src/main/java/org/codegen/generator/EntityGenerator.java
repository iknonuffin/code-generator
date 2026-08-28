package org.codegen.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codegen.exception.GenerationException;
import org.codegen.spec.EntityDefinition;
import org.codegen.template.TemplateProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class EntityGenerator {
    private final TemplateProcessor templateProcessor;

    private final static String ENTITY_TEMPLATE_FILE_NAME = "entity.ftl";

    public void generate(List<EntityDefinition> entities,
                         Path projectPath,
                         String basePackage) {
        Path generationPath = projectPath
                .resolve("src/main/java/")
                .resolve(basePackage.replace(".", "/"))
                .resolve("entity");

        try {
            Files.createDirectories(generationPath);
        } catch (IOException e) {
            throw new GenerationException(
                    "Failed to create directory '" + generationPath + "' for entities",
                    e
            );
        }

        log.debug(
                "Generating {} entities into package '{}'",
                entities.size(),
                basePackage + ".entity"
        );

        for (EntityDefinition entity : entities) {
            Map<String, Object> data = Map.of(
                    "entity", entity,
                    "basePackage", basePackage
            );

            Path output = generationPath.resolve(entity.name() + ".java");

            log.debug(
                    "Generating entity '{}' to '{}'",
                    entity.name(),
                    output
            );

            templateProcessor.process(
                    ENTITY_TEMPLATE_FILE_NAME,
                    data,
                    output
            );
        }
    }
}
