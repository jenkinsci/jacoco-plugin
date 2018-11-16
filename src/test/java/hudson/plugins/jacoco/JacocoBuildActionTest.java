package hudson.plugins.jacoco;


import hudson.util.LogTaskListener;
import org.junit.Test;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.file.Files.createDirectories;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * @author Kohsuke Kawaguchi
 */
public class JacocoBuildActionTest extends AbstractJacocoTestBase {
    private static final Logger logger = Logger.getLogger(JacocoBuildActionTest.class.getName());

    @Test
    public void testConstruct() throws Exception {
        File testDir =  new File("target/test/JacocoBuildActionTest");
        createDirectories(new File(testDir, "jacoco/classes").toPath());
        JacocoBuildAction r = JacocoBuildAction.load(
                new JacocoHealthReportThresholds(30, 90, 25, 80, 15, 60, 15, 60, 20, 70, 0, 0),
                new LogTaskListener(logger, Level.INFO),
                new JacocoReportDir(testDir), null, null);
        assertNotNull(r);
    }

    @Test
    public void testConstructNonExistingTestDir() throws Exception {
        File testDir =  new File("target/test/notExistingTest");
        assertFalse("Expecting " + testDir.getAbsolutePath() + " to not exist, but was found",
                testDir.exists());
        JacocoBuildAction r = JacocoBuildAction.load(
                new JacocoHealthReportThresholds(30, 90, 25, 80, 15, 60, 15, 60, 20, 70, 0, 0),
                new LogTaskListener(logger, Level.INFO),
                new JacocoReportDir(testDir), null, null);
        assertNotNull(r);
    }

	/*@Test
    public void testLoadReport1() throws Exception {
        JacocoBuildAction r = JacocoBuildAction.load(null,null,
                new JacocoHealthReportThresholds(30, 90, 25, 80, 15, 60, 15, 60, 20, 70, 0, 0),
                getClass().getResourceAsStream("jacoco.xml"));
        assertEquals(54, r.clazz.getPercentage());
        assertEquals(33, r.line.getPercentage());
        assertCoverage(r.clazz, 17, 20);
        assertCoverage(r.method, 167, 69);
        assertCoverage(r.line, 595, 293);
        assertCoverage(r.branch, 223, 67);
        assertCoverage(r.instruction, 2733, 1351);
        assertCoverage(r.complexity, 289, 92);
        assertEquals("Coverage: Classes 17/20 (54%). Methods 167/69 (29%). Lines 595/293 (33%). Branches 223/67 (23%). Instructions 2733/1351 (33%).",
                     r.getBuildHealth().getDescription());
    }
    
	@Test
    public void testLoadReport2() throws Exception {
        JacocoBuildAction r = JacocoBuildAction.load(null,null,
                new JacocoHealthReportThresholds(30, 90, 25, 80, 15, 60, 15, 60, 20, 70, 0, 0),
                getClass().getResourceAsStream("jacoco2.xml"));
        assertEquals(76, r.clazz.getPercentage());
        assertEquals(41, r.line.getPercentage());
        assertCoverage(r.clazz, 9, 28);
        assertCoverage(r.method, 122, 116);
        assertCoverage(r.line, 513, 361);
        assertCoverage(r.branch, 224, 66);
        assertCoverage(r.instruction, 2548, 1613);
        assertCoverage(r.complexity, 246, 137);
        assertEquals("Coverage: Classes 9/28 (76%). Methods 122/116 (49%). Lines 513/361 (41%). Branches 224/66 (23%). Instructions 2548/1613 (39%).",
                     r.getBuildHealth().getDescription());
    }
    
	@Test
    public void testLoadMultipleReports() throws Exception {
      JacocoBuildAction r = JacocoBuildAction.load(null,null,
              new JacocoHealthReportThresholds(30, 90, 25, 80, 15, 60, 15, 60, 20, 70, 0, 0),
              getClass().getResourceAsStream("jacoco.xml"),
              getClass().getResourceAsStream("jacoco2.xml"));
      assertEquals(76, r.clazz.getPercentage());
      assertEquals(41, r.line.getPercentage());
      assertCoverage(r.clazz,  9,  28);
      assertCoverage(r.method,  122,  116);
      assertCoverage(r.line, 513, 361);
      assertCoverage(r.branch,  224,  66);
      assertCoverage(r.instruction,  2548, 1613);
      assertCoverage(r.complexity,  246,  137);
      assertEquals("Coverage: Classes 9/28 (76%). Methods 122/116 (49%). Lines 513/361 (41%). Branches 224/66 (23%). Instructions 2548/1613" +
      		" (39%).",
                   r.getBuildHealth().getDescription());
  }*/
}
