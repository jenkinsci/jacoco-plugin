package hudson.plugins.emma;

/**
 * @author Kohsuke Kawaguchi
 */
public class CoverageReportTest extends AbstractEmmaTestBase {
    public void testLoad() throws Exception {
        CoverageReport r = new CoverageReport(null,getClass().getResourceAsStream("coverage.xml"));
        PackageReport pkg = r.getChildren().get("com.sun.tools.javac.v8.resources");
        assertRatio(pkg.getLineCoverage(),3,12);
    }
}
