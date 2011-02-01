package hudson.plugins.emma.portlet;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.FreeStyleProject;
import hudson.model.Job;
import hudson.plugins.emma.EmmaPublisher;
import hudson.plugins.emma.portlet.bean.EmmaCoverageResultSummary;
import hudson.tasks.Builder;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.joda.time.LocalDate;
import org.jvnet.hudson.test.HudsonTestCase;

/**
 * Tests {@link hudson.plugins.emma.portlet.EmmaLoadData} in a Hudson environment.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 * @author Mauro Durante Junior (Mauro.Durantejunior@sonyericsson.com)
 */
public class EmmaLoadDataHudsonTest extends HudsonTestCase {

    /**
     * This method tests loadChartDataWithinRange() when it has positive number of days.
     * Tests {@link hudson.plugins.emma.portlet.EmmaLoadData#loadChartDataWithinRange(java.util.List, int)}.
     *
     * @throws Exception if so.
     */
    public void testLoadChartDataWithinRangePositiveNumberOfDays() throws Exception {

        final float expectedBlockCoverage = 0.5f;
        final float expectedClassCoverage = 13.7f;
        final float expectedLineCoverage = 0.6f;
        final float expectedMethodCoverage = 2.0f;
        final int numberOfDays = 1;
        final int summaryMapSize = 1;

        //Create the project
        FreeStyleProject job1 = createFreeStyleProject("job1");

        //Make it do something, in this case it writes a coverage report to the workspace.
        job1.getBuildersList().add(
          new CopyResourceToWorkspaceBuilder(getClass().getResourceAsStream("/hudson/plugins/emma/coveragePortlet.xml"),
                        "reports/coverage/coveragePortlet.xml"));
        //Add a emma publisher
        EmmaPublisher emmaPublisher = new EmmaPublisher();
        emmaPublisher.includes = "reports/coverage/coveragePortlet.xml";
        job1.getPublishersList().add(emmaPublisher);
        //Build it
        job1.scheduleBuild2(0).get();

        //Do the test
        List<Job> jobs = new LinkedList<Job>();
        jobs.add(job1);
        //Verify the result
        Map<LocalDate, EmmaCoverageResultSummary> summaryMap = EmmaLoadData.loadChartDataWithinRange(jobs, numberOfDays);

        // Testing the size of the returned map against the exepected value,
        // which is a non-zero, therefore tha map must not be empty
        assertEquals(summaryMapSize, summaryMap.size());

        EmmaCoverageResultSummary summary = summaryMap.entrySet().iterator().next().getValue();

        // Test evaluated values against expected ones
        assertEquals(expectedBlockCoverage, summary.getBlockCoverage(), 0.1f);
        assertEquals(expectedClassCoverage, summary.getClassCoverage(), 0.1f);
        assertEquals(expectedLineCoverage, summary.getLineCoverage(), 0.1f);
        assertEquals(expectedMethodCoverage, summary.getMethodCoverage(), 0.1f);
    }

