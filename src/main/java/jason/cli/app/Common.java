package jason.cli.app;

import jason.util.CreateNewProject;

import java.io.File;


public class Common {
    protected File getProjectFile(String masName) {
        if (!masName.endsWith(".mas2j"))
            masName += ".temp.mas2j";

        var f = new File(masName);
        if (f.exists())
            return f;

        // find a .mas2j file in current directory
        for (var nf: new File(".").listFiles()) {
            if (nf.getName().endsWith(".mas2j"))
                return nf;
        }

        return null;
    }
    protected File createTempProjectFile(String masName) {
        var f = new File( ".temp.mas2j");
        CreateNewProject.copyFile("temp", "project", f, true);
        return f;
    }
}

