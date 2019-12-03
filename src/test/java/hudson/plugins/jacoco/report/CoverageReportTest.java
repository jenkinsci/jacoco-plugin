package hudson.plugins.jacoco.report;

import static org.easymock.EasyMock.*;
import static org.easymock.MockType.NICE;
import static org.junit.Assert.*;

import hudson.model.Run;
import hudson.plugins.jacoco.ExecutionFileLoader;
import hudson.plugins.jacoco.JacocoBuildAction;
import hudson.plugins.jacoco.JacocoHealthReportThresholds;

import hudson.util.StreamTaskListener;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class CoverageReportTest {
    private static final String EXAMPLE_ROOT_DIR = "/report/example";
    private static final String EXAMPLE_XML_FILE = "/report/example/target/jacoco/jacoco.xml";

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
    public void testDoJacocoXml() throws Exception {
        OutputStream output = new ByteArrayOutputStream();
        StaplerResponse response = mock(NICE, StaplerResponse.class);
        expect(response.getOutputStream()).andReturn(new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }

            @Override
            public void write(int b) throws IOException {
                output.write(b);
            }
        });
        response.setHeader("Content-Disposition", "attachment; filename=\"jacoco.xml\"");

        File rootDir = Paths.get(CoverageReportTest.class.getResource(EXAMPLE_ROOT_DIR).toURI()).toFile();
        Run<?, ?> owner = mock(NICE, Run.class);
        expect(owner.getRootDir()).andReturn(rootDir);

        File xmlFile = Paths.get(CoverageReportTest.class.getResource(EXAMPLE_XML_FILE).toURI()).toFile();
        String expected = FileUtils.readFileToString(xmlFile, StandardCharsets.UTF_8);

        replay(owner, response);
        action.onLoad(owner);
        CoverageReport report = new CoverageReport(action, new ExecutionFileLoader());
        report.setName("jacoco-example");
        report.doJacocoXml().generateResponse(null, response, null);

        assertEquals(expected, output.toString());
    }

    @Test
    public void testThresholds() throws Exception {
        CoverageReport report = new CoverageReport(action, new ExecutionFileLoader());
        report.setThresholds(new JacocoHealthReportThresholds());
    }

    @Before
    public void setUp() {
        action = new JacocoBuildAction(null, null, StreamTaskListener.fromStdout(), null, null);
    }

    private JacocoBuildAction action;
}
