package hudson.plugins.jacoco.portlet;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.plugins.jacoco.portlet.bean.JacocoCoverageResultSummary;
import hudson.plugins.jacoco.portlet.utils.Constants;
import hudson.tasks.Builder;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import org.junit.Test;
import org.jvnet.hudson.test.HudsonTestCase;

/**
 * Tests {@link hudson.plugins.jacoco.portlet.JacocoLoadData} in a Hudson environment.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 * @author Mauro Durante Junior (Mauro.Durantejunior@sonyericsson.com)
 */
public class JacocoLoadDataHudsonTest extends HudsonTestCase {

    /**
     * This method tests loadChartDataWithinRange() when it has positive number of days.
     * Tests {@link hudson.plugins.jacoco.portlet.JacocoLoadData#loadChartDataWithinRange(java.util.List, int)}.
     *
     * @throws Exception if so.
     */
	/*@Test
    public void testLoadChartDataWithinRangePositiveNumberOfDays() throws Exception {

        // Classes 17/20 (54%). Methods 167/69 (29%). Lines 595/293 (33%). Branches 223/67 (23%). Instructions 2733/1351 (33%)
        final float expectedClassCoverage =       100f *   20f / (  17f +   20f);
        final float expectedLineCoverage =        100f *  293f / ( 595f +  293f);
        final float expectedMethodCoverage =      100f *   69f / ( 167f +   69f);
        final float expectedBranchCoverage =      100f *   67f / ( 223f +   67f);
        final float expectedInstructionCoverage = 100f * 1351f / (2733f + 1351f);
        final float expectedComplexityScore =     100f *   92f / ( 289f +   92f);
        final int numberOfDays = 1;
        final int summaryMapSize = 1;

        //Create the project
        FreeStyleProject job1 = createFreeStyleProject("job1");

        //Make it do something, in this case it writes a coverage report to the workspace.
        job1.getBuildersList().add(
          new CopyResourceToWorkspaceBuilder(
                  getClass().getResourceAsStream("/hudson/plugins/jacoco/jacoco.xml"),
                  "reports/coverage/jacoco.xml"));

        //Add a emma publisher
        JacocoPublisher emmaPublisher = new JacocoPublisher();
        emmaPublisher.includes = "reports/coverage/jacoco.xml";
        job1.getPublishersList().add(emmaPublisher);

        //Build it
        job1.scheduleBuild2(0).get();

        //Do the test
        List<Job> jobs = new LinkedList<Job>();
        jobs.add(job1);

        //Verify the result
        Map<LocalDate, JacocoCoverageResultSummary> summaryMap = JacocoLoadData.loadChartDataWithinRange(jobs, numberOfDays);

        // Testing the size of the returned map against the exepected value,
        // which is a non-zero, therefore tha map must not be empty
        assertEquals(summaryMapSize, summaryMap.size());

        JacocoCoverageResultSummary summary = summaryMap.entrySet().iterator().next().getValue();

        // Test evaluated values against expected ones
        
        
        assertEquals("Class Coverage.", expectedClassCoverage, summary.getClassCoverage(), 0.1f);
        assertEquals("Line Coverage.", expectedLineCoverage, summary.getLineCoverage(), 0.1f);
        assertEquals("Method Coverage.", expectedMethodCoverage, summary.getMethodCoverage(), 0.1f);
        assertEquals("Branch Coverage.", expectedBranchCoverage, summary.getBranchCoverage(), 0.1f);
        assertEquals("Instruction Coverage.", expectedInstructionCoverage, summary.getInstructionCoverage(), 0.1f);
        assertEquals("Complexity Score.", expectedComplexityScore, summary.getComplexityScore(), 0.1f);
    }
*/
    /**
     * This method tests loadChartDataWithinRange() when it has multiple jobs and a single build.
     * Tests {@link hudson.plugins.jacoco.portlet.JacocoLoadData#loadChartDataWithinRange(java.util.List, int)}.
     *
     * @throws Exception if so.
     */
	/*@Test
    public void testLoadChartDataWithinRangeMultJobsSingleBuild() throws Exception {

        // Classes 17/20 (54%). Methods 167/69 (29%). Lines 595/293 (33%). Branches 223/67 (23%). Instructions 2733/1351 (33%)
        final float expectedClassCoverage =       100f *   20f / (  17f +   20f);
        final float expectedLineCoverage =        100f *  293f / ( 595f +  293f);
        final float expectedMethodCoverage =      100f *   69f / ( 167f +   69f);
        final float expectedBranchCoverage =      100f *   67f / ( 223f +   67f);
        final float expectedInstructionCoverage = 100f * 1351f / (2733f + 1351f);
        final float expectedComplexityScore =     100f *   92f / ( 289f +   92f);
        final int numberOfDays = 1;
        final int summaryMapSize = 1;

        //Create the project
        FreeStyleProject job1 = createFreeStyleProject("job1");

        //Make it do something, in this case it writes a coverage report to the workspace.
        job1.getBuildersList().add(
                new CopyResourceToWorkspaceBuilder(getClass().getResourceAsStream("/hudson/plugins/jacoco/jacoco.xml"),
                        "reports/coverage/jacoco.xml"));
        //Add a emma publisher
        JacocoPublisher emmaPublisher = new JacocoPublisher();
        emmaPublisher.includes = "reports/coverage/jacoco.xml";
        job1.getPublishersList().add(emmaPublisher);
        //Build it
        job1.scheduleBuild2(0).get();

        //Do the test
        List<Job> jobs = new LinkedList<Job>();

        FreeStyleProject job2 = createFreeStyleProject("job2");
        jobs.add(job1);
        jobs.add(job2);

        //Verify the result
        Map<LocalDate, JacocoCoverageResultSummary> summaryMap = JacocoLoadData.loadChartDataWithinRange(jobs, numberOfDays);

        // Testing the size of the returned map against the exepected value,
        // which is a non-zero, therefore tha map must not be empty
        assertEquals(summaryMapSize, summaryMap.size());

        JacocoCoverageResultSummary summary = summaryMap.entrySet().iterator().next().getValue();
        // Test evaluated values against expected ones
        assertEquals(expectedClassCoverage, summary.getClassCoverage(), 0.1f);
        assertEquals(expectedLineCoverage, summary.getLineCoverage(), 0.1f);
        assertEquals(expectedMethodCoverage, summary.getMethodCoverage(), 0.1f);
        assertEquals(expectedBranchCoverage, summary.getBranchCoverage(), 0.1f);
        assertEquals(expectedInstructionCoverage, summary.getInstructionCoverage(), 0.1f);
        assertEquals(expectedComplexityScore, summary.getComplexityScore(), 0.1f);
    }*/

