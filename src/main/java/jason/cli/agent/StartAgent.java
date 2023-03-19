package jason.cli.agent;

import jason.cli.mas.RunningMASs;
import jason.runtime.RuntimeServicesFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


@Command(
    name = "start",
    description = "starts a new (empty) agent"
)
public class StartAgent implements Runnable {
    
    @Parameters(paramLabel = "<agent name>", defaultValue = "",
               arity = "1",
               description = "agent unique identification")
    String agName;

    @CommandLine.Option(names = { "--instances" }, defaultValue = "1", description = "how many agents should be created")
    int instances;

    @CommandLine.Option(names = { "--source" }, defaultValue = "", description = "file (or URL) for the source code of the agent.")
    String sourceFile;

    @Parameters(hidden = true)  // "hidden": don't show this parameter in usage help message
    List<String> allParameters; // no "index" attribute: captures _all_ arguments

    @CommandLine.ParentCommand
    protected Agent parent;

    @Override
    public void run() {
        parent.parent.println("all="+allParameters + " agname "+agName);

        if (!RunningMASs.hasLocalRunningMAS()) {
            parent.parent.errorMsg("no running MAS, create one with 'mas start'.");
            return;
        }
        if (agName.isEmpty()) {
            parent.parent.errorMsg("the name of the new agent should be informed, e.g., 'agent start bob'.");
            return;
        }

        String code = null;
        if (allParameters.size()>0) {
            var last = allParameters.get( allParameters.size()-1).trim();
            if (last.startsWith("{")) {
                code =  last.substring(1,last.length()-1).trim();
            }
            parent.parent.println("*** code = "+code);
        }

        var ags = new ArrayList<String>();
        var rt =  RuntimeServicesFactory.get();
        try {
            for (int i=0; i<instances; i++) {
                var n = agName;
                if (instances>1)
                    n = agName + i;
                ags.add(rt.createAgent( n, sourceFile, null, null, null, null, null));
            }
        } catch (Exception e) {
            parent.parent.errorMsg("error creating agent: "+e.getMessage());
            return;
        }

        for (String a: ags) {
            // load code informed as parameter
            if (code != null && !code.isEmpty()) {
                try {
                    var ag = RunningMASs.getLocalRunningMAS().getAg(a).getTS().getAg();
                    ag.parseAS(
                            new StringReader(code),
                            "from-JasonCLI-parameter"
                    );
                    ag.addInitialBelsInBB();
                    ag.addInitialGoalsInTS();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            rt.startAgent(a);
            parent.parent.println("agent " + a + " started.");
        }
    }
}
