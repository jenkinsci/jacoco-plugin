package hudson.plugins.jacoco;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
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
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildStep;
import org.apache.tools.ant.DirectoryScanner;
import org.jenkinsci.Symbol;
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
    private boolean skipCopyOfSrcFiles; // Added for enabling/disabling copy of source files

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

    /**
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

    private static final Integer THRESHOLD_DEFAULT = 0;

    @DataBoundConstructor
    public JacocoPublisher() {
        this.execPattern = "**/**.exec";
        this.classPattern = "**/classes";
        this.sourcePattern = "**/src/main/java";
        this.inclusionPattern = "";
        this.exclusionPattern = "";
        this.skipCopyOfSrcFiles = false;
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
    public JacocoPublisher(String execPattern, String classPattern, String sourcePattern, String inclusionPattern, String exclusionPattern, boolean skipCopyOfSrcFiles, String maximumInstructionCoverage, String maximumBranchCoverage
    		, String maximumComplexityCoverage, String maximumLineCoverage, String maximumMethodCoverage, String maximumClassCoverage, String minimumInstructionCoverage, String minimumBranchCoverage
    		, String minimumComplexityCoverage, String minimumLineCoverage, String minimumMethodCoverage, String minimumClassCoverage, boolean changeBuildStatus,
                           String deltaInstructionCoverage, String deltaBranchCoverage, String deltaComplexityCoverage, String deltaLineCoverage, String deltaMethodCoverage, String deltaClassCoverage, boolean buildOverBuild) {
    	this.execPattern = execPattern;
    	this.classPattern = classPattern;
    	this.sourcePattern = sourcePattern;
    	this.inclusionPattern = inclusionPattern;
    	this.exclusionPattern = exclusionPattern;
        this.skipCopyOfSrcFiles = skipCopyOfSrcFiles;
    	this.minimumInstructionCoverage = minimumInstructionCoverage;
    	this.minimumBranchCoverage = minimumBranchCoverage;
    	this.minimumComplexityCoverage = minimumComplexityCoverage;
    	this.minimumLineCoverage = minimumLineCoverage;
    	this.minimumMethodCoverage = minimumMethodCoverage;
    	this.minimumClassCoverage = minimumClassCoverage;
    	this.maximumInstructionCoverage = maximumInstructionCoverage;
    	this.maximumBranchCoverage = maximumBranchCoverage;
    	this.maximumComplexityCoverage = maximumComplexityCoverage;
    	this.maximumLineCoverage = maximumLineCoverage;
    	this.maximumMethodCoverage = maximumMethodCoverage;
    	this.maximumClassCoverage = maximumClassCoverage;
    	this.changeBuildStatus = changeBuildStatus;
        this.deltaInstructionCoverage = deltaInstructionCoverage;
        this.deltaBranchCoverage = deltaBranchCoverage;
        this.deltaComplexityCoverage = deltaComplexityCoverage;
        this.deltaLineCoverage = deltaLineCoverage;
        this.deltaMethodCoverage = deltaMethodCoverage;
        this.deltaClassCoverage = deltaClassCoverage;
        this.buildOverBuild = buildOverBuild;
    }
    
    private Integer convertThresholdInputToInteger(String input, EnvVars env) {
    	if ((input == null) || ("".equals(input))) {
    		return THRESHOLD_DEFAULT;
    	}
    	try {
    		String expandedInput = env.expand(input);
            //noinspection ResultOfMethodCallIgnored
            return Integer.parseInt(expandedInput);
    	} catch(NumberFormatException nfe) {
    		return  THRESHOLD_DEFAULT;
    	}
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

    public boolean isSkipCopyOfSrcFiles() {
        return skipCopyOfSrcFiles;
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
    public void setSkipCopyOfSrcFiles(boolean skipCopyOfSrcFiles) {
        this.skipCopyOfSrcFiles = skipCopyOfSrcFiles;
    }

    @DataBoundSetter
    public void setMinimumInstructionCoverage(String minimumInstructionCoverage) {
        this.minimumInstructionCoverage = minimumInstructionCoverage;
    }

    @DataBoundSetter
    public void setMinimumBranchCoverage(String minimumBranchCoverage) {
        this.minimumBranchCoverage = minimumBranchCoverage;
    }

    @DataBoundSetter
    public void setMinimumComplexityCoverage(String minimumComplexityCoverage) {
        this.minimumComplexityCoverage = minimumComplexityCoverage;
    }

    @DataBoundSetter
    public void setMinimumLineCoverage(String minimumLineCoverage) {
        this.minimumLineCoverage = minimumLineCoverage;
    }

    @DataBoundSetter
    public void setMinimumMethodCoverage(String minimumMethodCoverage) {
        this.minimumMethodCoverage = minimumMethodCoverage;
    }

    @DataBoundSetter
    public void setMinimumClassCoverage(String minimumClassCoverage) {
        this.minimumClassCoverage = minimumClassCoverage;
    }

    @DataBoundSetter
    public void setMaximumInstructionCoverage(String maximumInstructionCoverage) {
        this.maximumInstructionCoverage = maximumInstructionCoverage;
    }

    @DataBoundSetter
    public void setMaximumBranchCoverage(String maximumBranchCoverage) {
        this.maximumBranchCoverage = maximumBranchCoverage;
    }

    @DataBoundSetter
    public void setMaximumComplexityCoverage(String maximumComplexityCoverage) {
        this.maximumComplexityCoverage = maximumComplexityCoverage;
    }

    @DataBoundSetter
    public void setMaximumLineCoverage(String maximumLineCoverage) {
        this.maximumLineCoverage = maximumLineCoverage;
    }

    @DataBoundSetter
    public void setMaximumMethodCoverage(String maximumMethodCoverage) {
        this.maximumMethodCoverage = maximumMethodCoverage;
    }

    @DataBoundSetter
    public void setMaximumClassCoverage(String maximumClassCoverage) {
        this.maximumClassCoverage = maximumClassCoverage;
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
		} catch(InterruptedException | IOException ie) {
			ie.printStackTrace();
		}
        return directoryPaths;
	}


    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener taskListener) throws InterruptedException, IOException {
        Map<String, String> envs = run instanceof AbstractBuild ? ((AbstractBuild<?,?>) run).getBuildVariables() : Collections.<String, String>emptyMap();

        EnvVars env = run.getEnvironment(taskListener);
        env.overrideAll(envs);

        healthReports = createJacocoHealthReportThresholds(env);

        // Initialize delta health report with user-configured threshold values
        deltaHealthReport = createJacocoDeltaHealthReportThresholds();

        if (run.getResult() == Result.FAILURE || run.getResult() == Result.ABORTED) {
            return;
        }

        final PrintStream logger = taskListener.getLogger();
        logger.println("[JaCoCo plugin] Collecting JaCoCo coverage data...");

        if ((execPattern==null) || (classPattern==null) || (sourcePattern==null)) {
            if(run.getResult().isWorseThan(Result.UNSTABLE)) {
                return;
            }

            logger.println("[JaCoCo plugin] ERROR: Missing configuration!");
            run.setResult(Result.FAILURE);
            return;
        }

        logger.println("[JaCoCo plugin] " + execPattern + ";" + classPattern +  ";" + sourcePattern + ";" + " locations are configured");

        JacocoReportDir reportDir = new JacocoReportDir(run.getRootDir());

        if (run instanceof AbstractBuild) {
            execPattern = resolveFilePaths((AbstractBuild<?,?>) run, taskListener, execPattern);
        }

        List<FilePath> matchedExecFiles = Arrays.asList(filePath.list(resolveFilePaths(run, taskListener, execPattern, env)));
        logger.println("[JaCoCo plugin] Number of found exec files for pattern " + execPattern + ": " + matchedExecFiles.size());
        logger.print("[JaCoCo plugin] Saving matched execfiles: ");
        reportDir.addExecFiles(matchedExecFiles);
        logger.print(" " + Util.join(matchedExecFiles," "));
        FilePath[] matchedClassDirs = resolveDirPaths(filePath, taskListener, classPattern);
        logger.print("\n[JaCoCo plugin] Saving matched class directories for class-pattern: " + classPattern + ": ");
        final String warning = "\n[JaCoCo plugin] WARNING: You are using directory patterns with trailing /, /* or /** . This will most likely" +
                " multiply the copied files in your build directory. Check the list below and ignore this warning if you know what you are doing.";
        if (hasSubDirectories(classPattern)) {
            logger.print(warning);
        }
        for (FilePath dir : matchedClassDirs) {
            int copied = reportDir.saveClassesFrom(dir, "**/*.class");
            logger.print("\n[JaCoCo plugin]  - " + dir + " " + copied + " files");
        }

        // Use skipCopyOfSrcFiles flag to determine if the source files should be copied or skipped. If skipped display appropriate logger message.
        if(!this.skipCopyOfSrcFiles) {
            FilePath[] matchedSrcDirs = resolveDirPaths(filePath, taskListener, sourcePattern);
            logger.print("\n[JaCoCo plugin] Saving matched source directories for source-pattern: " + sourcePattern + ": ");
            if (hasSubDirectories(sourcePattern)) logger.print(warning);
            for (FilePath dir : matchedSrcDirs) {
                int copied = reportDir.saveSourcesFrom(dir, "**/*.java");
                logger.print("\n[JaCoCo plugin] - " + dir + " " + copied + " files");
            }
        }
        else{
            logger.print("\n[JaCoCo plugin] Skipping save of matched source directories for source-pattern: " + sourcePattern);
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

        final JacocoBuildAction action = JacocoBuildAction.load(run, healthReports, taskListener, reportDir, includes, excludes);
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

            // Calculate final result of the current build according to the state of two flags: changeBuildStatus and buildOverBuild
            // Final result is the logical AND of two operation results
            // Initializing individual operation result as SUCCESS to eliminate impact if the corresponding flag is not set
            Result applyMinMaxTh = Result.SUCCESS, applyDeltaTh = Result.SUCCESS;
            if (changeBuildStatus) {
                applyMinMaxTh = checkResult(action); // Compare current coverage with minimum and maximum coverage thresholds
                logger.println("[JaCoCo plugin] Health thresholds: "+ healthReports.toString());
                logger.println("[JaCoCo plugin] Apply Min/Max thresholds result: "+ applyMinMaxTh.toString());
            }
            if(buildOverBuild){
                applyDeltaTh = checkBuildOverBuildResult(run, logger); // Compute delta coverage of current build and compare with delta thresholds
                logger.println("[JaCoCo plugin] Delta thresholds: " + deltaHealthReport.toString());
                logger.println("[JaCoCo plugin] Results of delta thresholds check: "+ applyDeltaTh.toString());
            }
            if(changeBuildStatus || buildOverBuild) {
                run.setResult(Utils.applyLogicalAnd(applyMinMaxTh, applyDeltaTh));
            }
        }
    }

    private boolean hasSubDirectories(String pattern) {
        for (String dir : pattern.split(DIR_SEP)) {
            if (dir.endsWith("\\") || dir.endsWith("/") ||
                    dir.endsWith("\\*") || dir.endsWith("/*") ||
                    dir.endsWith("\\**") || dir.endsWith("/**")
                    ) {
                return true;
            }
        }
        return false;
    }

    private JacocoHealthReportThresholds createJacocoHealthReportThresholds(EnvVars env) {
        try {
            return healthReports = new JacocoHealthReportThresholds(
                    convertThresholdInputToInteger(minimumClassCoverage, env), 
                    convertThresholdInputToInteger(maximumClassCoverage, env), 
                    convertThresholdInputToInteger(minimumMethodCoverage, env), 
                    convertThresholdInputToInteger(maximumMethodCoverage, env), 
                    convertThresholdInputToInteger(minimumLineCoverage, env), 
                    convertThresholdInputToInteger(maximumLineCoverage, env), 
                    convertThresholdInputToInteger(minimumBranchCoverage, env), 
                    convertThresholdInputToInteger(maximumBranchCoverage, env), 
                    convertThresholdInputToInteger(minimumInstructionCoverage, env), 
                    convertThresholdInputToInteger(maximumInstructionCoverage, env), 
                    convertThresholdInputToInteger(minimumComplexityCoverage, env), 
                    convertThresholdInputToInteger(maximumComplexityCoverage, env)
                );
        } catch (NumberFormatException nfe) {
            return healthReports = new JacocoHealthReportThresholds(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        }
    }

    /**
     * Creates JacocoHealthReportDeltaThresholds object to encapsulate user configured delta threshold values.
     * The values entered by the user are validated to be in range of [0, 100] percentage
     */
    private JacocoHealthReportDeltaThresholds createJacocoDeltaHealthReportThresholds(){
        return new JacocoHealthReportDeltaThresholds(this.deltaInstructionCoverage, this.deltaBranchCoverage, this.deltaComplexityCoverage, this.deltaLineCoverage, this.deltaMethodCoverage, this.deltaClassCoverage);
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

    // Calculates actual delta coverage of the current build by subtracting it's coverage from coverage of last successful build
    // and compares if the delta coverage is less than or equal to user-configured delta thresholds
    // Returns success (if delta coverage is equal to or less than delta thresholds) OR (if delta coverage is bigger than delta thresholds AND current coverage is bigger than last successful coverage)
    public Result checkBuildOverBuildResult(Run<?,?> run, PrintStream logger){

        JacocoDeltaCoverageResultSummary deltaCoverageResultSummary = JacocoDeltaCoverageResultSummary.build(run);
        logger.println("[JaCoCo plugin] Delta coverage: class: " + deltaCoverageResultSummary.getClassCoverage()
                + ", method: " + deltaCoverageResultSummary.getMethodCoverage()
                + ", line: " + deltaCoverageResultSummary.getLineCoverage()
                + ", branch: " + deltaCoverageResultSummary.getBranchCoverage()
                + ", instruction: " + deltaCoverageResultSummary.getInstructionCoverage()
                + ", complexity: " + deltaCoverageResultSummary.getComplexityCoverage());

        if(Math.abs(deltaCoverageResultSummary.getInstructionCoverage()) <= deltaHealthReport.getDeltaInstruction() &&
                Math.abs(deltaCoverageResultSummary.getBranchCoverage()) <= deltaHealthReport.getDeltaBranch() &&
                Math.abs(deltaCoverageResultSummary.getComplexityCoverage()) <= deltaHealthReport.getDeltaComplexity() &&
                Math.abs(deltaCoverageResultSummary.getLineCoverage()) <= deltaHealthReport.getDeltaLine() &&
                Math.abs(deltaCoverageResultSummary.getMethodCoverage()) <= deltaHealthReport.getDeltaMethod() &&
                Math.abs(deltaCoverageResultSummary.getClassCoverage()) <= deltaHealthReport.getDeltaClass())
            return Result.SUCCESS;
        else if(deltaCoverageResultSummary.isCoverageBetterThanPrevious())
            return Result.SUCCESS;
        else
            return Result.FAILURE;
    }
	
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public BuildStepDescriptor<Publisher> getDescriptor() {
        return (BuildStepDescriptor<Publisher>)super.getDescriptor();
    }

    /**
     * @deprecated
     *      use injection via {@link Jenkins#getInjector()}
     */
    public static /*final*/ BuildStepDescriptor<Publisher> DESCRIPTOR;

    @Extension @Symbol("jacoco")
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        public DescriptorImpl() {
            super(JacocoPublisher.class);
            DESCRIPTOR = this;
        }

		@Override
        public String getDisplayName() {
            return Messages.JacocoPublisher_DisplayName();
        }

		@Override
        @SuppressWarnings("rawtypes")
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
            ArrayList<FilePath> localDirectoryPaths= new ArrayList<>();
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
