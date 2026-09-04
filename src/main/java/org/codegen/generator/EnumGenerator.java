package org.codegen.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codegen.exception.GenerationException;
import org.codegen.spec.EnumDefinition;
import org.codegen.template.TemplateProcessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class EnumGenerator {
    private final TemplateProcessor templateProcessor;

    private static final String ENUM_TEMPLATE_FILE_NAME = "enum.ftl";

    public void generate(List<EnumDefinition> enums, Path projectPath, String basePackage) {
        if (enums.isEmpty()) {
            return;
        }

        Path generationPath = projectPath
                .resolve("src/main/java")
                .resolve(basePackage.replace(".", "/"))
                .resolve("enums");

        try {
            Files.createDirectories(generationPath);
        } catch (IOException e) {
            throw new GenerationException(
                    "Failed to create directory '" + generationPath + "' for enums",
                    e);
        }

        log.debug("Generating {} enums into package '{}'",
                enums.size(),
                basePackage + ".enums");

        for (EnumDefinition enumDefinition : enums) {
            Map<String, Object> data = Map.of(
                    "enum", enumDefinition,
                    "basePackage", basePackage
            );

            Path output = generationPath.resolve(enumDefinition.name() + ".java");

            log.debug("Generating enum '{}' to '{}'",
                    enumDefinition.name(),
                    output);

            templateProcessor.process(
                    ENUM_TEMPLATE_FILE_NAME,
                    data,
                    output
            );
        }
    }
}