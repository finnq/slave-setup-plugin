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

/**
 * Executes a deployment to all or a single node of the given fileset and executes the command line.
 *
 * @author Frederik Fromm
 */
public class SetupDeployer {
    /**
     * the logger.
     */
    private static final Logger LOGGER = Logger.getLogger(SetupDeployer.class.getName());

    /**
     * Returns true if the given setup config item is responsible for the given slave computer.
     *
     * @param c               the slave computer
     * @param setupConfigItem the setup config item to check
     * @return true if the given setup config item is responsible for the given slave computer
     */
    public boolean checkLabels(Computer c, SetupConfigItem setupConfigItem) {
        if (StringUtils.isBlank(setupConfigItem.getAssignedLabelString())) {
            return true;
        }

        //Label l = Jenkins.getInstance().getLabel(setupConfigItem.getAssignedLabelString());
        Label label = Label.get(setupConfigItem.getAssignedLabelString());

        return label.contains(c.getNode());
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

}
