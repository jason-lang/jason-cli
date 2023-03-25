package jason.cli.mas;

import jason.JasonException;
import jason.infra.local.RunLocalMAS;

public class CLILocalMAS extends RunLocalMAS implements Runnable {

    protected int init(String[] args, String masName) throws JasonException {
        runner = this;
        RunningMASs.setLocalRunningMAS(this);
        var r = super.init(args);
        if (project.getSocName() == null || project.getSocName().isEmpty() || project.getSocName().equals("default"))
            project.setSocName(masName);
        registerMBean();
        registerInRMI();
        registerWebMindInspector();

        create();
        return r;
    }

    public String getName() {
        return project.getSocName();
    }

    @Override
    public void run() {
        super.start();
        super.waitEnd();
    }

    protected void waitEnd() {
        super.waitEnd();
        finish(0, true, 0);
    }

    @Override
    public void finish() {
        super.finish();
        RunningMASs.setLocalRunningMAS(null);
        System.out.println(getName()+" stopped");
    }

    @Override
    public void finish(int deadline, boolean stopJVM, int exitValue) {
        if (deadline != 0)
            System.out.println("Stopping "+getName()+" in "+deadline+" ms...");
        super.finish(deadline, stopJVM, exitValue);
        RunningMASs.setLocalRunningMAS(null);
        System.out.println(getName()+" stopped");
    }
}
