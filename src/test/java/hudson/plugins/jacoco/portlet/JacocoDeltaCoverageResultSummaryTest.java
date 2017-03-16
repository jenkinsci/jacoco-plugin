package hudson.plugins.jacoco.portlet;

import hudson.model.Job;
import hudson.model.Run;
import hudson.plugins.jacoco.portlet.bean.JacocoCoverageResultSummary;
import hudson.plugins.jacoco.portlet.bean.JacocoDeltaCoverageResultSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PrepareForTest(JacocoLoadData.class)
public class JacocoDeltaCoverageResultSummaryTest {

    private final Run run = PowerMock.createNiceMock(Run.class);
    private final Job job = PowerMock.createNiceMock(Job.class);

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

        expect(run.getParent()).andReturn(job).anyTimes();
        expect(job.getLastSuccessfulBuild()).andReturn(run).anyTimes();
        PowerMock.mockStatic(JacocoLoadData.class);
        expect(JacocoLoadData.getResult(run)).andReturn(lastSuccessfulBuildCoverage);
        expect(JacocoLoadData.getResult(run)).andReturn(currentBuildwithMoreCoverage);

        PowerMock.replay(run, job);
        PowerMock.replay(JacocoLoadData.class);

        JacocoDeltaCoverageResultSummary deltaCoverageSummary = JacocoDeltaCoverageResultSummary.build(run);

        PowerMock.verify(run, job);
        PowerMock.verify(JacocoLoadData.class);

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
        assertTrue(deltaCoverageSummary.isCoverageBetterThanPrevious());
    }

    // Test delta coverage summary when current build has worse coverage than previous successful build
    @Test
    public void deltaCoverageSummaryForWorseBuildTest(){

        expect(run.getParent()).andReturn(job).anyTimes();
        expect(job.getLastSuccessfulBuild()).andReturn(run).anyTimes();
        PowerMock.mockStatic(JacocoLoadData.class);
        expect(JacocoLoadData.getResult(run)).andReturn(lastSuccessfulBuildCoverage);
        expect(JacocoLoadData.getResult(run)).andReturn(currentBuildWithLesserCoverage);

        PowerMock.replay(run, job);
        PowerMock.replay(JacocoLoadData.class);

        JacocoDeltaCoverageResultSummary deltaCoverageSummary = JacocoDeltaCoverageResultSummary.build(run);

        PowerMock.verify(run, job);
        PowerMock.verify(JacocoLoadData.class);

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
        assertFalse(deltaCoverageSummary.isCoverageBetterThanPrevious());
    }
}
