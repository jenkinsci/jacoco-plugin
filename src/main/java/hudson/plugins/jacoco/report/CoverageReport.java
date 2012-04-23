package hudson.plugins.jacoco.report;

import hudson.model.AbstractBuild;
import hudson.plugins.jacoco.EmmaBuildAction;
import hudson.plugins.jacoco.model.CoverageElement;
import hudson.util.IOException2;
import org.apache.commons.digester.Digester;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Root object of the coverage report.
 * 
 * @author Kohsuke Kawaguchi
 */
public final class CoverageReport extends AggregatedReport<CoverageReport/*dummy*/,CoverageReport,PackageReport> {
    private final EmmaBuildAction action;

    private CoverageReport(EmmaBuildAction action) {
        this.action = action;
        setName("Jacoco");
    }

    public CoverageReport(EmmaBuildAction action, InputStream... xmlReports) throws IOException {
        this(action);
        for (InputStream is: xmlReports) {
          try {
            createDigester().parse(is);
          } catch (SAXException e) {
              throw new IOException2("Failed to parse XML",e);
          }
        }
        setParent(null);
    }

    public CoverageReport(EmmaBuildAction action, File xmlReport) throws IOException {
        this(action);
        try {
            createDigester().parse(xmlReport);
        } catch (SAXException e) {
            throw new IOException2("Failed to parse "+xmlReport,e);
        }
        setParent(null);
    }

    @Override
    public CoverageReport getPreviousResult() {
        EmmaBuildAction prev = action.getPreviousResult();
        if(prev!=null)
            return prev.getResult();
        else
            return null;
    }

    @Override
    public AbstractBuild<?,?> getBuild() {
        return action.owner;
    }

    /**
     * Creates a configured {@link Digester} instance for parsing report XML.
     */
    private Digester createDigester() {
        Digester digester = new Digester();
        digester.setClassLoader(getClass().getClassLoader());
        digester.register("-//JACOCO//DTD Report 1.0//EN", getClass().getResource("jacoco-report.dtd").toString());

        digester.push(this);

        // Create the list of Packages
        digester.addObjectCreate( "report/package", PackageReport.class);
        digester.addSetNext(      "report/package","add");
        digester.addSetProperties("report/package");
        
        
        // Now the classes
        digester.addObjectCreate( "report/package/class", ClassReport.class);
        digester.addSetNext(      "report/package/class","add");
        digester.addSetProperties("report/package/class");
        
        digester.addObjectCreate( "report/package/class/method", MethodReport.class);
        digester.addSetNext(      "report/package/class/method","add");
        digester.addSetProperties("report/package/class/method");
        
        // Create the list of Source Files next
//        digester.addObjectCreate( "report/package/sourcefile", SourceFileReport.class);
//        digester.addSetNext(      "report/package/sourcefile","add");
//        digester.addSetProperties("report/package/sourcefile");


        digester.addObjectCreate( "*/counter", CoverageElement.class);
        digester.addSetProperties("*/counter");
        digester.addSetNext(      "*/counter","addCoverage");

        return digester;
    }
}
