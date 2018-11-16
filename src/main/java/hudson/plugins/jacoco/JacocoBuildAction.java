package hudson.plugins.jacoco;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.jacoco.core.analysis.IBundleCoverage;
import org.jvnet.localizer.Localizable;
import org.kohsuke.stapler.StaplerProxy;

import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.HealthReport;
import hudson.model.HealthReportingAction;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.jacoco.model.Coverage;
import hudson.plugins.jacoco.model.CoverageElement;
import hudson.plugins.jacoco.model.CoverageElement.Type;
import hudson.plugins.jacoco.model.CoverageObject;
import hudson.plugins.jacoco.report.CoverageReport;
import jenkins.model.RunAction2;
import jenkins.tasks.SimpleBuildStep.LastBuildAction;

/**
 * Build view extension by JaCoCo plugin.
 *
 * As {@link CoverageObject}, it retains the overall coverage report.
 *
 * @author Kohsuke Kawaguchi
 * @author Jonathan Fuerth
 * @author Ognjen Bubalo
 */
public final class JacocoBuildAction extends CoverageObject<JacocoBuildAction> implements HealthReportingAction, StaplerProxy, Serializable, RunAction2, LastBuildAction {
    private static final long serialVersionUID = 1L;

	private transient Run<?,?> owner;
	
	@Deprecated public transient AbstractBuild<?,?> build;
	
	private final transient PrintStream logger;
	@Deprecated private transient ArrayList<?> reports;
	private transient WeakReference<CoverageReport> report;
	private final String[] inclusions;
	private final String[] exclusions;
 
	/**
	 * The thresholds that applied when this build was built.
	 * TODO: add ability to trend thresholds on the graph
	 */
	private final JacocoHealthReportThresholds thresholds;
	private transient JacocoProjectAction jacocoProjectAction;

