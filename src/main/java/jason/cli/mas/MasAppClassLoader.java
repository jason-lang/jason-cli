package jason.cli.mas;

import java.io.*;

class MasAppClassLoader extends ClassLoader {
    public MasAppClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Class loadClass(String name) throws ClassNotFoundException {
        //System.out.println("Loading Class '" + name + "' ");
        if (name.equals(CLILocalMAS.class.getName())) {
            //Class<?> c = super.loadClass(name, false); //findLoadedClass(name);
            //Class<?> c = super.findSystemClass(name);
            Class<?> c = getJasonCLIClass();
            // force this class to be assigned with this loader
            resolveClass(c);
            return c;
        }

        Class<?> c = null;
        try {
            c = super.loadClass(name);
            if (c != null)
                return c;
        } catch (Exception e) {
            System.out.println("no super");
        }

        if (c == null) { // c still null
            System.out.println("looking for  " + name);
            c = getClass(name);
            if (c != null) {
                resolveClass(c);
                return c;
            }
        }

        return null;
    }

    private Class getJasonCLIClass() throws ClassNotFoundException {
        // TODO: fix it to load from jason-cli jar in the classpath
        String file = "/Users/jomi/pro/jason-cli/build/classes/java/main/jason/cli/mas/CLILocalMAS.class";
        try {
            var b = loadClassFileData(file);
            if (b != null) {
                return defineClass(CLILocalMAS.class.getName(), b, 0, b.length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Class getClass(String name) throws ClassNotFoundException {
        String file = "bin/classes/" + name.replace('.', File.separatorChar) + ".class";
        try {
            // This loads the byte code data from the file
            var b = loadClassFileData(file);
            if (b != null) {
                return defineClass(name, b, 0, b.length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] loadClassFileData(String name) throws IOException {
        InputStream stream = new FileInputStream(name); //getClass().getClassLoader().getResourceAsStream(name);
        if (stream == null)
            return null;
        int size = stream.available();
        byte buff[] = new byte[size];
        DataInputStream in = new DataInputStream(stream);
        in.readFully(buff);
        in.close();
        return buff;
    }

    @Override
    public String toString() {
        return "Jason Application Class Loader ---" + super.toString();
    }
}
