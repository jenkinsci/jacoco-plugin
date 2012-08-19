package hudson.plugins.jacoco;

import hudson.FilePath;
import hudson.model.BuildListener;
import hudson.model.HealthReport;
import hudson.model.HealthReportingAction;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.plugins.jacoco.model.Coverage;
import hudson.plugins.jacoco.model.CoverageElement;
import hudson.plugins.jacoco.model.CoverageElement.Type;
import hudson.plugins.jacoco.model.CoverageObject;
import hudson.plugins.jacoco.model.ModuleInfo;
import hudson.plugins.jacoco.report.CoverageReport;
import hudson.util.IOException2;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.jacoco.core.analysis.IBundleCoverage;
import org.jvnet.localizer.Localizable;
import org.kohsuke.stapler.StaplerProxy;

/**
 * Build view extension by JaCoCo plugin.
 *
 * As {@link CoverageObject}, it retains the overall coverage report.
 *
 * @author Kohsuke Kawaguchi
 */
public final class JacocoBuildAction extends CoverageObject<JacocoBuildAction> implements HealthReportingAction, StaplerProxy, Serializable {
	
    public final AbstractBuild<?,?> owner;
    public final PrintStream logger;
    private transient WeakReference<CoverageReport> report;
    public ArrayList<ModuleInfo> reports;


	/**
     * Non-null if the coverage has pass/fail rules.
     */
    private final Rule rule;

    /**
     * The thresholds that applied when this build was built.
     * @TODO add ability to trend thresholds on the graph
     */
    private final JacocoHealthReportThresholds thresholds;

    /**
     * 
     * @param owner
     * @param rule
     * @param ratios
     *            The available coverage ratios in the report. Null is treated
     *            the same as an empty map.
     * @param thresholds
     */
    public JacocoBuildAction(AbstractBuild<?,?> owner, Rule rule,
    		Map<CoverageElement.Type, Coverage> ratios,
    		JacocoHealthReportThresholds thresholds, BuildListener listener) {
    	logger = listener.getLogger();
    	if (ratios == null) {
            ratios = Collections.emptyMap();
        }
        this.owner = owner;
        this.rule = rule;
        this.clazz = getOrCreateRatio(ratios, CoverageElement.Type.CLASS);
        this.method = getOrCreateRatio(ratios, CoverageElement.Type.METHOD);
        this.line = getOrCreateRatio(ratios, CoverageElement.Type.LINE);
        this.thresholds = thresholds;
        this.branch = getOrCreateRatio(ratios, CoverageElement.Type.BRANCH);
        this.instruction = getOrCreateRatio(ratios, CoverageElement.Type.INSTRUCTION);
        this.complexity = getOrCreateRatio(ratios, CoverageElement.Type.COMPLEXITY);
    }

    private Coverage getOrCreateRatio(Map<CoverageElement.Type, Coverage> ratios, CoverageElement.Type type) {
        Coverage r = ratios.get(type);
        if (r == null) {
            r = new Coverage();
        }
        return r;
    }

    public String getDisplayName() {
        return Messages.BuildAction_DisplayName();
    }

    public String getIconFileName() {
        return "graph.gif";
    }

    public String getUrlName() {
        return "jacoco";
    }


    public ArrayList<ModuleInfo> getReports() {
		return reports;
	}

