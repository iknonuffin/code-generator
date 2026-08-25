import org.codegen.app.Application;

void main(String[] args) {
    Application app = Application.create();

    int exitCode = app.cli().run(args);

    System.exit(exitCode);
}
