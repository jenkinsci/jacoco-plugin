package hudson.plugins.emma;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.Project;
import hudson.model.Result;
import hudson.tasks.Publisher;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

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

    public boolean perform(Build build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        final PrintStream logger = listener.getLogger();

        logger.println("Recording Emma reports " + includes);

        final FilePath src = build.getProject().getWorkspace().child(includes);
        final File localReport = getEmmaReport(build);
        src.copyTo(new FilePath(localReport));

        final EmmaBuildAction action = EmmaBuildAction.load(build,rule,localReport);

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
    static File getEmmaReport(Build build) {
        return new File(build.getRootDir(), "emma.xml");
    }

    public Descriptor<Publisher> getDescriptor() {
        return DESCRIPTOR;
    }

    public static final Descriptor<Publisher> DESCRIPTOR = new DescriptorImpl();

    public static class DescriptorImpl extends Descriptor<Publisher> {
        public DescriptorImpl() {
            super(EmmaPublisher.class);
        }

        public String getDisplayName() {
            return "Record Emma coverage report";
        }

        public String getHelpFile() {
            return "/plugin/emma/help.html";
        }

        public Publisher newInstance(StaplerRequest req) throws FormException {
            EmmaPublisher pub = new EmmaPublisher();
            req.bindParameters(pub, "emma.");
            return pub;
        }
    }
}
