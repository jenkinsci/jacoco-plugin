package hudson.plugins.jacoco;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Result;
import hudson.plugins.jacoco.Messages;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Project view extension by JaCoCo plugin.
 * 
 * @author Kohsuke Kawaguchi
 */
public final class JacocoProjectAction implements Action {
    public final AbstractProject<?,?> project;

    public JacocoProjectAction(AbstractProject<?,?> project) {
        this.project = project;
    }

    public String getIconFileName() {
        return "graph.gif";
    }

    public String getDisplayName() {
        return Messages.ProjectAction_DisplayName();
    }

    public String getUrlName() {
        return "jacoco";
    }

    /**
     * Gets the most recent {@link JacocoBuildAction} object.
     */
    public JacocoBuildAction getLastResult() {
        for (AbstractBuild<?, ?> b = project.getLastBuild(); b != null; b = b.getPreviousBuild()) {
            if (b.getResult() == Result.FAILURE)
                continue;
            JacocoBuildAction r = b.getAction(JacocoBuildAction.class);
            if (r != null)
                return r;
        }
        return null;
    }

    public void doGraph(StaplerRequest req, StaplerResponse rsp) throws IOException {
       if (getLastResult() != null)
          getLastResult().doGraph(req,rsp);
    }
    private static final Logger logger = Logger.getLogger(JacocoBuildAction.class.getName());
}
