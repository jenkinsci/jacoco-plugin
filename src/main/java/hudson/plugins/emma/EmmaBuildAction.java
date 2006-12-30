package hudson.plugins.emma;

import hudson.model.Action;
import hudson.model.Build;
import hudson.model.Result;
import hudson.util.IOException2;
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
 * @author Kohsuke Kawaguchi
 */
public final class EmmaBuildAction implements Action, StaplerProxy {
    public final Build owner;

    /**
     * Total class coverage.
     */
    public final Ratio classCoverage;

    /**
     * Total method coverage.
     */
    public final Ratio methodCoverage;

    /**
     * Total block coverage.
     */
    public final Ratio blockCoverage;

    /**
     * Total line coverage.
     */
    public final Ratio lineCoverage;

    private transient WeakReference<CoverageReport> report;

    public EmmaBuildAction(Build owner, Ratio classCoverage, Ratio methodCoverage, Ratio blockCoverage, Ratio lineCoverage) {
        this.owner = owner;
        this.classCoverage = classCoverage;
        this.methodCoverage = methodCoverage;
        this.blockCoverage = blockCoverage;
        this.lineCoverage = lineCoverage;
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

    public Object getTarget() {
        return getResult();
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
            CoverageReport r = new CoverageReport(reportFile);
            report = new WeakReference<CoverageReport>(r);
            return r;
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to load "+reportFile,e);
            return null;
        }
    }

    /*package*/ EmmaBuildAction getPreviousResult() {
        return getPreviousResult(owner);
    }

    /**
     * Gets the previous {@link EmmaBuildAction} of the given build.
     */
    /*package*/ static EmmaBuildAction getPreviousResult(Build start) {
        Build b = start;
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
    public static EmmaBuildAction load(Build owner, File f) throws IOException {
        FileInputStream in = new FileInputStream(f);
        try {
            return load(owner,in);
        } catch (XmlPullParserException e) {
            throw new IOException2("Failed to parse "+f,e);
        } finally {
            in.close();
        }
    }

    public static EmmaBuildAction load(Build owner, InputStream in) throws IOException, XmlPullParserException {
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
            parser.require(XmlPullParser.START_TAG,"","coverage");
            r[i] = readCoverageTag(parser);
        }

        return new EmmaBuildAction(owner,r[0],r[1],r[2],r[3]);
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
