package org.codegen.generator;

import lombok.RequiredArgsConstructor;
import org.codegen.spec.EntityDefinition;
import org.codegen.template.TemplateProcessor;

import java.nio.file.Path;

@RequiredArgsConstructor
public class EntityGenerator {
    private final TemplateProcessor templateProcessor;

    public void generate(EntityDefinition entityDefinition, Path generationPath) {

    }
}
