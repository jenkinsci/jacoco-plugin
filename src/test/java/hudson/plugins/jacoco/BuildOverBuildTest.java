package hudson.plugins.jacoco;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.plugins.jacoco.portlet.bean.JacocoDeltaCoverageResultSummary;
import hudson.plugins.jacoco.portlet.utils.Constants;
import org.easymock.IAnswer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertTrue;


@RunWith(PowerMockRunner.class)
@PrepareForTest(JacocoDeltaCoverageResultSummary.class)
public class BuildOverBuildTest {

    private JacocoDeltaCoverageResultSummary jacocoDeltaCoverageResultSummary_1, jacocoDeltaCoverageResultSummary_2;
    private JacocoHealthReportDeltaThresholds deltaHealthThresholds;
    private JacocoHealthReportThresholds healthThresholds;

    private Run run = PowerMock.createNiceMock(Run.class);
    private final PrintStream logger = System.out;

    @Before
    public void setUp(){
        jacocoDeltaCoverageResultSummary_1 = new JacocoDeltaCoverageResultSummary();
        jacocoDeltaCoverageResultSummary_1.setInstructionCoverage(12.234f);
        jacocoDeltaCoverageResultSummary_1.setClassCoverage(0.5523f);
        jacocoDeltaCoverageResultSummary_1.setMethodCoverage(11.8921f);
        jacocoDeltaCoverageResultSummary_1.setLineCoverage(21.523f);
        jacocoDeltaCoverageResultSummary_1.setBranchCoverage(0f);
        jacocoDeltaCoverageResultSummary_1.setComplexityCoverage(1.34f);
        jacocoDeltaCoverageResultSummary_1.setCoverageBetterThanPrevious(false);

        jacocoDeltaCoverageResultSummary_2 = new JacocoDeltaCoverageResultSummary();
        jacocoDeltaCoverageResultSummary_2.setInstructionCoverage(7.54f);
        jacocoDeltaCoverageResultSummary_2.setClassCoverage(0.439f);
        jacocoDeltaCoverageResultSummary_2.setMethodCoverage(5.340f);
        jacocoDeltaCoverageResultSummary_2.setLineCoverage(7.8921f);
        jacocoDeltaCoverageResultSummary_2.setBranchCoverage(0f);
        jacocoDeltaCoverageResultSummary_2.setComplexityCoverage(1.678f);
        jacocoDeltaCoverageResultSummary_2.setCoverageBetterThanPrevious(true);

        deltaHealthThresholds = new JacocoHealthReportDeltaThresholds("10.556", "0", "2.3434", "9.11457", "8.2525", "1.5556");
        healthThresholds = new JacocoHealthReportThresholds(88, 100, 85, 100, 75, 90, 100, 100, 83, 95, 86, 92);
    }

    // Test if the build with delta coverage > delta threshold and overall coverage lesser than last successful build will fail
    @Test
    public void checkBuildOverBuildFailureTest(){

        PowerMock.mockStatic(JacocoDeltaCoverageResultSummary.class);
        expect(JacocoDeltaCoverageResultSummary.build(anyObject(Run.class))).andReturn(jacocoDeltaCoverageResultSummary_1);

        PowerMock.replay(JacocoDeltaCoverageResultSummary.class);

        JacocoPublisher jacocoPublisher = new JacocoPublisher();
        jacocoPublisher.deltaHealthReport = deltaHealthThresholds;
        Result result = jacocoPublisher.checkBuildOverBuildResult(run, logger);

        PowerMock.verify(JacocoDeltaCoverageResultSummary.class);

        Assert.assertEquals("Delta coverage is greater than delta health threshold values", Result.FAILURE, result);

    }

    // Test if the build with delta coverage < delta threshold will pass
    // and build with delta coverage > delta threshold but overall coverage better than last successful will pass
    @Test
    public void checkBuildOverBuildSuccessTest(){

        PowerMock.mockStatic(JacocoDeltaCoverageResultSummary.class);
        expect(JacocoDeltaCoverageResultSummary.build(anyObject(Run.class))).andReturn(jacocoDeltaCoverageResultSummary_2);
        jacocoDeltaCoverageResultSummary_1.setCoverageBetterThanPrevious(true);
        expect(JacocoDeltaCoverageResultSummary.build(anyObject(Run.class))).andReturn(jacocoDeltaCoverageResultSummary_1);

        PowerMock.replay(JacocoDeltaCoverageResultSummary.class);

        JacocoPublisher jacocoPublisher = new JacocoPublisher();
        jacocoPublisher.deltaHealthReport = deltaHealthThresholds;
        Result result = jacocoPublisher.checkBuildOverBuildResult(run, logger); // check for first test case: delta coverage < delta threshold

        Assert.assertEquals("Delta coverage is lesser than delta health threshold values", Result.SUCCESS, result);

        result = jacocoPublisher.checkBuildOverBuildResult(run, logger); // check for second test case: delta coverage > delta threshold but overall coverage better than last successful build
        Assert.assertEquals("Delta coverage is greater than delta health threshold values but overall coverage is better than last successful build's coverage", Result.SUCCESS, result);

        PowerMock.verify(JacocoDeltaCoverageResultSummary.class);

    }
}
