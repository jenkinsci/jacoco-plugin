package hudson.plugins.jacococoveragecolumn;

import static org.junit.Assert.assertEquals;

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
}
