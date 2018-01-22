package hudson.plugins.jacoco.model;

import hudson.Util;
import hudson.model.Api;
import hudson.model.Run;
import hudson.plugins.jacoco.Rule;
import hudson.plugins.jacoco.model.CoverageGraphLayout.Axis;
import hudson.plugins.jacoco.model.CoverageGraphLayout.CoverageType;
import hudson.plugins.jacoco.model.CoverageGraphLayout.CoverageValue;
import hudson.plugins.jacoco.model.CoverageGraphLayout.Plot;
import hudson.plugins.jacoco.report.AggregatedReport;
import hudson.util.ChartUtil;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;
import hudson.util.DataSetBuilder;
import hudson.util.Graph;
import hudson.util.ShiftedCategoryAxis;
import java.awt.Color;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import org.jacoco.core.analysis.ICoverageNode;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;


/**
 * Base class of all coverage objects.
 *
 * @author Kohsuke Kawaguchi
 * @author Martin Heinzerling
 * @param <SELF> self-type
 */
@ExportedBean
public abstract class CoverageObject<SELF extends CoverageObject<SELF>> {
    //private static final Logger logger = Logger.getLogger(CoverageObject.class.getName());

	public Coverage clazz = new Coverage();
	public Coverage method = new Coverage();
	public Coverage line = new Coverage();
	public Coverage complexity = new Coverage();
	public Coverage instruction = new Coverage();
	public Coverage branch = new Coverage();



	/**
	 * Variables used to store which child has to highest coverage for each coverage type.
	 */
	public int maxClazz=1;
	public int maxMethod=1;
	public int maxLine=1;
	public int maxComplexity=1;
	public int maxInstruction=1;
	public int maxBranch=1;

	private volatile boolean failed = false;


	/**
     * @return the maxClazz
     */
    public int getMaxClazz() {
        return maxClazz;
    }

    /**
     * @param maxClazz the maxClazz to set
     */
    public void setMaxClazz(int maxClazz) {
        this.maxClazz = maxClazz;
    }

    /**
     * @return the maxMethod
     */
    public int getMaxMethod() {
        return maxMethod;
    }

    /**
     * @param maxMethod the maxMethod to set
     */
    public void setMaxMethod(int maxMethod) {
        this.maxMethod = maxMethod;
    }

    /**
     * @return the maxLine
     */
    public int getMaxLine() {
        return maxLine;
    }

    /**
     * @param maxLine the maxLine to set
     */
    public void setMaxLine(int maxLine) {
        this.maxLine = maxLine;
    }

    /**
     * @return the maxComplexity
     */
    public int getMaxComplexity() {
        return maxComplexity;
    }

    /**
     * @param maxComplexity the maxComplexity to set
     */
    public void setMaxComplexity(int maxComplexity) {
        this.maxComplexity = maxComplexity;
    }

    /**
     * @return the maxInstruction
     */
    public int getMaxInstruction() {
        return maxInstruction;
    }

    /**
     * @param maxInstruction the maxInstruction to set
     */
    public void setMaxInstruction(int maxInstruction) {
        this.maxInstruction = maxInstruction;
    }

    /**
     * @return the maxBranch
     */
    public int getMaxBranch() {
        return maxBranch;
    }

    /**
     * @param maxBranch the maxBranch to set
     */
    public void setMaxBranch(int maxBranch) {
        this.maxBranch = maxBranch;
    }

    public boolean isFailed() {
		return failed;
	}

	/**
	 * Marks this coverage object as failed.
	 * @see Rule
	 */
	public void setFailed() {
		failed = true;
	}

	@Exported(inline=true)
	public Coverage getClassCoverage() {
		return clazz;
	}

	@Exported(inline=true)
	public Coverage getMethodCoverage() {
		return method;
	}

	@Exported(inline=true)
	public Coverage getComplexityScore() {
		return complexity;
	}

	@Exported(inline=true)
	public Coverage getInstructionCoverage() {
		return instruction;
	}

	@Exported(inline=true)
	public Coverage getBranchCoverage() {
		return branch;
	}

	/**
	 * Line coverage. Can be null if this information is not collected.
	 * @return Line coverage.
	 */
	@Exported(inline=true)
	public Coverage getLineCoverage() {
		return line;
	}

