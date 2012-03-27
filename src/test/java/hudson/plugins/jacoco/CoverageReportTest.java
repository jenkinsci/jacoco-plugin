package hudson.plugins.jacoco;

import org.junit.Ignore;
import org.junit.Test;

import hudson.plugins.jacoco.CoverageReport;
import hudson.plugins.jacoco.PackageReport;
import hudson.plugins.jacoco.SourceFileReport;
import static org.junit.Assert.*;

/**
 * @author Kohsuke Kawaguchi
 * @author David Carver - Refactored for cleaner seperation of tests
 */
public class CoverageReportTest extends AbstractEmmaTestBase {
	
	@Test
    public void testLoad() throws Exception {
        CoverageReport r = new CoverageReport(null, getClass().getResourceAsStream("jacoco.xml"));
        PackageReport pkg = r.getChildren().get("hudson.plugins.emma");
        System.out.println(pkg);
        assertCoverage(pkg.getLineCoverage(), 393, 196);
        assertEquals(595, r.getLineCoverage().getMissed());
    }

    /**
     * Ensures the coverage after loading two reports represents the combined metrics of both reports.
     */
	@Test
    public void testLoadMultipleReports() throws Exception {
      CoverageReport r = new CoverageReport(null,  
          getClass().getResourceAsStream("jacoco.xml"), 
          getClass().getResourceAsStream("jacoco2.xml"));

      assertCoverage(r.getLineCoverage(), 595 + 513, 293 + 361);
      
      PackageReport pkg = r.getChildren().get("hudson.plugins.emma.portlet.bean");
      assertCoverage(pkg.getLineCoverage(), 34, 41);
      
      pkg = r.getChildren().get("hudson.plugins.emma.portlet.chart");
      assertCoverage(pkg.getLineCoverage(), 68, 0 + 1);
      
    }
	
	@Test
	public void testCoverageReport() throws Exception {
		CoverageReport r = new CoverageReport(null,getClass().getResourceAsStream("jacoco2.xml"));
        assertCoverage(r.getLineCoverage(), 513, 361);
	}
	
	@Test
	public void testPackageReport() throws Exception {
		CoverageReport r = new CoverageReport(null,getClass().getResourceAsStream("jacoco2.xml"));
		PackageReport pkg = r.getChildren().get("hudson.plugins.emma.portlet.bean");
		assertCoverage(pkg.getLineCoverage(), 34, 41);
	}
	
	@Test
	public void testSourceFileReport() throws Exception {
		CoverageReport r = new CoverageReport(null,getClass().getResourceAsStream("jacoco2.xml"));
		PackageReport pkg = r.getChildren().get("hudson.plugins.emma.portlet.bean");
		SourceFileReport src = pkg.getChildren().get("EmmaCoverageResultSummary.java");
        assertCoverage(src.getLineCoverage(), 34, 41);
    }
	
	@Test
	@Ignore("Currently no method level coverage data")
	public void testMethodReport() throws Exception {
        //        ClassReport clz = src.getChildren().get("EmailListValidator");
		//      assertRatio(clz.getLineCoverage(), 9, 18);
		//      assertTrue(clz.hasClassCoverage());
		//
		//      MethodReport mth = clz.getChildren().get("isValidAddress (String): boolean");
		//      assertRatio(mth.getLineCoverage(), 1, 1);
		//      assertFalse(mth.hasClassCoverage());
		//
		//      mth = clz.getChildren().get("Foo (): void");
		//      assertRatio(mth.getLineCoverage(), 0, 0);
		//      assertFalse(mth.hasClassCoverage());
		//      assertFalse(mth.hasLineCoverage());
	}
        
	@Test
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
