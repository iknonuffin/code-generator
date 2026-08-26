package org.codegen.template;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.codegen.exception.GenerationException;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Slf4j
public class TemplateProcessor {
    private final Configuration freemarkerCfg;

    public TemplateProcessor() {
        freemarkerCfg = new Configuration(Configuration.VERSION_2_3_34);

        freemarkerCfg.setClassForTemplateLoading(
                TemplateProcessor.class, "/templates"
        );

        freemarkerCfg.setDefaultEncoding("UTF-8");
    }

    public void process(String templateName, Map<String, Object> dataModel, Path output) {
        log.debug("Rendering template '{}' to '{}'", templateName, output);

        try (Writer writer = Files.newBufferedWriter(output)) {
            Template template = freemarkerCfg.getTemplate(templateName);
            template.process(dataModel, writer);
        } catch (IOException | TemplateException e) {
            String msg = "Failed to render template '" + templateName + "' to '" + output + "'";

            throw new GenerationException(msg, e);
        }
    }
}
