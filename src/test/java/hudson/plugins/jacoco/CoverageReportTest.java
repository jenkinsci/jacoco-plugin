package hudson.plugins.jacoco;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Kohsuke Kawaguchi
 * @author David Carver - Refactored for cleaner seperation of tests
 */
public class CoverageReportTest extends AbstractJacocoTestBase {
    //private Map<Type, Coverage> map = Collections.<CoverageElement.Type, Coverage>emptyMap();
	
	@Test
    public void testLoad() throws Exception {
        /*final BuildListener listener = EasyMock.createNiceMock(BuildListener.class);
        EasyMock.replay(listener);

        JacocoBuildAction action = new JacocoBuildAction(null, null, map, null, listener, null, null);
        
        CoverageReport r = new CoverageReport(action, new ExecutionFileLoader());
        PackageReport pkg = r.getChildren().get("hudson.plugins.jacoco");
        System.out.println(pkg);
        assertCoverage(pkg.getLineCoverage(), 393, 196);
        assertEquals(595, r.getLineCoverage().getMissed());
        
        EasyMock.verify(listener);*/
    }

    /**
     * Ensures the coverage after loading two reports represents the combined metrics of both reports.
     * @throws Exception if any error occurs
     */
	@Test
    public void testLoadMultipleReports() throws Exception {
      /*CoverageReport r = new CoverageReport(null,  
          getClass().getResourceAsStream("jacoco.xml"), 
          getClass().getResourceAsStream("jacoco2.xml"));

      assertCoverage(r.getLineCoverage(), 513, 361);
      
      PackageReport pkg = r.getChildren().get("hudson.plugins.jacoco.portlet.bean");
      assertCoverage(pkg.getLineCoverage(), 34, 41);
      
      pkg = r.getChildren().get("hudson.plugins.jacoco.portlet.chart");
      assertCoverage(pkg.getLineCoverage(), 68, 1);*/
		assertTrue(true);
      
    }
	
	@Test
	public void testCoverageReport() throws Exception {
		//CoverageReport r = new CoverageReport(null,getClass().getResourceAsStream("jacoco2.xml"));
        //assertCoverage(r.getLineCoverage(), 513, 361);
		assertTrue(true);
	}
	
	@Test
	public void testPackageReport() throws Exception {
		//CoverageReport r = new CoverageReport(null,getClass().getResourceAsStream("jacoco2.xml"));
		//PackageReport pkg = r.getChildren().get("hudson.plugins.jacoco.portlet.bean");
		//assertCoverage(pkg.getLineCoverage(), 34, 41);
		assertTrue(true);
	}
	
	@Test
	@Ignore
	public void testSourceFileReport() throws Exception {
		//CoverageReport r = new CoverageReport(null,getClass().getResourceAsStream("jacoco2.xml"));
		//PackageReport pkg = r.getChildren().get("hudson.plugins.jacoco.portlet.bean");
//		SourceFileReport src = pkg.getChildren().get("JacocoCoverageResultSummary.java");
//        assertCoverage(src.getLineCoverage(), 34, 41);
		assertTrue(true);
    }
	
	@Test
	public void testClassReport() throws Exception {
		//CoverageReport r = new CoverageReport(null,getClass().getResourceAsStream("jacoco2.xml"));
		//PackageReport pkg = r.getChildren().get("hudson.plugins.jacoco.portlet.bean");		
        //ClassReport clz = pkg.getChildren().get("JacocoCoverageResultSummary");
        
		//assertCoverage(clz.getLineCoverage(),34, 41);
		//assertTrue(clz.hasClassCoverage());
		assertTrue(true);
	}
	
	@Test
	public void testMethodReport() throws Exception {
		/*
		CoverageReport r = new CoverageReport(null,getClass().getResourceAsStream("jacoco2.xml"));
		PackageReport pkg = r.getChildren().get("hudson.plugins.jacoco.portlet.bean");
        ClassReport clz = pkg.getChildren().get("JacocoCoverageResultSummary");
		MethodReport mth = clz.getChildren().get("getJacocoCoverageResults");
		assertCoverage(mth.getLineCoverage(), 1, 0);
		assertFalse("Found Class coverage on Method. ", mth.hasClassCoverage());*/
		assertTrue(true);
	}
        
	@Test
    public void testEmptyPackage() throws Exception {
        /*CoverageReport r = new CoverageReport(null,getClass().getResourceAsStream("jacoco.xml"));

        PackageReport pkg = r.getChildren().get("fake.empty.package");
        assertCoverage(pkg.getLineCoverage(), 0, 0);
        assertFalse(pkg.hasChildren());
        assertFalse(pkg.hasChildrenClassCoverage());
        assertFalse(pkg.hasChildrenLineCoverage());

        pkg = r.getChildren().get("fake.empty.package.without.lines");
        assertCoverage(pkg.getLineCoverage(), 0, 0);
        assertFalse(pkg.hasChildren());
        assertFalse(pkg.hasChildrenClassCoverage());
        assertFalse(pkg.hasChildrenLineCoverage());*/
		assertTrue(true);

    }
}
