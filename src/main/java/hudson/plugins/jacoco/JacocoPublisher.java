package hudson.plugins.jacoco;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.Action;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.jacoco.portlet.utils.Constants;
import hudson.plugins.jacoco.portlet.utils.Utils;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jenkins.MasterToSlaveFileCallable;
import jenkins.tasks.SimpleBuildStep;
import org.apache.tools.ant.DirectoryScanner;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import hudson.plugins.jacoco.portlet.bean.JacocoDeltaCoverageResultSummary;

/**
 * {@link Publisher} that captures jacoco coverage reports.
 *
 * @author Kohsuke Kawaguchi
 * @author Jonathan Fuerth
 * @author Ognjen Bubalo
 * 
 */
public class JacocoPublisher extends Recorder implements SimpleBuildStep {

    /**
     * Rule to be enforced. Can be null.
     * <p>
     * TODO: define a configuration mechanism.
     */
    public Rule rule;
    @Deprecated
    public transient String includes;
    @Deprecated
    public transient int moduleNum;
    /**
     * {@link hudson.model.HealthReport} thresholds to apply.
     */
    public JacocoHealthReportThresholds healthReports;

    // Added by Aditi Rajawat for build-over-build feature
    // Delta coverage thresholds to apply
    public JacocoHealthReportDeltaThresholds deltaHealthReport;

    
    /**
     * Variables containing the configuration set by the user.
     */
    private String execPattern;
    private String classPattern;
    private String sourcePattern;
    private String inclusionPattern;
    private String exclusionPattern;

    private String minimumInstructionCoverage;
    private String minimumBranchCoverage;
    private String minimumComplexityCoverage;
    private String minimumLineCoverage;
    private String minimumMethodCoverage;
    private String minimumClassCoverage;
    private String maximumInstructionCoverage;
    private String maximumBranchCoverage;
    private String maximumComplexityCoverage;
    private String maximumLineCoverage;
    private String maximumMethodCoverage;
    private String maximumClassCoverage;
    private boolean changeBuildStatus;

    /** Added by Aditi Rajawat for build-over-build feature
     * Following variables contain delta coverage thresholds as configured by the user
     * Delta coverage = | Last Successful Coverage - Current Coverage |
     */
    private String deltaInstructionCoverage;
    private String deltaBranchCoverage;
    private String deltaComplexityCoverage;
    private String deltaLineCoverage;
    private String deltaMethodCoverage;
    private String deltaClassCoverage;
    private boolean buildOverBuild;
    
	private static final String DIR_SEP = "\\s*,\\s*";

    @DataBoundConstructor
    public JacocoPublisher() {
        this.execPattern = "**/**.exec";
        this.classPattern = "**/classes";
        this.sourcePattern = "**/src/main/java";
        this.inclusionPattern = "";
        this.exclusionPattern = "";
        this.minimumInstructionCoverage = "0";
        this.minimumBranchCoverage = "0";
        this.minimumComplexityCoverage = "0";
        this.minimumLineCoverage = "0";
        this.minimumMethodCoverage = "0";
        this.minimumClassCoverage = "0";
        this.maximumInstructionCoverage = "0";
        this.maximumBranchCoverage = "0";
        this.maximumComplexityCoverage = "0";
        this.maximumLineCoverage = "0";
        this.maximumMethodCoverage = "0";
        this.maximumClassCoverage = "0";
        this.changeBuildStatus = false;
        // Added by Aditi Rajawat for build-over-build feature
        // Updated DataBoundConstructor for the new build-over-build variables
        this.deltaInstructionCoverage = "0";
        this.deltaBranchCoverage = "0";
        this.deltaComplexityCoverage = "0";
        this.deltaLineCoverage = "0";
        this.deltaMethodCoverage = "0";
        this.deltaClassCoverage = "0";
        this.buildOverBuild = false;
    }

