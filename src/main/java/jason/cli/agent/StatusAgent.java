package jason.cli.agent;

import jason.cli.mas.RunningMASs;
import picocli.CommandLine;
import picocli.CommandLine.Command;


@Command(
    name = "status",
    description = "show the status of an agent"
)
public class StatusAgent implements Runnable {

    @CommandLine.Parameters(paramLabel = "<agent name>", defaultValue = "",
            arity = "1",
            description = "agent unique identification")
    String agName;

    @CommandLine.Option(names = { "--mas-name" }, paramLabel = "<mas name>", defaultValue = "", description = "MAS unique identification")
    String masName;

    @CommandLine.ParentCommand
    protected Agent parent;

    @Override
    public void run() {
        if (!RunningMASs.isRunningMAS(masName)) {
            parent.parent.errorMsg("no running MAS, so, no agent to inspect.");
            return;
        }
        if (agName.isEmpty()) {
            parent.parent.errorMsg("the name of the agent should be informed, e.g., 'agent status bob'.");
            return;
        }
        if (!RunningMASs.hasAgent(masName, agName)) {
            parent.parent.errorMsg("the agent with name " + agName + " is not running!");
            return;
        }

        var status = RunningMASs.getRTS(masName).getAgStatus(agName);
        for (var k : status.keySet()) {
            parent.parent.println("    " + k + ": " + status.get(k));
        }
    }
}

