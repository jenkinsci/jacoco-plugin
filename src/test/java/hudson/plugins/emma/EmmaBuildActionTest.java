package hudson.plugins.emma;

import junit.framework.TestCase;

/**
 * @author Kohsuke Kawaguchi
 */
public class EmmaBuildActionTest extends TestCase {
    private void assertRatio(Ratio r, float numerator, float denominator) {
        assertEquals(numerator, r.numerator);
        assertEquals(denominator, r.denominator);
    }
    public void testLoad() throws Exception {
        EmmaBuildAction r = EmmaBuildAction.load(getClass().getResourceAsStream("coverage.xml"));
        assertEquals(r.classCoverage.getPercentage(),100);
        assertEquals(r.lineCoverage.getPercentage(),64);
        assertRatio(r.classCoverage, 185,185);
        assertRatio(r.methodCoverage, 1345,2061);
        assertRatio(r.blockCoverage, 44997,74846);
        assertRatio(r.lineCoverage, 8346.3f,13135);
    }
}
