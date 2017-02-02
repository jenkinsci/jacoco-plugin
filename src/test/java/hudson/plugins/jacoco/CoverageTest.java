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
        assertEquals(67, c.getPercentage());
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
        assertEquals(100, c.getPercentage());
    }
}