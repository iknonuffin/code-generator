package org.codegen.generator;

import java.util.Map;

public record GenerationContext(
        String currentPackage,
        Map<String, String> typePackages
) {}
