package jason.cli.app;

import jason.asSemantics.Intention;
import jason.cli.agent.Agent;
import jason.cli.mas.RunningMASs;
import jason.mas2j.MAS2JProject;
import jason.mas2j.parser.mas2j;
import jason.util.Config;
import jason.util.CreateNewProject;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;
import java.io.FileReader;
import java.rmi.RemoteException;


@Command(
    name = "compile",
    description = "compiles the java classes of an application"
)
public class Compile implements Runnable {

    @CommandLine.Parameters(paramLabel = "<MAS name>", defaultValue = "",
            arity = "0..1")
    String masName;

    @CommandLine.ParentCommand
    protected Application parent;

    @Override
    public void run() {
        try {
            File file = getProjectFile(masName);
            if (file == null) {
                // create a "temporary" project file
                file = createTempProjectFile(masName);
            }
            if (file == null) {
                parent.parent.errorMsg("can not find a file for the project!");
                return;
            }

            // parsing
            var project = new MAS2JProject();
            var parser = new mas2j(new FileReader(file)); //new java.io.FileInputStream(name));
            project = parser.mas();
            if (Config.get().getJasonJar() == null) {
                Config.get().setShowFixMsgs(false);
                Config.get().fix();
            }
            project.setProjectFile(file);
            project.setDirectory(file.getAbsoluteFile().getParentFile().getAbsolutePath());
            parent.parent.println("file "+file+" parsed successfully!");

            // running task compile
            var launcher = project.getInfrastructureFactory().createMASLauncher();
            launcher.setProject(project);
            launcher.writeScripts(false, false);
            launcher.setTask("compile");
            launcher.run();
        } catch(Exception e) {
            System.err.println("parsing errors found... \n" + e);
        }
    }

    private File getProjectFile(String masName) {
        var f = new File(masName+".mas2j");
        if (f.exists())
            return f;

        // find a .mas2j file in current directory
        for (var nf: new File(".").listFiles()) {
            if (nf.getName().endsWith(".mas2j"))
                return nf;
        }

        return null;
    }

    private File createTempProjectFile(String masName) {
        var f = new File( ".temp.mas2j");
        CreateNewProject.copyFile("temp", "project", f, true);
        return f;
    }


}

