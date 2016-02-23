package hudson.plugins.jacoco;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
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
import java.util.Arrays;
import java.util.List;

import org.apache.tools.ant.DirectoryScanner;
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
    public JacocoHealthReportThresholds healthReports;

    
    /**
     * Variables containing the configuration set by the user.
     */
	private final String execPattern;
	private final String classPattern;
	private final String sourcePattern;
	private final String inclusionPattern;
	private final String exclusionPattern;
	
    
    private final String minimumInstructionCoverage;
    private final String minimumBranchCoverage;
    private final String minimumComplexityCoverage;
    private final String minimumLineCoverage;
    private final String minimumMethodCoverage;
    private final String minimumClassCoverage;
    private final String maximumInstructionCoverage;
    private final String maximumBranchCoverage;
    private final String maximumComplexityCoverage;
    private final String maximumLineCoverage;
    private final String maximumMethodCoverage;
    private final String maximumClassCoverage;
    private final boolean changeBuildStatus;
    
	private static final String DIR_SEP = "\\s*,\\s*";

	/**
     * Loads the configuration set by user.
     */
    @DataBoundConstructor
    public JacocoPublisher(String execPattern, String classPattern, String sourcePattern, String inclusionPattern, String exclusionPattern, String maximumInstructionCoverage, String maximumBranchCoverage
    		, String maximumComplexityCoverage, String maximumLineCoverage, String maximumMethodCoverage, String maximumClassCoverage, String minimumInstructionCoverage, String minimumBranchCoverage
    		, String minimumComplexityCoverage, String minimumLineCoverage, String minimumMethodCoverage, String minimumClassCoverage, boolean changeBuildStatus) {
    	this.execPattern = execPattern;
    	this.classPattern = classPattern;
    	this.sourcePattern = sourcePattern;
    	this.inclusionPattern = inclusionPattern;
    	this.exclusionPattern = exclusionPattern;
    	this.minimumInstructionCoverage = checkThresholdInput(minimumInstructionCoverage);
    	this.minimumBranchCoverage = checkThresholdInput(minimumBranchCoverage);
    	this.minimumComplexityCoverage = checkThresholdInput(minimumComplexityCoverage);
    	this.minimumLineCoverage = checkThresholdInput(minimumLineCoverage);
    	this.minimumMethodCoverage = checkThresholdInput(minimumMethodCoverage);
    	this.minimumClassCoverage = checkThresholdInput(minimumClassCoverage);
    	this.maximumInstructionCoverage = checkThresholdInput(maximumInstructionCoverage);
    	this.maximumBranchCoverage = checkThresholdInput(maximumBranchCoverage);
    	this.maximumComplexityCoverage = checkThresholdInput(maximumComplexityCoverage);
    	this.maximumLineCoverage = checkThresholdInput(maximumLineCoverage);
    	this.maximumMethodCoverage = checkThresholdInput(maximumMethodCoverage);
    	this.maximumClassCoverage = checkThresholdInput(maximumClassCoverage);
    	this.changeBuildStatus = changeBuildStatus;
    }
    
    public String checkThresholdInput(String input) {
    	if ((input == null) || ("".equals(input))) {
    		return 0+"";
    	}
    	try {
    		Integer.parseInt(input);
    	} catch(NumberFormatException nfe) {
    		return  0+"";
    	}
    	return input;
    }


	@Override
	public String toString() {
		return "JacocoPublisher [execPattern=" + execPattern
				+ ", classPattern=" + classPattern + ", sourcePattern="
				+ sourcePattern + ", inclusionPattern=" + inclusionPattern
				+ ", exclusionPattern=" + exclusionPattern
				+ ", minimumInstructionCoverage=" + minimumInstructionCoverage
				+ ", minimumBranchCoverage=" + minimumBranchCoverage
				+ ", minimumComplexityCoverage=" + minimumComplexityCoverage
				+ ", minimumLineCoverage=" + minimumLineCoverage
				+ ", minimumMethodCoverage=" + minimumMethodCoverage
				+ ", minimumClassCoverage=" + minimumClassCoverage
				+ ", maximumInstructionCoverage=" + maximumInstructionCoverage
				+ ", maximumBranchCoverage=" + maximumBranchCoverage
				+ ", maximumComplexityCoverage=" + maximumComplexityCoverage
				+ ", maximumLineCoverage=" + maximumLineCoverage
				+ ", maximumMethodCoverage=" + maximumMethodCoverage
				+ ", maximumClassCoverage=" + maximumClassCoverage + "]";
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
	
	public String getInclusionPattern() {
		return inclusionPattern;
	}

	public String getExclusionPattern() {
		return exclusionPattern;
	}



	public String getMinimumInstructionCoverage() {
		return minimumInstructionCoverage;
	}



	public String getMinimumBranchCoverage() {
		return minimumBranchCoverage;
	}



	public String getMinimumComplexityCoverage() {
		return minimumComplexityCoverage;
	}



	public String getMinimumLineCoverage() {
		return minimumLineCoverage;
	}



	public String getMinimumMethodCoverage() {
		return minimumMethodCoverage;
	}



	public String getMinimumClassCoverage() {
		return minimumClassCoverage;
	}



	public String getMaximumInstructionCoverage() {
		return maximumInstructionCoverage;
	}



	public String getMaximumBranchCoverage() {
		return maximumBranchCoverage;
	}



	public String getMaximumComplexityCoverage() {
		return maximumComplexityCoverage;
	}



	public String getMaximumLineCoverage() {
		return maximumLineCoverage;
	}



	public String getMaximumMethodCoverage() {
		return maximumMethodCoverage;
	}



	public String getMaximumClassCoverage() {
		return maximumClassCoverage;
	}


	public boolean isChangeBuildStatus() {
		return changeBuildStatus;
	}
	
    public boolean getChangeBuildStatus() {
		return changeBuildStatus;
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
		//final PrintStream logger = listener.getLogger();
		FilePath[] directoryPaths = null;
		try {
			directoryPaths = build.getWorkspace().act(new FilePath.FileCallable<FilePath[]>() 
			{
				static final long serialVersionUID = 1552178457453558870L;

				public FilePath[] invoke(File f, VirtualChannel channel) throws IOException {
					FilePath base = new FilePath(f);
					ArrayList<FilePath> localDirectoryPaths= new ArrayList<FilePath>();
					String[] includes = input.split(DIR_SEP);
					DirectoryScanner ds = new DirectoryScanner();
			        
			        ds.setIncludes(includes);
			        ds.setCaseSensitive(false);
			        ds.setBasedir(f);
			        ds.scan();
			        String[] dirs = ds.getIncludedDirectories();
			        
			        for (String dir : dirs) {
                        localDirectoryPaths.add(base.child(dir));
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
    @SuppressWarnings("resource")
    @Override
    public boolean perform(AbstractBuild<?,?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
	
		final PrintStream logger = listener.getLogger();
		FilePath[] matchedClassDirs = null;
		FilePath[] matchedSrcDirs = null;

		if (build.getResult() == Result.FAILURE || build.getResult() == Result.ABORTED) {
			return true;
		}
		
		
		logger.println("[JaCoCo plugin] Collecting JaCoCo coverage data...");
		
		
		EnvVars env = build.getEnvironment(listener);
        env.overrideAll(build.getBuildVariables());
        try {
        	healthReports = new JacocoHealthReportThresholds(Integer.parseInt(minimumClassCoverage), Integer.parseInt(maximumClassCoverage), Integer.parseInt(minimumMethodCoverage), Integer.parseInt(maximumMethodCoverage), Integer.parseInt(minimumLineCoverage), Integer.parseInt(maximumLineCoverage)
        			,Integer.parseInt(minimumBranchCoverage), Integer.parseInt(maximumBranchCoverage), Integer.parseInt(minimumInstructionCoverage), Integer.parseInt(maximumInstructionCoverage), Integer.parseInt(minimumComplexityCoverage), Integer.parseInt(maximumComplexityCoverage));
        } catch(NumberFormatException nfe) {
        	healthReports = new JacocoHealthReportThresholds(0,0,0,0,0,0,0,0,0,0,0,0);
        }		
        
        if ((execPattern==null) || (classPattern==null) || (sourcePattern==null)) {
            if(build.getResult().isWorseThan(Result.UNSTABLE)) {
                return true;
            }
            
            logger.println("[JaCoCo plugin] ERROR: Missing configuration!");
            build.setResult(Result.FAILURE);
            return true;
        }
        
        logger.println("[JaCoCo plugin] " + execPattern + ";" + classPattern +  ";" + sourcePattern + ";" + " locations are configured");

        JacocoReportDir dir = new JacocoReportDir(build);

        List<FilePath> matchedExecFiles = Arrays.asList(build.getWorkspace().list(resolveFilePaths(build, listener, execPattern)));
        logger.println("[JaCoCo plugin] Number of found exec files for pattern " + execPattern + ": " + matchedExecFiles.size());
        logger.print("[JaCoCo plugin] Saving matched execfiles: ");
        dir.addExecFiles(matchedExecFiles);
        logger.print(" " + Util.join(matchedExecFiles," "));
        matchedClassDirs = resolveDirPaths(build, listener, classPattern);
        logger.print("\n[JaCoCo plugin] Saving matched class directories for class-pattern: " + classPattern + ": ");
        for (FilePath file : matchedClassDirs) {
            dir.saveClassesFrom(file);
        	logger.print(" " + file);
        }
        matchedSrcDirs = resolveDirPaths(build, listener, sourcePattern);
        logger.print("\n[JaCoCo plugin] Saving matched source directories for source-pattern: " + sourcePattern + ": ");
        for (FilePath file : matchedSrcDirs) {
            dir.saveSourcesFrom(file);
        	logger.print(" " + file);
        }
	     
        logger.println("\n[JaCoCo plugin] Loading inclusions files..");
        String[] includes = {};
        if (inclusionPattern != null) {
        	includes = inclusionPattern.split(DIR_SEP);
        	logger.println("[JaCoCo plugin] inclusions: " + Arrays.toString(includes));
        }
        String[] excludes = {};
        if (exclusionPattern != null) {
        	excludes = exclusionPattern.split(DIR_SEP);
        	logger.println("[JaCoCo plugin] exclusions: " + Arrays.toString(excludes));
        }
        
        final JacocoBuildAction action = JacocoBuildAction.load(build, rule, healthReports, listener, dir, includes, excludes);
        action.getThresholds().ensureValid();
        logger.println("[JaCoCo plugin] Thresholds: " + action.getThresholds());
        build.getActions().add(action);
        
        logger.println("[JaCoCo plugin] Publishing the results..");
        final CoverageReport result = action.getResult();
        
        if (result == null) {
            logger.println("[JaCoCo plugin] Could not parse coverage results. Setting Build to failure.");
            build.setResult(Result.FAILURE);
        } else {
            logger.println("[JaCoCo plugin] Overall coverage: class: " + result.getClassCoverage().getPercentage()
                    + ", method: " + result.getMethodCoverage().getPercentage()
                    + ", line: " + result.getLineCoverage().getPercentage()
                    + ", branch: " + result.getBranchCoverage().getPercentage()
                    + ", instruction: " + result.getInstructionCoverage().getPercentage());
        	result.setThresholds(healthReports);
        	if (changeBuildStatus) {
        		build.setResult(checkResult(action));
        	}
        }
        return true;
    }

	public Result checkResult(JacocoBuildAction action) {
		if ((action.getBranchCoverage().getPercentageFloat() < action.getThresholds().getMinBranch()) || (action.getInstructionCoverage().getPercentageFloat() < action.getThresholds().getMinInstruction())  || (action.getClassCoverage().getPercentageFloat() < action.getThresholds().getMinClass())  || (action.getLineCoverage().getPercentageFloat() < action.getThresholds().getMinLine())  || (action.getComplexityScore().getPercentageFloat() < action.getThresholds().getMinComplexity())  || (action.getMethodCoverage().getPercentageFloat() < action.getThresholds().getMinMethod())) {
			return Result.FAILURE;
		}
		if ((action.getBranchCoverage().getPercentageFloat() < action.getThresholds().getMaxBranch()) || (action.getInstructionCoverage().getPercentageFloat() < action.getThresholds().getMaxInstruction())  || (action.getClassCoverage().getPercentageFloat() < action.getThresholds().getMaxClass())  || (action.getLineCoverage().getPercentageFloat() < action.getThresholds().getMaxLine())  || (action.getComplexityScore().getPercentageFloat() < action.getThresholds().getMaxComplexity())  || (action.getMethodCoverage().getPercentageFloat() < action.getThresholds().getMaxMethod())) {
			return Result.UNSTABLE;
		}
		return Result.SUCCESS;
	}
	
    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        return new JacocoProjectAction(project);
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
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
		
		/*@Override
        public Publisher newInstance(StaplerRequest req, JSONObject json) throws FormException {
            JacocoPublisher pub = new JacocoPublisher();
            req.bindParameters(pub, "jacoco.");
            req.bindParameters(pub.healthReports, "jacocoHealthReports.");
            // start ugly hack
            //@TODO remove ugly hack
            // the default converter for integer values used by req.bindParameters
            // defaults an empty value to 0. This happens even if the type is Integer
            // and not int.  We want to change the default values, so we use this hack.
            //
            // If you know a better way, please fix.
            if ("".equals(req.getParameter("jacocoHealthReports.maxClass"))) {
                pub.healthReports.setMaxClass(100);
            }
            if ("".equals(req.getParameter("jacocoHealthReports.maxMethod"))) {
                pub.healthReports.setMaxMethod(70);
            }
            if ("".equals(req.getParameter("jacocoHealthReports.maxLine"))) {
                pub.healthReports.setMaxLine(70);
            }
            if ("".equals(req.getParameter("jacocoHealthReports.maxBranch"))) {
                pub.healthReports.setMaxBranch(70);
            }
            if ("".equals(req.getParameter("jacocoHealthReports.maxInstruction"))) {
                pub.healthReports.setMaxInstruction(70);
            }
            if ("".equals(req.getParameter("jacocoHealthReports.maxComplexity"))) {
                pub.healthReports.setMaxComplexity(70);
            }
            // end ugly hack
            return pub;
        }*/

    }
    
    //private static final Logger logger = Logger.getLogger(JacocoPublisher.class.getName());
}
