package jason.cli.mas;

import java.io.File;
import java.io.FileReader;
import java.net.Socket;
import java.util.Properties;

import picocli.CommandLine.Command;

@Command(
    name = "mas",
    description = "commands to change the state of the Multi-Agent System",
    subcommands = { Start.class, Stop.class }
)
public class MAS { 

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

