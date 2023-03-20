package jason.cli.agent;

import jason.cli.JasonCLI;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "agent",
    description = "commands to handle agents",
    subcommands = { StartAgent.class, StopAgent.class, ListAgents.class, RunAsAgent.class, BeliefsAgent.class, PlansAgent.class, StatusAgent.class },
    synopsisSubcommandLabel = "(start | stop | list | run-as | beliefs | plans | status)"
)
public class Agent {

    @CommandLine.ParentCommand
    protected JasonCLI parent;

}

