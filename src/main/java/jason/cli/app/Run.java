package jason.cli.app;

import java.io.File;


public class Run extends  Common {

    public void run(String mas2j) {
        // use gradle to run
        var mas2jFile = new File(mas2j);
        if (!mas2jFile.exists()) {
            System.err.println("the application file "+mas2j+" does not exist!");
            return;
        }
        var buildFile = ensureGradleFile( mas2j );

        try (var connection = getGradleConnection(buildFile.getAbsoluteFile().getParentFile())) {
            getGradleBuild(connection, false, true)
                    .forTasks("run")
                    .run();
        } catch (Exception e) {
            System.err.println("Error running 'gradle run'");
        }
    }
}

