package hudson.plugins.jacoco;

import hudson.plugins.jacoco.EmmaBuildAction;
import hudson.plugins.jacoco.EmmaHealthReportThresholds;


/**
 * @author Kohsuke Kawaguchi
 */
public class EmmaBuildActionTest extends AbstractEmmaTestBase {
  
    public void testLoadReport1() throws Exception {
        EmmaBuildAction r = EmmaBuildAction.load(null,null,
                new EmmaHealthReportThresholds(30, 90, 25, 80, 15, 60, 15, 60, 20, 70, 0, 0),
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
    
    public void testLoadReport2() throws Exception {
        EmmaBuildAction r = EmmaBuildAction.load(null,null,
                new EmmaHealthReportThresholds(30, 90, 25, 80, 15, 60, 15, 60, 20, 70, 0, 0),
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
    
    public void testLoadMultipleReports() throws Exception {
      EmmaBuildAction r = EmmaBuildAction.load(null,null,
              new EmmaHealthReportThresholds(30, 90, 25, 80, 15, 60, 15, 60, 20, 70, 0, 0),
              getClass().getResourceAsStream("jacoco.xml"),
              getClass().getResourceAsStream("jacoco2.xml"));
      assertEquals(65, r.clazz.getPercentage());
      assertEquals(37, r.line.getPercentage());
      assertCoverage(r.clazz, 17 + 9, 20 + 28);
      assertCoverage(r.method, 167 + 122, 69 + 116);
      assertCoverage(r.line, 595 + 513, 293 + 361);
      assertCoverage(r.branch, 223 + 224, 67 + 66);
      assertCoverage(r.instruction, 2733 + 2548, 1351 + 1613);
      assertCoverage(r.complexity, 289 + 246, 92 + 137);
      assertEquals("Coverage: Classes 26/48 (65%). Methods 289/185 (39%). Lines 1108/654 (37%). Branches 447/133 (23%). Instructions 5281/2964 (36%).",
                   r.getBuildHealth().getDescription());
  }
}
