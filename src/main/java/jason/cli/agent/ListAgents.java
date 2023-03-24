package jason.cli.agent;

import jason.cli.mas.RunningMASs;
import picocli.CommandLine;
import picocli.CommandLine.Command;


@Command(
    name = "list",
    description = "list running agents"
)
public class ListAgents implements Runnable {

    @CommandLine.ParentCommand
    protected Agent parent;

    @CommandLine.Option(names = { "--mas-name" }, paramLabel = "<mas name>", defaultValue = "", description = "MAS unique identification")
    String masName;

    @Override
    public void run() {
        if (!RunningMASs.isRunningMAS(masName))
            return;

        for  (var ag: RunningMASs.getRTS(masName).getAgentsNames()) {
            parent.parent.println("    "+ag);
        }
    }
}

