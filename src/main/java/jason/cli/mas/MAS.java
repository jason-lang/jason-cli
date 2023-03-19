package jason.cli.mas;

import java.io.File;
import java.io.FileReader;
import java.net.Socket;
import java.util.Properties;

import jason.cli.JasonCLI;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "mas",
    description = "commands to handle running Multi-Agent Systems",
    subcommands = { Start.class, Stop.class, List.class }
)
public class MAS {

    @CommandLine.ParentCommand
    protected JasonCLI parent;

}

