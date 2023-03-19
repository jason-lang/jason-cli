package jason.cli.mas;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/** handle the current/known MAS that are running */
public class RunningMASs {

    public static String RUNNING_MAS_FILE_NAME = "jason-cmd-server";

    protected static CLILocalMAS localRunningMAS = null;

    public static void setLocalRunningMAS(CLILocalMAS r) {
        localRunningMAS = r;
    }
    public static CLILocalMAS getLocalRunningMAS() {
        return localRunningMAS;
    }

    public static boolean hasLocalRunningMAS() {
        return  localRunningMAS != null && localRunningMAS.isRunning();
    }

    public static Map<String,String> getAllRunningMAS() {
        var map = new HashMap<String,String>();
        var all = testAllRemoteMAS();
        for (var mas: all.keySet()) {
            if (mas.equals("latest___mas"))
                continue;
            map.put(mas.toString(), all.getProperty(mas.toString()));
        }
        if (hasLocalRunningMAS()) {
            map.put(localRunningMAS.getProject().getSocName(), "local");
        }
        return map;
    }

    public static Properties testAllRemoteMAS() {
        var props = new Properties();
        var f = getRunningMASFile();
        if (f.exists()) {
            try {
                props.load(new FileReader(f));
            } catch (IOException e) {
                return props;
            }
            boolean changed = false;
            var localMAS = "";
            if (hasLocalRunningMAS())
                localMAS = localRunningMAS.getProject().getSocName();
            for (var masName: props.keySet()) {
                if (masName.equals(localMAS))
                    continue;
                if (masName.equals("latest___mas"))
                    continue;
                var addr = props.getProperty(masName.toString());
                var saddr = addr.split(":");
                try {
                    // try to connect
                    var s = new Socket(saddr[0], Integer.parseInt(saddr[1]));
                    var out = new PrintWriter(s.getOutputStream(), true);
                    var in  = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    out.println("mas__running");
                    if (in.readLine().equals("no")) {
                        //System.out.println("    (removing "+masName+" from cache)");
                        props.remove(masName);
                        changed = true;
                    }
                    s.close();
                } catch (Exception e) {
                    // e.printStackTrace();
                    //System.out.println("    (removing "+masName+" from cache)");
                    props.remove(masName);
                    changed = true;
                }
            }

            if (changed) {
                try {
                    props.store(new FileWriter(f),"running mas in jason");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return props;
    }

    public static File getRunningMASFile() {
        return new File(System.getProperty("java.io.tmpdir") + RUNNING_MAS_FILE_NAME);
    }

    public static Socket getRemoteRunningMAS(String masName) {
        var props = new Properties();
        var f = getRunningMASFile();
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
//                System.out.println("add found at "+addr);
                if (!addr.isEmpty()) {
                    // try to connect
                    var saddr = addr.split(":");
                    var s = new Socket(saddr[0], Integer.parseInt(saddr[1]));
//                    System.out.println("returning s "+s);
                    return s;
                }
            } catch (Exception e) {
//                 e.printStackTrace();
            }
        }
        return null;
    }

    public static File storeRunningMAS(String address) {
        if (!hasLocalRunningMAS())
            return null;
        try {
            var props = new Properties();

            var f = getRunningMASFile();
            if (f.exists()) {
                props.load(new FileReader(f));
            }
            var masName = localRunningMAS.getProject().getSocName();
            props.put(masName, address);
            props.put("latest___mas", masName);
            props.store(new FileWriter(f),"running mas in jason");
            // System.out.println("store server data in "+f.getAbsolutePath());
            return f;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

