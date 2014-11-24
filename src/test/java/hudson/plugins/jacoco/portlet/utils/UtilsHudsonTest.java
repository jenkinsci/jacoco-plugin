package hudson.plugins.jacoco.portlet.utils;

import hudson.model.FreeStyleProject;
import hudson.model.Job;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.jvnet.hudson.test.HudsonTestCase;

/**
 * Test {@link hudson.plugins.jacoco.portlet.utils.Utils}
 * through HudsonTestCase extension.
 *
 * @author Mauro Durante Junior &lt;Mauro.Durantejunior@sonyericsson.com&gt;
 */
public class UtilsHudsonTest extends HudsonTestCase {

  /**
   * Tests {@link hudson.plugins.jacoco.portlet.utils.Utils#getLastDate(java.util.List) }.
   * @throws Exception on any exception occurrence.
   */
  public void testGetLastDate() throws Exception {

    FreeStyleProject prj = createFreeStyleProject("prj1");
    prj.scheduleBuild2(0).get();
    FreeStyleProject prj2 = createFreeStyleProject("prj2");
    prj2.scheduleBuild2(0).get();

    List<Job> jobs = new ArrayList<Job>();
    jobs.add(prj);
    jobs.add(prj2);

    LocalDate lastDate = Utils.getLastDate(jobs);
    assertNotNull(lastDate);
  }

  /**
   * Tests {@link hudson.plugins.jacoco.portlet.utils.Utils#roundFLoat(int scale, int roundingMode, float value) }.
   */
  public void testRoundFloat() {
    int scale = 1;
    int roundingMode = BigDecimal.ROUND_HALF_EVEN;
    final float value = 9.987f;
    final float roundedAs = 10f;

    assertEquals(roundedAs, Utils.roundFLoat(scale, roundingMode, value));
  }
}
