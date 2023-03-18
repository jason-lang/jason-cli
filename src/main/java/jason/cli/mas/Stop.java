package jason.cli.mas;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;


@Command(
    name = "stop",
    description = "stops a MAS"
)
public class Stop implements Runnable {
    
    @Option(names = { "--mas-name" }, paramLabel = "<mas name>", defaultValue = "", 
               description = "MAS unique identification")
    String masName;

    
    @Override
    public void run() {
        var s = getRunningMAS(masName);
        if (s != null) {
            try (var out = new PrintWriter(s.getOutputStream(), true)) {
                out.println("stop");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (masName.isEmpty()) {
            System.err.println("the MAS name should be informed");
        } else {
            System.err.println("could not connect to the MAS named "+masName);
        }

    }

    Socket getRunningMAS(String masName) {
        var props = new Properties();
        var f = new File(System.getProperty("java.io.tmpdir") + CommandServer.RUNNING_MAS_FILE_NAME);
        if (f.exists()) {
            try {
                props.load(new FileReader(f));
                var addr = "";
                if (masName.isEmpty()) {
                    masName = props.getProperty("latest___mas", "");
                    if (!masName.isEmpty()) {
                        addr = props.getProperty(masName, "");
                    }
                } else {
                    addr = props.getProperty(masName, "");
                }
                System.out.println("(trying to) stop MAS "+masName+" at "+addr);
                if (!addr.isEmpty()) {
                    // try to connect
                    var saddr = addr.split(":");
                    var host = saddr[0];
                    var port = Integer.parseInt(saddr[1]);

                    return new Socket(host, port);                        
                }

            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        return null;
    }

}