	/**
	 * 
	 * @param ratios
	 *            The available coverage ratios in the report. Null is treated
	 *            the same as an empty map.
	 * @param thresholds
	 *            The thresholds that applied when this build was built.
	 * @param listener
	 *            The listener from which we get logger
	 * @param inclusions
	 *            See {@link JacocoReportDir#parse(String[], String...)}
	 * @param exclusions
	 *            See {@link JacocoReportDir#parse(String[], String...)}
	 */
	public JacocoBuildAction(
			Map<CoverageElement.Type, Coverage> ratios,
			JacocoHealthReportThresholds thresholds, TaskListener listener, String[] inclusions, String[] exclusions) {
		logger = listener.getLogger();
		if (ratios == null) {
			ratios = Collections.emptyMap();
		}
		this.inclusions = inclusions != null ? Arrays.copyOf(inclusions, inclusions.length) : null;
		this.exclusions = exclusions != null ? Arrays.copyOf(exclusions, exclusions.length) : null;
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
		int score = 100;
		float percent;
		ArrayList<Localizable> reports = new ArrayList<>(5);
		if (clazz != null && thresholds.getMaxClass() > 0) {
			percent = clazz.getPercentageFloat();
			if (percent < thresholds.getMaxClass()) {
				reports.add(Messages._BuildAction_Classes(clazz, percent));
			}
			score = updateHealthScore(score, thresholds.getMinClass(),
					percent, thresholds.getMaxClass());
		}
		if (method != null && thresholds.getMaxMethod() > 0) {
			percent = method.getPercentageFloat();
			if (percent < thresholds.getMaxMethod()) {
				reports.add(Messages._BuildAction_Methods(method, percent));
			}
			score = updateHealthScore(score, thresholds.getMinMethod(),
					percent, thresholds.getMaxMethod());
		}
		if (line != null && thresholds.getMaxLine() > 0) {
			percent = line.getPercentageFloat();
			if (percent < thresholds.getMaxLine()) {
				reports.add(Messages._BuildAction_Lines(line, percent));
			}
			score = updateHealthScore(score, thresholds.getMinLine(),
					percent, thresholds.getMaxLine());
		}
		if (branch != null && thresholds.getMaxBranch() > 0) {
			percent = branch.getPercentageFloat();
			if (percent < thresholds.getMaxBranch()) {
				reports.add(Messages._BuildAction_Branches(branch, percent));
			}
			score = updateHealthScore(score, thresholds.getMinBranch(),
					percent, thresholds.getMaxBranch());
		}
		if (instruction != null && thresholds.getMaxInstruction() > 0) {
			percent = instruction.getPercentageFloat();
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
		//throw new RuntimeException("Jebiga");
		Object[] args = reports.toArray(new Object[5]);
		for (int i = 4; i >= 0; i--) {
			if (args[i]==null) {
				args[i] = "";
			} else {
				break;
			}
		}
		return new HealthReport(score, Messages._BuildAction_Description(
				args[0], args[1], args[2], args[3], args[4]));
	}

	public JacocoHealthReportThresholds getThresholds() {
		return thresholds;
	}

	private static int updateHealthScore(int score, int min, float value, int max) {
		if (value >= max) {
			return score;
		}
		if (value <= min) {
			return 0;
		}
		assert max != min;
		final int scaled = (int) (100.0 * (value - min) / (max - min));
		if (scaled < score) {
			return scaled;
		}
		return score;
	}

	public Object getTarget() {
		return getResult();
	}

	@Override
	public Run<?,?> getBuild() {
		return owner;
	}

    public JacocoReportDir getJacocoReport() {
        return new JacocoReportDir(owner.getRootDir());
    }

	/**
	 * Obtains the detailed {@link CoverageReport} instance.
	 * @return the report, or null if these was a problem
	 */
	public synchronized @Nullable CoverageReport getResult() {

		if(report!=null) {
			final CoverageReport r = report.get();
			if(r!=null) {
				return r;
			}
		}

		final JacocoReportDir reportFolder = getJacocoReport();

		try {
			CoverageReport r = new CoverageReport(this, reportFolder.parse(inclusions, exclusions));
			report = new WeakReference<>(r);
			r.setThresholds(thresholds);
			return r;
		} catch (IOException | RuntimeException e) {
			getLogger().println("Failed to load " + reportFolder);
			e.printStackTrace(getLogger());
			return null;
		}
	}

	@Override
	public JacocoBuildAction getPreviousResult() {
		return getPreviousResult(owner);
	}

	/**
	 * @return A map which represents coverage objects and their status to show on build status page (summary.jelly).
	 */
	public Map<Coverage,Boolean> getCoverageRatios(){
		CoverageReport result = getResult();
		Map<Coverage,Boolean> ratios = new LinkedHashMap<>();
		if( result != null ) {
			Coverage instructionCoverage = result.getInstructionCoverage();
			Coverage classCoverage = result.getClassCoverage();
			Coverage complexityScore = result.getComplexityScore();
			Coverage branchCoverage = result.getBranchCoverage();
			Coverage lineCoverage = result.getLineCoverage();
			Coverage methodCoverage = result.getMethodCoverage();

			instructionCoverage.setType(CoverageElement.Type.INSTRUCTION);			
			classCoverage.setType(CoverageElement.Type.CLASS);
			complexityScore.setType(CoverageElement.Type.COMPLEXITY);			
			branchCoverage.setType(CoverageElement.Type.BRANCH);			
			lineCoverage.setType(CoverageElement.Type.LINE);
			methodCoverage.setType(CoverageElement.Type.METHOD);
			
			ratios.put(instructionCoverage,JacocoHealthReportThresholds.RESULT.BELOWMINIMUM == thresholds.getResultByTypeAndRatio(instructionCoverage));
			ratios.put(branchCoverage,JacocoHealthReportThresholds.RESULT.BELOWMINIMUM == thresholds.getResultByTypeAndRatio(branchCoverage));
			ratios.put(complexityScore,JacocoHealthReportThresholds.RESULT.BELOWMINIMUM == thresholds.getResultByTypeAndRatio(complexityScore));
			ratios.put(lineCoverage,JacocoHealthReportThresholds.RESULT.BELOWMINIMUM == thresholds.getResultByTypeAndRatio(lineCoverage));
			ratios.put(methodCoverage,JacocoHealthReportThresholds.RESULT.BELOWMINIMUM == thresholds.getResultByTypeAndRatio(methodCoverage));
			ratios.put(classCoverage,JacocoHealthReportThresholds.RESULT.BELOWMINIMUM == thresholds.getResultByTypeAndRatio(classCoverage));
		}
		return ratios;
	}
	
	/**
	 * Gets the previous {@link JacocoBuildAction} of the given build.
	 */
	/*package*/ static JacocoBuildAction getPreviousResult(Run<?,?> start) {
		Run<?,?> b = start;
		while(true) {
			b = b.getPreviousBuild();
			if(b==null) {
				return null;
			}
			if (b.isBuilding() || b.getResult() == Result.FAILURE || b.getResult() == Result.ABORTED) {
				continue;
			}
			JacocoBuildAction r = b.getAction(JacocoBuildAction.class);
			if(r!=null) {
				return r;
			}
		}
	}

	/**
	 * Constructs the object from JaCoCo exec files.
	 * @param thresholds
	 *            The thresholds that applied when this build was built.
	 * @param listener
	 *            The listener from which we get logger
	 * @param layout
	 *             The object parsing the saved "jacoco.exec" files
     * @param includes
     *            See {@link JacocoReportDir#parse(String[], String...)}
     * @param excludes
     *            See {@link JacocoReportDir#parse(String[], String...)}
	 * @return new {@code JacocoBuildAction} from JaCoCo exec files
	 * @throws IOException
	 *      if failed to parse the file.
	 */
	public static JacocoBuildAction load(JacocoHealthReportThresholds thresholds, TaskListener listener, JacocoReportDir layout, String[] includes, String[] excludes) throws IOException {
		Map<CoverageElement.Type,Coverage> ratios = loadRatios(layout, includes, excludes);
		return new JacocoBuildAction(ratios, thresholds, listener, includes, excludes);
	}


	/**
	 * Extracts top-level coverage information from the JaCoCo report document.
	 */
	private static Map<Type, Coverage> loadRatios(JacocoReportDir layout, String[] includes, String... excludes) throws IOException {
		Map<CoverageElement.Type,Coverage> ratios = new LinkedHashMap<>();
		ExecutionFileLoader efl = layout.parse(includes, excludes);
        IBundleCoverage bundleCoverage = efl.getBundleCoverage();
        if(bundleCoverage == null) {
        	return null;
		}

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
		//logGer.log(Level.INFO, ratios.toString());
		return ratios;

	}
	
	//private static final Logger logger = Logger.getLogger(JacocoBuildAction.class.getName());
	public final PrintStream getLogger() {
	    if(logger != null) {
	        return logger;
	    }

	    // use System.out as a fallback if the BuildAction was de-serialized which
	    // does not run the construct and thus leaves the transient variables empty
	    return System.out;
	}

	public Run<?, ?> getOwner() {
		return owner;
	}

	private void setOwner(Run<?, ?> owner) {
		jacocoProjectAction = new JacocoProjectAction(owner.getParent());
		this.owner = owner;
	}

	@Override
	public void onAttached(Run<?, ?> run) {
		setOwner(run);
	}

	@Override
	public void onLoad(Run<?, ?> run) {
		setOwner(run);
	}

	@Override
	public Collection<? extends Action> getProjectActions() {
		return jacocoProjectAction != null ? Collections.singletonList(jacocoProjectAction) : Collections.emptyList();
	}
}
