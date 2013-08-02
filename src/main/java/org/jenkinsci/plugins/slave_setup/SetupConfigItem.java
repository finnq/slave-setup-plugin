package org.jenkinsci.plugins.slave_setup;

import antlr.ANTLRException;
import hudson.Util;
import hudson.model.labels.LabelAtom;
import hudson.model.labels.LabelExpression;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;

/**
 * Represents a setup config for one set of labels. It may have its own prepare script, files to copy and command line.
 */
public class SetupConfigItem {


    /**
     * the prepare script code
     */
    private String prepareScript;

    /**
     * jenkins label to be assigned to this setup config
     */
    private String assignedLabelString;

    /**
     * true if prepare script was executed successfully
     */
    private boolean prepareScriptExecuted = false;

    /**
     * Constructor uesd to create the setup config instance
     *
     * @param prepareScript
     * @param assignedLabelString
     */
    @DataBoundConstructor
    public SetupConfigItem(String prepareScript, String assignedLabelString) {
        this.prepareScript = prepareScript;
        this.assignedLabelString = assignedLabelString;
    }

    /**
     * Default constructor
     */
    public SetupConfigItem() {
    }

    /**
     * Returns the prepare script code.
     *
     * @return the prepare script code
     */
    public String getPrepareScript() {
        return prepareScript;
    }

    /**
     * Sets the prepare script code
     *
     * @param prepareScript
     */
    public void setPrepareScript(String prepareScript) {
        this.prepareScript = prepareScript;
    }

    /**
     * Returns the prepare script executed status.
     * @return the prepare script executed status
     */
    public boolean isPrepareScriptExecuted() {
        return this.prepareScriptExecuted;
    }

    /**
     * sets the prepare script executed status.
     * @param prepareScriptExecuted the prepare script executed status
     */
    public void setPrepareScriptExecuted(boolean prepareScriptExecuted) {
        this.prepareScriptExecuted = prepareScriptExecuted;
    }

    /**
     * Gets the textual representation of the assigned label as it was entered by the user.
     */
    public String getAssignedLabelString() {
        if (StringUtils.isEmpty(this.assignedLabelString)) {
            return "";
        }

        try {
            LabelExpression.parseExpression(this.assignedLabelString);
            return this.assignedLabelString;
        } catch (ANTLRException e) {
            // must be old label or host name that includes whitespace or other unsafe chars
            return LabelAtom.escape(this.assignedLabelString);
        }
    }

    /**
     * sets the assigned slaves' labels
     *
     * @param assignedLabelString
     */
    public void setAssignedLabelString(String assignedLabelString) {
        this.assignedLabelString = assignedLabelString;
    }
}
