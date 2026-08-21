package org.codegen.spec;

import java.util.Map;

public record EntityDefinition(
        String name,
        Map<String, String> fields
) {}
