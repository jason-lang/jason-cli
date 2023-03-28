package jason.cli.app;

import org.gradle.tooling.GradleConnector;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.*;


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
            var file = getProjectFile("");
            if (file == null) {
                parent.parent.errorMsg("can not find a .mas2j file in the current directory!");
                return;
            }

            var gradleFile = new File("build.gradle");
            if (gradleFile.exists()) {
                parent.parent.errorMsg("a file '"+gradleFile+"' exists already and can not be replaced.");
                return;
            }

            Create.copyFile(file.getName(), "build.gradle", "", gradleFile, true);
            parent.parent.println("file "+gradleFile+" created.");
            runGradleWrapper(file.getParentFile());
            parent.parent.println("\nyou can execute your application with:");
            parent.parent.println("    ./gradlew run");
        } catch(Exception e) {
            parent.parent.errorMsg("error adding Gradle:\n" + e);
        }
    }

    void runGradleWrapper(File path) {
        try {
            var connection = GradleConnector
                    .newConnector()
                    .forProjectDirectory(path)
                    .connect();
            connection.newBuild()
                    .forTasks("wrapper")
                    .run();
            connection.close();
        } catch (Exception e) {
            parent.parent.errorMsg("Error creating gradle wrapper "+e);
        }
    }
}

