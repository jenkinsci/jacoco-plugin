package hudson.plugins.jacoco;

import hudson.plugins.jacoco.Coverage;

/**
 * JUnit test for {@link Coverage}
 */
public class CoverageTest extends AbstractEmmaTestBase {

    public void testPercentageCalculation() throws Exception {
        Coverage c = new Coverage(1, 2);
        assertEquals(67, c.getPercentage());
    }

    public void testUninitialized() throws Exception {
        Coverage c = new Coverage();
        assertFalse(c.isInitialized());
    }

    public void testAccumulateInitializes() throws Exception {
        Coverage c = new Coverage();
        c.accumulate(3, 2);
        assertTrue(c.isInitialized());
    }

    public void testNormalConstructorInitializes() throws Exception {
        Coverage c = new Coverage(1, 2);
        assertTrue(c.isInitialized());
    }

    public void testVacuousCoverage() throws Exception {
        Coverage c = new Coverage(0, 0);
        assertEquals(0, c.getPercentage());
    }
}