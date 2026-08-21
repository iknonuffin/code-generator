package org.codegen.parser;

import org.codegen.spec.ProjectSpecification;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class YamlParser {
    private final Yaml yaml =  new Yaml();

    public ProjectSpecification parse(Path spec) throws IOException {

        try (InputStream specStream = Files.newInputStream(spec)) {
            return yaml.loadAs(specStream, ProjectSpecification.class);
        }
    }
}
