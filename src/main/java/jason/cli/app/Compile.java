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
//            connection.model(GradleProject.class) // *** does not work
//                    .setStandardOutput(System.out)
//                    .withArguments("--build-file", buildFile.getAbsolutePath());
            getGradleBuild(connection)
                    .forTasks("compileJava")
                    .run();
        } catch(Exception e) {
            parent.parent.errorMsg("error compiling");
        }
    }
}

