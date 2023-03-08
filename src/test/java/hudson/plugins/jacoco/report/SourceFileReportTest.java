package hudson.plugins.jacoco.report;

import static org.junit.Assert.assertEquals;

import org.junit.Test;



public class SourceFileReportTest {
    @Test
    public void test() {
        SourceFileReport report = new SourceFileReport();
        report.setName("myname");
        assertEquals("myname", report.getName());
        report.setName("myname/2");
        assertEquals("myname.2", report.getName());
        report.setName("myname/&:<>2%;");
        assertEquals("myname.____2__", report.getName());
        assertEquals("myname.____2__", report.getDisplayName());
    }
}
