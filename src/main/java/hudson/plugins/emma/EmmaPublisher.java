package hudson.plugins.emma;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * {@link Publisher} that captures Emma coverage reports.
 *
 * @author Kohsuke Kawaguchi
 */
public class EmmaPublisher extends Recorder {
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
    
    /**
     * look for emma reports based in the configured parameter includes.
     * 'includes' is 
     *   - an Ant-style pattern
     *   - a list of files and folders separated by the characters ;:,  
     */
    protected static FilePath[] locateCoverageReports(FilePath workspace, String includes) throws IOException, InterruptedException {

    	// First use ant-style pattern
    	try {
        	FilePath[] ret = workspace.list(includes);
            if (ret.length > 0) { 
            	return ret;
            }
        } catch (Exception e) {
        }

        // If it fails, do a legacy search
        ArrayList<FilePath> files = new ArrayList<FilePath>();
		String parts[] = includes.split("\\s*[;:,]+\\s*");
		for (String path : parts) {
			FilePath src = workspace.child(path);
			if (src.exists()) {
				if (src.isDirectory()) {
					files.addAll(Arrays.asList(src.list("**/coverage*.xml")));
				} else {
					files.add(src);
				}
			}
		}
		return files.toArray(new FilePath[files.size()]);
	}
	
    /**
     * save emma reports from the workspace to build folder  
     */
	protected static void saveCoverageReports(FilePath folder, FilePath[] files) throws IOException, InterruptedException {
		folder.mkdirs();
		for (int i = 0; i < files.length; i++) {
			String name = "coverage" + (i > 0 ? i : "") + ".xml";
			FilePath src = files[i];
			FilePath dst = folder.child(name);
			src.copyTo(dst);
		}
	}

    public boolean perform(AbstractBuild<?,?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
    	
        final PrintStream logger = listener.getLogger();

        FilePath[] reports;
        if (includes == null || includes.trim().length() == 0) {
            logger.println("Emma: looking for coverage reports in the entire workspace: " + build.getWorkspace().getRemote());
            reports = locateCoverageReports(build.getWorkspace(), "**/emma/coverage.xml");
        } else {
            logger.println("Emma: looking for coverage reports in the provided path: " + includes );
            reports = locateCoverageReports(build.getWorkspace(), includes);
        }
        
        if (reports.length == 0) {
            if(build.getResult().isWorseThan(Result.UNSTABLE))
                return true;
            
            logger.println("Emma: no coverage files found in workspace. Was any report generated?");
            build.setResult(Result.FAILURE);
            return true;
        } else {
        	String found = "";
        	for (FilePath f: reports) 
        		found += "\n          " + f.getRemote();
            logger.println("Emma: found " + reports.length  + " report files: " + found );
        }
        
        FilePath emmafolder = new FilePath(getEmmaReport(build));
        saveCoverageReports(emmafolder, reports);
        logger.println("Emma: stored " + reports.length + " report files in the build folder: "+ emmafolder);
        
        final EmmaBuildAction action = EmmaBuildAction.load(build, rule, healthReports, reports);
        
        logger.println("Emma: " + action.getBuildHealth().getDescription());

        build.getActions().add(action);

        if (action.getResult().isFailed()) {
            logger.println("Emma: code coverage enforcement failed. Setting Build to unstable.");
            build.setResult(Result.UNSTABLE);
        }

        return true;
    }

    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        return new EmmaProjectAction(project);
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    /**
     * Gets the directory to store report files
     */
    static File getEmmaReport(AbstractBuild<?,?> build) {
        return new File(build.getRootDir(), "emma");
    }

    @Override
    public BuildStepDescriptor<Publisher> getDescriptor() {
        return DESCRIPTOR;
    }

    @Extension
    public static final BuildStepDescriptor<Publisher> DESCRIPTOR = new DescriptorImpl();

    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        public DescriptorImpl() {
            super(EmmaPublisher.class);
        }

        public String getDisplayName() {
            return Messages.EmmaPublisher_DisplayName();
        }

        @Override
        public String getHelpFile() {
            return "/plugin/emma/help.html";
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
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
