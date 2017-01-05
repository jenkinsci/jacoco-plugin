package hudson.plugins.jacoco.portlet.utils;

import java.math.RoundingMode;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UtilsTest {
    /**
     * Tests {@link hudson.plugins.jacoco.portlet.utils.Utils#roundFloat(int scale, RoundingMode roundingMode, float value) }.
     */
    @Test
    public void testRoundFloat() {
        int scale = 1;
        RoundingMode roundingMode = RoundingMode.HALF_EVEN;
        final float value = 9.987f;
        final float roundedAs = 10f;

        assertEquals(roundedAs, Utils.roundFloat(scale, roundingMode, value), 0.00000000000001);
    }
}
