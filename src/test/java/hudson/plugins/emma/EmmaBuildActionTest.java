package hudson.plugins.emma;

/**
 * @author Kohsuke Kawaguchi
 */
public class EmmaBuildActionTest extends AbstractEmmaTestBase {
    public void testLoad() throws Exception {
        EmmaBuildAction r = EmmaBuildAction.load(null,null,
                new EmmaHealthReportThresholds(30, 90, 25, 80, 20, 70, 15, 60),
                getClass().getResourceAsStream("coverage.xml"));
        assertEquals(r.clazz.getPercentage(),100);
        assertEquals(r.line.getPercentage(),64);
        assertRatio(r.clazz, 185,185);
        assertRatio(r.method, 1345,2061);
        assertRatio(r.block, 44997,74846);
        assertRatio(r.line, 8346.3f,13135);
        assertEquals("Coverage: Methods 1345/2061 (65%). Blocks 44997/74846 (60%).   ",
                     r.getBuildHealth().getDescription());
    }
}
