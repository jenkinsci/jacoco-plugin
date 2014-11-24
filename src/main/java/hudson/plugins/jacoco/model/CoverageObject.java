package hudson.plugins.jacoco.model;

import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.Api;
import hudson.plugins.jacoco.Messages;
import hudson.plugins.jacoco.Rule;
import hudson.plugins.jacoco.report.AggregatedReport;
import hudson.util.ChartUtil;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;
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

import org.jacoco.core.analysis.ICoverageNode;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
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
	
	public CoverageObject<?> getParent() {return null;}

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
				"<td class='percentgraph' colspan='2'>").append("<span class='text'>").append("<b>M:</b> "+numerator).append(" ").append("<b>C:</b> "+ denominator).append("</span></td></tr>")
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

		@Override
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
}
