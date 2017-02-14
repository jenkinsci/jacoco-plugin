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
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertTrue;

/**
 * Created by Aditi Rajawat on 2/13/17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(JacocoDeltaCoverageResultSummary.class)
public class BuildOverBuildTest {

    private JacocoDeltaCoverageResultSummary jacocoDeltaCoverageResultSummary_1, jacocoDeltaCoverageResultSummary_2;
    private JacocoHealthReportDeltaThresholds deltaHealthThresholds;
    private JacocoHealthReportThresholds healthThresholds;

    private Run run = PowerMock.createNiceMock(Run.class);
    private final TaskListener taskListener = PowerMock.createNiceMock(TaskListener.class);
    private final Launcher launcher = PowerMock.createNiceMock(Launcher.class);

    @Before
    public void setUp(){
        jacocoDeltaCoverageResultSummary_1 = new JacocoDeltaCoverageResultSummary();
        jacocoDeltaCoverageResultSummary_1.setInstructionCoverage(new BigDecimal(12.234).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP));
        jacocoDeltaCoverageResultSummary_1.setClassCoverage(new BigDecimal(0.5523).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP));
        jacocoDeltaCoverageResultSummary_1.setMethodCoverage(new BigDecimal(11.8921).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP));
        jacocoDeltaCoverageResultSummary_1.setLineCoverage(new BigDecimal(21.523).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP));
        jacocoDeltaCoverageResultSummary_1.setBranchCoverage(new BigDecimal(0).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP));
        jacocoDeltaCoverageResultSummary_1.setComplexityCoverage(new BigDecimal(1.34).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP));
        jacocoDeltaCoverageResultSummary_1.setCoverageBetterThanPrevious(false);

        jacocoDeltaCoverageResultSummary_2 = new JacocoDeltaCoverageResultSummary();
        jacocoDeltaCoverageResultSummary_2.setInstructionCoverage(new BigDecimal(7.54).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP));
        jacocoDeltaCoverageResultSummary_2.setClassCoverage(new BigDecimal(0.439).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP));
        jacocoDeltaCoverageResultSummary_2.setMethodCoverage(new BigDecimal(5.340).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP));
        jacocoDeltaCoverageResultSummary_2.setLineCoverage(new BigDecimal(7.8921).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP));
        jacocoDeltaCoverageResultSummary_2.setBranchCoverage(new BigDecimal(0).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP));
        jacocoDeltaCoverageResultSummary_2.setComplexityCoverage(new BigDecimal(1.678).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP));
        jacocoDeltaCoverageResultSummary_2.setCoverageBetterThanPrevious(true);

        deltaHealthThresholds = new JacocoHealthReportDeltaThresholds("10.556", "0", "2.3434", "9.11457", "8.2525", "1.5556");
        healthThresholds = new JacocoHealthReportThresholds(88, 100, 85, 100, 75, 90, 100, 100, 83, 95, 86, 92);
        expect(taskListener.getLogger()).andReturn(System.out).anyTimes();
    }

    // Test if the build with delta coverage > delta threshold will fail
    @Test
    public void checkBuildOverBuildFailureTest(){

        PowerMock.mockStatic(JacocoDeltaCoverageResultSummary.class);
        expect(JacocoDeltaCoverageResultSummary.build(anyObject(Run.class))).andReturn(jacocoDeltaCoverageResultSummary_1);

        PowerMock.replay(JacocoDeltaCoverageResultSummary.class);

        JacocoPublisher jacocoPublisher = new JacocoPublisher();
        jacocoPublisher.deltaHealthReport = deltaHealthThresholds;
        Result result = jacocoPublisher.checkBuildOverBuildResult(run);

        PowerMock.verify(JacocoDeltaCoverageResultSummary.class);

        Assert.assertEquals("Delta coverage is beyond delta health threshold values", Result.FAILURE, result);

    }

    // Test if the build with delta coverage < delta threshold will pass
    @Test
    public void checkBuildOverBuildSuccessTest(){

        PowerMock.mockStatic(JacocoDeltaCoverageResultSummary.class);
        expect(JacocoDeltaCoverageResultSummary.build(anyObject(Run.class))).andReturn(jacocoDeltaCoverageResultSummary_2);

        PowerMock.replay(JacocoDeltaCoverageResultSummary.class);

        JacocoPublisher jacocoPublisher = new JacocoPublisher();
        jacocoPublisher.deltaHealthReport = deltaHealthThresholds;
        Result result = jacocoPublisher.checkBuildOverBuildResult(run);

        PowerMock.verify(JacocoDeltaCoverageResultSummary.class);

        Assert.assertEquals("Delta coverage is beyond delta health threshold values", Result.SUCCESS, result);
    }

}
