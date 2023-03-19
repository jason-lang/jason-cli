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
public class JasonCLI {

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
        out.println("Jason interactive shell with completion and autosuggestions.");
        out.println("      Hit <TAB> to see available commands.");
        out.println("      Press Ctrl-D to exit.");
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

    public static void main(String[] args) {
        if (args.length == 0) {
            startTerminal();
        } else {
            int exitCode = new CommandLine(new JasonCLI()).execute(args);
            if (!RunningMASs.hasLocalRunningMAS())
                System.exit(exitCode);
        }
    }

    static void startTerminal() {
        AnsiConsole.systemInstall();
        try {
            Supplier<Path> workDir = () -> Paths.get(System.getProperty("user.dir"));
            var confPath = new ConfigurationPath(
                    Paths.get(System.getProperty("user.dir")),
                    Paths.get(System.getProperty("user.home"))
            );

            // set up jason commands
            var jasonCommands = new JasonCLI();

            PicocliCommandsFactory factory = new PicocliCommandsFactory();
            // Or, if you have your own factory, you can chain them like this:
            // MyCustomFactory customFactory = createCustomFactory(); // your application custom factory
            // PicocliCommandsFactory factory = new PicocliCommandsFactory(customFactory); // chain the factories

            CommandLine cmd = new CommandLine(jasonCommands, factory);
            PicocliCommands picocliCommands = new PicocliCommands(cmd);

            var parser = new DefaultParser();
            parser.setEofOnUnclosedBracket(Bracket.CURLY); //, Bracket.ROUND, Bracket.SQUARE);
            parser.setEofOnUnclosedQuote(true);
            parser.blockCommentDelims(new DefaultParser.BlockCommentDelims("/*", "*/"))
                    .lineCommentDelims(new String[] {"#", "//"});
            try (var terminal = TerminalBuilder.builder().build()) {
                SystemRegistry systemRegistry = new SystemRegistryImpl(parser, terminal, workDir, confPath);
                systemRegistry.setCommandRegistries(picocliCommands);
                systemRegistry.register("help", picocliCommands);

                var reader = LineReaderBuilder.builder()
                        .terminal(terminal)
                        .completer(systemRegistry.completer())
                        .parser(parser)
                        .variable(LineReader.LIST_MAX, 50)   // max tab completion candidates
                        .variable(LineReader.INDENTATION, 4)
                        .variable(LineReader.HISTORY_FILE, Paths.get(System.getProperty("user.home")+"/.jason-cli", "history"))
                        .option(LineReader.Option.DISABLE_EVENT_EXPANSION,  true)
                        .build();

                jasonCommands.setReader(reader);
                factory.setTerminal(terminal);
                TailTipWidgets widgets = new TailTipWidgets(reader, systemRegistry::commandDescription, 5, TailTipWidgets.TipType.COMPLETER);
                widgets.enable();
                KeyMap<Binding> keyMap = reader.getKeyMaps().get("main");
                keyMap.bind(new Reference("tailtip-toggle"), KeyMap.alt("s"));

                // start the shell and process input until the user quits with Ctrl-D
                String line;
                while (true) {
                    try {
                        systemRegistry.cleanUp();
                        line = reader.readLine("jason> ");
                        systemRegistry.execute(line);
                    } catch (UserInterruptException e) {
                        systemRegistry.trace(e);
                    } catch (EndOfFileException e) {
                        terminal.writer().println("\n<end of script>");
                        return;
                    } catch (Exception e) {
                        systemRegistry.trace(e);
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            AnsiConsole.systemUninstall();
        }
    }
}

class VersionProvider implements IVersionProvider {
    public String[] getVersion() {
        return new String[] { "Jason " + Config.get().getJasonVersion() };
    }
}

@Command(name = "echo")
class Echo implements Runnable {

    @CommandLine.ParentCommand
    protected JasonCLI parent;

    @CommandLine.Parameters(paramLabel = "<message>", defaultValue = "")
    String msg;

    @Override
    public void run() {
        parent.println(msg);
    }
}
