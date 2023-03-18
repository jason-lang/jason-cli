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
public class JasonCLI { 
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