	/**
	 * Gets the build object that owns the whole coverage report tree.
	 * @return the build object that owns the whole coverage report tree.
	 */
	public abstract Run<?,?> getBuild();

	/**
	 * Gets the corresponding coverage report object in the previous
	 * run that has the record.
	 *
	 * @return
	 *      null if no earlier record was found.
	 */
	@Exported
	public abstract SELF getPreviousResult();
	
	public CoverageObject<?> getParent() {return null;}

	/**
	 * Used in the view to print out four table columns with the coverage info.
	 * @return HTML code.
	 */
	public String printFourCoverageColumns() {
		StringBuilder buf = new StringBuilder();
		instruction.setType(CoverageElement.Type.INSTRUCTION);
		clazz.setType(CoverageElement.Type.CLASS);
		complexity.setType(CoverageElement.Type.COMPLEXITY);
		branch.setType(CoverageElement.Type.BRANCH);
		line.setType(CoverageElement.Type.LINE);
		method.setType(CoverageElement.Type.METHOD);
		printRatioCell(isFailed(), instruction, buf);
		printRatioCell(isFailed(), branch, buf);
		printRatioCell(isFailed(), complexity, buf);
		printRatioCell(isFailed(), line, buf);
		printRatioCell(isFailed(), method, buf);
		printRatioCell(isFailed(), clazz, buf);
		return buf.toString();
	}

	public boolean hasLineCoverage() {
		return line.isInitialized();
	}

	public boolean hasClassCoverage() {
		return clazz.isInitialized();
	}


