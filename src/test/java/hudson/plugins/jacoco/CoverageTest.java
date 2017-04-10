package hudson.plugins.jacoco;

import org.junit.Test;

import hudson.plugins.jacoco.model.Coverage;
import static org.junit.Assert.*;

/**
 * JUnit test for {@link Coverage}
 */
public class CoverageTest extends AbstractJacocoTestBase {

	@Test
    public void testPercentageCalculation() throws Exception {
        Coverage c = new Coverage(1, 2);
        assertEquals("Coverage percentage calculation", 66.666667, c.getPercentage(), 0.00001);
    }

	@Test
    public void testUninitialized() throws Exception {
        Coverage c = new Coverage();
        assertFalse(c.isInitialized());
    }

	@Test
    public void testAccumulateInitializes() throws Exception {
        Coverage c = new Coverage();
        c.accumulate(3, 2);
        assertTrue(c.isInitialized());
    }

	@Test
    public void testNormalConstructorInitializes() throws Exception {
        Coverage c = new Coverage(1, 2);
        assertTrue(c.isInitialized());
    }

	@Test
    public void testVacuousCoverage() throws Exception {
        final Coverage c = new Coverage(0, 0);
        assertEquals("Vacuous coverage percentage calculation", 100.000000, c.getPercentage(), 0.00001);
    }
}