package hudson.plugins.emma;

import hudson.model.Action;
import hudson.model.Project;

/**
 * Project view extension by Emma plugin.
 * 
 * @author Kohsuke Kawaguchi
 */
public class EmmaProjectAction implements Action {
    public final Project project;

    public EmmaProjectAction(Project project) {
        this.project = project;
    }

    public String getIconFileName() {
        return "graph.gif";
    }

    public String getDisplayName() {
        return "Coverage Trend";
    }

    public String getUrlName() {
        return "emma";
    }
}
