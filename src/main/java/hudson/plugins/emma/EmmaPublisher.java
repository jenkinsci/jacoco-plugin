package hudson.plugins.emma;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.Project;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import net.sf.json.JSONObject;

/**
 * {@link Publisher} that captures Emma coverage reports.
 *
 * @author Kohsuke Kawaguchi
 */
public class EmmaPublisher extends Publisher {
    /**
     * Relative path to the Emma XML file inside the workspace.
     */
    public String includes;

    /**
     * Rule to be enforced. Can be null.
     *
     * TODO: define a configuration mechanism.
     */
    public Rule rule;

    /**
     * {@link hudson.model.HealthReport} thresholds to apply.
     */
    public EmmaHealthReportThresholds healthReports = new EmmaHealthReportThresholds();

    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        final PrintStream logger = listener.getLogger();

        logger.println("Recording Emma reports " + includes);

        final FilePath src = build.getProject().getWorkspace().child(includes);

        if(!src.exists()) {
            if(build.getResult().isWorseThan(Result.UNSTABLE))
                // build has failed, so that's probably why this was not generated.
                // so don't report an error
                return true;
            logger.println("Coverage file "+src+" not found. Has the report generated?");
            build.setResult(Result.FAILURE);
            return true;
        }

        final File localReport = getEmmaReport(build);
        src.copyTo(new FilePath(localReport));

        final EmmaBuildAction action = EmmaBuildAction.load(build,rule,healthReports,localReport);

        build.getActions().add(action);

        if (action.getResult().isFailed()) {
            logger.println("Code coverage enforcement failed. Setting Build to unstable.");
            build.setResult(Result.UNSTABLE);
        }

        return true;
    }

    public Action getProjectAction(Project project) {
        return new EmmaProjectAction(project);
    }

    /**
     * Gets the directory to store report files
     */
    static File getEmmaReport(AbstractBuild build) {
        return new File(build.getRootDir(), "emma.xml");
    }

    public Descriptor<Publisher> getDescriptor() {
        return DESCRIPTOR;
    }

    public static final Descriptor<Publisher> DESCRIPTOR = new DescriptorImpl();

    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        public DescriptorImpl() {
            super(EmmaPublisher.class);
        }

        public String getDisplayName() {
            return "Record Emma coverage report";
        }

        public String getHelpFile() {
            return "/plugin/emma/help.html";
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public Publisher newInstance(StaplerRequest req, JSONObject json) throws FormException {
            EmmaPublisher pub = new EmmaPublisher();
            req.bindParameters(pub, "emma.");
            req.bindParameters(pub.healthReports, "emmaHealthReports.");
            // start ugly hack
            //@TODO remove ugly hack
            // the default converter for integer values used by req.bindParameters
            // defaults an empty value to 0. This happens even if the type is Integer
            // and not int.  We want to change the default values, so we use this hack.
            //
            // If you know a better way, please fix.
            if ("".equals(req.getParameter("emmaHealthReports.maxClass"))) {
                pub.healthReports.setMaxClass(100);
            }
            if ("".equals(req.getParameter("emmaHealthReports.maxMethod"))) {
                pub.healthReports.setMaxMethod(70);
            }
            if ("".equals(req.getParameter("emmaHealthReports.maxBlock"))) {
                pub.healthReports.setMaxBlock(80);
            }
            if ("".equals(req.getParameter("emmaHealthReports.maxLine"))) {
                pub.healthReports.setMaxLine(80);
            }
            // end ugly hack
            return pub;
        }
    }
}
