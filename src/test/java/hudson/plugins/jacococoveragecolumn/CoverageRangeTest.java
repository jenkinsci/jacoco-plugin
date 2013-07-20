package hudson.plugins.jacococoveragecolumn;

import static org.junit.Assert.*;

import java.awt.Color;

import org.junit.Test;

public class CoverageRangeTest {

	@Test
	public void testAbyssmal() {
		assertEquals(CoverageRange.ABYSSMAL, CoverageRange.valueOf(-1d));
	}

	@Test
	public void testFillColorOf87() throws Exception {
		final Color color = CoverageRange.fillColorOf(87d);
		assertEquals(177, color.getRed());
		assertEquals(255, color.getGreen());
		assertEquals(63, color.getBlue());

	}

	@Test
	public void testFillColorOfHandlesNull() throws Exception {
		final Color color = CoverageRange.fillColorOf(null);
		assertEquals(CoverageRange.ABYSSMAL.getFillColor(), color);
	}
	
	@Test
	public void test() {
		expect(CoverageRange.ABYSSMAL, -20.0);
		expect(CoverageRange.ABYSSMAL, 0.0);
		expect(CoverageRange.ABYSSMAL, 10.3);
		expect(CoverageRange.TRAGIC, 25.0);
		expect(CoverageRange.TRAGIC, 32.0);
		expect(CoverageRange.POOR, 50.0);
		expect(CoverageRange.POOR, 68.2);
		expect(CoverageRange.FAIR, 75.0);
		expect(CoverageRange.FAIR, 83.0);
		expect(CoverageRange.SUFFICIENT, 85.0);
		expect(CoverageRange.SUFFICIENT, 91.0);
		expect(CoverageRange.GOOD, 92.0);
		expect(CoverageRange.GOOD, 96.999);
		expect(CoverageRange.EXCELLENT, 97.0);
		expect(CoverageRange.EXCELLENT, 97.1);
		expect(CoverageRange.PERFECT, 100.0);
		expect(CoverageRange.PERFECT, 230.0);
	}
	
	private void expect(CoverageRange result, double value) {
		assertEquals(result, CoverageRange.valueOf(value));
		assertNotNull(CoverageRange.fillColorOf(value));
		assertNotNull(result.getLineColor());
		assertNotNull(result.getFillHexString());
		assertNotNull(result.getLineHexString());
		assertNotNull(CoverageRange.colorAsHexString(result.getLineColor()));
		assertTrue("Had " + value + " and floor " + result.getFloor(),
				value < 0 || 	// special handling for negative values, should not be passed in anyway
				value >= result.getFloor());
	}
}
