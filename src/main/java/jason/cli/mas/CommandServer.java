package jason.cli.mas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;


public class CommandServer implements Runnable {

    public static String RUNNING_MAS_FILE_NAME = "jason-cmd-server";

    private ServerSocket serverSocket;
    private CLILocalMAS localRunner;

    private boolean running = false;

    private int port = 0;

    public CommandServer(CLILocalMAS r) {
        localRunner = r;
    }

    public int configure() {
        try {
            serverSocket = new ServerSocket(0);
            port = serverSocket.getLocalPort();
            // System.out.println( "**" +serverSocket.getLocalSocketAddress() + " " + serverSocket.getInetAddress());
            return port;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public String getAddress() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress()+":"+port;
    }

    public File storeAddr(String masName) {
        try {            
            var props = new Properties();

            var f = new File(System.getProperty("java.io.tmpdir") + RUNNING_MAS_FILE_NAME);
            if (f.exists()) {
                props.load(new FileReader(f));
            }
            props.put(masName,getAddress());
            props.put("latest___mas", masName);
            props.store(new FileWriter(f),"running mas in jason");
            // System.out.println("store server data in "+f.getAbsolutePath());
            return f;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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
                            while ((cmd = in.readLine()) != null && running) {
                                if (".".equals(cmd)) {
                                    break;
                                }
                                if ("stop".equals(cmd)) {
                                    running = false;
                                    localRunner.finish();
                                }
                                out.println( (i++)+" hello client "+cmd);
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
            e.printStackTrace();
        }
    }
}
