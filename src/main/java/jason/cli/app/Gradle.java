package jason.cli.app;

import picocli.CommandLine;
import picocli.CommandLine.Command;


@Command(
    name = "add-gradle",
    description = "adds a Gradle script for the application"
)
public class Gradle extends Common implements Runnable {

    @CommandLine.ParentCommand
    protected Application parent;

    @Override
    public void run() {
        try {
            var gradleFile = ensureGradleFile( "" );
            try (var connection = getGradleConnection(gradleFile.getAbsoluteFile().getParentFile())) {
                getGradleBuild(connection)
                        .forTasks("wrapper")
                        .run();
            } catch (Exception e) {
                System.err.println("Error running gradle run "+e);
            }
            parent.parent.println("\n\nfile "+gradleFile+" created.");
            parent.parent.println("\nyou can execute your application with:");
            parent.parent.println("    ./gradlew run");
        } catch(Exception e) {
            parent.parent.errorMsg("error adding Gradle");
        }
    }
}

