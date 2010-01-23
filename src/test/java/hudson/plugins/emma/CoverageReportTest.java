package hudson.plugins.emma;

/**
 * @author Kohsuke Kawaguchi
 */
public class CoverageReportTest extends AbstractEmmaTestBase {
	
    public void testLoad() throws Exception {
        CoverageReport r = new CoverageReport(null, getClass().getResourceAsStream("coverage.xml"));
        PackageReport pkg = r.getChildren().get("com.sun.tools.javac.v8.resources");
        assertRatio(pkg.getLineCoverage(),3,12);
        assertEquals(8346.3f, r.getLineCoverage().getNumerator());
    }

    public void testLoadMultipleReports() throws Exception {
      CoverageReport r = new CoverageReport(null,  
          getClass().getResourceAsStream("coverage.xml"), 
          getClass().getResourceAsStream("coverageh.xml"));
      
      PackageReport pkg = r.getChildren().get("com.sun.tools.javac.v8.resources");
      assertRatio(pkg.getLineCoverage(),3,12);
      
      pkg = r.getChildren().get("org.apache.hupa.client.validation");
      assertRatio(pkg.getLineCoverage(),9,27);
      
      assertEquals(8355.3f, r.getLineCoverage().getNumerator());
  }
}
