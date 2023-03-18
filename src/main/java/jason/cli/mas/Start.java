package jason.cli.mas;

import java.util.ArrayList;

import jason.JasonException;
import jason.architecture.MindInspectorWeb;
import jason.infra.local.RunLocalMAS;
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
            r.showInfo(console);
            r.waitEnd();
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
}

class CLILocalMAS extends RunLocalMAS {
    protected int initStart(String[] args, String masName) throws JasonException {
        runner = this;
        var r = super.init(args);
        project.setSocName(masName);
        registerMBean();
        create();
        start();
        return r;
    }

    protected void showInfo(boolean console) {
        System.out.println("MAS "+project.getSocName()+" is running");
        MindInspectorWeb.get(); // to start http server for jason

        if (console) {
            System.out.println("open another terminal to enter more commands");
        }
    }

    protected void waitEnd() {
        super.waitEnd();
        super.finish(0, true, 0);
    }
}