    /**
     * This method tests loadChartDataWithinRange() when it has multiple jobs and a single build.
     * Tests {@link hudson.plugins.emma.portlet.EmmaLoadData#loadChartDataWithinRange(java.util.List, int)}.
     *
     * @throws Exception if so.
     */
    public void testLoadChartDataWithinRangeMultJobsSingleBuild() throws Exception {

        final float expectedBlockCoverage = 0.5f;
        final float expectedClassCoverage = 13.7f;
        final float expectedLineCoverage = 0.6f;
        final float expectedMethodCoverage = 2.0f;
        final int numberOfDays = 1;
        final int summaryMapSize = 1;

        //Create the project
        FreeStyleProject job1 = createFreeStyleProject("job1");

        //Make it do something, in this case it writes a coverage report to the workspace.
        job1.getBuildersList().add(
                new CopyResourceToWorkspaceBuilder(getClass().getResourceAsStream("/hudson/plugins/emma/coveragePortlet.xml"),
                        "reports/coverage/coveragePortlet.xml"));
        //Add a emma publisher
        EmmaPublisher emmaPublisher = new EmmaPublisher();
        emmaPublisher.includes = "reports/coverage/coveragePortlet.xml";
        // emmaPublisher.includes = "resources/hudson/plugins/emma/coveragePortlet.xml";
        job1.getPublishersList().add(emmaPublisher);
        //Build it
        job1.scheduleBuild2(0).get();

        //Do the test
        List<Job> jobs = new LinkedList<Job>();

        FreeStyleProject job2 = createFreeStyleProject("job2");
        jobs.add(job1);
        jobs.add(job2);

        //Verify the result
        Map<LocalDate, EmmaCoverageResultSummary> summaryMap = EmmaLoadData.loadChartDataWithinRange(jobs, numberOfDays);

        // Testing the size of the returned map against the exepected value,
        // which is a non-zero, therefore tha map must not be empty
        assertEquals(summaryMapSize, summaryMap.size());

        EmmaCoverageResultSummary summary = summaryMap.entrySet().iterator().next().getValue();
        // Test evaluated values against expected ones
        assertEquals(expectedBlockCoverage, summary.getBlockCoverage(), 0.1f);
        assertEquals(expectedClassCoverage, summary.getClassCoverage(), 0.1f);
        assertEquals(expectedLineCoverage, summary.getLineCoverage(), 0.1f);
        assertEquals(expectedMethodCoverage, summary.getMethodCoverage(), 0.1f);
    }

    /**
     * This method tests the getResultSummary() behavior.
     * Tests {@link hudson.plugins.emma.portlet.EmmaLoadData#getResultSummary(java.util.Collection)}.
     * @throws Exception if any
     */
    public void testGetResultSummary() throws Exception {

        float blockCoverage = 12.0f;
        float classCoverage = 78.0f;
        float lineCoverage = 82.0f;
        float methodCoverage = 0.7f;

        float blockCoverage2 = 54.0f;
        float classCoverage2 = 86.9f;
        float lineCoverage2 = 21.7f;
        float methodCoverage2 = 60.0f;

        // create a result summary with data from the first emma action
        EmmaCoverageResultSummary coverageResultSummary = new EmmaCoverageResultSummary(null, blockCoverage, lineCoverage, methodCoverage,
          classCoverage);

        // create a result summary with data from the second emma action
        EmmaCoverageResultSummary coverageResultSummary2 = new EmmaCoverageResultSummary(null, blockCoverage2, lineCoverage2, methodCoverage2,
          classCoverage2);

        // add both coverage result summaries to the emma result summary
        EmmaCoverageResultSummary summary = new EmmaCoverageResultSummary();
        summary.addCoverageResult(coverageResultSummary);
        summary.addCoverageResult(coverageResultSummary2);

        // assert the sum has occurred correctly
        assertEquals(blockCoverage + blockCoverage2, summary.getBlockCoverage());
        assertEquals(classCoverage + classCoverage2, summary.getClassCoverage());
        assertEquals(lineCoverage + lineCoverage2, summary.getLineCoverage());
        assertEquals(methodCoverage + methodCoverage2, summary.getMethodCoverage());
    }

    /**
     * Test utility class.
     * A Builder that writes some data into a file in the workspace.
     */
    static class CopyResourceToWorkspaceBuilder extends Builder {

        private final InputStream content;
        private final String fileName;

        /**
         * Default constructor.
         *
         * @param content  the content to write to the file.
         * @param fileName the name of the file relative to the workspace.
         */
        CopyResourceToWorkspaceBuilder(InputStream content, String fileName) {
            this.content = content;
            this.fileName = fileName;
        }

        @Override
        public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
                throws InterruptedException, IOException {
            FilePath path = build.getWorkspace().child(fileName);
            path.copyFrom(content);
            return true;
        }
    }
}
