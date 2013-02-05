package hudson.plugins.jacoco.model;

import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.Api;
import hudson.plugins.jacoco.Messages;
import hudson.plugins.jacoco.Rule;
import hudson.plugins.jacoco.report.AggregatedReport;
import hudson.util.ChartUtil;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;
import hudson.util.ColorPalette;
import hudson.util.DataSetBuilder;
import hudson.util.Graph;
import hudson.util.ShiftedCategoryAxis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Logger;

import org.jacoco.core.analysis.ICoverageNode;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;


/**
 * Base class of all coverage objects.
 *
 * @author Kohsuke Kawaguchi
 */
@ExportedBean
public abstract class CoverageObject<SELF extends CoverageObject<SELF>> {

	public Coverage clazz = new Coverage();
	public Coverage method = new Coverage();
	public Coverage line = new Coverage();
	public Coverage complexity = new Coverage();
	public Coverage instruction = new Coverage();
	public Coverage branch = new Coverage();



	/**
	 * Variables used to store which child has to highest coverage for each coverage type.
	 */
	public int maxCoveredClazz=1;
	public int maxCoveredMethod=1;
	public int maxCoveredLine=1;
	public int maxCoveredComplexity=1;
	public int maxCoveredInstruction=1;
	public int maxCoveredBranch=1;
	public int maxMissedClazz=1;
	public int maxMissedMethod=1;
	public int maxMissedLine=1;
	public int maxMissedComplexity=1;
	public int maxMissedInstruction=1;
	public int maxMissedBranch=1;
	
	@Deprecated public transient int maxClazz=1;
	@Deprecated public transient int maxMethod=1;
	@Deprecated public transient int maxLine=1;
	@Deprecated public transient int maxComplexity=1;
	@Deprecated public transient int maxInstruction=1;
	@Deprecated public transient int maxBranch=1;

	private volatile boolean failed = false;

	public int getMaxCoveredClazz() {
		return maxCoveredClazz;
	}

	public void setMaxCoveredClazz(int maxClazz) {
		this.maxCoveredClazz = maxClazz;
	}

	public int getMaxCoveredMethod() {
		return maxCoveredMethod;
	}

	public void setMaxCoveredMethod(int maxMethod) {
		this.maxCoveredMethod = maxMethod;
	}

	public int getMaxCoveredLine() {
		return maxCoveredLine;
	}

	public void setMaxCoveredLine(int maxLine) {
		this.maxCoveredLine = maxLine;
	}

	public int getMaxCoveredComplexity() {
		return maxCoveredComplexity;
	}

	public void setMaxCoveredComplexity(int maxComplexity) {
		this.maxCoveredComplexity = maxComplexity;
	}

	public int getMaxCoveredInstruction() {
		return maxCoveredInstruction;
	}

	public void setMaxCoveredInstruction(int maxInstruction) {
		this.maxCoveredInstruction = maxInstruction;
	}

	public int getMaxCoveredBranch() {
		return maxCoveredBranch;
	}

	public void setMaxCoveredBranch(int maxBranch) {
		this.maxCoveredBranch = maxBranch;
	}

	public int getMaxMissedClazz() {
		return maxMissedClazz;
	}

	public void setMaxMissedClazz(int maxMissedClazz) {
		this.maxMissedClazz = maxMissedClazz;
	}

	public int getMaxMissedMethod() {
		return maxMissedMethod;
	}

	public void setMaxMissedMethod(int maxMissedMethod) {
		this.maxMissedMethod = maxMissedMethod;
	}

	public int getMaxMissedLine() {
		return maxMissedLine;
	}

	public void setMaxMissedLine(int maxMissedLine) {
		this.maxMissedLine = maxMissedLine;
	}

	public int getMaxMissedComplexity() {
		return maxMissedComplexity;
	}

	public void setMaxMissedComplexity(int maxMissedComplexity) {
		this.maxMissedComplexity = maxMissedComplexity;
	}

	public int getMaxMissedInstruction() {
		return maxMissedInstruction;
	}

	public void setMaxMissedInstruction(int maxMissedInstruction) {
		this.maxMissedInstruction = maxMissedInstruction;
	}

	public int getMaxMissedBranch() {
		return maxMissedBranch;
	}