	public void setReports(ArrayList<ModuleInfo> reports) {
		this.reports = reports;
	}
    /**
     * Get the coverage {@link hudson.model.HealthReport}.
     *
     * @return The health report or <code>null</code> if health reporting is disabled.
     * @since 1.7
     */
    public HealthReport getBuildHealth() {
        if (thresholds == null) {
            // no thresholds => no report
            return null;
        }
        thresholds.ensureValid();
        int score = 100, percent;
        ArrayList<Localizable> reports = new ArrayList<Localizable>(5);
        if (clazz != null && thresholds.getMaxClass() > 0) {
            percent = clazz.getPercentage();
            if (percent < thresholds.getMaxClass()) {
                reports.add(Messages._BuildAction_Classes(clazz, percent));
            }
            score = updateHealthScore(score, thresholds.getMinClass(),
                                      percent, thresholds.getMaxClass());
        }
        if (method != null && thresholds.getMaxMethod() > 0) {
            percent = method.getPercentage();
            if (percent < thresholds.getMaxMethod()) {
                reports.add(Messages._BuildAction_Methods(method, percent));
            }
            score = updateHealthScore(score, thresholds.getMinMethod(),
                                      percent, thresholds.getMaxMethod());
        }
        if (line != null && thresholds.getMaxLine() > 0) {
            percent = line.getPercentage();
            if (percent < thresholds.getMaxLine()) {
                reports.add(Messages._BuildAction_Lines(line, percent));
            }
            score = updateHealthScore(score, thresholds.getMinLine(),
                                      percent, thresholds.getMaxLine());
        }
        if (branch != null && thresholds.getMaxBranch() > 0) {
            percent = branch.getPercentage();
            if (percent < thresholds.getMaxBranch()) {
                reports.add(Messages._BuildAction_Branches(branch, percent));
            }
            score = updateHealthScore(score, thresholds.getMinBranch(),
                                      percent, thresholds.getMaxBranch());
        }
        if (instruction != null && thresholds.getMaxInstruction() > 0) {
            percent = instruction.getPercentage();
            if (percent < thresholds.getMaxInstruction()) {
                reports.add(Messages._BuildAction_Instructions(instruction, percent));
            }
            score = updateHealthScore(score, thresholds.getMinInstruction(),
                                      percent, thresholds.getMaxInstruction());
        }
        if (score == 100) {
            reports.add(Messages._BuildAction_Perfect());
        }
        // Collect params and replace nulls with empty string
        Object[] args = reports.toArray(new Object[5]);
        for (int i = 4; i >= 0; i--) if (args[i]==null) args[i] = ""; else break;
        return new HealthReport(score, Messages._BuildAction_Description(
                args[0], args[1], args[2], args[3], args[4]));
    }

    private static int updateHealthScore(int score, int min, int value, int max) {
        if (value >= max) return score;
        if (value <= min) return 0;
        assert max != min;
        final int scaled = (int) (100.0 * ((float) value - min) / (max - min));
        if (scaled < score) return scaled;
        return score;
    }

    public Object getTarget() {
        return getResult();
    }

