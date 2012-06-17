package hudson.plugins.jacoco;

import hudson.plugins.jacoco.model.Coverage;
import static org.junit.Assert.*;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class AbstractJacocoTestBase {
    protected final void assertRatio(Coverage r, float numerator, float denominator) {
        fail("Replaced by assertCoverage()");
    }
    
    protected final void assertCoverage(Coverage coverage, int missed, int covered) {
        assertEquals(missed + "/" + covered, coverage.getMissed() + "/" + coverage.getCovered());
    }
}
