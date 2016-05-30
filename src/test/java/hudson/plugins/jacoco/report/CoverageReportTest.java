package hudson.plugins.jacoco.report;

import static org.junit.Assert.*;
import hudson.console.ConsoleNote;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.Cause;
import hudson.model.TaskListener;
import hudson.plugins.jacoco.ExecutionFileLoader;
import hudson.plugins.jacoco.JacocoBuildAction;
import hudson.plugins.jacoco.JacocoHealthReportThresholds;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

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
        // TODO: how to simluate  JaCoCoBuildAction without full Jenkins test-framework?
        // report.doJacocoExec();
    }

    @Test
    public void testThresholds() throws Exception {
        CoverageReport report = new CoverageReport(action, new ExecutionFileLoader());
        report.setThresholds(new JacocoHealthReportThresholds());
    }

    private JacocoBuildAction action = new JacocoBuildAction(null, null, StreamTaskListener.fromStdout(), null, null);
}
