package hudson.plugins.jacoco.report;

import static org.junit.Assert.*;

import org.junit.Test;


public class AggregatedReportTest {

    @Test
    public void testSetFailed() throws Exception {
        AggregatedReport<PackageReport,ClassReport,MethodReport> report = new AggregatedReport<PackageReport,ClassReport,MethodReport>() {
        };
        
        assertEquals(0, report.getChildren().size());
        assertFalse(report.hasChildren());
        
        MethodReport child = new MethodReport();
        child.setName("testmethod");
        report.add(child);
        assertEquals(1, report.getChildren().size());
        assertTrue(report.hasChildren());
        assertFalse(report.hasChildrenClassCoverage());
        assertFalse(report.hasChildrenLineCoverage());
        
        report.setParent(new PackageReport());
        assertNotNull(report.getParent());
        
        assertNull(report.getDynamic("test", null, null));
        assertNotNull(report.getDynamic("testmethod", null, null));
        
        report.setFailed();
        
        child.getLineCoverage().accumulate(0, 3);
        assertTrue(report.hasChildrenLineCoverage());

        child.getClassCoverage().accumulate(0, 3);
        assertFalse("For method childs it's always false", report.hasChildrenClassCoverage());
    }
    
    @Test
    public void testClassCoverage() {
        AggregatedReport<CoverageReport,PackageReport,ClassReport> packageReport = 
                new AggregatedReport<CoverageReport, PackageReport, ClassReport>() {
                };

        ClassReport classChild = new ClassReport();
        classChild.setName("testclass");
        packageReport.add(classChild);

        assertFalse(packageReport.hasChildrenClassCoverage());
        assertFalse(packageReport.hasChildrenLineCoverage());

        classChild.getClassCoverage().accumulate(0, 3);
        
        assertTrue(packageReport.hasChildrenClassCoverage());
        assertFalse(packageReport.hasChildrenLineCoverage());
    }
}
