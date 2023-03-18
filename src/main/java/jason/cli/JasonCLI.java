package jason.cli;

import jason.cli.mas.MAS;
import jason.util.Config;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;

@Command(name = "jason", 
    // version = "1.0", 
    versionProvider = jason.cli.VersionProvider.class,
    mixinStandardHelpOptions = true,
    subcommands = {
        MAS.class
    }
)
public class JasonCLI { //implements Runnable {

    // @Parameters(paramLabel = "<mas2j file>", 
    //             defaultValue = "",
    //             description = "mas2j file describing the initial state of the MAS")
    // private String mas2j;

    // @Override
    // public void run() {
    //     if (mas2j.isEmpty()) {
    //         return;
    //     }

    //     System.out.println("Starting MAS from "+mas2j);
    // }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new JasonCLI()).execute(args);
        System.exit(exitCode);
    }
}

class VersionProvider implements IVersionProvider {
    public String[] getVersion() throws Exception {
        return new String[] { "Jason " + Config.get().getJasonVersion() };
    }
}