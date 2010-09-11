package hudson.plugins.emma;

import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.Api;
import hudson.util.ChartUtil;
import hudson.util.ColorPalette;
import hudson.util.DataSetBuilder;
import hudson.util.ShiftedCategoryAxis;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;
import hudson.util.Graph;

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

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

/**
 * Base class of all coverage objects.
 *
 * @author Kohsuke Kawaguchi
 */
@ExportedBean
public abstract class CoverageObject<SELF extends CoverageObject<SELF>> {

	Ratio clazz = new Ratio();
    Ratio method = new Ratio();
    Ratio block = new Ratio();
    Ratio line = new Ratio();
    
    private volatile boolean failed = false;

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
    public Ratio getClassCoverage() {
        return clazz;
    }

    @Exported(inline=true)
    public Ratio getMethodCoverage() {
        return method;
    }

    @Exported(inline=true)
    public Ratio getBlockCoverage() {
        return block;
    }

    /**
     * Line coverage. Can be null if this information is not collected.
     */
    @Exported(inline=true)
    public Ratio getLineCoverage() {
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

    /**
     * Used in the view to print out four table columns with the coverage info.
     */
    public String printFourCoverageColumns() {
        StringBuilder buf = new StringBuilder();
        printRatioCell(isFailed(), clazz, buf);
        printRatioCell(isFailed(), method, buf);
        printRatioCell(isFailed(), block, buf);
        printRatioCell(isFailed(), line, buf);
        return buf.toString();
    }

    public boolean hasLineCoverage() {
        return line.isInitialized();
    }

    public boolean hasClassCoverage() {
        return clazz.isInitialized();
    }

    
    static NumberFormat dataFormat = new DecimalFormat("000.00");
    static NumberFormat percentFormat = new DecimalFormat("0.0");
    static NumberFormat intFormat = new DecimalFormat("0");
    
	protected static void printRatioCell(boolean failed, Ratio ratio, StringBuilder buf) {
		if (ratio != null && ratio.isInitialized()) {
			String className = "nowrap" + (failed ? " red" : "");
			buf.append("<td class='").append(className).append("'");
			buf.append(" data='").append(dataFormat.format(ratio.getPercentageFloat()));
			buf.append("'>\n");
			printRatioTable(ratio, buf);
			buf.append("</td>\n");
		}
	}
	
	protected static void printRatioTable(Ratio ratio, StringBuilder buf){
		String data = dataFormat.format(ratio.getPercentageFloat());
		String percent = percentFormat.format(ratio.getPercentageFloat());
		String numerator = intFormat.format(ratio.getNumerator());
		String denominator = intFormat.format(ratio.getDenominator());
		buf.append("<table class='percentgraph' cellpadding='0px' cellspacing='0px'><tr class='percentgraph'>")
				.append("<td width='64px' class='data'>").append(percent).append("%</td>")
				.append("<td class='percentgraph'>")
				.append("<div class='percentgraph'><div class='greenbar' style='width: ").append(ratio.getPercentageFloat()).append("px;'>")
				.append("<span class='text'>").append(numerator).append("/").append(denominator)
				.append("</span></div></div></td></tr></table>") ;
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
                    dsb.add(a.clazz.getPercentageFloat(), Messages.CoverageObject_Legend_Class(), label);
                    dsb.add(a.block.getPercentageFloat(), Messages.CoverageObject_Legend_Block(), label);
                    dsb.add(a.method.getPercentageFloat(), Messages.CoverageObject_Legend_Method(), label);
                    if (a.line != null) {
                        dsb.add(a.line.getPercentageFloat(), Messages.CoverageObject_Legend_Line(), label);
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
                    "%", // range axis label
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
            rangeAxis.setUpperBound(100);
            rangeAxis.setLowerBound(0);

            final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
            renderer.setBaseStroke(new BasicStroke(4.0f));
            ColorPalette.apply(renderer);

            // crop extra space around the graph
            plot.setInsets(new RectangleInsets(5.0, 0, 0, 5.0));

            return chart;
        }
    }
}
