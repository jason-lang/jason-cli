package jason.cli.mas;

import picocli.CommandLine;
import picocli.CommandLine.Command;


@Command(
    name = "list",
    description = "list current running MAS"
)
public class List implements Runnable {

    @CommandLine.ParentCommand
    protected MAS parent;

    @Override
    public void run() {
        var all = RunningMASs.getAllRunningMAS();
        for  (var mas: all.keySet()) {
            parent.parent.println(mas+" @"+all.get(mas));
        }
    }
}

