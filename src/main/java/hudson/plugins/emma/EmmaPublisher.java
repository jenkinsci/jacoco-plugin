package hudson.plugins.emma;

import hudson.Launcher;
import hudson.FilePath;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.Publisher;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.io.File;

/**
 * {@link Publisher} that captures Emma coverage reports.
 * @author Kohsuke Kawaguchi
 */
public class EmmaPublisher extends Publisher {
    /**
     * Relative path to the Emma XML file inside the workspace.
     */
    public String includes;

    public boolean perform(Build build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        listener.getLogger().println("Recording Emma reports "+includes);

        FilePath src = build.getProject().getWorkspace().child(includes);
        File localReport = getEmmaReport(build);
        src.copyTo(new FilePath(localReport));

        // TODO: load report into DOM
        
        return true;
    }

    /**
     * Gets the directory to store report files
     */
    static File getEmmaReport(Build build) {
        return new File(build.getRootDir(),"emma.xml");
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
            req.bindParameters(pub,"emma.");
            return pub;
        }
    }
}