	/**
     * Loads the configuration set by user.
     */
    @Deprecated
    public JacocoPublisher(String execPattern, String classPattern, String sourcePattern, String inclusionPattern, String exclusionPattern, String maximumInstructionCoverage, String maximumBranchCoverage
    		, String maximumComplexityCoverage, String maximumLineCoverage, String maximumMethodCoverage, String maximumClassCoverage, String minimumInstructionCoverage, String minimumBranchCoverage
    		, String minimumComplexityCoverage, String minimumLineCoverage, String minimumMethodCoverage, String minimumClassCoverage, boolean changeBuildStatus,
                           String deltaInstructionCoverage, String deltaBranchCoverage, String deltaComplexityCoverage, String deltaLineCoverage, String deltaMethodCoverage, String deltaClassCoverage, boolean buildOverBuild) {
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
        // Added by Aditi Rajawat for build-over-build feature
        // Updated deprecated constructor for the new build-over-build variables
        this.deltaInstructionCoverage = deltaInstructionCoverage;
        this.deltaBranchCoverage = deltaBranchCoverage;
        this.deltaComplexityCoverage = deltaComplexityCoverage;
        this.deltaLineCoverage = deltaLineCoverage;
        this.deltaMethodCoverage = deltaMethodCoverage;
        this.deltaClassCoverage = deltaClassCoverage;
        this.buildOverBuild = buildOverBuild;
    }
    
