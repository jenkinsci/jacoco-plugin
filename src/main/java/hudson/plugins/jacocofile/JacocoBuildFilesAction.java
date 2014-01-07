package hudson.plugins.jacocofile;

import hudson.model.BuildListener;
import hudson.model.HealthReport;
import hudson.model.HealthReportingAction;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.plugins.jacoco.Messages;
import hudson.plugins.jacoco.model.Coverage;
import hudson.plugins.jacoco.model.CoverageElement;
import hudson.plugins.jacoco.model.CoverageObject;
import hudson.plugins.jacoco.report.CoverageReport;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Map;

import org.kohsuke.stapler.StaplerProxy;

/**
 * Build view extension by JaCoCo plugin.
 *
 * As {@link CoverageObject}, it retains the overall coverage report.
 *
 * @author Kohsuke Kawaguchi
 * @author Jonathan Fuerth
 * @author Ognjen Bubalo
 */
public final class JacocoBuildFilesAction extends CoverageObject<JacocoBuildFilesAction> implements HealthReportingAction, StaplerProxy, Serializable {
	public final AbstractBuild<?,?> owner;
	
	public final transient PrintStream logger;
	
	private final JacocoReportFilesDir dir;

	/**
	 * 
	 * @param owner
	 * @param rule
	 * @param ratios
	 *            The available coverage ratios in the report. Null is treated
	 *            the same as an empty map.
	 * @param thresholds
	 */
	public JacocoBuildFilesAction(AbstractBuild<?,?> owner, 
			BuildListener listener, JacocoReportFilesDir dir) {
		logger = listener.getLogger();
		this.owner = owner;
		this.dir = dir;
	}

	private Coverage getOrCreateRatio(Map<CoverageElement.Type, Coverage> ratios, CoverageElement.Type type) {
		Coverage r = ratios.get(type);
		if (r == null) {
			r = new Coverage();
		}
		return r;
	}

	public String getDisplayName() {
		return Messages.BuildFilesAction_DisplayName();
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
	    // no thresholds => no report
	    return null;
	}

	public Object getTarget() {
		return getResult();
	}

	@Override
	public AbstractBuild<?,?> getBuild() {
		return owner;
	}

    public JacocoReportFilesDir getJacocoReport() {
        return new JacocoReportFilesDir(owner);
    }

	/**
	 * Obtains the detailed {@link CoverageReport} instance.
	 */
	public synchronized CoverageReport getResult() {
		return null;
	}

	@Override
	public JacocoBuildFilesAction getPreviousResult() {
		return getPreviousResult(owner);
	}

	/**
	 * Gets the previous {@link JacocoBuildFilesAction} of the given build.
	 */
	/*package*/ static JacocoBuildFilesAction getPreviousResult(AbstractBuild<?,?> start) {
		AbstractBuild<?,?> b = start;
		while(true) {
			b = b.getPreviousBuild();
			if(b==null) {
				return null;
			}
			if(b.getResult()== Result.FAILURE || b.getResult() == Result.ABORTED) {
				continue;
			}
			JacocoBuildFilesAction r = b.getAction(JacocoBuildFilesAction.class);
			if(r!=null) {
				return r;
			}
		}
	}

	/**
	 * Constructs the object from JaCoCo exec files.
	 *
	 * @throws IOException
	 *      if failed to parse the file.
	 */
	public static JacocoBuildFilesAction load(AbstractBuild<?,?> owner, BuildListener listener, JacocoReportFilesDir layout) throws IOException {
		return new JacocoBuildFilesAction(owner, listener, layout);
	}
}
