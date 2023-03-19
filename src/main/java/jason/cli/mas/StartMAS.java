package jason.cli.mas;

import jason.architecture.MindInspectorWeb;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.ArrayList;


@Command(
    name = "start",
    description = "starts a new (empty) MAS"
)
public class StartMAS implements Runnable {
    
    static private int masCount = 1;

    @Parameters(paramLabel = "<mas name>", defaultValue = "",
               arity = "0..1",
               description = "MAS unique identification")
    String masName;

    @Option(names = { "--console" }, negatable = true, description = "output will be sent to the console instead of a GUI")
    boolean console;

    @Option(names = "--mas2j", 
                paramLabel = "<mas2j file>", defaultValue = "",
                description = "mas2j file describing the initial state of the MAS")
    String mas2j;

    @CommandLine.ParentCommand
    protected MAS parent;

    @Override
    public void run() {
        if (RunningMASs.hasLocalRunningMAS()) {
            parent.parent.errorMsg("this process can run only one MAS, that currently is "+RunningMASs.getLocalRunningMAS().getName());
            parent.parent.errorMsg("open another terminal for the new MAS, or stop the current one with 'mas stop'");
            return;
        }
        var args = new ArrayList<String>();

        var existing = RunningMASs.getAllRunningMAS().keySet();
        if (masName.isEmpty()) {
            masName = "mas_" + (masCount++);
            while (existing.contains(masName)) {
                masName = "mas_" + (masCount++);
            }
        } else if (existing.contains(masName)) {
            parent.parent.errorMsg("there is an MAS named "+masName+" already running, select another name");
            return;
        }
        
        if (!mas2j.isEmpty()) {
            args.add(mas2j);
        } else {
            args.add("--empty-mas");
        }

        if (console) {
            args.add("--log-conf");
            args.add("$jasonJar/templates/console-logging.properties");
        }

        try {
            parent.parent.println("starting MAS "+masName+" ...");

            var r = new CLILocalMAS();
            r.init(args.toArray(new String[args.size()]), masName);
            new Thread(r).start();

            if (!parent.parent.hasCmdServer()) {
                parent.parent.startCmdServer();
            }
            RunningMASs.storeRunningMAS(parent.parent.getCmdServer().getAddress());

            showInfo(masName, console);
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    protected void showInfo(String masName, boolean console) {
        parent.parent.println("MAS "+masName+" is running ("+parent.parent.getCmdServer().getAddress()+")");
        MindInspectorWeb.get(); // to start http server for jason
//        if (console && parent.parent.isTerminal()) {
//            parent.parent.println("\nsince the output of the MAS will be shown here, you may open another terminal to enter more commands");
////            parent.parent.println("     jason <commands>");
////            parent.parent.println("example:");
////            parent.parent.println("     jason agent create bob");
//        }
    }
}

