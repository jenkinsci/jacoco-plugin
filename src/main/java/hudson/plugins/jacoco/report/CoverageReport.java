package hudson.plugins.jacoco.report;

import hudson.model.AbstractBuild;
import hudson.plugins.jacoco.JacocoBuildAction;
import hudson.plugins.jacoco.model.Coverage;
import hudson.plugins.jacoco.model.CoverageElement;
import hudson.plugins.jacoco.model.CoverageObject;
import hudson.plugins.jacoco.model.ModuleInfo;
import hudson.util.IOException2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.IMethodCoverage;
import org.jacoco.core.analysis.IPackageCoverage;

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
        		  ArrayList<PackageReport> packageReportList = new ArrayList<PackageReport>();
        		  for (IPackageCoverage packageCov: packageList) {
        			  PackageReport packageReport = new PackageReport();
        			  packageReport.setName(packageCov.getName());
        			  packageReport.setParent(this);
        			  setCoverage(packageReport, packageCov);
        			  ArrayList<IClassCoverage> classList = new ArrayList<IClassCoverage>(packageCov.getClasses());
        			  ArrayList<ClassReport> classReportList = new ArrayList<ClassReport>();
        			  for (IClassCoverage classCov: classList) {
        				  ClassReport classReport = new ClassReport();
        				  classReport.setName(classCov.getName());
        				  classReport.setParent(packageReport);
            			  setCoverage(classReport, classCov);
            			  ArrayList<IMethodCoverage> methodList = new ArrayList<IMethodCoverage>(classCov.getMethods());
            			  ArrayList<MethodReport> methodReportList = new ArrayList<MethodReport>();
            			  for (IMethodCoverage methodCov: methodList) {
            				  MethodReport methodReport = new MethodReport();
            				  methodReport.setName(methodCov.getName());
            				  methodReport.setParent(classReport);
            				  methodReport.setCoverage(methodCov);
                			  //methodReport.sourceFilePath = action.getBuild().getRootDir().getPath()+"/jacoco/index.html";
                			  classReport.add(methodReport);
                			  methodReportList.add(methodReport);
            			  }
            			  classReport.reSetMaximumsMethod(methodReportList,methodList);
            			  packageReport.add(classReport);
            			  classReportList.add(classReport);
        			  }
        			  packageReport.reSetMaximumsClass(classReportList,classList);
        			  this.add(packageReport);
        			  packageReportList.add(packageReport);
        		  }
        		  reSetMaximumsPackage(packageReportList,packageList);
        	  }
        	  reSetMaximumsBundle(is.getBundleCoverage());
        	  
          } catch (Exception e) {
              e.printStackTrace();
          }
        }
        setParent(null);
    }
    
    
    private void reSetMaximumsBundle(IBundleCoverage coverageCov) {
         if (maxMethod < coverageCov.getMethodCounter().getCoveredCount()) {
			 maxMethod = coverageCov.getMethodCounter().getCoveredCount();
		 }
		 if (maxLine < coverageCov.getLineCounter().getCoveredCount()) {
			 maxLine = coverageCov.getLineCounter().getCoveredCount();
		 }
		 if (maxComplexity < coverageCov.getComplexityCounter().getCoveredCount()) {
			 maxComplexity = coverageCov.getComplexityCounter().getCoveredCount();
		 }
		 if (maxInstruction < coverageCov.getInstructionCounter().getCoveredCount()) {
			 maxInstruction = coverageCov.getInstructionCounter().getCoveredCount();
		 }
		 if (maxBranch < coverageCov.getBranchCounter().getCoveredCount()) {
			 maxBranch = coverageCov.getBranchCounter().getCoveredCount();
		 }
		 if (maxClazz < coverageCov.getClassCounter().getCoveredCount()) {
			 maxClazz = coverageCov.getClassCounter().getCoveredCount();
		 }
	}

	private  void reSetMaximumsPackage(ArrayList<PackageReport> reportList,
    		ArrayList<IPackageCoverage> coverageList) {
    	 int maxClazz = 1;
    	 int maxMethod=1;
    	 int maxLine=1;
    	 int maxComplexity=1;
    	 int maxInstruction=1;
    	 int maxBranch=1;
    	 
    	 for (ICoverageNode coverageCov: coverageList) {
    		 
    		 if (maxMethod < coverageCov.getMethodCounter().getCoveredCount()) {
    			 maxMethod = coverageCov.getMethodCounter().getCoveredCount();
    		 }
    		 if (maxLine < coverageCov.getLineCounter().getCoveredCount()) {
    			 maxLine = coverageCov.getLineCounter().getCoveredCount();
    		 }
    		 if (maxComplexity < coverageCov.getComplexityCounter().getCoveredCount()) {
    			 maxComplexity = coverageCov.getComplexityCounter().getCoveredCount();
    		 }
    		 if (maxInstruction < coverageCov.getInstructionCounter().getCoveredCount()) {
    			 maxInstruction = coverageCov.getInstructionCounter().getCoveredCount();
    		 }
    		 if (maxBranch < coverageCov.getBranchCounter().getCoveredCount()) {
    			 maxBranch = coverageCov.getBranchCounter().getCoveredCount();
    		 }
    		 if (maxClazz < coverageCov.getClassCounter().getCoveredCount()) {
    			 maxClazz = coverageCov.getClassCounter().getCoveredCount();
    		 }
    	 }
    	 for (PackageReport report:  reportList) {
    		 report.setMaxClazz(maxClazz);
    		 report.setMaxBranch(maxBranch);
    		 report.setMaxMethod(maxMethod);
    		 report.setMaxLine(maxLine);
    		 report.setMaxComplexity(maxComplexity);
    		 report.setMaxInstruction(maxInstruction);
    	 }
    }
    

	private  < ReportType extends AggregatedReport > void setCoverage( ReportType reportToSet, ICoverageNode covReport) {
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