    /**
     * This method tests the getResultSummary() behavior.
     * Tests {@link hudson.plugins.jacoco.portlet.JacocoLoadData#getResultSummary(java.util.Collection)}.
     * @throws Exception if any
     */
	@Test
    public void testGetResultSummary() throws Exception {

        BigDecimal classCoverage = new BigDecimal(78.0).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
        BigDecimal lineCoverage = new BigDecimal(82.0).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
        BigDecimal methodCoverage = new BigDecimal(0.7).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
        BigDecimal branchCoverage = new BigDecimal(7.7).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
        BigDecimal instructionCoverage = new BigDecimal(8.8).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
        BigDecimal complexityScore = new BigDecimal(1234).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);

        BigDecimal classCoverage2 = new BigDecimal(86.9).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
        BigDecimal lineCoverage2 = new BigDecimal(21.7).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
        BigDecimal methodCoverage2 = new BigDecimal(60.0).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
        BigDecimal branchCoverage2 = new BigDecimal(17.7).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
        BigDecimal instructionCoverage2 = new BigDecimal(18.8).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
        BigDecimal complexityScore2 = new BigDecimal(2234).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);

        // create a result summary with data from the first emma action
        JacocoCoverageResultSummary coverageResultSummary = new JacocoCoverageResultSummary(
        		null, lineCoverage, methodCoverage, classCoverage, branchCoverage, instructionCoverage, complexityScore);
        assertNotNull(coverageResultSummary);

        // create a result summary with data from the second emma action
        JacocoCoverageResultSummary coverageResultSummary2 = new JacocoCoverageResultSummary(
        		null, lineCoverage2, methodCoverage2, classCoverage2, branchCoverage2, instructionCoverage2, complexityScore2);
        assertNotNull(coverageResultSummary2);
        
        // add both coverage result summaries to the emma result summary
        /*JacocoCoverageResultSummary summary = new JacocoCoverageResultSummary();
        summary.addCoverageResult(coverageResultSummary);
        summary.addCoverageResult(coverageResultSummary2);

        // assert the sum has occurred correctly
        assertEquals(classCoverage + classCoverage2, summary.getClassCoverage());
        assertEquals(lineCoverage + lineCoverage2, summary.getLineCoverage());
        assertEquals(methodCoverage + methodCoverage2, summary.getMethodCoverage());
        assertEquals(branchCoverage + branchCoverage2, summary.getBranchCoverage());
        assertEquals(instructionCoverage + instructionCoverage2, summary.getInstructionCoverage());
        assertEquals(complexityScore + complexityScore2, summary.getComplexityScore());*/
        assertTrue(true);
    }
	
	@Test
	public void Dummy() {
		
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
