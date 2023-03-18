package jason.cli.mas;

import java.net.UnknownHostException;
import jason.JasonException;
import jason.architecture.MindInspectorWeb;
import jason.infra.local.RunLocalMAS;

public class CLILocalMAS extends RunLocalMAS {
    CommandServer server;

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
        var addr = "";
        try {
            addr = server.getAddress();
        } catch (UnknownHostException e) {
            addr = "error getting addr "+e.getMessage();
        }
        System.out.println("MAS "+project.getSocName()+" is running ("+addr+")");
        MindInspectorWeb.get(); // to start http server for jason
        if (console) {
            System.out.println("\nopen another terminal to enter more commands");
            System.out.println("     jason --mas="+project.getSocName()+" <commands>");
            System.out.println("example:");
            System.out.println("     jason --mas="+project.getSocName()+" agent create bob");
        }
    }

    protected void startCommandServer(String masName) throws UnknownHostException {
        server = new CommandServer(this);
        // var port = 
        server.configure();
        // var addr = InetAddress.getLocalHost().getHostAddress()+":"+port;
        // System.out.println("command server is running on port "+addr);
        //File f = File.createTempFile(logPropFile, defaultProjectFileName)
        new Thread(server).start();
        server.storeAddr(masName);
    }

    protected void waitEnd() {
        super.waitEnd();
        server.stop();
        super.finish(0, true, 0);
    }
}
