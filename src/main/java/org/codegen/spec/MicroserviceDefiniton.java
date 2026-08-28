package org.codegen.spec;

import java.util.List;

public record MicroserviceDefiniton(
        String name,
        String packageName,
        List<EnumDefinition> enums,
        List<EntityDefinition> entities
) {}
