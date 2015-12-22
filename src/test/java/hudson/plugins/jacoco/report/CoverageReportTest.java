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

    private JacocoBuildAction action = new JacocoBuildAction(null, null, null, new TaskListener() {
        
        public void hyperlink(String url, String text) throws IOException {
        }
        
        public PrintStream getLogger() {
            return System.out;
        }
        
        public PrintWriter fatalError(String format, Object... args) {
            return null;
        }
        
        public PrintWriter fatalError(String msg) {
            return null;
        }
        
        public PrintWriter error(String format, Object... args) {
            return null;
        }
        
        public PrintWriter error(String msg) {
            return null;
        }
        
        public void annotate(@SuppressWarnings("rawtypes") ConsoleNote ann) throws IOException {
        }
        
        public void started(List<Cause> causes) {
        }
        
        public void finished(Result result) {
        }
    }, null, null);
}
