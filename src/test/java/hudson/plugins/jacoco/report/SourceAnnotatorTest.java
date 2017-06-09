package hudson.plugins.jacoco.report;

import static org.junit.Assert.*;

import java.io.File;
import java.io.PrintWriter;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jacoco.core.analysis.ISourceNode;
import org.jacoco.core.internal.analysis.CounterImpl;
import org.jacoco.core.internal.analysis.SourceFileCoverageImpl;
import org.jacoco.core.internal.analysis.SourceNodeImpl;
import org.junit.Test;


public class SourceAnnotatorTest {

    @Test
    public void testSourceAnnotatorSimple() throws Exception {
        File file = new File("src/test/java/hudson/plugins/jacoco/report/SourceAnnotatorTest.java");
        assertTrue(file.exists());
        SourceAnnotator annotator = new SourceAnnotator(file);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out)) {
            ISourceNode cov = new SourceFileCoverageImpl("testclass", "com.example.test");
            annotator.printHighlightedSrcFile(cov, writer);
        }
        String string = new String(out.toByteArray());
        assertTrue(string.contains("package hudson.plugins.jacoco.report"));
        assertTrue(string.contains("public void testSourceAnnotator()"));
        assertTrue(string.contains("</code>"));        
    }

    @Test
    public void testSourceAnnotator() throws Exception {
        File file = new File("src/test/java/hudson/plugins/jacoco/report/SourceAnnotatorTest.java");
        assertTrue(file.exists());
        SourceAnnotator annotator = new SourceAnnotator(file);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out)) {
            SourceNodeImpl cov = new SourceFileCoverageImpl("testclass", "com.example.test");

            // set some coverage
            cov.increment(CounterImpl.getInstance(0, 1), CounterImpl.getInstance(1, 2), 21);
            cov.increment(CounterImpl.getInstance(0, 1), CounterImpl.getInstance(0, 4), 22);
            cov.increment(CounterImpl.getInstance(1, 0), CounterImpl.getInstance(3, 0), 23);

            annotator.printHighlightedSrcFile(cov, writer);
        }
        String string = new String(out.toByteArray());
        assertTrue(string.contains("package hudson.plugins.jacoco.report"));
        assertTrue(string.contains("public void testSourceAnnotator()"));
        assertTrue(string, string.contains("</code>"));
        
        // coverage data is also contained
        assertTrue(string, string.contains("21:•<SPAN title=\"1 of 3 branches missed.\" style=\"BACKGROUND-COLOR: #ffff80\">"));
        assertTrue(string, string.contains("22:•<SPAN title=\"All 4 branches covered.\" style=\"BACKGROUND-COLOR: #ccffcc\">"));
        assertTrue(string, string.contains("23:•<SPAN title=\"All 3 branches missed.\" style=\"BACKGROUND-COLOR: #ffaaaa\">"));
    }

    @Test
    public void testMissingFile() throws Exception {
        File file = new File("notexisting");
        assertFalse(file.exists());
        SourceAnnotator annotator = new SourceAnnotator(file);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out)) {
            ISourceNode cov = new SourceFileCoverageImpl("testclass", "com.example.test");
            annotator.printHighlightedSrcFile(cov, writer);
        }
        String string = new String(out.toByteArray());
        assertEquals("ERROR: Error while reading the sourcefile!", string);
    }


}
