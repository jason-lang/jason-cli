package jason.cli.mas;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;


@Command(
    name = "stop",
    description = "stops a MAS"
)
public class Stop implements Runnable {
    
    @Option(names = { "--mas-name" }, paramLabel = "<mas name>", defaultValue = "", 
               description = "MAS unique identification")
    String masName;

    @ParentCommand
    private MAS parent;
    
    @Override
    public void run() {
        var s = parent.getRunningMAS(masName);
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


}

