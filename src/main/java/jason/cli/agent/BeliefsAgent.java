package jason.cli.agent;

import jason.cli.mas.RunningMASs;
import picocli.CommandLine;
import picocli.CommandLine.Command;


@Command(
    name = "beliefs",
    description = "list the belief base of an agent"
)
public class BeliefsAgent implements Runnable {

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

        //parent.parent.println("beliefs of "+agName+":");

        var ag = RunningMASs.getLocalRunningMAS().getAg(agName).getTS().getAg();
        var out = new StringBuilder();
        for (var ns: ag.getBB().getNameSpaces()) {
            if (ns.toString().equals("kqml"))
                continue;
            var first = true;
            for (var b: ag.getBB()) {
                if (b.getNS().equals(ns)) {
                    if (first) {
                        if (!ns.toString().equals("default"))
                            out.append(ns+"::\n");
                        first = false;
                    }
                    // remove namespace
                    var bs = b.toString();
                    var p  = bs.indexOf("::");
                    if (p>0)
                        bs = bs.substring(p+2);
                    out.append("    "+bs+"\n");
                }
            }
        }
        parent.parent.println( out.toString());
    }
}

