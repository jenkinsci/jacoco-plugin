package hudson.plugins.jacoco;

import hudson.plugins.jacoco.Coverage;
import junit.framework.TestCase;

/**
 * @author Kohsuke Kawaguchi
 */
abstract class AbstractEmmaTestBase extends TestCase {
    protected final void assertRatio(Coverage r, float numerator, float denominator) {
        fail("Replaced by assertCoverage()");
    }
    
    protected final void assertCoverage(Coverage coverage, int missed, int covered) {
        assertEquals(missed + "/" + covered, coverage.getMissed() + "/" + coverage.getCovered());
    }
}
