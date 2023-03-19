package jason.cli.agent;

import jason.cli.mas.MAS;
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

    @Override
    public void run() {
        var all = RunningMASs.getLocalRunningMAS();
        //parent.parent.println("agents:");
        for  (var ag: all.getAgs().keySet()) {
            parent.parent.println("    "+ag);
        }
    }
}

