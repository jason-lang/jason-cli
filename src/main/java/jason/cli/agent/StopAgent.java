package jason.cli.agent;

import jason.cli.mas.RunningMASs;
import jason.runtime.RuntimeServicesFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.ArrayList;
import java.util.List;


@Command(
    name = "stop",
    description = "kills an agent"
)
public class StopAgent implements Runnable {
    
    @Parameters(paramLabel = "<agent name>", defaultValue = "",
               arity = "1",
               description = "agent unique identification")
    String agName;
    @CommandLine.ParentCommand
    protected Agent parent;

    @Override
    public void run() {
        if (!RunningMASs.hasLocalRunningMAS()) {
            parent.parent.errorMsg("no running MAS, so, no agent to kill.");
            return;
        }
        if (agName.isEmpty()) {
            parent.parent.errorMsg("the name of the agent should be informed, e.g., 'agent stop bob'.");
            return;
        }
        if (RunningMASs.getLocalRunningMAS().getAg(agName) == null) {
            parent.parent.errorMsg("the agent with name " + agName + " is not running!");
            return;
        }


        var rt =  RuntimeServicesFactory.get();
        if (!rt.getAgentsNames().contains(agName)) {
            parent.parent.errorMsg("there is no agent named "+agName+" running.");
            return;
        }
        rt.killAgent(agName, null, 0);
    }
}

