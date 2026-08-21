package org.codegen.spec;

import java.util.List;

public record ProjectSpecification(
        ProjectMetadata project,
        GeneratorConfiguration generator,
        List<MicroserviceDefiniton> services
) {}