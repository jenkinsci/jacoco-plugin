package hudson.plugins.jacococoveragecolumn;

import java.awt.Color;

public enum CoverageRange {
	/*
	 * This ordering is important for the lookup; the lowest value should be
	 * last
	 */
	PERFECT(100.0, new Color(0x00, 0x8B, 0x00), new Color(0xEE, 0xEE, 0xEE)), //
	EXCELLENT(97.0, new Color(0x00, 0xCD, 0x00), new Color(0x00, 0x00, 0x00)), //
	GOOD(92.0, new Color(0x7A, 0xFF, 0x3F), new Color(0x00, 0x00, 0x00)), //
	SUFFICIENT(85.0, new Color(0xC8, 0xFF, 0x3F), new Color(0x00, 0x00, 0x00)), //
	FAIR(75.0, new Color(0xFF, 0xFF, 0x00), new Color(0x00, 0x00, 0x00)), //
	POOR(50.0, new Color(0xFF, 0x7F, 0x00), new Color(0x00, 0x00, 0x00)), //
	TRAGIC(25.0, new Color(0xFF, 0x00, 0x00), new Color(0xEE, 0xEE, 0xEE)), //
	ABYSSMAL(0.0, new Color(0x00, 0x00, 0x00), new Color(0xEE, 0xEE, 0xEE)),
	NA(0.0, new Color(0xFF, 0xFF, 0xFF), new Color(0x00, 0x00, 0x00));

	/**
	 * Minimum coverage amount for this range
	 */
	private final double floor;

	private final Color fillColor;
	private final Color lineColor;

	CoverageRange(final double floor, final Color fillColor,
			final Color lineColor) {
		this.floor = floor;
		this.fillColor = fillColor;
		this.lineColor = lineColor;
	}

	public static CoverageRange valueOf(final Double amount) {
		for (final CoverageRange range : values()) {
			if (amount >= range.floor) {
				return range;
			}
		}
		return ABYSSMAL;
	}

	public static Color fillColorOf(final Double amount) {
		try {
			for (int i = 0; i < values().length; i++) {
				final CoverageRange range = values()[i];
				if (amount == range.floor) {
					return range.fillColor;
				} else if (amount > range.floor) {
					final CoverageRange range1 = values()[i - 1];
					final double t0 = amount - range.floor;
					final double t1 = range1.floor - amount;
					return blendedColor(range.fillColor, range1.fillColor, t0,
							t1);
				}
			}
		} catch (final RuntimeException e) {
			return ABYSSMAL.fillColor;
		}
		return ABYSSMAL.fillColor;
	}

	private static Color blendedColor(final Color fillColor0,
			final Color fillColor1, final double t0, final double t1) {
		final double total = t0 + t1;
		final int r = (int) ((fillColor0.getRed() * t1 + fillColor1.getRed()
				* t0) / total);
		final int g = (int) ((fillColor0.getGreen() * t1 + fillColor1
				.getGreen() * t0) / total);
		final int b = (int) ((fillColor0.getBlue() * t1 + fillColor1.getBlue()
				* t0) / total);
		return new Color(r, g, b);
	}

	public Color getFillColor() {
		return fillColor;
	}

	public Color getLineColor() {
		return lineColor;
	}

	public String getFillHexString() {
		return colorAsHexString(fillColor);
	}

	public String getLineHexString() {
		return colorAsHexString(lineColor);
	}

	public static String colorAsHexString(final Color c) {
		return String.format("%02X%02X%02X", c.getRed(), c.getGreen(),
				c.getBlue());
	}

	public double getFloor() {
		return floor;
	}
}
