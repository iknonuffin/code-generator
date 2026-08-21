package org.codegen.spec;

import java.util.List;

public record MicroserviceDefiniton(
        String name,
        List<EnumDefinition> enums,
        List<EntityDefinition> entities
) {}
