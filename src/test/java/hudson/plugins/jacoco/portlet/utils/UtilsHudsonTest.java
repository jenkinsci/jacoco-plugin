package hudson.plugins.jacoco.portlet.utils;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jvnet.hudson.test.HudsonTestCase;

import hudson.model.FreeStyleProject;
import hudson.model.Job;

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

    List<Job<?,?>> jobs = new ArrayList<>();
    jobs.add(prj);
    jobs.add(prj2);

    Calendar lastDate = Utils.getLastDate(jobs);
    assertNotNull(lastDate);
  }

  /**
   * Tests {@link hudson.plugins.jacoco.portlet.utils.Utils#roundFloat(int scale, RoundingMode roundingMode, float value) }.
   */
  public void testRoundFloat() {
    int scale = 1;
    RoundingMode roundingMode = RoundingMode.HALF_EVEN;
    final float value = 9.987f;
    final float roundedAs = 10f;

    assertEquals(roundedAs, Utils.roundFloat(scale, roundingMode, value));
  }
}
