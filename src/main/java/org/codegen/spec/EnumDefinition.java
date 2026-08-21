package org.codegen.spec;

import java.util.List;

public record EnumDefinition(
        String name,
        List<String> values
) {}
