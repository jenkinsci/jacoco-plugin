package hudson.plugins.jacoco;

import hudson.plugins.jacoco.portlet.utils.Constants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class JacocoHealthReportDeltaThresholdsTest {

    private JacocoHealthReportDeltaThresholds jacocoHealthReportDeltaThresholds;

    @Before
    public void setUp() {
        this.jacocoHealthReportDeltaThresholds = new JacocoHealthReportDeltaThresholds("10.0555503", "-1", "5.0647928", "190.90", "2.0222", "0");
    }

    // Test if negative coverage thresholds are changed to zero percentage
    @Test
    public void changeNegativeThToZeroTest(){
        Assert.assertEquals("Negative TH changed to zero", 0, jacocoHealthReportDeltaThresholds.getDeltaBranch().compareTo(new BigDecimal(0)));
    }

    // Test if coverage thresholds greater than 100 are changed to 100 percentage
    @Test
    public void changeTooBigToHundredTest(){
        Assert.assertEquals("TH greater than 100 changed to 100", 0, jacocoHealthReportDeltaThresholds.getDeltaLine().compareTo(new BigDecimal(100)));
    }

    // Test if coverage thresholds greater than X.XXXXXX5 are half round up
    @Test
    public void halfRoundUpTest(){
        Assert.assertEquals("Delta threshold with scale greater than six is round half up when greater than X.XXXXXX5", 0, jacocoHealthReportDeltaThresholds.getDeltaComplexity().compareTo(new BigDecimal(5.064793).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP)));
    }

    // Test if coverage thresholds lesser than X.XXXXXX5 are half round down
    @Test
    public void halfRoundDownTest(){
        Assert.assertEquals("Delta threshold with scale greater than six is round half down when lesser than X.XXXXXX5", 0, jacocoHealthReportDeltaThresholds.getDeltaInstruction().compareTo(new BigDecimal(10.055550).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP)));
    }

    // Test if coverage thresholds with scale lesser than six are scaled to six digits after decimal
    @Test
    public void scaleToSixTest(){
        Assert.assertEquals("Delta threshold with scale lesser than six is scaled to six", 0, jacocoHealthReportDeltaThresholds.getDeltaMethod().compareTo(new BigDecimal(2.022200).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP)));
    }
}
