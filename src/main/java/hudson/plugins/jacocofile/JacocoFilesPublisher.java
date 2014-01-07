package hudson.plugins.jacocofile;

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
import hudson.plugins.jacoco.Messages;
import hudson.plugins.jacoco.JacocoPublisher;
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


public class JacocoFilesPublisher extends Recorder {
    private final String execPattern;
    private final String reportPattern;

    /**
     * Loads the configuration set by user.
     */
    @DataBoundConstructor
    public JacocoFilesPublisher(String execPattern, String reportPattern) {
        this.execPattern = execPattern;
        this.reportPattern = reportPattern;
    }

    @Override
    public String toString() {
        return "JacocoPublisher [execPattern=" + execPattern
                + ", reportPattern=" + reportPattern + "]";
    }

    public String getExecPattern() {
        return execPattern;
    }

    public String getReportPattern() {
        return reportPattern;
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
                    String[] includes = input.split(",");
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
    @Override
    public boolean perform(AbstractBuild<?,?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        final PrintStream logger = listener.getLogger();

        if (build.getResult() == Result.FAILURE || build.getResult() == Result.ABORTED) {
            return true;
        }
        
        logger.println("[JaCoCo Files plugin] Collecting JaCoCo coverage data...");
        
        
        EnvVars env = build.getEnvironment(listener);
        env.overrideAll(build.getBuildVariables());
        
        if ((execPattern==null) || (reportPattern==null)) {
            if(build.getResult().isWorseThan(Result.UNSTABLE)) {
                return true;
            }
            
            logger.println("[JaCoCo Files plugin] ERROR: Missing configuration!");
            build.setResult(Result.FAILURE);
            return true;
        }
        
        logger.println("[JaCoCo Files plugin] " + execPattern + ";" + reportPattern + ";" + " locations are configured");

        JacocoReportFilesDir dir = new JacocoReportFilesDir(build);

        List<FilePath> matchedExecFiles = Arrays.asList(build.getWorkspace().list(resolveFilePaths(build, listener, execPattern)));
        logger.println("[JaCoCo Files plugin] Number of found exec files: " + matchedExecFiles.size());
        logger.print("[JaCoCo Files plugin] Saving matched execfiles: ");
        dir.addExecFiles(matchedExecFiles);
        logger.print(" " + Util.join(matchedExecFiles," "));
        FilePath[] matchedReportDirs = resolveDirPaths(build, listener, reportPattern);
        logger.print("\n[JaCoCo Files plugin] Saving matched report directories: ");
        for (FilePath file : matchedReportDirs) {
            dir.saveReportFrom(file);
            logger.print(" " + file);
        }
         
        /*final JacocoBuildAction action = JacocoBuildAction.load(build, rule, healthReports, listener, dir, includes, excludes);
        action.getThresholds().ensureValid();
        logger.println("[JaCoCo Files plugin] Thresholds: " + action.getThresholds());
        build.getActions().add(action);
        
        logger.println("[JaCoCo Files plugin] Publishing the results..");
        final CoverageReport result = action.getResult();
        
        if (result == null) {
            logger.println("[JaCoCo Files plugin] Could not parse coverage results. Setting Build to failure.");
            build.setResult(Result.FAILURE);
        } else {
            result.setThresholds(healthReports);
            if (changeBuildStatus) {
                build.setResult(checkResult(action));
            }
        }*/
        return true;
    }

    public Result checkResult(JacocoBuildFilesAction action) {
//        if ((action.getBranchCoverage().getPercentage() < action.getThresholds().getMinBranch()) || (action.getInstructionCoverage().getPercentage() < action.getThresholds().getMinInstruction())  || (action.getClassCoverage().getPercentage() < action.getThresholds().getMinClass())  || (action.getLineCoverage().getPercentage() < action.getThresholds().getMinLine())  || (action.getComplexityScore().getPercentage() < action.getThresholds().getMinComplexity())  || (action.getMethodCoverage().getPercentage() < action.getThresholds().getMinMethod())) {
//            return Result.FAILURE;
//        }
//        if ((action.getBranchCoverage().getPercentage() < action.getThresholds().getMaxBranch()) || (action.getInstructionCoverage().getPercentage() < action.getThresholds().getMaxInstruction())  || (action.getClassCoverage().getPercentage() < action.getThresholds().getMaxClass())  || (action.getLineCoverage().getPercentage() < action.getThresholds().getMaxLine())  || (action.getComplexityScore().getPercentage() < action.getThresholds().getMaxComplexity())  || (action.getMethodCoverage().getPercentage() < action.getThresholds().getMaxMethod())) {
//            return Result.UNSTABLE;
//        }
        return Result.SUCCESS;
    }
    
    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        return new JacocoProjectFilesAction(project);
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
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
            return Messages.JacocoFilesPublisher_DisplayName();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }
    }
}
