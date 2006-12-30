package hudson.plugins.emma;

import junit.framework.TestCase;

/**
 * @author Kohsuke Kawaguchi
 */
public class CoverageReportTest extends TestCase {
    public void testLoad() throws Exception {
        CoverageReport r = new CoverageReport(getClass().getResourceAsStream("coverage.xml"));
        System.out.println(r);
    }
}
