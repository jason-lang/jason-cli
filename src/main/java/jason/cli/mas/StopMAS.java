package jason.cli.mas;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;


@Command(
    name = "stop",
    description = "stops a MAS"
)
public class StopMAS implements Runnable {
    
    @Option(names = { "--mas-name" }, paramLabel = "<mas name>", defaultValue = "", 
               description = "MAS unique identification")
    String masName;

    @Option(names = { "--exit" }, description = "stops the MAS and terminates the process")
    boolean exit;

    @ParentCommand
    private MAS parent;

    @Override
    public void run() {
        if (masName.isEmpty() && RunningMASs.hasLocalRunningMAS() ||
                RunningMASs.hasLocalRunningMAS() && RunningMASs.getLocalRunningMAS().getName().equals(masName)) {
            // stop the local running MAS
            var localMAS = RunningMASs.getLocalRunningMAS();
            if (exit || !parent.parent.isTerminal()) {
                localMAS.finish();
                parent.parent.println(localMAS.getName()+" stopped");
                System.exit(0);
            } else {
                localMAS.finish(0, false, 0);
                parent.parent.println(localMAS.getName()+" stopped");
            }
            return;
        } else {
            var s = RunningMASs.getRemoteRunningMAS(masName);
            if (s != null) {
                parent.parent.println("(trying to) stop MAS "+masName+" at "+s);

                try (var out = new PrintWriter(s.getOutputStream(), true)) {
                    if (exit)
                        out.println("exit");
                    else
                        out.println("mas stop");

                    var in  = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    parent.parent.println(in.readLine());
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        parent.parent.errorMsg("could not find an MAS to stop, run 'mas list' to see the list of running MAS.");
    }


}

