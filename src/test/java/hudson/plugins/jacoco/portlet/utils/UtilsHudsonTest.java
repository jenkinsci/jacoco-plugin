package hudson.plugins.jacoco.portlet.utils;

import hudson.model.FreeStyleProject;
import hudson.model.Job;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.assertNotNull;

/**
 * Test {@link hudson.plugins.jacoco.portlet.utils.Utils}
 * through HudsonTestCase extension.
 *
 * @author Mauro Durante Junior &lt;Mauro.Durantejunior@sonyericsson.com&gt;
 */
public class UtilsHudsonTest {

  @Rule
  public JenkinsRule j = new JenkinsRule();
  /**
   * Tests {@link hudson.plugins.jacoco.portlet.utils.Utils#getLastDate(java.util.List) }.
   * @throws Exception on any exception occurrence.
   */
  @Test
  public void testGetLastDate() throws Exception {

    FreeStyleProject prj = j.createFreeStyleProject("prj1");
    prj.scheduleBuild2(0).get();
    FreeStyleProject prj2 = j.createFreeStyleProject("prj2");
    prj2.scheduleBuild2(0).get();

    List<Job<?,?>> jobs = new ArrayList<>();
    jobs.add(prj);
    jobs.add(prj2);

    LocalDate lastDate = Utils.getLastDate(jobs);
    assertNotNull(lastDate);
  }

}
