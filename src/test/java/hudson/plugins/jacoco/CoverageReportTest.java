package hudson.plugins.jacoco;

import org.junit.Ignore;
import org.junit.Test;

import hudson.plugins.jacoco.report.ClassReport;
import hudson.plugins.jacoco.report.CoverageReport;
import hudson.plugins.jacoco.report.MethodReport;
import hudson.plugins.jacoco.report.PackageReport;
import hudson.plugins.jacoco.report.SourceFileReport;
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
        assertCoverage(pkg.getLineCoverage(), 786, 392);
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
      assertCoverage(pkg.getLineCoverage(), 68, 82);
      
      pkg = r.getChildren().get("hudson.plugins.emma.portlet.chart");
      assertCoverage(pkg.getLineCoverage(), 136, 0 + 2);
      
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
		assertCoverage(pkg.getLineCoverage(), 68, 82);
	}
	
	@Test
	@Ignore
	public void testSourceFileReport() throws Exception {
		CoverageReport r = new CoverageReport(null,getClass().getResourceAsStream("jacoco2.xml"));
		PackageReport pkg = r.getChildren().get("hudson.plugins.emma.portlet.bean");
//		SourceFileReport src = pkg.getChildren().get("EmmaCoverageResultSummary.java");
//        assertCoverage(src.getLineCoverage(), 34, 41);
    }
	
	@Test
	public void testClassReport() throws Exception {
		CoverageReport r = new CoverageReport(null,getClass().getResourceAsStream("jacoco2.xml"));
		PackageReport pkg = r.getChildren().get("hudson.plugins.emma.portlet.bean");		
        ClassReport clz = pkg.getChildren().get("EmmaCoverageResultSummary");
        
		assertCoverage(clz.getLineCoverage(),34, 41);
		assertTrue(clz.hasClassCoverage());
	}
	
	@Test
	public void testMethodReport() throws Exception {
		CoverageReport r = new CoverageReport(null,getClass().getResourceAsStream("jacoco2.xml"));
		PackageReport pkg = r.getChildren().get("hudson.plugins.emma.portlet.bean");
        ClassReport clz = pkg.getChildren().get("EmmaCoverageResultSummary");
		MethodReport mth = clz.getChildren().get("getEmmaCoverageResults");
		assertCoverage(mth.getLineCoverage(), 1, 0);
		assertFalse("Found Class coverage on Method. ", mth.hasClassCoverage());
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
        assertFalse(pkg.hasChildren());
        assertFalse(pkg.hasChildrenClassCoverage());
        assertFalse(pkg.hasChildrenLineCoverage());

    }
}
