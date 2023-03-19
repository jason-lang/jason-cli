package jason.cli.agent;

import jason.cli.mas.RunningMASs;
import picocli.CommandLine;
import picocli.CommandLine.Command;


@Command(
    name = "plans",
    description = "list the plans of an agent"
)
public class PlansAgent implements Runnable {

    @CommandLine.Parameters(paramLabel = "<agent name>", defaultValue = "",
            arity = "1",
            description = "agent unique identification")
    String agName;

    @CommandLine.ParentCommand
    protected Agent parent;

    @Override
    public void run() {
        if (!RunningMASs.hasLocalRunningMAS()) {
            parent.parent.errorMsg("no running MAS, so, no agent to inspect.");
            return;
        }
        if (agName.isEmpty()) {
            parent.parent.errorMsg("the name of the agent should be informed, e.g., 'agent beliefs bob'.");
            return;
        }
        //parent.parent.println("plans of "+agName+":");
        var ag = RunningMASs.getLocalRunningMAS().getAg(agName).getTS().getAg();
        parent.parent.println( ag.getPL().getAsTxt(false).trim());
    }
}

