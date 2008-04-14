package hudson.plugins.emma;

import hudson.model.AbstractBuild;
import hudson.util.ChartUtil;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;
import hudson.util.ColorPalette;
import hudson.util.DataSetBuilder;
import hudson.util.ShiftedCategoryAxis;
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

import java.awt.*;
import java.io.IOException;
import java.util.Calendar;

/**
 * Base class of all coverage objects.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class CoverageObject<SELF extends CoverageObject<SELF>> {
    /*package*/ Ratio clazz,method,block,line;
    
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

    public Ratio getClassCoverage() {
        return clazz;
    }

    public Ratio getMethodCoverage() {
        return method;
    }

    public Ratio getBlockCoverage() {
        return block;
    }

    /**
     * Line converage. Can be null if this information is not collected.
     */
    public Ratio getLineCoverage() {
        return line;
    }

    /**
     * Gets the build object that owns the whole coverage report tree.
     */
    public abstract AbstractBuild getBuild();

    /**
     * Gets the corresponding coverage report object in the previous
     * run that has the record.
     *
     * @return
     *      null if no earlier record was found.
     */
    public abstract SELF getPreviousResult();

    /**
     * Used in the view to print out four table columns with the coverage info.
     */
    public String printFourCoverageColumns() {
        StringBuilder buf = new StringBuilder();
        if(clazz==null)
            buf.append("<td></td>");
        else
            printColumn(clazz,buf);
        printColumn(method,buf);
        printColumn(block,buf);
        printColumn(line,buf);
        return buf.toString();
    }

    public boolean hasLineCoverage() {
        return line!=null;
    }

    private void printColumn(Ratio ratio, StringBuilder buf) {
        if(ratio==null)     return; // not recorded

       if (isFailed())
          buf.append("<td bgcolor=red");
       else
          buf.append("<td");

        buf.append(" data='>").append(ratio.getPercentageFloat()).append("'>");

        String p = String.valueOf(ratio.getPercentage());
        for(int i=p.length();i<3;i++)
            buf.append("&nbsp;");    // padding
        buf.append(p).append("% (").append(ratio.toString()).append(')');

        buf.append("</td>");
    }

    /**
     * Generates the graph that shows the coverage trend up to this report.
     */
    public void doGraph(StaplerRequest req, StaplerResponse rsp) throws IOException {
        if(ChartUtil.awtProblem) {
            // not available. send out error message
            rsp.sendRedirect2(req.getContextPath()+"/images/headless.png");
            return;
        }

        AbstractBuild build = getBuild();
        Calendar t = build.getTimestamp();

        if(req.checkIfModified(t,rsp))
            return; // up to date

        DataSetBuilder<String,NumberOnlyBuildLabel> dsb = new DataSetBuilder<String,NumberOnlyBuildLabel>();

        for( CoverageObject<SELF> a=this; a!=null; a=a.getPreviousResult() ) {
            NumberOnlyBuildLabel label = new NumberOnlyBuildLabel(a.getBuild());
            dsb.add( a.clazz.getPercentageFloat(), "class", label);
            dsb.add( a.block.getPercentageFloat(), "block", label);
            dsb.add( a.method.getPercentageFloat(), "method", label);
            if(a.line!=null)
                dsb.add( a.line.getPercentageFloat(), "line", label);
        }

        ChartUtil.generateGraph(req,rsp,createChart(dsb.build()),400,200);
    }

    private JFreeChart createChart(CategoryDataset dataset) {

        final JFreeChart chart = ChartFactory.createLineChart(
            null,                   // chart title
            null,                   // unused
            "%",                    // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            true,                     // include legend
            true,                     // tooltips
            false                     // urls
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
        renderer.setStroke(new BasicStroke(4.0f));
        ColorPalette.apply(renderer);

        // crop extra space around the graph
        plot.setInsets(new RectangleInsets(5.0,0,0,5.0));

        return chart;
    }
}
