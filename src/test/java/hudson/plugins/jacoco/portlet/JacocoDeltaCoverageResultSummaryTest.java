package hudson.plugins.jacoco.portlet;

import hudson.model.Action;
import hudson.model.Job;
import hudson.model.Run;
import hudson.plugins.jacoco.JacocoBuildAction;
import hudson.plugins.jacoco.portlet.bean.JacocoCoverageResultSummary;
import hudson.plugins.jacoco.portlet.bean.JacocoDeltaCoverageResultSummary;
import hudson.plugins.jacoco.portlet.utils.Constants;
import org.easymock.IAnswer;
import org.easymock.IExpectationSetters;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.*;
import org.powermock.api.easymock.PowerMock;
import static org.easymock.EasyMock.*;

/**
 * Created by Aditi Rajawat on 2/10/17.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(JacocoLoadData.class)
public class JacocoDeltaCoverageResultSummaryTest {

    private final Run run = PowerMock.createNiceMock(Run.class);
    private final Job job = PowerMock.createNiceMock(Job.class);

    JacocoCoverageResultSummary lastSuccessfulBuildCoverage, currentBuildwithMoreCoverage, currentBuildWithLesserCoverage;

    @Before
    public void setUp(){
        lastSuccessfulBuildCoverage = new JacocoCoverageResultSummary(job, new BigDecimal(88.9090).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP)
                , new BigDecimal(95.5055).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP),
                new BigDecimal(98.889).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP),
                new BigDecimal(68.90).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP),
                new BigDecimal(80.822).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP),
                new BigDecimal(60.05623).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP));

        currentBuildwithMoreCoverage = new JacocoCoverageResultSummary(job, new BigDecimal(89.231).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP)
                , new BigDecimal(95.750).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP),
                new BigDecimal(98.999).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP),
                new BigDecimal(68.90).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP),
                new BigDecimal(85.565).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP),
                new BigDecimal(61.232).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP));

        currentBuildWithLesserCoverage = new JacocoCoverageResultSummary(job, new BigDecimal(85.556).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP)
                , new BigDecimal(95.5055).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP),
                new BigDecimal(99.0909).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP),
                new BigDecimal(65.223).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP),
                new BigDecimal(80.822).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP),
                new BigDecimal(61.234).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP));
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

        Assert.assertEquals("Absolute difference in instruction coverage", currentBuildwithMoreCoverage.getInstructionCoverage().subtract(lastSuccessfulBuildCoverage.getInstructionCoverage()).abs(), deltaCoverageSummary.getInstructionCoverage());
        Assert.assertEquals("Absolute difference in branch coverage", currentBuildwithMoreCoverage.getBranchCoverage().subtract(lastSuccessfulBuildCoverage.getBranchCoverage()).abs(), deltaCoverageSummary.getBranchCoverage());
        Assert.assertEquals("Absolute difference in complexity coverage", currentBuildwithMoreCoverage.getComplexityScore().subtract(lastSuccessfulBuildCoverage.getComplexityScore()).abs(), deltaCoverageSummary.getComplexityCoverage());
        Assert.assertEquals("Absolute difference in line coverage", currentBuildwithMoreCoverage.getLineCoverage().subtract(lastSuccessfulBuildCoverage.getLineCoverage()).abs(),deltaCoverageSummary.getLineCoverage());
        Assert.assertEquals("Absolute difference in method coverage", currentBuildwithMoreCoverage.getMethodCoverage().subtract(lastSuccessfulBuildCoverage.getMethodCoverage()).abs(), deltaCoverageSummary.getMethodCoverage());
        Assert.assertEquals("Absolute difference in class coverage", currentBuildwithMoreCoverage.getClassCoverage().subtract(lastSuccessfulBuildCoverage.getClassCoverage()).abs(), deltaCoverageSummary.getClassCoverage());
        Assert.assertTrue(deltaCoverageSummary.isCoverageBetterThanPrevious());

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

        Assert.assertEquals("Absolute difference in instruction coverage", currentBuildWithLesserCoverage.getInstructionCoverage().subtract(lastSuccessfulBuildCoverage.getInstructionCoverage()).abs(), deltaCoverageSummary.getInstructionCoverage());
        Assert.assertEquals("Absolute difference in branch coverage", currentBuildWithLesserCoverage.getBranchCoverage().subtract(lastSuccessfulBuildCoverage.getBranchCoverage()).abs(), deltaCoverageSummary.getBranchCoverage());
        Assert.assertEquals("Absolute difference in complexity coverage", currentBuildWithLesserCoverage.getComplexityScore().subtract(lastSuccessfulBuildCoverage.getComplexityScore()).abs(), deltaCoverageSummary.getComplexityCoverage());
        Assert.assertEquals("Absolute difference in line coverage", currentBuildWithLesserCoverage.getLineCoverage().subtract(lastSuccessfulBuildCoverage.getLineCoverage()).abs(),deltaCoverageSummary.getLineCoverage());
        Assert.assertEquals("Absolute difference in method coverage", currentBuildWithLesserCoverage.getMethodCoverage().subtract(lastSuccessfulBuildCoverage.getMethodCoverage()).abs(), deltaCoverageSummary.getMethodCoverage());
        Assert.assertEquals("Absolute difference in class coverage", currentBuildWithLesserCoverage.getClassCoverage().subtract(lastSuccessfulBuildCoverage.getClassCoverage()).abs(), deltaCoverageSummary.getClassCoverage());
        Assert.assertFalse(deltaCoverageSummary.isCoverageBetterThanPrevious());

    }


}
