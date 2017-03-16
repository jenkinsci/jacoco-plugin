package hudson.plugins.jacoco;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JacocoHealthReportDeltaThresholdsTest {

    private JacocoHealthReportDeltaThresholds jacocoHealthReportDeltaThresholds;

    @Before
    public void setUp() {
        this.jacocoHealthReportDeltaThresholds = new JacocoHealthReportDeltaThresholds("10.0555503", "-1", "5.0647928", "190.90", "2.0222", "0");
    }

    // Test if negative coverage thresholds are changed to zero percentage
    @Test
    public void changeNegativeThToZeroTest(){
        assertEquals("Negative TH changed to zero", 0f, jacocoHealthReportDeltaThresholds.getDeltaBranch(), 0.00001);
    }

    // Test if coverage thresholds greater than 100 are changed to 100 percentage
    @Test
    public void changeTooBigToHundredTest(){
        assertEquals("TH greater than 100 changed to 100", 100f, jacocoHealthReportDeltaThresholds.getDeltaLine(), 0.00001);
    }

    // Test if coverage thresholds greater than X.XXXXXX5 are half round up
    @Test
    public void halfRoundUpTest(){
        assertEquals("Delta threshold with scale greater than six is round half up when greater than X.XXXXXX5", 5.064793f, jacocoHealthReportDeltaThresholds.getDeltaComplexity(), 0.00001);
    }

    // Test if coverage thresholds lesser than X.XXXXXX5 are half round down
    @Test
    public void halfRoundDownTest(){
        assertEquals("Delta threshold with scale greater than six is round half down when lesser than X.XXXXXX5", 10.055550f, jacocoHealthReportDeltaThresholds.getDeltaInstruction(), 0.00001);
    }

    // Test if coverage thresholds with scale lesser than six are scaled to six digits after decimal
    @Test
    public void scaleToSixTest(){
        assertEquals("Delta threshold with scale lesser than six is scaled to six", 2.022200f, jacocoHealthReportDeltaThresholds.getDeltaMethod(), 0.00001);
    }
}
