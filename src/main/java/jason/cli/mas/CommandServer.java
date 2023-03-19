package jason.cli.mas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;


public class CommandServer implements Runnable {

    private ServerSocket serverSocket;
//    private final CLILocalMAS localRunner;

    private boolean running = false;

    private int port = 0;

    public CommandServer() {
        try {
            serverSocket = new ServerSocket(0);
            port = serverSocket.getLocalPort();
            // System.out.println( "**" +serverSocket.getLocalSocketAddress() + " " + serverSocket.getInetAddress());
//            return port;
        } catch (IOException e) {
            e.printStackTrace();
//            return -1;
        }
    }

    public String getAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress()+":"+port;
        } catch (UnknownHostException e) {
            return "";
        }
    }

    public void run()  {
        running = true;
        while (running) {
            try {
                var clientSocket = serverSocket.accept();

                // create a thread for this client
                new Thread(
                    () -> {
                        try {                            
                            var out = new PrintWriter(clientSocket.getOutputStream(), true);
                            var in  = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                            int i = 0;
                            String  cmd;
                            if ((cmd = in.readLine()) != null && running) {
//                                System.out.println("processing cmd "+cmd);
                                if ("mas__running".equals(cmd)) {
                                    if (RunningMASs.hasLocalRunningMAS()) {
                                        out.println("yes");
                                    } else {
                                        out.println("no");
                                    }
                                }
                                if ("mas stop".equals(cmd)) {
                                    if (RunningMASs.hasLocalRunningMAS()) {
                                        var n = RunningMASs.getLocalRunningMAS().getName();
                                        RunningMASs.getLocalRunningMAS().finish(0, false, 0);
                                        out.println(n+" stopped");
                                    } else {
                                        out.println("no MAS running here");
                                    }
                                }
                                if ("exit".equals(cmd)) {
                                    running = false;
                                    if (RunningMASs.hasLocalRunningMAS()) {
                                        var n = RunningMASs.getLocalRunningMAS().getName();
                                        RunningMASs.getLocalRunningMAS().finish();
                                        out.println(n+" stopped & exit!");
                                    } else {
                                        out.println("no MAS running here, but exiting anyway");
                                    }
                                    System.exit(0);
                                }
                                out.println( (i++)+" done: "+cmd);
                            }
                            in.close();
                            out.close();
                            clientSocket.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                ).start();
    
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop()  {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }
}
