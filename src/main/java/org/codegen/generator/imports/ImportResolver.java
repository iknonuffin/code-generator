package org.codegen.generator.imports;

import org.codegen.generator.GenerationContext;

import java.util.*;

import static java.util.Map.entry;

public class ImportResolver {

    private static final Map<String, String> TYPE_IMPORTS = Map.ofEntries(
            entry("UUID", "java.util.UUID"),
            entry("BigDecimal", "java.math.BigDecimal"),

            entry("Instant", "java.time.Instant"),
            entry("LocalDate", "java.time.LocalDate"),
            entry("LocalDateTime", "java.time.LocalDateTime"),

            entry("List", "java.util.List"),
            entry("Map", "java.util.Map"),
            entry("Set", "java.util.Set"),

            entry("Entity", "jakarta.persistence.Entity"),
            entry("Id", "jakarta.persistence.Id"),
            entry("GeneratedValue", "jakarta.persistence.GeneratedValue"),
            entry("GenerationType", "jakarta.persistence.GenerationType"),

            entry("Getter", "lombok.Getter"),
            entry("Setter", "lombok.Setter"),
            entry("NoArgsConstructor", "lombok.NoArgsConstructor")
    );

    public ResolvedImports resolve(Set<String> types, GenerationContext context) {
        Set<String> otherImports = new TreeSet<>();
        Set<String> javaImports = new TreeSet<>();

        for (String type : types) {
            for (String typeName : extractTypeNames(type)) {
                String importName = resolveType(typeName, context);

                if (importName == null) {
                    continue;
                }

                if (importName.startsWith("java.")) {
                    javaImports.add(importName);
                } else {
                    otherImports.add(importName);
                }
            }
        }

        return new ResolvedImports(
                List.copyOf(otherImports),
                List.copyOf(javaImports)
        );
    }

    private String resolveType(String type, GenerationContext context) {
        String importName = TYPE_IMPORTS.get(type);

        if (importName != null) {
            return importName;
        }

        String packageName = context.typePackages().get(type);

        if (packageName == null
                || packageName.equals(context.currentPackage())) {
            return null;
        }

        return packageName + "." + type;
    }

    private Set<String> extractTypeNames(String type) {
        Set<String> typeNames = new HashSet<>();
        StringBuilder current = new StringBuilder();

        for (char c : type.toCharArray()) {
            if (current.isEmpty()) {
                if (Character.isJavaIdentifierStart(c)) {
                    current.append(c);
                }
            } else if (Character.isJavaIdentifierPart(c)) {
                current.append(c);
            } else {
                typeNames.add(current.toString());
                current.setLength(0);
            }
        }

        if (!current.isEmpty()) {
            typeNames.add(current.toString());
        }

        return typeNames;
    }
}