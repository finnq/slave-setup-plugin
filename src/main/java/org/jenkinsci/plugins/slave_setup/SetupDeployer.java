package org.jenkinsci.plugins.slave_setup;

import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Computer;
import hudson.model.Label;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.slaves.EnvironmentVariablesNodeProperty;
import hudson.tasks.Shell;
import hudson.util.LogTaskListener;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SetupDeployer {
    private void executeScript(Computer c, FilePath root, TaskListener listener, String cmdLine) throws IOException, InterruptedException {
        if (StringUtils.isNotBlank(cmdLine)) {
            listener.getLogger().println("Executing script '" + cmdLine + "' on " + c.getName());
            Node node = c.getNode();
            Launcher launcher = root.createLauncher(listener);
            Shell s = new Shell(cmdLine);
            FilePath script = s.createScriptFile(root);
            int r = launcher.launch().cmds(s.buildCommandLine(script)).envs(getEnvironment(node)).stdout(listener).pwd(root).join();

            if (r != 0) {
                listener.getLogger().println("script failed!");
                throw new AbortException("script failed!");
            }

            listener.getLogger().println("script executed successfully.");
        }
    }
    
    /**
     * Returns 0 if all prepare scripts were executed without error.
     *
     * @return 0 if all prepare scripts were executed without error
     */
    public void executePrepareScripts(Computer c, SetupConfig config, TaskListener listener) {
        // execute prepare scripts on master relative to jenkins install dir
        Computer computer = Jenkins.MasterComputer.currentComputer();
        FilePath filePath = Jenkins.getInstance().getRootPath();

        for (SetupConfigItem setupConfigItem : config.getSetupConfigItems()) {
            if (StringUtils.isBlank(setupConfigItem.getPrepareScript())) {
                listener.getLogger().println("No prepare script given");
                setupConfigItem.setPrepareScriptExecuted(true);
            } else {
                try {
                    this.executeScript(computer, filePath, listener, setupConfigItem.getPrepareScript());
                    setupConfigItem.setPrepareScriptExecuted(true);
                } catch (Exception e) {
                    listener.getLogger().println("Execute prepare script failed with exception: " + e.getMessage());
                    setupConfigItem.setPrepareScriptExecuted(false);
                }
            }
        }
    }
    
    /**
     * Returns the environment variables for the given node.
     *
     * @param node node to get the environment variables from
     * @return the environment variables for the given node
     */
    private EnvVars getEnvironment(Node node) {
        EnvironmentVariablesNodeProperty env = node.getNodeProperties().get(EnvironmentVariablesNodeProperty.class);
        return env != null ? env.getEnvVars() : new EnvVars();
    }
}
