package hudson.plugins.emma;


/**
 * @author Kohsuke Kawaguchi
 */
public class EmmaBuildActionTest extends AbstractEmmaTestBase {
  
    public void testLoadReport1() throws Exception {
        EmmaBuildAction r = EmmaBuildAction.load(null,null,
                new EmmaHealthReportThresholds(30, 90, 25, 80, 20, 70, 15, 60),
                getClass().getResourceAsStream("coverage.xml"));
        assertEquals(100, r.clazz.getPercentage());
        assertEquals(64, r.line.getPercentage());
        assertRatio(r.clazz, 185,185);
        assertRatio(r.method, 1345,2061);
        assertRatio(r.block, 44997,74846);
        assertRatio(r.line, 8346.3f,13135);
        assertEquals("Coverage: Methods 1345/2061 (65%). Blocks 44997/74846 (60%).   ",
                     r.getBuildHealth().getDescription());
    }
    
    public void testLoadReport2() throws Exception {
        EmmaBuildAction r = EmmaBuildAction.load(null,null,
                new EmmaHealthReportThresholds(30, 90, 25, 80, 20, 70, 15, 60),
                getClass().getResourceAsStream("coverageh.xml"));
        assertEquals(1, r.clazz.getPercentage());
        assertEquals(1, r.line.getPercentage());
        assertRatio(r.clazz, 1, 149);
        assertRatio(r.method, 2, 678);
        assertRatio(r.block, 42, 9659);
        assertRatio(r.line, 9, 1693);
        assertEquals("Coverage: Classes 1/149 (1%). Methods 2/678 (0%). Blocks 42/9659 (0%). Lines 9/1693 (1%). ",
                     r.getBuildHealth().getDescription());
    }
    
    public void testLoadMultipleReports() throws Exception {
      EmmaBuildAction r = EmmaBuildAction.load(null,null,
              new EmmaHealthReportThresholds(30, 90, 25, 80, 20, 70, 15, 60),
              getClass().getResourceAsStream("coverage.xml"), 
              getClass().getResourceAsStream("coverageh.xml"));
      assertEquals(56, r.clazz.getPercentage());
      assertEquals(56, r.line.getPercentage());
      assertRatio(r.clazz, 186, 334);
      assertRatio(r.method, 1347, 2739);
      assertRatio(r.block, 45039, 84505);
      assertRatio(r.line, 8355.3f,14828);
      assertEquals("Coverage: Classes 186/334 (56%). Methods 1347/2739 (49%). Blocks 45039/84505 (53%). Lines 8355.3/14828 (56%). ",
                   r.getBuildHealth().getDescription());
  }
}
