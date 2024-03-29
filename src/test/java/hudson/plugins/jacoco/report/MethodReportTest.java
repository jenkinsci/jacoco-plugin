package hudson.plugins.jacoco.report;

import static org.junit.Assert.*;

import java.io.StringWriter;

import org.junit.Test;


public class MethodReportTest {
    @Test
    public void testMissingFile() {
        MethodReport report = new MethodReport();
        assertFalse(report.hasClassCoverage());

        report.setSrcFileInfo(null);

        ClassReport p = new ClassReport();
        p.setSrcFileInfo(null, "some/path");
        report.setParent(p);

        StringWriter writer = new StringWriter();
        report.printHighlightedSrcFile(writer);
        String string = writer.toString();
        assertEquals("ERROR: Error while reading the sourcefile!", string);

        report.setName("myname/&:<>2%;");
        assertEquals("myname/____2__", report.getName());
        assertEquals("myname/____2__", report.getDisplayName());
    }

    @Test
    public void testPrint() {
        MethodReport report = new MethodReport();
        assertNotNull(report.printFourCoverageColumns());
    }

    @Test
    public void testChildren() {
        MethodReport report = new MethodReport();
        report.setName("pkg");

        assertEquals(0, report.getChildren().size());
        SourceFileReport child = new SourceFileReport();
        child.setName("testname");
        report.add(child);
        assertEquals("testname", child.getName());
        assertEquals(1, report.getChildren().size());
        assertEquals("testname", report.getChildren().values().iterator().next().getName());
    }

    @Test
    public void testChildrenRemovePkgName() {
        MethodReport report = new MethodReport();
        report.setName("pkg");

        assertEquals(0, report.getChildren().size());
        SourceFileReport child = new SourceFileReport();
        child.setName("pkg.testname");
        report.add(child);
        assertEquals("testname", child.getName());
        assertEquals(1, report.getChildren().size());
        assertEquals("testname", report.getChildren().values().iterator().next().getName());
    }
}
