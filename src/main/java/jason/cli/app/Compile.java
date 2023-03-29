package jason.cli.app;

import picocli.CommandLine;
import picocli.CommandLine.Command;


@Command(
    name = "compile",
    description = "compiles the java classes of an application"
)
public class Compile extends Common implements Runnable {

    @CommandLine.ParentCommand
    protected Application parent;

    @Override
    public void run() {
        var buildFile = ensureGradleFile("");

        try (var connection = getGradleConnection(buildFile.getAbsoluteFile().getParentFile())) {
            connection.newBuild()
                    .forTasks("compileJava")
                    .run();
        } catch(Exception e) {
            parent.parent.errorMsg("error compiling:\n" + e);
        }
    }
}

