package hudson.plugins.jacoco.report;

import static org.junit.Assert.*;
import hudson.plugins.jacoco.ExecutionFileLoader;
import hudson.plugins.jacoco.JacocoBuildAction;
import hudson.plugins.jacoco.JacocoHealthReportThresholds;

import hudson.plugins.jacoco.model.Coverage;
import hudson.util.StreamTaskListener;
import org.junit.Test;


public class CoverageReportTest {
    @Test
    public void testGetBuild() throws Exception {
        CoverageReport report = new CoverageReport(action, new ExecutionFileLoader());
        assertNull(report.getBuild());
    }

    @Test
    public void testName() throws Exception {
        CoverageReport report = new CoverageReport(action, new ExecutionFileLoader());
        assertEquals("Jacoco", report.getName());
    }

    @Test
    public void testDoJaCoCoExec() throws Exception {
        CoverageReport report = new CoverageReport(action, new ExecutionFileLoader());
        assertNotNull(report);
        // TODO: how to simulate JaCoCoBuildAction without full Jenkins test-framework?
        // report.doJacocoExec();
    }

    @Test
    public void testThresholds() throws Exception {
        CoverageReport report = new CoverageReport(action, new ExecutionFileLoader());
        report.setThresholds(new JacocoHealthReportThresholds());
    }

    @Test
    public void testPrintRationTable() {
        CoverageReport report = new CoverageReport(action, new ExecutionFileLoader());

        Coverage ratio = new Coverage(37, 73); // 67.36% covered. 33.64 missed.
        StringBuilder buf = new StringBuilder();

        report.printRatioTable(ratio, buf);
        assertTrue("redbar pixel width must be integer.", buf.toString().contains("class='redbar' style='width:34px'"));

    }

    private JacocoBuildAction action = new JacocoBuildAction(null, null, StreamTaskListener.fromStdout(), null, null);
}
