package hudson.plugins.emma;

import hudson.model.AbstractBuild;
import hudson.model.HealthReport;
import hudson.model.HealthReportingAction;
import hudson.model.Result;
import hudson.util.IOException2;
import hudson.util.NullStream;
import hudson.util.StreamTaskListener;
import org.kohsuke.stapler.StaplerProxy;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Build view extension by Emma plugin.
 *
 * As {@link CoverageObject}, it retains the overall coverage report.
 *
 * @author Kohsuke Kawaguchi
 */
public final class EmmaBuildAction extends CoverageObject<EmmaBuildAction> implements HealthReportingAction, StaplerProxy {
    public final AbstractBuild owner;

    private transient WeakReference<CoverageReport> report;

    /**
     * Non-null if the coverage has pass/fail rules.
     */
    private final Rule rule;

    /**
     * The thresholds that applied when this build was built.
     * @TODO add ability to trend thresholds on the graph
     */
    private final EmmaHealthReportThresholds thresholds;

    public EmmaBuildAction(AbstractBuild owner, Rule rule, Ratio classCoverage, Ratio methodCoverage, Ratio blockCoverage, Ratio lineCoverage, EmmaHealthReportThresholds thresholds) {
        this.owner = owner;
        this.rule = rule;
        this.clazz = classCoverage;
        this.method = methodCoverage;
        this.block = blockCoverage;
        this.line = lineCoverage;
        this.thresholds = thresholds;
    }

    public String getDisplayName() {
        return "Coverage Report";
    }

    public String getIconFileName() {
        return "graph.gif";
    }

    public String getUrlName() {
        return "emma";
    }

    /**
     * Get the coverage {@link hudson.model.HealthReport}.
     *
     * @return The health report or <code>null</code> if health reporting is disabled.
     * @since 1.7
     */
    public HealthReport getBuildHealth() {
        if (thresholds == null) {
            // no thresholds => no report
            return null;
        }
        thresholds.ensureValid();
        int score = 100;
        StringBuilder description = new StringBuilder("Coverage: ");
        if (clazz != null && thresholds.getMaxClass() > 0) {
            score = updateHealthReport(score, "Classes",
                            thresholds.getMinClass(),
                            clazz,
                            thresholds.getMaxClass(),
                            description);
        }
        if (method != null && thresholds.getMaxMethod() > 0) {
            score = updateHealthReport(score, "Methods",
                            thresholds.getMinMethod(),
                            method,
                            thresholds.getMaxMethod(),
                            description);
        }
        if (block != null && thresholds.getMaxBlock() > 0) {
            score = updateHealthReport(score, "Blocks",
                            thresholds.getMinBlock(),
                            block,
                            thresholds.getMaxBlock(),
                            description);
        }
        if (line != null && thresholds.getMaxLine() > 0) {
            score = updateHealthReport(score, "Lines",
                            thresholds.getMinLine(),
                            line,
                            thresholds.getMaxLine(),
                            description);
        }
        if (score == 100) {
            description.append("All coverage targets have been met.");
        }
        return new HealthReport(score, description.toString());
    }

    private int updateHealthReport(int score, String name, int min, Ratio coverage, int max, StringBuilder title) {
        final int value = coverage.getPercentage();
        if (value < max) {
            title.append(name);
            title.append(' ');
            title.append(coverage);
            title.append(" (");
            title.append(value);
            title.append("%). ");
        }
        if (value >= max) return score;
        if (value <= min) return 0;
        assert max != min;
        final int scaled = (int) (100.0 * ((float) value - min) / (max - min));
        if (scaled < score) return scaled;
        return score;
    }

    public Object getTarget() {
        return getResult();
    }

    @Override
    public AbstractBuild getBuild() {
        return owner;
    }

    /**
     * Obtains the detailed {@link CoverageReport} instance.
     */
    public synchronized CoverageReport getResult() {
        if(report!=null) {
            CoverageReport r = report.get();
            if(r!=null)     return r;
        }

        File reportFile = EmmaPublisher.getEmmaReport(owner);
        try {
            CoverageReport r = new CoverageReport(this,reportFile);

            if(rule!=null) {
                // we change the report so that the FAILED flag is set correctly
                logger.info("calculating failed packages based on " + rule);
                rule.enforce(r,new StreamTaskListener(new NullStream()));
            }

            report = new WeakReference<CoverageReport>(r);
            return r;
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to load "+reportFile,e);
            return null;
        }
    }

    @Override
    public EmmaBuildAction getPreviousResult() {
        return getPreviousResult(owner);
    }

    /**
     * Gets the previous {@link EmmaBuildAction} of the given build.
     */
    /*package*/ static EmmaBuildAction getPreviousResult(AbstractBuild start) {
        AbstractBuild<?,?> b = start;
        while(true) {
            b = b.getPreviousBuild();
            if(b==null)
                return null;
            if(b.getResult()== Result.FAILURE)
                continue;
            EmmaBuildAction r = b.getAction(EmmaBuildAction.class);
            if(r!=null)
                return r;
        }
    }

    /**
     * Constructs the object from emma XML report file.
     * See <a href="http://emma.sourceforge.net/coverage_sample_c/coverage.xml">an example XML file</a>.
     *
     * @throws IOException
     *      if failed to parse the file.
     */
    public static EmmaBuildAction load(AbstractBuild owner, Rule rule, EmmaHealthReportThresholds thresholds, File f) throws IOException {
        FileInputStream in = new FileInputStream(f);
        try {
            return load(owner,rule,thresholds,in);
        } catch (XmlPullParserException e) {
            throw new IOException2("Failed to parse "+f,e);
        } finally {
            in.close();
        }
    }

    public static EmmaBuildAction load(AbstractBuild owner, Rule rule, EmmaHealthReportThresholds thresholds, InputStream in) throws IOException, XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();

        parser.setInput(in,null);
        while(true) {
            if(parser.nextTag()!=XmlPullParser.START_TAG)
                continue;
            if(!parser.getName().equals("coverage"))
                continue;
            break;
        }

        // head for the first <coverage> tag.
        Ratio[] r = new Ratio[4];
        for( int i=0; i<r.length; i++ ) {
            if(!parser.getName().equals("coverage"))
                break;  // line coverage is optional
            parser.require(XmlPullParser.START_TAG,"","coverage");
            r[i] = readCoverageTag(parser);
        }

        return new EmmaBuildAction(owner,rule,r[0],r[1],r[2],r[3],thresholds);
    }

    private static Ratio readCoverageTag(XmlPullParser parser) throws IOException, XmlPullParserException {
        String v = parser.getAttributeValue("", "value");
        Ratio r = Ratio.parseValue(v);

        // move to the next coverage tag.
        parser.nextTag();
        parser.nextTag();

        return r;
    }

    private static final Logger logger = Logger.getLogger(EmmaBuildAction.class.getName());
}
