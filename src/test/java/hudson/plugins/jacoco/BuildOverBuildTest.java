package hudson.plugins.jacoco;

import hudson.model.Result;
import hudson.model.Run;
import hudson.plugins.jacoco.portlet.bean.JacocoDeltaCoverageResultSummary;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.io.PrintStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

public class BuildOverBuildTest {

    private JacocoDeltaCoverageResultSummary jacocoDeltaCoverageResultSummary_1, jacocoDeltaCoverageResultSummary_2;
    private JacocoHealthReportDeltaThresholds deltaHealthThresholds;
    //private JacocoHealthReportThresholds healthThresholds;

    private Run run = mock(Run.class);
    private final PrintStream logger = System.out;

    @Before
    public void setUp(){
        jacocoDeltaCoverageResultSummary_1 = new JacocoDeltaCoverageResultSummary();
        jacocoDeltaCoverageResultSummary_1.setInstructionCoverage(-9.234f);
        jacocoDeltaCoverageResultSummary_1.setClassCoverage(0.5523f);
        jacocoDeltaCoverageResultSummary_1.setMethodCoverage(11.8921f);
        jacocoDeltaCoverageResultSummary_1.setLineCoverage(21.523f);
        jacocoDeltaCoverageResultSummary_1.setBranchCoverage(0f);
        jacocoDeltaCoverageResultSummary_1.setComplexityCoverage(1.34f);

        jacocoDeltaCoverageResultSummary_2 = new JacocoDeltaCoverageResultSummary();
        jacocoDeltaCoverageResultSummary_2.setInstructionCoverage(7.54f);
        jacocoDeltaCoverageResultSummary_2.setClassCoverage(0.439f);
        jacocoDeltaCoverageResultSummary_2.setMethodCoverage(5.340f);
        jacocoDeltaCoverageResultSummary_2.setLineCoverage(7.8921f);
        jacocoDeltaCoverageResultSummary_2.setBranchCoverage(0f);
        jacocoDeltaCoverageResultSummary_2.setComplexityCoverage(1.678f);

        deltaHealthThresholds = new JacocoHealthReportDeltaThresholds("10.556", "0", "2.3434", "9.11457", "8.2525", "1.5556");
        //healthThresholds = new JacocoHealthReportThresholds(88, 100, 85, 100, 75, 90, 100, 100, 83, 95, 86, 92);
    }

    /**
     * [JENKINS-58184] - This test verifies that we are ignoring coverage increase while checking against the thresholds.
     * In this test data, Instruction Coverage has gone down but it is still within the configured threshold limit.
     *                  Method and line coverage has increased and are way above thresholds.
     * The check passes the build as no decrease is more than the configured threshold
     */
    @Test
    public void shouldPassIfNegativeMetricIsWithinThresholdAndOtherMetricesArePositiveAndAboveThreshold(){

        try (MockedStatic<JacocoDeltaCoverageResultSummary> staticJacocoDeltaCoverageResultSummary =
                mockStatic(JacocoDeltaCoverageResultSummary.class)) {
            staticJacocoDeltaCoverageResultSummary
                    .when(() -> JacocoDeltaCoverageResultSummary.build(any()))
                    .thenReturn(jacocoDeltaCoverageResultSummary_1);

            JacocoPublisher jacocoPublisher = new JacocoPublisher();
            jacocoPublisher.deltaHealthReport = deltaHealthThresholds;
            Result result = jacocoPublisher.checkBuildOverBuildResult(run, logger);

            Assert.assertEquals("Delta coverage drop is lesser than delta health threshold values", Result.SUCCESS, result);
        }

    }

    // Test if the build with delta coverage < delta threshold will pass
    @Test
    public void checkBuildOverBuildSuccessTest(){

        try (MockedStatic<JacocoDeltaCoverageResultSummary> staticJacocoDeltaCoverageResultSummary = mockStatic(JacocoDeltaCoverageResultSummary.class)) {
            staticJacocoDeltaCoverageResultSummary
                    .when(() -> JacocoDeltaCoverageResultSummary.build(any()))
                    .thenReturn(jacocoDeltaCoverageResultSummary_2)
                    .thenReturn(jacocoDeltaCoverageResultSummary_1);
            JacocoPublisher jacocoPublisher = new JacocoPublisher();
            jacocoPublisher.deltaHealthReport = deltaHealthThresholds;
            Result result = jacocoPublisher.checkBuildOverBuildResult(run, logger); // check for first test case: delta coverage < delta threshold

            Assert.assertEquals("Delta coverage is lesser than delta health threshold values", Result.SUCCESS, result);

            result = jacocoPublisher.checkBuildOverBuildResult(run, logger); // check for second test case: delta coverage > delta threshold but overall coverage better than last successful build
            Assert.assertEquals("Delta coverage is greater than delta health threshold values but overall coverage is better than last successful build's coverage", Result.SUCCESS, result);
        }

    }

    /**
     * [JENKINS-58184] - This test verifies that we are still respecting the thresholds and are failing the build
     *                  in case the drop in coverage is more than the configured threshold for any parameter
     * In this test data, drop in complexity coverage is more than the configured limit of 2.3434 and so the build fails
     */
    @Test
    public void shouldFailIfNegativeMetricIsAboveThresholdAndOtherMetricesArePositive(){
        JacocoDeltaCoverageResultSummary jacocoDeltaCoverageResultSummary = new JacocoDeltaCoverageResultSummary();
        jacocoDeltaCoverageResultSummary.setInstructionCoverage(7.54f);
        jacocoDeltaCoverageResultSummary.setClassCoverage(0.439f);
        jacocoDeltaCoverageResultSummary.setMethodCoverage(5.340f);
        jacocoDeltaCoverageResultSummary.setLineCoverage(7.8921f);
        jacocoDeltaCoverageResultSummary.setBranchCoverage(0f);
        jacocoDeltaCoverageResultSummary.setComplexityCoverage(-2.678f);

        try (MockedStatic<JacocoDeltaCoverageResultSummary> staticJacocoDeltaCoverageResultSummary = mockStatic(JacocoDeltaCoverageResultSummary.class)) {
            staticJacocoDeltaCoverageResultSummary
                    .when(() -> JacocoDeltaCoverageResultSummary.build(any()))
                    .thenReturn(jacocoDeltaCoverageResultSummary);

            JacocoPublisher jacocoPublisher = new JacocoPublisher();
            jacocoPublisher.deltaHealthReport = deltaHealthThresholds;
            Result result = jacocoPublisher.checkBuildOverBuildResult(run, logger);

            Assert.assertEquals("Delta coverage drop is greater than delta health threshold values", Result.FAILURE, result);
        }
    }
}
