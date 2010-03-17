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

      assertRatio(r.getLineCoverage(), 8355.3f, 14828.0f);
      
      PackageReport pkg = r.getChildren().get("com.sun.tools.javac.v8.resources");
      assertRatio(pkg.getLineCoverage(),3,12);
      
      pkg = r.getChildren().get("org.apache.hupa.client.validation");
      assertRatio(pkg.getLineCoverage(), 9,27);
      
    }
    
    public void testTreeReport() throws Exception {
        CoverageReport r = new CoverageReport(null,getClass().getResourceAsStream("coverageh.xml"));
        assertRatio(r.getLineCoverage(), 9, 1693);
        
        PackageReport pkg = r.getChildren().get("org.apache.hupa.client.validation");
        assertRatio(pkg.getLineCoverage(), 9, 27);

        SourceFileReport src = pkg.getChildren().get("EmailListValidator.java");
        assertRatio(src.getLineCoverage(), 9, 18);

        ClassReport clz = src.getChildren().get("EmailListValidator");
        assertRatio(clz.getLineCoverage(), 9, 18);
        assertTrue(clz.hasClassCoverage());

        MethodReport mth = clz.getChildren().get("isValidAddress (String): boolean");
        assertRatio(mth.getLineCoverage(), 1, 1);
        assertFalse(mth.hasClassCoverage());

        mth = clz.getChildren().get("Foo (): void");
        assertRatio(mth.getLineCoverage(), 0, 0);
        assertFalse(mth.hasClassCoverage());
        assertFalse(mth.hasLineCoverage());
    }
    
    public void testEmptyPackage() throws Exception {
        CoverageReport r = new CoverageReport(null,getClass().getResourceAsStream("coverage.xml"));

        PackageReport pkg = r.getChildren().get("an.empty.package");
        assertRatio(pkg.getLineCoverage(), 0, 0);
        assertFalse(pkg.hasChildren());
        assertFalse(pkg.hasChildrenClassCoverage());
        assertFalse(pkg.hasChildrenLineCoverage());

        pkg = r.getChildren().get("an.package.without.lines");
        assertRatio(pkg.getLineCoverage(), 0, 0);
        assertTrue(pkg.hasChildren());
        assertFalse(pkg.hasChildrenClassCoverage());
        assertFalse(pkg.hasChildrenLineCoverage());

    }
}
