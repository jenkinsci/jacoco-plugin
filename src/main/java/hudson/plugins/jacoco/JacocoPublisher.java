package hudson.plugins.jacoco;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.plugins.jacoco.report.CoverageReport;
import hudson.remoting.VirtualChannel;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;
import org.kohsuke.stapler.DataBoundConstructor;


/**
 * {@link Publisher} that captures jacoco coverage reports.
 *
 * @author Kohsuke Kawaguchi
 * @author Jonathan Fuerth
 * @author Ognjen Bubalo
 * 
 */
public class JacocoPublisher extends Recorder {
    
    /**
     * Rule to be enforced. Can be null.
     *
     * TODO: define a configuration mechanism.
     */
    public Rule rule;
    @Deprecated public transient String includes;
    @Deprecated public transient int moduleNum;
    /**
     * {@link hudson.model.HealthReport} thresholds to apply.
     */
    public JacocoHealthReportThresholds healthReports = new JacocoHealthReportThresholds();

    
    /**
     * Variables containing the configuration set by the user.
     */
	private final String execPattern;
	private final String classPattern;
	private final String sourcePattern;
    
    /**
     * Loads the configuration set by user.
     *
     */
    @DataBoundConstructor
    public JacocoPublisher(String execPattern, String classPattern, String sourcePattern) {
    	this.execPattern = execPattern;
    	this.classPattern = classPattern;
    	this.sourcePattern = sourcePattern;
	}

    
	
	public String getExecPattern() {
		return execPattern;
	}

	public String getClassPattern() {
		return classPattern;
	}

	public String getSourcePattern() {
		return sourcePattern;
	}



	protected static void saveCoverageReports(FilePath destFolder, FilePath sourceFolder) throws IOException, InterruptedException {
		destFolder.mkdirs();
		
		sourceFolder.copyRecursiveTo(destFolder);
	}
	
	protected static String resolveFilePaths(AbstractBuild<?, ?> build, BuildListener listener, String input) {
        try {
           
        	return build.getEnvironment(listener).expand(input);
            
        } catch (Exception e) {
            listener.getLogger().println("Failed to resolve parameters in string \""+
            input+"\" due to following error:\n"+e.getMessage());
        }
        return input;
    }
	
	protected static FilePath[] resolveDirPaths(AbstractBuild<?, ?> build, BuildListener listener, final String input) {
		final PrintStream logger = listener.getLogger();
		FilePath[] directoryPaths = null;
		try {
			directoryPaths = build.getWorkspace().act(new FilePath.FileCallable<FilePath[]>() {
				public FilePath[] invoke(File f, VirtualChannel channel) throws IOException {
					ArrayList<FilePath> localDirectoryPaths= new ArrayList<FilePath>();
					String[] includes = input.split(",");
					DirectoryScanner ds = new DirectoryScanner();
			        
			        ds.setIncludes(includes);
			        ds.setCaseSensitive(false);
			        ds.setBasedir(f);
			        ds.scan();
			        String[] dirs = ds.getIncludedDirectories();
			        
			        for (String dir : dirs) {
			        	localDirectoryPaths.add(new FilePath(new File(dir)));
			        }
			        FilePath[] lfp = {};//trick to have an empty array as a parameter, so the returned array will contain the elements
			        return localDirectoryPaths.toArray(lfp);
	            }
			});
			
		} catch(InterruptedException ie) {
			ie.printStackTrace();
		} catch(IOException io) {
			io.printStackTrace();
		}
		return directoryPaths;
	}
	
