package hudson.plugins.jacoco.model;

import java.awt.BasicStroke;
import java.awt.Color;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

/**
 * @author Martin Heinzerling
 */
public class CoverageGraphLayout
{
	public void apply(JFreeChart chart)
	{
		final CategoryPlot plot = chart.getCategoryPlot();

		final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();

		renderer.setSeriesPaint(0, Color.green);
		renderer.setSeriesPaint(1, Color.red);
		renderer.setSeriesItemLabelPaint(0, Color.green);
		renderer.setSeriesItemLabelPaint(1, Color.red);
		renderer.setSeriesFillPaint(0, Color.green);
		renderer.setSeriesFillPaint(1, Color.red);

		renderer.setBaseStroke(new BasicStroke(4.0f));

		chart.getLegend().setPosition(RectangleEdge.RIGHT);
		chart.setBackgroundPaint(Color.white);
		// plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
		plot.setBackgroundPaint(Color.WHITE);
		plot.setOutlinePaint(null);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.black);
		plot.setInsets(new RectangleInsets(5.0, 0, 0, 5.0));
		// add common layout here
	}
}
