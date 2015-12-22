package hudson.plugins.jacoco.report;

import static org.junit.Assert.*;
import hudson.console.ConsoleNote;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.Cause;
import hudson.model.TaskListener;
import hudson.plugins.jacoco.JacocoBuildAction;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

import org.junit.Test;


public class AbstractReportTest {

    @Test
    public void test() throws Exception {
        AbstractReport<ClassReport,MethodReport> report = new AbstractReport<ClassReport,MethodReport>() {
            // abstract class but not abstract method to override
        };
        assertNotNull(report);
        
        report.setParent(new ClassReport());
        report.getParent().setParent(new PackageReport());

        TaskListener taskListener = new TaskListener() {
            public PrintStream getLogger() {
                return null;
            }

            public void annotate(ConsoleNote consoleNote) throws IOException {

            }

            public void hyperlink(String s, String s1) throws IOException {

            }

            public PrintWriter error(String s) {
                return null;
            }

            public PrintWriter error(String s, Object... objects) {
                return null;
            }

            public PrintWriter fatalError(String s) {
                return null;
            }

            public PrintWriter fatalError(String s, Object... objects) {
                return null;
            }
        };

        JacocoBuildAction action = new JacocoBuildAction(null, null, null, taskListener, null, null);
        report.getParent().getParent().setParent(new CoverageReport(action, null));
        assertNull(report.getBuild());

        assertNull(report.getName());
        assertNull(report.getDisplayName());
        report.setName("testname");
        assertEquals("testname", report.getName());
        assertEquals("testname", report.getDisplayName());
        
        // TODO: cause NPEs, did not find out how to test this without a full jenkins-test
        //assertNull(report.getPreviousResult());
        //CoverageElement cv = new CoverageElement();
        //report.addCoverage(cv);
    }
}