	/* 
	 * Entry point of this report plugin.
	 * 
	 * @see hudson.tasks.BuildStepCompatibilityLayer#perform(hudson.model.AbstractBuild, hudson.Launcher, hudson.model.BuildListener)
	 */
	@Override
    public boolean perform(AbstractBuild<?,?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
	
		final PrintStream logger = listener.getLogger();
		FilePath[] matchedExecFiles = null;
		FilePath[] matchedClassDirs = null;
		FilePath[] matchedSrcDirs = null;
		FilePath actualBuildDirRoot = null;
		FilePath actualBuildClassDir = null;
		FilePath actualBuildSrcDir = null;
		FilePath actualBuildExecDir = null;
		
		logger.println("[JaCoCo plugin] Collecting JaCoCo coverage data...");
		
		
		EnvVars env = build.getEnvironment(listener);
        env.overrideAll(build.getBuildVariables());
        
        if ((execPattern==null) || (classPattern==null) || (sourcePattern==null)) {
            if(build.getResult().isWorseThan(Result.UNSTABLE))
                return true;
            
            logger.println("[JaCoCo plugin] ERROR: Missing configuration!");
            build.setResult(Result.FAILURE);
            return true;
        } else {
        		logger.println("[JaCoCo plugin] " + execPattern + ";" + classPattern +  ";" + sourcePattern + ";" + " locations are configured");
        }
        actualBuildDirRoot = new FilePath(getJacocoReport(build));
        actualBuildClassDir = new FilePath(actualBuildDirRoot, "classes");
        actualBuildSrcDir = new FilePath(actualBuildDirRoot, "sources");
        actualBuildExecDir = new FilePath(actualBuildDirRoot, "execFiles");
        
        matchedExecFiles = build.getWorkspace().list(resolveFilePaths(build, listener, execPattern));
        logger.println("[JaCoCo plugin] Number of found exec files: " + matchedExecFiles.length); 
        logger.print("[JaCoCo plugin] Saving matched execfiles: ");
        int i=0;
        for (FilePath file : matchedExecFiles) {
        	FilePath separateExecDir = new FilePath(actualBuildExecDir, "exec"+i);
        	FilePath fullExecName = separateExecDir.child("jacoco.exec");
        	file.copyTo(fullExecName);
        	logger.print(" " + file.getRemote());
        	++i;
        }
        matchedClassDirs = resolveDirPaths(build, listener, classPattern);
        logger.print("\n[JaCoCo plugin] Saving matched class directories: ");
        for (FilePath file : matchedClassDirs) {
        	file= new FilePath(build.getWorkspace(), file.getRemote()+"\\");
        	saveCoverageReports(actualBuildClassDir, file);
        	logger.print(" " + file.getRemote());
        }
        matchedSrcDirs = resolveDirPaths(build, listener, sourcePattern);
        logger.print("\n[JaCoCo plugin] Saving matched source directories: ");
        for (FilePath file : matchedSrcDirs) {
        	file= new FilePath(build.getWorkspace(), file.getRemote());
        	saveCoverageReports(actualBuildSrcDir, file);
        	logger.print(" " + file.getRemote());
        }
        
        //logger.println("[JaCoCo plugin] BuildENV: " +build.getEnvironment(listener).toString());
       /* try {
			ReportFactory reportFactory = new ReportFactory(new File(build.getWorkspace().getRemote()), listener); // FIXME probably doesn't work with jenkins remote build slaves
			reportFactory.createReport();
			logger.println("ReportFactory lunched!");
			
		} catch (IOException e) {
			logger.println("ReportFactory failed! WorkspaceDir: "+ build.getWorkspace().getRemote()+ e.getMessage());
		}*/
 
	     
        logger.println("\n[JaCoCo plugin] Loading EXEC files..");
        final JacocoBuildAction action = JacocoBuildAction.load(build, rule, healthReports, listener, actualBuildDirRoot);

        build.getActions().add(action);
        
        logger.println("[JaCoCo plugin] Publishing the results..");
        final CoverageReport result = action.getResult();
        if (result == null) {
            logger.println("JaCoCo: Could not parse coverage results. Setting Build to failure.");
            build.setResult(Result.FAILURE);
        }
        return true;
    }

    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        return new JacocoProjectAction(project);
    }

	@Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    /**
     * Gets the directory to store report files
     */
    static File getJacocoReport(AbstractBuild<?,?> build) {
        return new File(build.getRootDir(), "jacoco");
    }

    @Override
    public BuildStepDescriptor<Publisher> getDescriptor() {
        return DESCRIPTOR;
    }

    @Extension
    public static final BuildStepDescriptor<Publisher> DESCRIPTOR = new DescriptorImpl();

    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        public DescriptorImpl() {
            super(JacocoPublisher.class);
        }

		@Override
        public String getDisplayName() {
            return Messages.JacocoPublisher_DisplayName();
        }

		@Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

    }
    private static final Logger logger = Logger.getLogger(JacocoPublisher.class.getName());
}
