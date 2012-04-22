package hudson.plugins.jacoco;

import hudson.ExtensionPoint;
import hudson.model.Build;
import hudson.model.TaskListener;
import hudson.plugins.jacoco.model.CoverageObject;
import hudson.plugins.jacoco.report.CoverageReport;

import java.io.Serializable;

/**
 * Rule object encapsulates the logic to mark {@link CoverageObject}s as "failed".
 * Such logic is used to mark builds as unstable when certain condition is met.
 *
 * <p>
 * For example, one can define a rule where "line coverage must be better than 50%
 * for any class", and if this rule is violated, the build will be marked as
 * unstable.
 *
 * <p>
 * The rule instances are persisted as a part of {@link Build}, so make sure
 * to make your class serializable. This is so that we can consistently mark
 * coverage results even if the job configuration changes.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class Rule implements Serializable, ExtensionPoint {
    public abstract void enforce(CoverageReport report, TaskListener listener);

    private static final long serialVersionUID = 1L;
}
