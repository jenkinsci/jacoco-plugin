package hudson.plugins.emma;

/**
 * JUnit test for {@link Ratio}
 */
public class RatioTest extends AbstractEmmaTestBase {

    /**
     * Tests that {@link Ratio#parseValue(String)} parses correctly float
     * numbers with either dot or comma as decimal point.
     *
     * @throws Exception
     */
    public void testParseValue() throws Exception {
        assertRatio(Ratio.parseValue("X% (1/2)"), 1.0f, 2.0f);
        assertRatio(Ratio.parseValue("X% (1,3/2)"), 1.3f, 2.0f);
        assertRatio(Ratio.parseValue("X% (1.3/2)"), 1.3f, 2.0f);
        assertRatio(Ratio.parseValue("X% (,3/2)"), 0.3f, 2.0f);
        assertRatio(Ratio.parseValue("X% (.3/2)"), 0.3f, 2.0f);
        assertRatio(Ratio.parseValue("X% (1./2)"), 1.0f, 2.0f);
        assertRatio(Ratio.parseValue("X% (1,/2)"), 1.0f, 2.0f);
        try {
            Ratio.parseValue("X% (1.a/2)");
            fail("Ratio.parseValue() should have raised NumberFormatException.");
        } catch (NumberFormatException e) {
            // OK, we are expecting this.
        }
        Ratio r = Ratio.parseValue("X% (1,3/2)");
        assertRatio(r, 1.3f, 2.0f);
        r.addValue("X% (1,3/2)");
        assertRatio(r, 2.6f, 4.0f);
    }
}