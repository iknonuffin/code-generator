package org.codegen.parser;

import org.codegen.spec.ProjectSpecification;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.yaml.YAMLMapper;

import java.nio.file.Path;

public class YamlParser {
    private final ObjectMapper objectMapper =  new YAMLMapper();

    public ProjectSpecification parse(Path spec) {
        return objectMapper.readValue(spec, ProjectSpecification.class);
    }
}
