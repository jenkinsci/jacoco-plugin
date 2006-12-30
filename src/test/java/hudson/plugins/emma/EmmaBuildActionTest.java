package hudson.plugins.emma;

/**
 * @author Kohsuke Kawaguchi
 */
public class EmmaBuildActionTest extends AbstractEmmaTestBase {
    public void testLoad() throws Exception {
        EmmaBuildAction r = EmmaBuildAction.load(null,getClass().getResourceAsStream("coverage.xml"));
        assertEquals(r.classCoverage.getPercentage(),100);
        assertEquals(r.lineCoverage.getPercentage(),64);
        assertRatio(r.classCoverage, 185,185);
        assertRatio(r.methodCoverage, 1345,2061);
        assertRatio(r.blockCoverage, 44997,74846);
        assertRatio(r.lineCoverage, 8346.3f,13135);
    }
}