	public void setMaxMissedBranch(int maxMissedBranch) {
		this.maxMissedBranch = maxMissedBranch;
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
	 */
	@Exported(inline=true)
	public Coverage getLineCoverage() {
		return line;
	}

	/**
	 * Gets the build object that owns the whole coverage report tree.
	 */
	public abstract AbstractBuild<?,?> getBuild();

	/**
	 * Gets the corresponding coverage report object in the previous
	 * run that has the record.
	 *
	 * @return
	 *      null if no earlier record was found.
	 */
	@Exported
	public abstract SELF getPreviousResult();
	
	public CoverageObject getParent() {return null;}

	/**
	 * Used in the view to print out four table columns with the coverage info.
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
		String percent = percentFormat.format(ratio.getPercentageFloat());
		String numerator = intFormat.format(ratio.getMissed());
		String denominator = intFormat.format(ratio.getCovered());
		int maximumCovered = 2;
		int maximumMissed=2;
		if (ratio.getType().equals(CoverageElement.Type.INSTRUCTION)) {
			maximumCovered = getParent().maxCoveredInstruction;
			maximumMissed = getParent().maxMissedInstruction;
		} else if (ratio.getType().equals(CoverageElement.Type.BRANCH)) {
			maximumCovered = getParent().maxCoveredBranch;
			maximumMissed = getParent().maxMissedBranch;
		} else if (ratio.getType().equals(CoverageElement.Type.COMPLEXITY)) {
			maximumCovered = getParent().maxCoveredComplexity;
			maximumMissed = getParent().maxMissedComplexity;
		} else if (ratio.getType().equals(CoverageElement.Type.LINE)) {
			maximumCovered = getParent().maxCoveredLine;
			maximumMissed = getParent().maxMissedLine;
		} else if (ratio.getType().equals(CoverageElement.Type.METHOD)) {
			maximumCovered = getParent().maxCoveredMethod;
			maximumMissed = getParent().maxMissedMethod;
		} else if (ratio.getType().equals(CoverageElement.Type.CLASS)) {
			maximumCovered = getParent().maxCoveredClazz;
			maximumMissed = getParent().maxMissedClazz;
		}
		buf.append("<table class='percentgraph' cellpadding='0px' cellspacing='0px'>")
		.append("<tr>" +
				"<td class='percentgraph'>").append("<span class='text'>").append("<b>M:</b> "+numerator).append(" ").append("<b>C:</b> "+ denominator).append("</span></td></tr>")
		.append("<tr>")
		    .append("<td width='40px' class='data'>").append(ratio.getPercentage()).append("%</td>")	
		    .append("<td>")
		    .append("<div class='percentgraph' style='width: ").append(((float)ratio.getCovered()/(float)maximumCovered)*100).append("px;'>")
		    .append("<div class='redbar' style='width: ").append(ratio.getMissed()> ratio.getCovered() ? ((float)ratio.getMissed()/(float)maximumMissed)*100: ((float)ratio.getMissed()/(float)maximumCovered)*100).append("px;'>")
		    .append("</td></tr>")
		    .append("</table>");
	}
	
	protected <ReportLevel extends AggregatedReport > void setAllCovTypes( ReportLevel reportToSet, ICoverageNode covReport) {
		
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
	
	public  < ReportLevel extends AggregatedReport > void setCoverage( ReportLevel reportToSet, ICoverageNode covReport) {
		
		setAllCovTypes(reportToSet, covReport);
		
		if (this.maxCoveredClazz < reportToSet.clazz.getCovered()) {
			this.maxCoveredClazz = reportToSet.clazz.getCovered();
		}
		if (this.maxMissedClazz < reportToSet.clazz.getMissed()) {
			this.maxMissedClazz =reportToSet.clazz.getMissed();
		}
		
		if (this.maxCoveredBranch < reportToSet.branch.getCovered()) {
			this.maxCoveredBranch = reportToSet.branch.getCovered();
		}
		if (this.maxMissedBranch < reportToSet.branch.getMissed()) {
			this.maxMissedBranch = reportToSet.branch.getMissed();
		}

		if (this.maxCoveredLine < reportToSet.line.getCovered()) {
			this.maxCoveredLine = reportToSet.line.getCovered();
		}
		if (this.maxMissedLine < reportToSet.line.getMissed()) {
			this.maxMissedLine = reportToSet.line.getMissed();
		}
		
		if (this.maxCoveredInstruction < reportToSet.instruction.getCovered()) {
			this.maxCoveredInstruction = reportToSet.instruction.getCovered();
		}
		if (this.maxMissedInstruction < reportToSet.instruction.getMissed()) {
			this.maxMissedInstruction = reportToSet.instruction.getMissed();
		}
		
		if (this.maxCoveredMethod < reportToSet.method.getCovered()) {
			this.maxCoveredMethod = reportToSet.method.getCovered();
		}
		if (this.maxMissedMethod < reportToSet.method.getMissed()) {
			this.maxMissedMethod = reportToSet.method.getMissed();
		}

		if (this.maxCoveredComplexity < reportToSet.complexity.getCovered()) {
			this.maxCoveredComplexity = reportToSet.complexity.getCovered();
		}
		if (this.maxMissedComplexity < reportToSet.complexity.getMissed()) {
			this.maxMissedComplexity = reportToSet.complexity.getMissed();
		}

	}

	/**
	 * Generates the graph that shows the coverage trend up to this report.
	 */
	public void doGraph(StaplerRequest req, StaplerResponse rsp) throws IOException {
		if(ChartUtil.awtProblemCause != null) {
			// not available. send out error message
			rsp.sendRedirect2(req.getContextPath()+"/images/headless.png");
			return;
		}

		AbstractBuild<?,?> build = getBuild();
		Calendar t = build.getTimestamp();

		String w = Util.fixEmptyAndTrim(req.getParameter("width"));
		String h = Util.fixEmptyAndTrim(req.getParameter("height"));
		int width = (w != null) ? Integer.valueOf(w) : 500;
		int height = (h != null) ? Integer.valueOf(h) : 200;

		new GraphImpl(this, t, width, height) {

			@Override
			protected DataSetBuilder<String, NumberOnlyBuildLabel> createDataSet(CoverageObject<SELF> obj) {
				DataSetBuilder<String, NumberOnlyBuildLabel> dsb = new DataSetBuilder<String, NumberOnlyBuildLabel>();

				for (CoverageObject<SELF> a = obj; a != null; a = a.getPreviousResult()) {
					NumberOnlyBuildLabel label = new NumberOnlyBuildLabel(a.getBuild());
					/*dsb.add(a.instruction.getPercentageFloat(), Messages.CoverageObject_Legend_Instructions(), label);
                    dsb.add(a.branch.getPercentageFloat(), Messages.CoverageObject_Legend_Branch(), label);
                    dsb.add(a.complexity.getPercentageFloat(), Messages.CoverageObject_Legend_Complexity(), label);
                    dsb.add(a.method.getPercentageFloat(), Messages.CoverageObject_Legend_Method(), label);
                    dsb.add(a.clazz.getPercentageFloat(), Messages.CoverageObject_Legend_Class(), label);*/
					if (a.line != null) {
						dsb.add(a.line.getCovered(), Messages.CoverageObject_Legend_LineCovered(), label);
						dsb.add(a.line.getMissed(), Messages.CoverageObject_Legend_LineMissed(), label);
						
					} else {
						dsb.add(0, Messages.CoverageObject_Legend_LineCovered(), label);
						dsb.add(0, Messages.CoverageObject_Legend_LineMissed(), label);
					}
				}

				return dsb;
			}
		}.doPng(req, rsp);
	}

	public Api getApi() {
		return new Api(this);
	}

	private abstract class GraphImpl extends Graph {

		private CoverageObject<SELF> obj;

		public GraphImpl(CoverageObject<SELF> obj, Calendar timestamp, int defaultW, int defaultH) {
			super(timestamp, defaultW, defaultH);
			this.obj = obj;
		}

		protected abstract DataSetBuilder<String, NumberOnlyBuildLabel> createDataSet(CoverageObject<SELF> obj);

		protected JFreeChart createGraph() {
			final CategoryDataset dataset = createDataSet(obj).build();
			final JFreeChart chart = ChartFactory.createLineChart(
					null, // chart title
					null, // unused
					"", // range axis label
					dataset, // data
					PlotOrientation.VERTICAL, // orientation
					true, // include legend
					true, // tooltips
					false // urls
					);

			// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

			final LegendTitle legend = chart.getLegend();
			legend.setPosition(RectangleEdge.RIGHT);

			chart.setBackgroundPaint(Color.white);

			final CategoryPlot plot = chart.getCategoryPlot();

			// plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
			plot.setBackgroundPaint(Color.WHITE);
			plot.setOutlinePaint(null);
			plot.setRangeGridlinesVisible(true);
			plot.setRangeGridlinePaint(Color.black);

			CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
			plot.setDomainAxis(domainAxis);
			domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
			domainAxis.setLowerMargin(0.0);
			domainAxis.setUpperMargin(0.0);
			domainAxis.setCategoryMargin(0.0);

			final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
			rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			if (line!=null) {
				rangeAxis.setUpperBound(line.getCovered() > line.getMissed() ? line.getCovered() + 5 : line.getMissed() + 5);
			}
			rangeAxis.setLowerBound(0);

			final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
			
			renderer.setSeriesPaint(0, Color.green);
			renderer.setSeriesPaint(1, Color.red);
			
			renderer.setSeriesItemLabelPaint(0, Color.green);
			renderer.setSeriesItemLabelPaint(1, Color.red);
			
			renderer.setSeriesFillPaint(0, Color.green);
			renderer.setSeriesFillPaint(1, Color.red);
			
			renderer.setBaseStroke(new BasicStroke(4.0f));
			//ColorPalette.apply(renderer);

			// crop extra space around the graph
			plot.setInsets(new RectangleInsets(5.0, 0, 0, 5.0));
			
			
			return chart;
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
	private static final Logger logger = Logger.getLogger(CoverageObject.class.getName());
}
