package hudson.plugins.emma;

import hudson.model.Action;
import hudson.model.Build;
import hudson.util.IOException2;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Kohsuke Kawaguchi
 */
public class EmmaBuildAction implements Action {
    /*package*/ Build owner;

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

    public EmmaBuildAction(Ratio classCoverage, Ratio methodCoverage, Ratio blockCoverage, Ratio lineCoverage) {
        this.classCoverage = classCoverage;
        this.methodCoverage = methodCoverage;
        this.blockCoverage = blockCoverage;
        this.lineCoverage = lineCoverage;
    }

    /**
     * Constructs the object from emma XML report file.
     * See <a href="http://emma.sourceforge.net/coverage_sample_c/coverage.xml">an example XML file</a>.
     *
     * @throws IOException
     *      if failed to parse the file.
     */
    public static EmmaBuildAction load(File f) throws IOException {
        FileInputStream in = new FileInputStream(f);
        try {
            return load(in);
        } catch (XmlPullParserException e) {
            throw new IOException2("Failed to parse "+f,e);
        } finally {
            in.close();
        }
    }
    
    public static EmmaBuildAction load(InputStream in) throws IOException, XmlPullParserException {
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

        return new EmmaBuildAction(r[0],r[1],r[2],r[3]);
    }

    private static Ratio readCoverageTag(XmlPullParser parser) throws IOException, XmlPullParserException {
        String v = parser.getAttributeValue("", "value");
        Ratio r = Ratio.parseValue(v);

        // move to the next coverage tag.
        parser.nextTag();
        parser.nextTag();

        return r;
    }

    public Build getOwner() {
        return owner;
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
}