    @Override
    public AbstractBuild<?,?> getBuild() {
        return owner;
    }
    
	
	protected static ArrayList<ModuleInfo> getJacocoReports(File file) throws IOException {
		FilePath path = new FilePath(file);
		ArrayList<ModuleInfo> reports= new ArrayList<ModuleInfo>();
		int i=0;
		try {
			FilePath checkPath=null;
			Properties props = new Properties();
			props.load((new FileReader(path+"/Modules.properties")));
			//props.load(new File(path+"/Modules.properties"));
			while(true){
				
				if ((checkPath=new FilePath(path,"module"+i)).exists()) {
					
					ModuleInfo moduleInfo = new ModuleInfo();
					moduleInfo.setName(props.getProperty("module"+i));
					moduleInfo.setClassDir(new FilePath(checkPath, "classes"));
					moduleInfo.setSrcDir(new FilePath(checkPath, "src"));
					moduleInfo.setExecFile(new FilePath(checkPath, "jacoco.exec"));
					moduleInfo.loadBundleCoverage();
					reports.add(moduleInfo);
				} else {
					break;
				}
				i++;
				
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return reports;
	}

    /**
     * Obtains the detailed {@link CoverageReport} instance.
     */
    public synchronized CoverageReport getResult() {

        if(report!=null) {
            final CoverageReport r = report.get();
            if(r!=null)     return r;
        }

        final File reportFolder = JacocoPublisher.getJacocoReport(owner);

        try {
        	ArrayList<ModuleInfo> reports = getJacocoReports(reportFolder);
            CoverageReport r = new CoverageReport(this, reports);
            report = new WeakReference<CoverageReport>(r);
            return r;
        } catch (IOException e) {
            logger.println("Failed to load " + reportFolder);
            e.printStackTrace(logger);
            return null;
        }
    }

    @Override
    public JacocoBuildAction getPreviousResult() {
        return getPreviousResult(owner);
    }

    /**
     * Gets the previous {@link JacocoBuildAction} of the given build.
     */
    /*package*/ static JacocoBuildAction getPreviousResult(AbstractBuild<?,?> start) {
        AbstractBuild<?,?> b = start;
        while(true) {
            b = b.getPreviousBuild();
            if(b==null)
                return null;
            if(b.getResult()== Result.FAILURE)
                continue;
            JacocoBuildAction r = b.getAction(JacocoBuildAction.class);
            if(r!=null)
                return r;
        }
    }

    /**
     * Constructs the object from JaCoCo XML report files.
     * See <a href="https://github.com/jfuerth/emma-plugin/blob/jacoco/src/test/resources/hudson/plugins/emma/jacoco.xml">an example XML file</a>.
     *
     * @throws IOException
     *      if failed to parse the file.
     */
    public static JacocoBuildAction load(AbstractBuild<?,?> owner, Rule rule, JacocoHealthReportThresholds thresholds, BuildListener listener, ArrayList<ModuleInfo> modules) throws IOException {
    	PrintStream logger = listener.getLogger();
    	Map<CoverageElement.Type,Coverage> ratios = null;
    	
        for (ModuleInfo moduleInfo: modules ) {
            try {
                ratios = loadRatios(moduleInfo, ratios);
            } catch (IOException e) {
                throw new IOException2("Failed to parse modules.", e);
            }    
        }
        return new JacocoBuildAction(owner, rule, ratios, thresholds, listener);
    }


    /**
     * Extracts top-level coverage information from the JaCoCo report document.
     * 
     * @param in
     * @param ratios
     * @return
     * @throws IOException
     */
    private static Map<Type, Coverage> loadRatios(ModuleInfo in, Map<Type, Coverage> ratios) throws IOException {

        if (ratios == null) {
            ratios = new LinkedHashMap<CoverageElement.Type, Coverage>();
        }
        IBundleCoverage bundleCoverage = in.loadBundleCoverage();
        
        Coverage ratio = new Coverage();
        ratio.accumulatePP(bundleCoverage.getClassCounter().getMissedCount(), bundleCoverage.getClassCounter().getCoveredCount());
        ratios.put(CoverageElement.Type.CLASS, ratio);
        
        ratio = new Coverage();
        ratio.accumulatePP(bundleCoverage.getBranchCounter().getMissedCount(), bundleCoverage.getBranchCounter().getCoveredCount());
        ratios.put(CoverageElement.Type.BRANCH, ratio);
        
        ratio = new Coverage();
        ratio.accumulatePP(bundleCoverage.getInstructionCounter().getMissedCount(), bundleCoverage.getInstructionCounter().getCoveredCount());
        ratios.put(CoverageElement.Type.INSTRUCTION, ratio);
        
        ratio = new Coverage();
        ratio.accumulatePP(bundleCoverage.getMethodCounter().getMissedCount(), bundleCoverage.getMethodCounter().getCoveredCount());
        ratios.put(CoverageElement.Type.METHOD, ratio);
        
        ratio = new Coverage();
        ratio.accumulatePP(bundleCoverage.getComplexityCounter().getMissedCount(), bundleCoverage.getComplexityCounter().getCoveredCount());
        ratios.put(CoverageElement.Type.COMPLEXITY, ratio);
        
        ratio = new Coverage();
        ratio.accumulatePP(bundleCoverage.getLineCounter().getMissedCount(), bundleCoverage.getLineCounter().getCoveredCount());
        ratios.put(CoverageElement.Type.LINE, ratio);
        
        return ratios;

    }

}
