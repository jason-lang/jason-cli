package jason.cli.agent;

import jason.architecture.AgArch;
import jason.asSemantics.IntendedMeans;
import jason.asSemantics.Intention;
import jason.asSemantics.Option;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Plan;
import jason.asSyntax.PlanBody;
import jason.asSyntax.Trigger;
import jason.cli.mas.RunningMASs;
import jason.infra.repl.print_unifier;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.List;


@Command(
    name = "run-as",
    description = "execute commands for an agent"
)
public class RunAsAgent implements Runnable {

    @CommandLine.Parameters(paramLabel = "<agent name>", defaultValue = "",
            arity = "1",
            description = "agent unique identification")
    String agName;

    @CommandLine.Parameters(hidden = true)  // "hidden": don't show this parameter in usage help message
    List<String> allParameters; // no "index" attribute: captures _all_ arguments

    @CommandLine.ParentCommand
    protected Agent parent;

    @Override
    public void run() {
        if (!RunningMASs.hasLocalRunningMAS()) {
            parent.parent.errorMsg("no running MAS, so, no agent to run.");
            return;
        }
        if (agName.isEmpty()) {
            parent.parent.errorMsg("the name of the agent should be informed, e.g., 'agent run-as bob { .print(oi) }'.");
            return;
        }
        if (RunningMASs.getLocalRunningMAS().getAg(agName) == null) {
            parent.parent.errorMsg("the agent with name " + agName + " is not running!");
            return;
        }

        String code = "";
        if (allParameters.size()>0) {
            var last = allParameters.get( allParameters.size()-1).trim();
            if (last.startsWith("{")) {
                code =  last.substring(1,last.length()-1).trim();
            }
        }

        if (!code.isEmpty()) {
            execCmd(code, RunningMASs.getLocalRunningMAS().getAg(agName));
        } else {
            parent.parent.errorMsg("no code to execute was informed. E.g., 'agent run-as bob { .print(oi) }' ");
        }
    }

    void execCmd(String sCmd, AgArch ag) {
        try {
            if (sCmd.endsWith("."))
                sCmd = sCmd.substring(0,sCmd.length()-1);

            sCmd += ";"+ print_unifier.class.getName();
            PlanBody  lCmd = ASSyntax.parsePlanBody(sCmd);
            Trigger   te   = ASSyntax.parseTrigger("+!run_repl_expr");
            Intention i    = new Intention();
            i.push(new IntendedMeans(
                    new Option(
                            new Plan(null,te,null,lCmd),
                            new Unifier()),
                    te));
            ag.getTS().getC().addRunningIntention(i);
            ag.getTS().getAgArch().wake();
        } catch (Exception e) {
            parent.parent.errorMsg("Error parsing "+sCmd+"\n"+e);
        }
    }

}

