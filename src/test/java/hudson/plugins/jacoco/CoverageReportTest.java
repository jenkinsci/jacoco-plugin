package hudson.plugins.jacoco;

import hudson.plugins.jacoco.CoverageReport;
import hudson.plugins.jacoco.PackageReport;
import hudson.plugins.jacoco.SourceFileReport;

/**
 * @author Kohsuke Kawaguchi
 */
public class CoverageReportTest extends AbstractEmmaTestBase {
	
    public void testLoad() throws Exception {
        CoverageReport r = new CoverageReport(null, getClass().getResourceAsStream("jacoco.xml"));
        PackageReport pkg = r.getChildren().get("hudson.plugins.jacoco");
        System.out.println(pkg);
        assertCoverage(pkg.getLineCoverage(), 393, 196);
        assertEquals(595, r.getLineCoverage().getMissed());
    }

    /**
     * Ensures the coverage after loading two reports represents the combined metrics of both reports.
     */
    public void testLoadMultipleReports() throws Exception {
      CoverageReport r = new CoverageReport(null,  
          getClass().getResourceAsStream("jacoco.xml"), 
          getClass().getResourceAsStream("jacoco2.xml"));

      assertCoverage(r.getLineCoverage(), 595 + 513, 293 + 361);
      
      PackageReport pkg = r.getChildren().get("hudson.plugins.jacoco.portlet.bean");
      assertCoverage(pkg.getLineCoverage(), 34 + 34, 41 + 41);
      
      pkg = r.getChildren().get("hudson.plugins.jacoco.portlet.chart");
      assertCoverage(pkg.getLineCoverage(), 71 + 68, 0 + 1);
      
    }
    
    public void testTreeReport() throws Exception {
        CoverageReport r = new CoverageReport(null,getClass().getResourceAsStream("jacoco2.xml"));
        assertCoverage(r.getLineCoverage(), 513, 361);
        
        PackageReport pkg = r.getChildren().get("hudson.plugins.jacoco.portlet.bean");
        assertCoverage(pkg.getLineCoverage(), 34, 41);

        SourceFileReport src = pkg.getChildren().get("EmmaCoverageResultSummary.java");
        assertCoverage(src.getLineCoverage(), 34, 41);

        fail("No test yet for method-level coverage data");
        //        ClassReport clz = src.getChildren().get("EmailListValidator");
//        assertRatio(clz.getLineCoverage(), 9, 18);
//        assertTrue(clz.hasClassCoverage());
//
//        MethodReport mth = clz.getChildren().get("isValidAddress (String): boolean");
//        assertRatio(mth.getLineCoverage(), 1, 1);
//        assertFalse(mth.hasClassCoverage());
//
//        mth = clz.getChildren().get("Foo (): void");
//        assertRatio(mth.getLineCoverage(), 0, 0);
//        assertFalse(mth.hasClassCoverage());
//        assertFalse(mth.hasLineCoverage());
    }
    
    public void testEmptyPackage() throws Exception {
        CoverageReport r = new CoverageReport(null,getClass().getResourceAsStream("jacoco.xml"));

        PackageReport pkg = r.getChildren().get("fake.empty.package");
        assertCoverage(pkg.getLineCoverage(), 0, 0);
        assertFalse(pkg.hasChildren());
        assertFalse(pkg.hasChildrenClassCoverage());
        assertFalse(pkg.hasChildrenLineCoverage());

        pkg = r.getChildren().get("fake.empty.package.without.lines");
        assertCoverage(pkg.getLineCoverage(), 0, 0);
        assertTrue(pkg.hasChildren());
        assertFalse(pkg.hasChildrenClassCoverage());
        assertFalse(pkg.hasChildrenLineCoverage());

    }
}
