package hudson.plugins.jacocofile;

import hudson.model.Action;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.plugins.jacoco.JacocoBuildAction;
import hudson.plugins.jacoco.Messages;

import java.io.IOException;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Project view extension by JaCoCo plugin.
 * 
 * @author Kohsuke Kawaguchi
 */
public final class JacocoProjectFilesAction implements Action {
    public final AbstractProject<?,?> project;

    public JacocoProjectFilesAction(AbstractProject<?,?> project) {
        this.project = project;
    }

    public String getIconFileName() {
        return "graph.gif";
    }

    public String getDisplayName() {
        return Messages.ProjectFilesAction_DisplayName();
    }

    public String getUrlName() {
        return "jacoco";
    }

    /**
     * Gets the most recent {@link JacocoBuildAction} object.
     */
    public JacocoBuildFilesAction getLastResult() {
        for (AbstractBuild<?, ?> b = project.getLastBuild(); b != null; b = b.getPreviousBuild()) {
            if (b.getResult() == Result.FAILURE || b.getResult() == Result.ABORTED)
                continue;
            JacocoBuildFilesAction r = b.getAction(JacocoBuildFilesAction.class);
            if (r != null)
                return r;
        }
        return null;
    }

    public void doGraph(StaplerRequest req, StaplerResponse rsp) throws IOException {
       if (getLastResult() != null)
          getLastResult().doGraph(req,rsp);
    }
}
