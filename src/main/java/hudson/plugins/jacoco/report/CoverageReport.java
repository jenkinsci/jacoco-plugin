package hudson.plugins.jacoco.report;

import hudson.model.AbstractBuild;
import hudson.plugins.jacoco.JacocoBuildAction;
import hudson.plugins.jacoco.model.Coverage;
import hudson.plugins.jacoco.model.CoverageElement;
import hudson.plugins.jacoco.model.ModuleInfo;
import hudson.util.IOException2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.digester.Digester;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.IMethodCoverage;
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
         /* try {
            //createDigester().parse(is);
          } catch (SAXException e) {
              throw new IOException2("Failed to parse XML",e);
          }*/
        }
        setParent(null);
    }
    
    public CoverageReport(JacocoBuildAction action, ArrayList<ModuleInfo> reports ) throws IOException {
        this(action);
        for (ModuleInfo is: reports) {
          try {
        	  if (is.getBundleCoverage() !=null ) {
        		  setCoverage(this,is.getBundleCoverage());
        		  ArrayList<IPackageCoverage> packageList = new ArrayList<IPackageCoverage>(is.getBundleCoverage().getPackages());
        		  for (IPackageCoverage packageCov: packageList) {
        			  PackageReport packageReport = new PackageReport();
        			  packageReport.setName(packageCov.getName());
        			  packageReport.setParent(this);
        			  setCoverage(packageReport, packageCov);
        			  ArrayList<IClassCoverage> classList = new ArrayList<IClassCoverage>(packageCov.getClasses());
        			  for (IClassCoverage classCov: classList) {
        				  ClassReport classReport = new ClassReport();
        				  classReport.setName(classCov.getName());
        				  classReport.setParent(packageReport);
            			  setCoverage(classReport, classCov);
            			  ArrayList<IMethodCoverage> methodList = new ArrayList<IMethodCoverage>(classCov.getMethods());
            			  for (IMethodCoverage methodCov: methodList) {
            				  MethodReport methodReport = new MethodReport();
            				  methodReport.setName(methodCov.getName());
            				  methodReport.setParent(classReport);
                			  setCoverage(methodReport, methodCov);
                			  classReport.add(methodReport);
            			  }
            			  packageReport.add(classReport);
        			  }
        			  this.add(packageReport);
        		  }
        	  }
        	  
          } catch (Exception e) {
              e.printStackTrace();
        	  //throw new Exception("Failed to parse XML",e);
          }
        }
        setParent(null);
    }
    private  < ReportType extends AggregatedReport > void setCoverage( ReportType reportToSet, ICoverageNode covReport) {
    	  Coverage tempCov = new Coverage();
		  tempCov.accumulate(covReport.getClassCounter().getMissedCount(), covReport.getClassCounter().getCoveredCount());
		  reportToSet.clazz = tempCov;
		  
		  //throw new RuntimeException(((Integer)covReport.getClassCounter().getMissedCount()).toString());
		  tempCov = new Coverage();
		  tempCov.accumulate(covReport.getBranchCounter().getMissedCount(), 20);
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

    
}
