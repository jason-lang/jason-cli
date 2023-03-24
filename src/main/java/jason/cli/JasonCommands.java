package jason.cli;

import jason.cli.agent.Agent;
import jason.cli.mas.CommandServer;
import jason.cli.mas.MAS;
import jason.cli.mas.RunningMASs;
import jason.util.Config;
import org.fusesource.jansi.AnsiConsole;
import org.jline.builtins.ConfigurationPath;
import org.jline.console.SystemRegistry;
import org.jline.console.impl.SystemRegistryImpl;
import org.jline.keymap.KeyMap;
import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.DefaultParser.Bracket;
import org.jline.terminal.TerminalBuilder;
import org.jline.widget.TailTipWidgets;
import org.jline.widget.Widgets;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;
import picocli.shell.jline3.PicocliCommands;
import picocli.shell.jline3.PicocliCommands.PicocliCommandsFactory;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

// program "inspired" by https://github.com/remkop/picocli/tree/v4.7.1/picocli-shell-jline3

@Command(name = "jason",
        // version = "1.0",
        versionProvider = jason.cli.VersionProvider.class,
        mixinStandardHelpOptions = true,
        subcommands = {  MAS.class, Agent.class, Echo.class },
        synopsisSubcommandLabel = "(mas | agent)"
)
public class JasonCommands {

    private PrintWriter out = null;
    private PrintWriter err = null;

    public PrintWriter getOut() {
        return out;
    }

    public void println(String s) {
        if (out == null) {
            System.out.println(s);
        } else {
            out.println(s);
            out.flush();
        }
    }
    public void errorMsg(String s) {
        if (out == null) {
            System.err.println(s);
        } else {
            out.println(s);
        }
    }

    public boolean isTerminal() {
        return out != null;
    }

    public void setReader(LineReader reader) {
        out = reader.getTerminal().writer();
    }

    private CommandServer cmdServer = null;

    public boolean hasCmdServer() {
        return cmdServer != null;
    }

    public void startCmdServer() {
         cmdServer = new CommandServer();
         new Thread(cmdServer).start();
    }

    public CommandServer getCmdServer() {
        return cmdServer;
    }

//        public void run() {
//            System.out.println(new CommandLine(this).getUsageMessage());
//            CommandLine.ParseResult pr = spec.commandLine().getParseResult();
//            System.out.println("o="+pr.originalArgs());
//            System.out.println("m="+pr.matchedArgs());
//            System.out.println("e="+pr.expandedArgs());
//            startTerminal();
//        }
//    }
}
   
class VersionProvider implements IVersionProvider {
    public String[] getVersion() {
        return new String[] { "Jason " + Config.get().getJasonVersion() };
    }
}

@Command(name = "echo",  hidden = true)
class Echo implements Runnable {

    @CommandLine.ParentCommand
    protected JasonCommands parent;

    @CommandLine.Parameters(paramLabel = "<message>", defaultValue = "")
    String msg;

    @Override
    public void run() {
        parent.println(msg);
    }
}
