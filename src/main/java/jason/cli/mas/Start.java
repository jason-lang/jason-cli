package jason.cli.mas;

import java.net.UnknownHostException;
import java.util.ArrayList;

import jason.architecture.MindInspectorWeb;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;


@Command(
    name = "start",
    description = "starts a new (empty) MAS"
)
public class Start implements Runnable {
    
    static private int masCount = 1;

    @Parameters(paramLabel = "<mas name>", defaultValue = "",
               description = "MAS unique identification")
    String masName;

    @Option(names = { "--console" }, negatable = true, description = "output will be sent to the console instead of a GUI")
    boolean console;

    @Option(names = "--mas2j", 
                paramLabel = "<mas2j file>", defaultValue = "",
                description = "mas2j file describing the initial state of the MAS")
    String mas2j;


    @Override
    public void run() {
        var args = new ArrayList<String>();

        if (masName.isEmpty())
            masName = "mas_" + (masCount++);
        
        var mas2jMsg = "";
        if (!mas2j.isEmpty()) {
            mas2jMsg = " from " + mas2j;
            args.add(mas2j);
        } else {
            args.add("--empty-mas");
        }

        if (console) {
            args.add("--log-conf");
            args.add("$jasonJar/templates/console-logging.properties");
        }

        try {
            var r = new CLILocalMAS();
            r.initStart(args.toArray(new String[args.size()]), masName);
            CommandServer s = r.startCommandServer(masName);
            showInfo(s, masName, console);
            r.waitEnd();
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    protected void showInfo(CommandServer s, String masName, boolean console) {
        var addr = "";
        try {
            addr = s.getAddress();
        } catch (UnknownHostException e) {
            addr = "error getting addr "+e.getMessage();
        }
        System.out.println("MAS "+masName+" is running ("+addr+")");
        MindInspectorWeb.get(); // to start http server for jason
        if (console) {
            System.out.println("\nopen another terminal to enter more commands");
            System.out.println("     jason <commands>");
            System.out.println("example:");
            System.out.println("     jason agent create bob");
        }
    }


}

