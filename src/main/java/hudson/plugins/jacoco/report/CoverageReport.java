package hudson.plugins.jacoco.report;

import hudson.model.AbstractBuild;
import hudson.plugins.jacoco.JacocoBuildAction;
import hudson.plugins.jacoco.model.CoverageElement;
import hudson.plugins.jacoco.model.ModuleInfo;
import hudson.util.IOException2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.digester.Digester;
import org.jacoco.core.analysis.IPackageCoverage;
import org.xml.sax.SAXException;

/**
 * Root object of the coverage report.
 * 
 * @author Kohsuke Kawaguchi
 */
public final class CoverageReport extends AggregatedReport<CoverageReport/*dummy*/,CoverageReport,PackageReport> {
    private final JacocoBuildAction action;

    private CoverageReport(JacocoBuildAction action) {
    	
        this.action = action;
        setName("Jacoco");
    }

    public CoverageReport(JacocoBuildAction action, InputStream... xmlReports) throws IOException {
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
    
    public CoverageReport(JacocoBuildAction action, ArrayList<ModuleInfo> reports ) throws IOException {
        this(action);
        for (ModuleInfo is: reports) {
          try {
            //createDigester().parse(is);
        	  if (is.getBundleCoverage() !=null ) {
        		  ArrayList<IPackageCoverage> packageList = new ArrayList<IPackageCoverage>(is.getBundleCoverage().getPackages());
        		  for (IPackageCoverage packageCov: packageList) {
        			  PackageReport packageReport = new PackageReport();
        			  packageReport.setName(packageCov.getName());
        			  
        		  }
        	  }
        	  
          } catch (Exception e) {
              e.printStackTrace();
        	  //throw new Exception("Failed to parse XML",e);
          }
        }
        setParent(null);
    }

    public CoverageReport(JacocoBuildAction action, File xmlReport) throws IOException {
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
        JacocoBuildAction prev = action.getPreviousResult();
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
        CoverageReport[] a = new CoverageReport[1];
        a[0] = this;
        digester.addCallMethod("report/package", "setParent", 1);
 
        digester.addSetNext(      "report/package","add");
        digester.addSetProperties("report/package");
        
        //This wont work because object is created during the parsing.
        //PackageReport a = digester.peek();
        //a.setParent(this);
        
        // Now the classes
        digester.addObjectCreate( "report/package/class", ClassReport.class);
        digester.addSetNext(      "report/package/class","add");
        digester.addSetProperties("report/package/class");
        
        digester.addObjectCreate( "report/package/class/method", MethodReport.class);
        digester.addSetNext(      "report/package/class/method","add");
        digester.addSetProperties("report/package/class/method");
        
    //    Create the list of Source Files next
        //digester.addObjectCreate( "report/package/sourcefile", SourceFileReport.class);
       // digester.addSetNext(      "report/package/sourcefile","add");
        //digester.addSetProperties("report/package/sourcefile");


        digester.addObjectCreate( "*/counter", CoverageElement.class);
        digester.addSetProperties("*/counter");
        digester.addSetNext(      "*/counter","addCoverage");

        return digester;
    }
}