    public String checkThresholdInput(String input) {
    	if ((input == null) || ("".equals(input))) {
    		return 0+"";
    	}
    	try {
            //noinspection ResultOfMethodCallIgnored
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
				+ ", maximumClassCoverage=" + maximumClassCoverage
                // Added by Aditi Rajawat for build-over-build feature
                // Updated toString() for the new build-over-build variables
                + ", deltaInstructionCoverage=" + deltaInstructionCoverage
                + ", deltaBranchCoverage=" + deltaBranchCoverage
                + ", deltaComplexityCoverage=" + deltaComplexityCoverage
                + ", deltaLineCoverage=" + deltaLineCoverage
                + ", deltaMethodCoverage=" + deltaMethodCoverage
                + ", deltaClassCoverage=" + deltaClassCoverage
                + "]";
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

	// Added by Aditi Rajawat for build-over-build feature
    // Getter methods for delta coverage thresholds and build over build flag
    public String getDeltaInstructionCoverage() {
        return deltaInstructionCoverage;
    }

    public String getDeltaBranchCoverage() {
        return deltaBranchCoverage;
    }

    public String getDeltaComplexityCoverage() {
        return deltaComplexityCoverage;
    }

    public String getDeltaLineCoverage() {
        return deltaLineCoverage;
    }

    public String getDeltaMethodCoverage() {
        return deltaMethodCoverage;
    }

    public String getDeltaClassCoverage() {
        return deltaClassCoverage;
    }

    public boolean isBuildOverBuild() {
        return buildOverBuild;
    }

    @DataBoundSetter
    public void setExecPattern(String execPattern) {
        this.execPattern = execPattern;
    }

    @DataBoundSetter
    public void setClassPattern(String classPattern) {
        this.classPattern = classPattern;
    }

    @DataBoundSetter
    public void setSourcePattern(String sourcePattern) {
        this.sourcePattern = sourcePattern;
    }

    @DataBoundSetter
    public void setMinimumInstructionCoverage(String minimumInstructionCoverage) {
        this.minimumInstructionCoverage = checkThresholdInput(minimumInstructionCoverage);
    }

    @DataBoundSetter
    public void setMinimumBranchCoverage(String minimumBranchCoverage) {
        this.minimumBranchCoverage = checkThresholdInput(minimumBranchCoverage);
    }

    @DataBoundSetter
    public void setMinimumComplexityCoverage(String minimumComplexityCoverage) {
        this.minimumComplexityCoverage = checkThresholdInput(minimumComplexityCoverage);
    }

    @DataBoundSetter
    public void setMinimumLineCoverage(String minimumLineCoverage) {
        this.minimumLineCoverage = checkThresholdInput(minimumLineCoverage);
    }

    @DataBoundSetter
    public void setMinimumMethodCoverage(String minimumMethodCoverage) {
        this.minimumMethodCoverage = checkThresholdInput(minimumMethodCoverage);
    }

    @DataBoundSetter
    public void setMinimumClassCoverage(String minimumClassCoverage) {
        this.minimumClassCoverage = checkThresholdInput(minimumClassCoverage);
    }

    @DataBoundSetter
    public void setMaximumInstructionCoverage(String maximumInstructionCoverage) {
        this.maximumInstructionCoverage = checkThresholdInput(maximumInstructionCoverage);
    }

    @DataBoundSetter
    public void setMaximumBranchCoverage(String maximumBranchCoverage) {
        this.maximumBranchCoverage = checkThresholdInput(maximumBranchCoverage);
    }

    @DataBoundSetter
    public void setMaximumComplexityCoverage(String maximumComplexityCoverage) {
        this.maximumComplexityCoverage = checkThresholdInput(maximumComplexityCoverage);
    }

    @DataBoundSetter
    public void setMaximumLineCoverage(String maximumLineCoverage) {
        this.maximumLineCoverage = checkThresholdInput(maximumLineCoverage);
    }

    @DataBoundSetter
    public void setMaximumMethodCoverage(String maximumMethodCoverage) {
        this.maximumMethodCoverage = checkThresholdInput(maximumMethodCoverage);
    }

    @DataBoundSetter
    public void setMaximumClassCoverage(String maximumClassCoverage) {
        this.maximumClassCoverage = checkThresholdInput(maximumClassCoverage);
    }

    @DataBoundSetter
    public void setChangeBuildStatus(boolean changeBuildStatus) {
        this.changeBuildStatus = changeBuildStatus;
    }

    @DataBoundSetter
    public void setInclusionPattern(String inclusionPattern) {
        this.inclusionPattern = inclusionPattern;
    }

    @DataBoundSetter
    public void setExclusionPattern(String exclusionPattern) {
        this.exclusionPattern = exclusionPattern;
    }

    // Added by Aditi Rajawat for build-over-build feature
    // Setter methods for delta coverage thresholds and build over build flag
    @DataBoundSetter
    public void setDeltaInstructionCoverage(String deltaInstructionCoverage) {
        this.deltaInstructionCoverage = deltaInstructionCoverage;
    }

    @DataBoundSetter
    public void setDeltaBranchCoverage(String deltaBranchCoverage) {
        this.deltaBranchCoverage = deltaBranchCoverage;
    }

    @DataBoundSetter
    public void setDeltaComplexityCoverage(String deltaComplexityCoverage) {
        this.deltaComplexityCoverage = deltaComplexityCoverage;
    }

    @DataBoundSetter
    public void setDeltaLineCoverage(String deltaLineCoverage) {
        this.deltaLineCoverage = deltaLineCoverage;
    }

    @DataBoundSetter
    public void setDeltaMethodCoverage(String deltaMethodCoverage) {
        this.deltaMethodCoverage = deltaMethodCoverage;
    }

    @DataBoundSetter
    public void setDeltaClassCoverage(String deltaClassCoverage) {
        this.deltaClassCoverage = deltaClassCoverage;
    }

    @DataBoundSetter
    public void setBuildOverBuild(boolean buildOverBuild) {
        this.buildOverBuild = buildOverBuild;
    }

	protected static void saveCoverageReports(FilePath destFolder, FilePath sourceFolder) throws IOException, InterruptedException {
		destFolder.mkdirs();
		
		sourceFolder.copyRecursiveTo(destFolder);
	}
	
    protected String resolveFilePaths(Run<?, ?> build, TaskListener listener, String input, Map<String, String> env) {
        try {

            final EnvVars environment = build.getEnvironment(listener);
            environment.overrideAll(env);
            return environment.expand(input);
            
        } catch (Exception e) {
            listener.getLogger().println("Failed to resolve parameters in string \""+
            input+"\" due to following error:\n"+e.getMessage());
        }
        return input;
    }

    protected String resolveFilePaths(AbstractBuild<?, ?> build, TaskListener listener, String input) {
        try {

            final EnvVars environment = build.getEnvironment(listener);
            environment.overrideAll(build.getBuildVariables());
            return environment.expand(input);

        } catch (Exception e) {
            listener.getLogger().println("Failed to resolve parameters in string \""+
                    input+"\" due to following error:\n"+e.getMessage());
        }
        return input;
    }

    protected static FilePath[] resolveDirPaths(FilePath workspace, TaskListener listener, final String input) {
		//final PrintStream logger = listener.getLogger();
		FilePath[] directoryPaths = null;
		try {
            directoryPaths = workspace.act(new ResolveDirPaths(input));
		} catch(InterruptedException ie) {
			ie.printStackTrace();
		} catch(IOException io) {
			io.printStackTrace();
		}
		return directoryPaths;
	}


    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener taskListener) throws InterruptedException, IOException {
        Map<String, String> envs = run instanceof AbstractBuild ? ((AbstractBuild) run).getBuildVariables() : Collections.<String, String>emptyMap();

        healthReports = createJacocoHealthReportThresholds();

        // Added by Aditi Rajawat for build-over-build feature
        // Initialize delta health report with user-configured threshold values
        deltaHealthReport = createJacocoDeltaHealthReportThresholds();

        if (run.getResult() == Result.FAILURE || run.getResult() == Result.ABORTED) {
            return;
        }

        final PrintStream logger = taskListener.getLogger();
        logger.println("[JaCoCo plugin] Collecting JaCoCo coverage data...");

        EnvVars env = run.getEnvironment(taskListener);
        env.overrideAll(envs);

        if ((execPattern==null) || (classPattern==null) || (sourcePattern==null)) {
            if(run.getResult().isWorseThan(Result.UNSTABLE)) {
                return;
            }

            logger.println("[JaCoCo plugin] ERROR: Missing configuration!");
            run.setResult(Result.FAILURE);
            return;
        }

        logger.println("[JaCoCo plugin] " + execPattern + ";" + classPattern +  ";" + sourcePattern + ";" + " locations are configured");

        JacocoReportDir dir = new JacocoReportDir(run.getRootDir());

        if (run instanceof AbstractBuild) {
            execPattern = resolveFilePaths((AbstractBuild) run, taskListener, execPattern);
        }

        List<FilePath> matchedExecFiles = Arrays.asList(filePath.list(resolveFilePaths(run, taskListener, execPattern, env)));
        logger.println("[JaCoCo plugin] Number of found exec files for pattern " + execPattern + ": " + matchedExecFiles.size());
        logger.print("[JaCoCo plugin] Saving matched execfiles: ");
        dir.addExecFiles(matchedExecFiles);
        logger.print(" " + Util.join(matchedExecFiles," "));
        FilePath[] matchedClassDirs = resolveDirPaths(filePath, taskListener, classPattern);
        logger.print("\n[JaCoCo plugin] Saving matched class directories for class-pattern: " + classPattern + ": ");
        for (FilePath file : matchedClassDirs) {
            dir.saveClassesFrom(file);
            logger.print(" " + file);
        }
        FilePath[] matchedSrcDirs = resolveDirPaths(filePath, taskListener, sourcePattern);
        logger.print("\n[JaCoCo plugin] Saving matched source directories for source-pattern: " + sourcePattern + ": ");
        for (FilePath file : matchedSrcDirs) {
            dir.saveSourcesFrom(file);
            logger.print(" " + file);
        }

        logger.println("\n[JaCoCo plugin] Loading inclusions files..");
        String[] includes = {};
        if (inclusionPattern != null) {
            String expandedInclusion = env.expand(inclusionPattern);
            includes = expandedInclusion.split(DIR_SEP);
            logger.println("[JaCoCo plugin] inclusions: " + Arrays.toString(includes));
        }
        String[] excludes = {};
        if (exclusionPattern != null) {
            String expandedExclusion = env.expand(exclusionPattern);
            excludes = expandedExclusion.split(DIR_SEP);
            logger.println("[JaCoCo plugin] exclusions: " + Arrays.toString(excludes));
        }

        final JacocoBuildAction action = JacocoBuildAction.load(run, healthReports, taskListener, dir, includes, excludes);
        action.getThresholds().ensureValid();
        logger.println("[JaCoCo plugin] Thresholds: " + action.getThresholds());
        run.addAction(action);

        logger.println("[JaCoCo plugin] Publishing the results..");
        final CoverageReport result = action.getResult();

        if (result == null) {
            logger.println("[JaCoCo plugin] Could not parse coverage results. Setting Build to failure.");
            run.setResult(Result.FAILURE);
        } else {
            logger.println("[JaCoCo plugin] Overall coverage: class: " + result.getClassCoverage().getPercentage()
                    + ", method: " + result.getMethodCoverage().getPercentage()
                    + ", line: " + result.getLineCoverage().getPercentage()
                    + ", branch: " + result.getBranchCoverage().getPercentage()
                    + ", instruction: " + result.getInstructionCoverage().getPercentage());
            result.setThresholds(healthReports);

            // Modified by Aditi Rajawat for build-over-build feature
            // Calculate final result of the current build according to the state of two flags: changeBuildStatus and buildOverBuild
            // Final result is the logical AND of two operation results
            // Initializing individual operation result as SUCCESS to eliminate impact if the corresponding flag is not set
            Result applyMinMaxTh = Result.SUCCESS, applyDeltaTh = Result.SUCCESS;
            if (changeBuildStatus) {
                applyMinMaxTh = checkResult(action); // Compare current coverage with minimum and maximum coverage thresholds
                logger.println("[JaCoCo plugin] Health thresholds "+ healthReports.toString());
                logger.println("[JaCoCo plugin] Apply Min/Max thresholds result: "+ applyMinMaxTh.toString());
            }
            if(buildOverBuild){
                applyDeltaTh = checkBuildOverBuildResult(run); // Compute delta coverage of current build and compare with delta thresholds
                logger.println("[JaCoCo plugin] Delta Thresholds "+ deltaHealthReport.toString());
                logger.println("[JaCoCo plugin] Build over build result: "+ applyDeltaTh.toString());
            }
            if(changeBuildStatus || buildOverBuild) {
                run.setResult(Utils.applyLogicalAnd(applyMinMaxTh, applyDeltaTh));
            }
        }
    }

    private JacocoHealthReportThresholds createJacocoHealthReportThresholds() {
        try {
            return healthReports = new JacocoHealthReportThresholds(Integer.parseInt(minimumClassCoverage), Integer.parseInt(maximumClassCoverage), Integer.parseInt(minimumMethodCoverage), Integer.parseInt(maximumMethodCoverage), Integer.parseInt(minimumLineCoverage), Integer.parseInt(maximumLineCoverage)
                    , Integer.parseInt(minimumBranchCoverage), Integer.parseInt(maximumBranchCoverage), Integer.parseInt(minimumInstructionCoverage), Integer.parseInt(maximumInstructionCoverage), Integer.parseInt(minimumComplexityCoverage), Integer.parseInt(maximumComplexityCoverage));
        } catch (NumberFormatException nfe) {
            return healthReports = new JacocoHealthReportThresholds(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        }
    }

    // Added by Aditi Rajawat for build-over-build feature
    /**
     * Creates JacocoHealthReportDeltaThresholds object to encapsulate user configured delta threshold values.
     * The values entered by the user are validated to be in range of [0, 100] percentage
     */
    private JacocoHealthReportDeltaThresholds createJacocoDeltaHealthReportThresholds(){
        JacocoHealthReportDeltaThresholds jacocoHealthReportDeltaThresholds = new JacocoHealthReportDeltaThresholds(this.deltaInstructionCoverage, this.deltaBranchCoverage, this.deltaComplexityCoverage, this.deltaLineCoverage, this.deltaMethodCoverage, this.deltaClassCoverage);
        return jacocoHealthReportDeltaThresholds;
    }

    public static Result checkResult(JacocoBuildAction action) {
		if ((action.getBranchCoverage().getPercentageFloat() < action.getThresholds().getMinBranch()) || (action.getInstructionCoverage().getPercentageFloat() < action.getThresholds().getMinInstruction())  || (action.getClassCoverage().getPercentageFloat() < action.getThresholds().getMinClass())  || (action.getLineCoverage().getPercentageFloat() < action.getThresholds().getMinLine())  || (action.getComplexityScore().getPercentageFloat() < action.getThresholds().getMinComplexity())  || (action.getMethodCoverage().getPercentageFloat() < action.getThresholds().getMinMethod())) {
			return Result.FAILURE;
		}
		if ((action.getBranchCoverage().getPercentageFloat() < action.getThresholds().getMaxBranch()) || (action.getInstructionCoverage().getPercentageFloat() < action.getThresholds().getMaxInstruction())  || (action.getClassCoverage().getPercentageFloat() < action.getThresholds().getMaxClass())  || (action.getLineCoverage().getPercentageFloat() < action.getThresholds().getMaxLine())  || (action.getComplexityScore().getPercentageFloat() < action.getThresholds().getMaxComplexity())  || (action.getMethodCoverage().getPercentageFloat() < action.getThresholds().getMaxMethod())) {
			return Result.UNSTABLE;
		}
		return Result.SUCCESS;
	}

	// Added by Aditi Rajawat for build-over-build feature
    // Calculates actual delta coverage of the current build by subtracting it's coverage from coverage of last successful build
    // and compares if the delta coverage is less than or equal to user-configured delta thresholds
    // Returns success (if delta coverage is equal to or less than delta thresholds) OR (if delta coverage is bigger than delta thresholds AND current coverage is bigger than last successful coverage)
    public Result checkBuildOverBuildResult(Run<?,?> run){

        JacocoDeltaCoverageResultSummary deltaCoverageResultSummary = JacocoDeltaCoverageResultSummary.build(run);

        if(Utils.isEqualOrLessThan(deltaCoverageResultSummary.getInstructionCoverage(), deltaHealthReport.getDeltaInstruction()) &&
                (Utils.isEqualOrLessThan(deltaCoverageResultSummary.getBranchCoverage(), deltaHealthReport.getDeltaBranch())) &&
                (Utils.isEqualOrLessThan(deltaCoverageResultSummary.getComplexityCoverage(), deltaHealthReport.getDeltaComplexity())) &&
                (Utils.isEqualOrLessThan(deltaCoverageResultSummary.getLineCoverage(), deltaHealthReport.getDeltaLine())) &&
                (Utils.isEqualOrLessThan(deltaCoverageResultSummary.getMethodCoverage(), deltaHealthReport.getDeltaMethod())) &&
                (Utils.isEqualOrLessThan(deltaCoverageResultSummary.getClassCoverage(), deltaHealthReport.getDeltaClass())))
            return Result.SUCCESS;
        else if(deltaCoverageResultSummary.isCoverageBetterThanPrevious())
            return Result.SUCCESS;
        else
            return Result.FAILURE;
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

    private static class ResolveDirPaths extends MasterToSlaveFileCallable<FilePath[]> {
        static final long serialVersionUID = 1552178457453558870L;
        private final String input;

        public ResolveDirPaths(String input) {
            this.input = input;
        }

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

    }

    //private static final Logger logger = Logger.getLogger(JacocoPublisher.class.getName());
}
