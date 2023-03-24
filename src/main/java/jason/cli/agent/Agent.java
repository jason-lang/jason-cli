package jason.cli.agent;

import jason.cli.JasonCommands;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "agent",
    description = "commands to handle agents",
    subcommands = { StartAgent.class, StopAgent.class, ListAgents.class, RunAsAgent.class, MindAgent.class, StatusAgent.class },
    synopsisSubcommandLabel = "(start | stop | list | run-as | mind | status)"
)
public class Agent {

    @CommandLine.ParentCommand
    protected JasonCommands parent;

}

