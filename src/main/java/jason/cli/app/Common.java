package jason.cli.app;

import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import java.io.File;
import java.io.IOException;


public class Common {
    protected File getProjectFile(String masName) {
        if (!masName.isEmpty()) {
            if (!masName.endsWith(".mas2j"))
                masName += ".mas2j";

            var f = new File(masName);
            if (f.exists())
                return f;
        }

        // find a .mas2j file in current directory
        for (var nf: new File(".").listFiles()) {
            if (nf.getName().endsWith(".mas2j"))
                return nf;
        }

        return null;
    }
//    protected File createTempProjectFile(String masName) {
//        var f = new File( ".temp.mas2j");
//        CreateNewProject.copyFile("temp", "project", f, true);
//        return f;
//    }

    ProjectConnection getGradleConnection(File path) {
        return GradleConnector
                .newConnector()
                .forProjectDirectory(path)
                .connect();
    }

    File ensureGradleFile(String masName) {
        var masFile = getProjectFile(masName);
        if (masFile == null)
            masFile = new File(".");
        var projectDir = masFile.getAbsoluteFile().getParentFile();
        try {
            projectDir = projectDir.getCanonicalFile();
        } catch (IOException e) {  }
        var f = new File(projectDir+"/build.gradle");
        if (f.exists())
            return f;

        if (masName.isEmpty()) {
            // masName based on directory name
            masName = projectDir.getName();
        }
        if (masName.endsWith(".mas2j")) {
            masName = masName.substring(0,masName.length()-6);
        }

        // create a temp file
        Create.copyFile(masName, "build.gradle", "", f, true);
        return f;
    }
}

