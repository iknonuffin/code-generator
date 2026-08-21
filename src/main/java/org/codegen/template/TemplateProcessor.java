package org.codegen.template;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class TemplateProcessor {
    private final Configuration freemarkerCfg;

    public TemplateProcessor() {
        freemarkerCfg = new Configuration(Configuration.VERSION_2_3_34);

        freemarkerCfg.setClassForTemplateLoading(
                TemplateProcessor.class, "/templates"
        );

        freemarkerCfg.setDefaultEncoding("UTF-8");
    }

    public void process(String templateName, Object data, Path output)
            throws IOException, TemplateException {
        Template template = freemarkerCfg.getTemplate(templateName);

        try (Writer writer = Files.newBufferedWriter(output)) {
            template.process(data, writer);
        }
    }
}
