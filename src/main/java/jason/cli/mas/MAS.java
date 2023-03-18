package jason.cli.mas;

import picocli.CommandLine.Command;

@Command(
    name = "mas",
    description = "commands to change the state of the Multi-Agent System",
    subcommands = { Start.class, Stop.class }
)
public class MAS { 
}

