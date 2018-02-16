package hudson.plugins.jacoco.report;

import static org.junit.Assert.*;

import java.io.File;
import java.io.StringWriter;

import org.junit.Test;


public class ClassReportTest {

    @Test
    public void testName() throws Exception {
        ClassReport report = new ClassReport();
        report.setName("testname");
        assertEquals("testname", report.getName());
        report.setName("test/name/1");
        assertEquals("test.name.1", report.getName());
    }
    
    @Test
    public void testChildren() throws Exception {
        ClassReport report = new ClassReport();
        
        assertEquals(0, report.getChildren().size());
        MethodReport child = new MethodReport();
        child.setName("testname");
        report.add(child);
        assertEquals(1, report.getChildren().size());
    }
    
    @Test
    public void testSourceFile() throws Exception {
        ClassReport report = new ClassReport();
        report.setSrcFileInfo(null, "some/path");
        assertEquals(new File("some/path"), report.getSourceFilePath());
    }
    
    @Test
    public void testPrint() throws Exception {
        ClassReport report = new ClassReport();
        report.setSrcFileInfo(null, "some/path");
        
        StringWriter writer = new StringWriter();
        report.printHighlightedSrcFile(writer);
        
        String string = writer.toString();
        assertEquals("ERROR: Error while reading the sourcefile!", string);
    }
    
    @Test
    public void testToString() throws Exception {
        ClassReport report = new ClassReport();
        assertNotNull(report.toString());
    }
}
