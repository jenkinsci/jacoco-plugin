package hudson.plugins.jacoco.portlet;

import hudson.model.Job;
import hudson.model.Run;
import hudson.plugins.jacoco.portlet.bean.JacocoCoverageResultSummary;
import hudson.plugins.jacoco.portlet.bean.JacocoDeltaCoverageResultSummary;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class JacocoDeltaCoverageResultSummaryTest {

    private final Run run = mock(Run.class);
    private final Job job = mock(Job.class);

    JacocoCoverageResultSummary lastSuccessfulBuildCoverage, currentBuildwithMoreCoverage, currentBuildWithLesserCoverage;

    @Before
    public void setUp(){
        lastSuccessfulBuildCoverage = new JacocoCoverageResultSummary(job, 88.9090f
                , 95.5055f,
                98.889f,
                68.90f,
                80.822f,
                60.05623f);

        currentBuildwithMoreCoverage = new JacocoCoverageResultSummary(job, 89.231f
                , 95.750f,
                98.999f,
                68.90f,
                85.565f,
                61.232f);

        currentBuildWithLesserCoverage = new JacocoCoverageResultSummary(job, 85.556f
                , 95.5055f,
                99.0909f,
                65.223f,
                80.822f,
                61.234f);
    }

    // Test delta coverage summary when current build has better coverage than previous successful build
    @Test
    public void deltaCoverageSummaryForBetterBuildTest(){

        when(run.getParent()).thenReturn(job);
        when(job.getLastSuccessfulBuild()).thenReturn(run);

        try (MockedStatic<JacocoLoadData> staticJacocoLoadData = mockStatic(JacocoLoadData.class)) {
            staticJacocoLoadData
                    .when(() -> JacocoLoadData.getResult(any()))
                    .thenReturn(lastSuccessfulBuildCoverage)
                    .thenReturn(currentBuildwithMoreCoverage);

            JacocoDeltaCoverageResultSummary deltaCoverageSummary = JacocoDeltaCoverageResultSummary.build(run);

            assertEquals("Absolute difference in instruction coverage",
                currentBuildwithMoreCoverage.getInstructionCoverage() - lastSuccessfulBuildCoverage.getInstructionCoverage(), deltaCoverageSummary.getInstructionCoverage(), 0.00001);
            assertEquals("Absolute difference in branch coverage",
                currentBuildwithMoreCoverage.getBranchCoverage() - lastSuccessfulBuildCoverage.getBranchCoverage(), deltaCoverageSummary.getBranchCoverage(), 0.00001);
            assertEquals("Absolute difference in complexity coverage",
                currentBuildwithMoreCoverage.getComplexityScore() - lastSuccessfulBuildCoverage.getComplexityScore(), deltaCoverageSummary.getComplexityCoverage(), 0.00001);
            assertEquals("Absolute difference in line coverage",
                currentBuildwithMoreCoverage.getLineCoverage() - lastSuccessfulBuildCoverage.getLineCoverage(), deltaCoverageSummary.getLineCoverage(), 0.00001);
            assertEquals("Absolute difference in method coverage",
                currentBuildwithMoreCoverage.getMethodCoverage() - lastSuccessfulBuildCoverage.getMethodCoverage(), deltaCoverageSummary.getMethodCoverage(), 0.00001);
            assertEquals("Absolute difference in class coverage",
                currentBuildwithMoreCoverage.getClassCoverage() - lastSuccessfulBuildCoverage.getClassCoverage(), deltaCoverageSummary.getClassCoverage(), 0.00001);
        }
    }

    // Test delta coverage summary when current build has worse coverage than previous successful build
    @Test
    public void deltaCoverageSummaryForWorseBuildTest(){

        when(run.getParent()).thenReturn(job);
        when(job.getLastSuccessfulBuild()).thenReturn(run);

        try (MockedStatic<JacocoLoadData> staticJacocoLoadData = mockStatic(JacocoLoadData.class)) {
            staticJacocoLoadData
                    .when(() -> JacocoLoadData.getResult(any()))
                    .thenReturn(lastSuccessfulBuildCoverage)
                    .thenReturn(currentBuildWithLesserCoverage);

            JacocoDeltaCoverageResultSummary deltaCoverageSummary = JacocoDeltaCoverageResultSummary.build(run);

            assertEquals("Absolute difference in instruction coverage",
                currentBuildWithLesserCoverage.getInstructionCoverage() - lastSuccessfulBuildCoverage.getInstructionCoverage(), deltaCoverageSummary.getInstructionCoverage(), 0.00001);
            assertEquals("Absolute difference in branch coverage",
                currentBuildWithLesserCoverage.getBranchCoverage() - lastSuccessfulBuildCoverage.getBranchCoverage(), deltaCoverageSummary.getBranchCoverage(), 0.00001);
            assertEquals("Absolute difference in complexity coverage",
                currentBuildWithLesserCoverage.getComplexityScore() - lastSuccessfulBuildCoverage.getComplexityScore(), deltaCoverageSummary.getComplexityCoverage(), 0.00001);
            assertEquals("Absolute difference in line coverage",
                currentBuildWithLesserCoverage.getLineCoverage() - lastSuccessfulBuildCoverage.getLineCoverage(), deltaCoverageSummary.getLineCoverage(), 0.00001);
            assertEquals("Absolute difference in method coverage",
                currentBuildWithLesserCoverage.getMethodCoverage() - lastSuccessfulBuildCoverage.getMethodCoverage(), deltaCoverageSummary.getMethodCoverage(), 0.00001);
            assertEquals("Absolute difference in class coverage",
                currentBuildWithLesserCoverage.getClassCoverage() - lastSuccessfulBuildCoverage.getClassCoverage(), deltaCoverageSummary.getClassCoverage(), 0.00001);
        }
    }
}
