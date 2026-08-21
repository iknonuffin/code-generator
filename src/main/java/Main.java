import freemarker.template.TemplateException;
import org.codegen.app.Application;

void main(String[] args) throws TemplateException, IOException, InterruptedException {
    Application app = Application.create();

    int exitCode = app.run(args);

    System.exit(exitCode);
}
