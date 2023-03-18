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

    protected CommandServer startCommandServer(String masName) throws UnknownHostException {
        server = new CommandServer(this);
        // var port = 
        server.configure();
        // var addr = InetAddress.getLocalHost().getHostAddress()+":"+port;
        // System.out.println("command server is running on port "+addr);
        //File f = File.createTempFile(logPropFile, defaultProjectFileName)
        new Thread(server).start();
        server.storeAddr(masName);
        return server;
    }

    protected void waitEnd() {
        super.waitEnd();
        server.stop();
        super.finish(0, true, 0);
    }
}
