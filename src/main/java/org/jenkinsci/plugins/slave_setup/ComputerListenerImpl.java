package org.jenkinsci.plugins.slave_setup;

import hudson.model.Computer;
import hudson.model.TaskListener;
import hudson.slaves.ComputerListener;
import hudson.AbortException;

import java.io.IOException;
import java.util.List;

@Extension
public class ComputerListenerImpl extends ComputerListener {

    /**
     * Prepares the slave before it gets online by copying the given content in root and executing the configured setup script.
     * @param c the computer to set up
     * @param listener log listener
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public void preLaunch(Computer c, TaskListener listener) throws AbortException, IOException, InterruptedException {
        listener.getLogger().println("Start prepare script for" + c.getName() + " ...");

        SetupConfig config = SetupConfig.get();
        SetupDeployer deployer = new SetupDeployer();

        listener.getLogger().println("executing prepare script ...");
        deployer.executePrepareScripts(c, config, listener);

        listener.getLogger().println("slave setup done.");
    }

}
