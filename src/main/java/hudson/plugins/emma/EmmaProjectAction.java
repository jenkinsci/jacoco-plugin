package hudson.plugins.emma;

import hudson.model.Action;
import hudson.model.Build;
import hudson.model.Project;
import hudson.util.ChartUtil;
import hudson.util.DataSetBuilder;
import hudson.util.ShiftedCategoryAxis;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RectangleEdge;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.awt.Color;
import java.awt.BasicStroke;
import java.io.IOException;
import java.util.Calendar;

/**
 * Project view extension by Emma plugin.
 * 
 * @author Kohsuke Kawaguchi
 */
public final class EmmaProjectAction implements Action {
    public final Project project;

    public EmmaProjectAction(Project project) {
        this.project = project;
    }

    public String getIconFileName() {
        return "graph.gif";
    }

    public String getDisplayName() {
        return "Coverage Trend";
    }

    public String getUrlName() {
        return "emma";
    }

    public void doGraph(StaplerRequest req, StaplerResponse rsp) throws IOException {
        if(ChartUtil.awtProblem) {
            // not available. send out error message
            rsp.sendRedirect2(req.getContextPath()+"/images/headless.png");
            return;
        }

        Build last = project.getLastBuild();
        Calendar t = last.getTimestamp();

        if(req.checkIfModified(t,rsp))
            return; // up to date

        DataSetBuilder<String,NumberOnlyBuildLabel> dsb = new DataSetBuilder<String,NumberOnlyBuildLabel>();

        for( EmmaBuildAction a=EmmaBuildAction.getPreviousResult(last); a!=null; a=a.getPreviousResult() ) {
            NumberOnlyBuildLabel label = new NumberOnlyBuildLabel(a.owner);
            dsb.add( a.classCoverage.getPercentageFloat(), "class", label);
            dsb.add( a.blockCoverage.getPercentageFloat(), "block", label);
            dsb.add( a.methodCoverage.getPercentageFloat(), "method", label);
            dsb.add( a.lineCoverage.getPercentageFloat(), "line", label);
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

        // crop extra space around the graph
        plot.setInsets(new RectangleInsets(5.0,0,0,5.0));

        return chart;
    }
}
