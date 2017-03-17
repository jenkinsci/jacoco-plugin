package hudson.plugins.jacoco.model;

import hudson.plugins.jacoco.Messages;
import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
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
	enum CoverageType
	{
		INSTRUCTION
				{
					@Override
					public String getMessage() {
						return Messages.CoverageObject_Legend_Instructions();
					}

					@Override
					public Coverage getCoverage(CoverageObject<?> a) {
						return a.instruction;
					}

				},
		BRANCH
				{
					@Override
					public String getMessage() {
						return Messages.CoverageObject_Legend_Branch();
					}

					@Override
					public Coverage getCoverage(CoverageObject<?> a) {
						return a.branch;
					}

				},
		COMPLEXITY
				{
					@Override
					public String getMessage() {
						return Messages.CoverageObject_Legend_Complexity();
					}

					@Override
					public Coverage getCoverage(CoverageObject<?> a) {
						return a.complexity;
					}

				},
		METHOD
				{
					@Override
					public String getMessage() {
						return Messages.CoverageObject_Legend_Method();
					}

					@Override
					public Coverage getCoverage(CoverageObject<?> a) {
						return a.method;
					}

				},
		CLAZZ
				{
					@Override
					public String getMessage() {
						return Messages.CoverageObject_Legend_Class();
					}

					@Override
					public Coverage getCoverage(CoverageObject<?> a) {
						return a.clazz;
					}

				},
		LINE
				{
					@Override
					public String getMessage() {
						return Messages.CoverageObject_Legend_Line();
					}

					@Override
					public Coverage getCoverage(CoverageObject<?> a) {
						return a.line;
					}

				};


		public abstract String getMessage();

		public abstract Coverage getCoverage(CoverageObject<?> a);

		public Number getValue(CoverageObject<?> a, CoverageValue value)
		{
			Coverage c = getCoverage(a);
			if (c == null) return 0;
			return value.getValue(c);

		}

	}

	enum CoverageValue
	{
		MISSED
				{
					@Override
					public String getMessage(CoverageType type)
					{
						return
								Messages.CoverageObject_Legend_Missed(type.getMessage());

					}

					@Override
					public Number getValue(Coverage c)
					{
						return c.getMissed();
					}
				},
		COVERED
				{
					@Override
					public String getMessage(CoverageType type)
					{
						return Messages.CoverageObject_Legend_Covered(type.getMessage());
					}

					@Override
					public Number getValue(Coverage c)
					{
						return c.getCovered();
					}
				},
		PERCENTAGE
				{
					@Override
					public String getMessage(CoverageType type)
					{
						return type.getMessage();
					}

					@Override
					public Number getValue(Coverage c)
					{
						return c.getPercentageFloat();
					}
				};


		public abstract String getMessage(CoverageType type);

		public abstract Number getValue(Coverage c);
	}

	static class Axis
	{
		private String label = null;
		private int crop = -1;
		private boolean skipZero = false;

		public boolean isCrop()
		{
			return crop != -1;
		}

		public boolean isSkipZero()
		{
			return skipZero;
		}

		public String getLabel()
		{
			return label;
		}

		public int getCrop()
		{
			return crop;
		}
	}

	static class Plot
	{
		private CoverageValue value;
		private CoverageType type;
		private Axis axis;
		private Color color;

		public Plot(Axis axis)
		{
			this.axis = axis;
		}

		public Number getValue(CoverageObject<?> a)
		{
			return type.getValue(a, value);
		}

		public String getMessage()
		{
			return value.getMessage(type);
		}

		public Axis getAxis()
		{
			return axis;
		}

		@Override
		public String toString()
		{
			return axis + " " + type + " " + value + " " + color;
		}
	}

	private float baseStroke = 4f;
	private Stack<Axis> axes = new Stack<>();
	private Stack<Plot> plots = new Stack<>();

	public CoverageGraphLayout baseStroke(float baseStroke)
	{
		this.baseStroke = baseStroke;
		return this;
	}

	public CoverageGraphLayout axis()
	{
		axes.push(new Axis());
		return this;
	}

	private void assureAxis()
	{
		if (axes.isEmpty()) axis();
	}

	public CoverageGraphLayout crop()
	{
		return crop(5);
	}

	public CoverageGraphLayout crop(int marginInPercent)
	{
		assureAxis();
		axes.peek().crop = marginInPercent;
		return this;
	}

	public CoverageGraphLayout label(String label)
	{
		assureAxis();
		axes.peek().label = label;
		return this;
	}

	public CoverageGraphLayout skipZero()
	{
		assureAxis();
		axes.peek().skipZero = true;
		return this;
	}

	public CoverageGraphLayout plot()
	{
		assureAxis();
		plots.add(new Plot(axes.peek()));
		return this;
	}

	private void assurePlot()
	{
		if (plots.isEmpty()) plot();
	}

	public CoverageGraphLayout type(CoverageType type)
	{
		assurePlot();
		plots.peek().type = type;
		return this;
	}

	public CoverageGraphLayout value(CoverageValue value)
	{
		assurePlot();
		plots.peek().value = value;
		return this;
	}

	public CoverageGraphLayout color(Color color)
	{
		assurePlot();
		plots.peek().color = color;
		return this;
	}

	public List<Axis> getAxes()
	{
		return Collections.unmodifiableList(axes);
	}

	public List<Plot> getPlots()
	{
		return Collections.unmodifiableList(plots);
	}

	public void apply(JFreeChart chart, boolean onlyOneBuild)
	{
		final CategoryPlot plot = chart.getCategoryPlot();
		Map<Axis, Integer> axisIds = new HashMap<>();
		int axisId = 0;
		for (Axis axis : axes)
		{
			LineAndShapeRenderer renderer = new LineAndShapeRenderer(true, onlyOneBuild);
			if (onlyOneBuild) renderer.setUseOutlinePaint(true);
			renderer.setBaseStroke(new BasicStroke(baseStroke));
			//add axis layout here
			plot.setRenderer(axisId, renderer);
			axisIds.put(axis, axisId);
			axisId++;
		}

		for (Plot p : plots)
		{
			axisId = axisIds.get(p.axis);
			int lineIdPerAxis = plot.getDataset(axisId).getRowIndex(p.getMessage());
			LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer(axisId);
			renderer.setSeriesPaint(lineIdPerAxis, p.color);
			renderer.setSeriesItemLabelPaint(lineIdPerAxis, p.color);
			renderer.setSeriesFillPaint(lineIdPerAxis, p.color);
			//add line layout here
		}

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
