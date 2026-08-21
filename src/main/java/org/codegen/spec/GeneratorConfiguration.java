package org.codegen.spec;

public record GeneratorConfiguration(
        boolean generateControllers,
        boolean generateServices,
        boolean generateRepositories,
        boolean generateDtos,
        boolean generateEvents
) {}
