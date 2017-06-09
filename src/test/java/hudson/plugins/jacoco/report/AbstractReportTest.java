package hudson.plugins.jacoco.report;

import static org.junit.Assert.*;
import hudson.model.TaskListener;
import hudson.plugins.jacoco.ExecutionFileLoader;
import hudson.plugins.jacoco.JacocoBuildAction;

import hudson.util.StreamTaskListener;
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


        TaskListener taskListener = StreamTaskListener.fromStdout();

        JacocoBuildAction action = new JacocoBuildAction(null, null, taskListener, null, null);
        report.getParent().getParent().setParent(new CoverageReport(action, new ExecutionFileLoader()));
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
