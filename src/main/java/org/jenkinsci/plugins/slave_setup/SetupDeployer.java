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
}