	static NumberFormat dataFormat = new DecimalFormat("000.00", new DecimalFormatSymbols(Locale.US));
	static NumberFormat percentFormat = new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.US));
	static NumberFormat intFormat = new DecimalFormat("0", new DecimalFormatSymbols(Locale.US));

	protected void printRatioCell(boolean failed, Coverage ratio, StringBuilder buf) {
		if (ratio != null && ratio.isInitialized()) {
			//String className = "nowrap" + (failed ? " red" : "");
			buf.append("<td class='").append("").append("'");
			buf.append(" data='").append(dataFormat.format(ratio.getPercentageFloat()));
			buf.append("'>\n");
			printRatioTable(ratio, buf);
			buf.append("</td>\n");
		}
	}

	protected void printRatioTable(Coverage ratio, StringBuilder buf){
		//String percent = percentFormat.format(ratio.getPercentageFloat());
		String numerator = intFormat.format(ratio.getMissed());
		String denominator = intFormat.format(ratio.getCovered());
		int maximum = 1;
		if (ratio.getType().equals(CoverageElement.Type.INSTRUCTION)) {
			maximum = getParent().maxInstruction;
		} else if (ratio.getType().equals(CoverageElement.Type.BRANCH)) {
		    maximum = getParent().maxBranch;
		} else if (ratio.getType().equals(CoverageElement.Type.COMPLEXITY)) {
		    maximum = getParent().maxComplexity;
		} else if (ratio.getType().equals(CoverageElement.Type.LINE)) {
		    maximum = getParent().maxLine;
		} else if (ratio.getType().equals(CoverageElement.Type.METHOD)) {
		    maximum = getParent().maxMethod;
		} else if (ratio.getType().equals(CoverageElement.Type.CLASS)) {
		    maximum = getParent().maxClazz;
		}

		float redBar = ((float) ratio.getMissed())/maximum*100;
		float greenBar = ((float)ratio.getTotal())/maximum*100;

		buf.append("<table class='percentgraph' cellpadding='0px' cellspacing='0px'>")
				.append("<tr>" +
						"<td class='percentgraph' colspan='2'><span class='text'><b>M:</b> ").append(numerator).append(" <b>C:</b> ").append(denominator).append("</span></td></tr>")
		.append("<tr>")
		    .append("<td width='40px' class='data'>").append(ratio.getPercentage()).append("%</td>")	
		    .append("<td>")
		    .append("<div class='percentgraph' style='width: ").append(greenBar).append("px;'>")
		    .append("<div class='redbar' style='width: ").append(redBar).append("px;'>")
		    .append("</td></tr>")
		    .append("</table>");
	}
	
	protected <ReportLevel extends AggregatedReport<?,?,?> > void setAllCovTypes( ReportLevel reportToSet, ICoverageNode covReport) {
		
		Coverage tempCov = new Coverage();
		tempCov.accumulate(covReport.getClassCounter().getMissedCount(), covReport.getClassCounter().getCoveredCount());
		reportToSet.clazz = tempCov;
		
		tempCov = new Coverage();
		tempCov.accumulate(covReport.getBranchCounter().getMissedCount(), covReport.getBranchCounter().getCoveredCount());
		reportToSet.branch = tempCov;
		
		tempCov = new Coverage();
		tempCov.accumulate(covReport.getLineCounter().getMissedCount(), covReport.getLineCounter().getCoveredCount());
		reportToSet.line = tempCov;
		
		tempCov = new Coverage();
		tempCov.accumulate(covReport.getInstructionCounter().getMissedCount(), covReport.getInstructionCounter().getCoveredCount());
		reportToSet.instruction = tempCov;
		
		tempCov = new Coverage();
		tempCov.accumulate(covReport.getMethodCounter().getMissedCount(), covReport.getMethodCounter().getCoveredCount());
		reportToSet.method = tempCov;
		
		tempCov = new Coverage();
		tempCov.accumulate(covReport.getComplexityCounter().getMissedCount(), covReport.getComplexityCounter().getCoveredCount());
		reportToSet.complexity = tempCov;
		
	}
	
	public  < ReportLevel extends AggregatedReport<?,?,?> > void setCoverage( ReportLevel reportToSet, ICoverageNode covReport) {
		
		setAllCovTypes(reportToSet, covReport);
		
		if (this.maxClazz < reportToSet.clazz.getTotal()) {
			this.maxClazz = reportToSet.clazz.getTotal();
		}
		
		if (this.maxBranch < reportToSet.branch.getTotal()) {
			this.maxBranch = reportToSet.branch.getTotal();
		}

		if (this.maxLine < reportToSet.line.getTotal()) {
			this.maxLine = reportToSet.line.getTotal();
		}
		
		if (this.maxInstruction < reportToSet.instruction.getTotal()) {
			this.maxInstruction = reportToSet.instruction.getTotal();
		}

		if (this.maxMethod < reportToSet.method.getTotal()) {
			this.maxMethod = reportToSet.method.getTotal();
		}

		if (this.maxComplexity < reportToSet.complexity.getTotal()) {
			this.maxComplexity = reportToSet.complexity.getTotal();
		}
	}

	/**
	 * Generates the graph that shows the coverage trend up to this report.
	 * @param req Stapler request from which context, graph width and graph height are read
	 * @param rsp Stapler response to which is sent the graph
	 * @throws IOException if any I/O error occurs
	 */
	public void doGraph(StaplerRequest req, StaplerResponse rsp) throws IOException {
		if(ChartUtil.awtProblemCause != null) {
			// not available. send out error message
			rsp.sendRedirect2(req.getContextPath()+"/images/headless.png");
			return;
		}

		Run<?,?> build = getBuild();
		Calendar t = build.getTimestamp();

		String w = Util.fixEmptyAndTrim(req.getParameter("width"));
		String h = Util.fixEmptyAndTrim(req.getParameter("height"));
		int width = (w != null) ? Integer.parseInt(w) : 500;
		int height = (h != null) ? Integer.parseInt(h) : 200;

		CoverageGraphLayout layout = new CoverageGraphLayout()
				.baseStroke(4f)
				.axis()
				.plot().type(CoverageType.LINE).value(CoverageValue.MISSED).color(Color.RED)
				.plot().type(CoverageType.LINE).value(CoverageValue.COVERED).color(Color.GREEN);

		createGraph(t, width, height,layout).doPng(req, rsp);
	}

	GraphImpl createGraph(final Calendar t, final int width, final int height, final CoverageGraphLayout layout) throws IOException
	{
		return new GraphImpl(this, t, width, height, layout)
		{
			@Override
			protected Map<Axis, DataSetBuilder<String, NumberOnlyBuildLabel>> createDataSetBuilder(CoverageObject<SELF> obj)
			{
				Map<Axis, DataSetBuilder<String, NumberOnlyBuildLabel>> builders = new LinkedHashMap<>();
				for (Axis axis : layout.getAxes())
				{
					builders.put(axis, new DataSetBuilder<String, NumberOnlyBuildLabel>());
					if (axis.isCrop()) bounds.put(axis, new Bounds());
				}

				Map<Plot, Number> last = new HashMap<>();
				for (CoverageObject<SELF> a = obj; a != null; a = a.getPreviousResult())
				{
					NumberOnlyBuildLabel label = new NumberOnlyBuildLabel(a.getBuild());
					for (Plot plot : layout.getPlots())
					{
						Number value = plot.getValue(a);
						Axis axis = plot.getAxis();
						if (axis.isSkipZero() && (value == null || value.floatValue() == 0f)) value = null;
						if (value != null)
						{
							if (axis.isCrop()) bounds.get(axis).update(value);
							last.put(plot, value);
						}
						else
						{
							value = last.get(plot);
						}
						builders.get(axis).add(value, plot.getMessage(), label);
					}
				}
				return builders;
			}
		};
	}

	public Api getApi() {
		return new Api(this);
	}

	abstract class GraphImpl extends Graph {

		private CoverageObject<SELF> obj;
		private CoverageGraphLayout layout;
		protected Map<Axis,Bounds> bounds = new HashMap<>();

		protected class Bounds
		{
			float min=Float.MAX_VALUE;
			float max=Float.MIN_VALUE;

			public void update(Number value)
			{
				float v=value.floatValue();
				if (min>v) min=v;
				if (max<v) max=v+1;
			}
		}

		public GraphImpl(CoverageObject<SELF> obj, Calendar timestamp, int defaultW, int defaultH, CoverageGraphLayout layout) {
			super(timestamp, defaultW, defaultH);
			this.obj = obj;
			this.layout =layout;
		}

		protected abstract Map<Axis, DataSetBuilder<String, NumberOnlyBuildLabel>> createDataSetBuilder(CoverageObject<SELF> obj);

		public JFreeChart getGraph( )
		{
			return createGraph();
		}

		@Override
		protected JFreeChart createGraph() {
			Map<Axis, CategoryDataset> dataSets = new LinkedHashMap<>();
			Map<Axis, DataSetBuilder<String, NumberOnlyBuildLabel>> dataSetBuilders = createDataSetBuilder(obj);
			for (Entry<Axis, DataSetBuilder<String, NumberOnlyBuildLabel>> e : dataSetBuilders.entrySet())
			{
				dataSets.put(e.getKey(), e.getValue().build());
			}
			List<Axis> axes = new ArrayList<>(dataSets.keySet());
			boolean onlyOneBuild = dataSets.entrySet().iterator().next().getValue().getColumnCount() < 2;

			final JFreeChart chart = ChartFactory.createLineChart(
					null, // chart title
					null, // unused
					null, // range axis label
					dataSets.get(axes.get(0)), // data
					PlotOrientation.VERTICAL, // orientation
					true, // include legend
					true, // tooltips
					false // urls
			);

			final CategoryPlot plot = chart.getCategoryPlot();

			CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
			plot.setDomainAxis(domainAxis);
			domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
			domainAxis.setLowerMargin(onlyOneBuild ? 0.5 : 0.0);
			domainAxis.setUpperMargin(0.0);
			domainAxis.setCategoryMargin(0.0);

			int axisId = 0;
			for (Axis axis : axes)
			{
				int di = axisId;
				plot.setDataset(di, dataSets.get(axis));
				plot.mapDatasetToRangeAxis(di, axisId);
				NumberAxis numberAxis = new NumberAxis(axis.getLabel());
				plot.setRangeAxis(axisId, numberAxis);
				numberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); //TODO
				setBounds(axis, numberAxis);
				axisId++;
			}

			layout.apply(chart, onlyOneBuild);
			return chart;
		}

		private void setBounds(Axis a, ValueAxis axis)
		{
			if (!a.isCrop()) return;
			Bounds bounds = this.bounds.get(a);
			float border = (bounds.max - bounds.min) / 100 * a.getCrop();
			axis.setUpperBound(bounds.max + border);
			axis.setLowerBound(Math.max(0, bounds.min - border));
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ":"
				+ " instruction=" + instruction
				+ " branch=" + branch
				+ " complexity=" + complexity
				+ " line=" + line
				+ " method=" + method
				+ " class=" + clazz;
	}
}
