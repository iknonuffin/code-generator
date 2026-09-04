package org.codegen.generator.imports;

import java.util.List;

public record ResolvedImports(
        List<String> other,
        List<String> java
) {}
