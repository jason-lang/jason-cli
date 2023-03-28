package jason.cli.app;

import jason.cli.JasonCommands;
import jason.cli.agent.*;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "app",
    description = "commands to handle aplications",
    subcommands = { Compile.class },
    synopsisSubcommandLabel = "(create | compile | add-agent | set-env | add-gradle )"
)
public class Application {

    @CommandLine.ParentCommand
    protected JasonCommands parent;

}

