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
 * @author Ognjen Bubalo
 */
public final class CoverageReport extends AggregatedReport<CoverageReport/*dummy*/,CoverageReport,ModuleReport> {
    private final JacocoBuildAction action;

    private CoverageReport(JacocoBuildAction action) {
        this.action = action;
        setName("Jacoco");
    }

    public CoverageReport(JacocoBuildAction action, InputStream... xmlReports) throws IOException {
    	this(action);
    }
    
    public CoverageReport(JacocoBuildAction action, ArrayList<ModuleInfo> reports ) throws IOException {
    	this(action);
    	try {
	        for (ModuleInfo moduleInfo: reports) {
	        	Coverage tempCov = new Coverage();
	            tempCov.accumulatePP(moduleInfo.getBundleCoverage().getBranchCounter().getMissedCount(), moduleInfo.getBundleCoverage().getBranchCounter().getCoveredCount());
	            this.branch.accumulatePP(tempCov.getMissed(), tempCov.getCovered());
	            tempCov = new Coverage();
	            tempCov.accumulatePP(moduleInfo.getBundleCoverage().getLineCounter().getMissedCount(), moduleInfo.getBundleCoverage().getLineCounter().getCoveredCount());
	        	this.line.accumulatePP(tempCov.getMissed(), tempCov.getCovered());
	        	tempCov = new Coverage();
	        	tempCov.accumulatePP(moduleInfo.getBundleCoverage().getComplexityCounter().getMissedCount(), moduleInfo.getBundleCoverage().getComplexityCounter().getCoveredCount());
	        	this.complexity.accumulatePP(tempCov.getMissed(), tempCov.getCovered());
	        	tempCov = new Coverage();
	        	tempCov.accumulatePP(moduleInfo.getBundleCoverage().getClassCounter().getMissedCount(), moduleInfo.getBundleCoverage().getClassCounter().getCoveredCount());
	        	this.clazz.accumulatePP(tempCov.getMissed(), tempCov.getCovered());
	        	tempCov = new Coverage();
	        	tempCov.accumulatePP(moduleInfo.getBundleCoverage().getInstructionCounter().getMissedCount(), moduleInfo.getBundleCoverage().getInstructionCounter().getCoveredCount());
	        	this.instruction.accumulatePP(tempCov.getMissed(), tempCov.getCovered());
	        	tempCov = new Coverage();
	        	tempCov.accumulatePP(moduleInfo.getBundleCoverage().getMethodCounter().getMissedCount(), moduleInfo.getBundleCoverage().getMethodCounter().getCoveredCount());
	        	this.method.accumulatePP(tempCov.getMissed(), tempCov.getCovered());
	        }
	        
	        this.maxBranch = this.branch.getCovered();
	        this.maxLine = this.line.getCovered();
	        this.maxClazz = this.line.getCovered();
	        this.maxComplexity = this.complexity.getCovered();
	        this.maxInstruction = this.instruction.getCovered();
	        this.maxMethod = this.method.getCovered();
	        
	        
	        
	        ArrayList<IBundleCoverage> moduleList = new ArrayList<IBundleCoverage>();
			ArrayList<ModuleReport> moduleReportList = new ArrayList<ModuleReport>();
			int i=0;
	        for (ModuleInfo moduleInfo: reports) {
	          
	        	  ModuleReport moduleReport = new ModuleReport();
	        	  action.logger.println("[JaCoCo plugin] Loading module: " + moduleInfo.getName());
	        	  moduleReport.setName(moduleInfo.getName());
	        	  moduleReport.setParent(this);
	        	  if (moduleInfo.getBundleCoverage() !=null ) {
	        		  moduleList.add(moduleInfo.getBundleCoverage());
	        		  setCoverage(moduleReport, moduleInfo.getBundleCoverage());
	        		  
	        		  
	        		  ArrayList<IPackageCoverage> packageList = new ArrayList<IPackageCoverage>(moduleInfo.getBundleCoverage().getPackages());
	        		  ArrayList<PackageReport> packageReportList = new ArrayList<PackageReport>();
	        		  for (IPackageCoverage packageCov: packageList) {
	        			  PackageReport packageReport = new PackageReport();
	        			  packageReport.setName(packageCov.getName());
	        			  packageReport.setParent(moduleReport);
	        			  setCoverage(packageReport, packageCov);
	        			  
	        			  
	        			  ArrayList<IClassCoverage> classList = new ArrayList<IClassCoverage>(packageCov.getClasses());
	        			  ArrayList<ClassReport> classReportList = new ArrayList<ClassReport>();
	        			  for (IClassCoverage classCov: classList) {
	        				  ClassReport classReport = new ClassReport();
	        				  classReport.setName(classCov.getName());
	        				  classReport.setParent(packageReport);
	            			  setCoverage(classReport, classCov);
	            			  
	            			  //SourceFileReport sourceFileReport = new SourceFileReport();
	            			  //sourceFileReport.setName(classReport.getName());
	            			 
	            			  ArrayList<IMethodCoverage> methodList = new ArrayList<IMethodCoverage>(classCov.getMethods());
	            			  ArrayList<MethodReport> methodReportList = new ArrayList<MethodReport>();
	            			  for (IMethodCoverage methodCov: methodList) {
	            				  MethodReport methodReport = new MethodReport();
	            				  methodReport.setName(methodCov.getName());
	            				  methodReport.setParent(classReport);
	            				  methodReport.setCoverage(methodCov);
	            				  methodReport.setSrcFileInfo(methodCov, moduleInfo.getSrcDir()+ "/" + packageCov.getName() + "/"+ classCov.getSourceFileName());
	            				  //methodReport.add(sourceFileReport);
	            				  //methodReport.readFile(moduleInfo.getClassDir().getRemote()+"/html/Agave.java.html");
	            				  //sourceFileReport.setParent(methodReport);
	            				  
	            				
	                			  classReport.add(methodReport);
	                			  methodReportList.add(methodReport);
	            			  }
	            			  classReport.reSetMaximums(methodReportList,methodList);
	            			  
	            			  
	            			  packageReport.add(classReport);
	            			  classReportList.add(classReport);
	        			  }
	        			  packageReport.reSetMaximums(classReportList,classList);
	        			  
	        			  
	        			  moduleReport.add(packageReport);
	        			  packageReportList.add(packageReport);
	        		  }
	        		  moduleReport.reSetMaximums(packageReportList,packageList);
	        	  }
	        	  
	        	  moduleReportList.add(moduleReport);
	        	  this.add(moduleReport);
	         
	        }
	        reSetMaximums(moduleReportList, moduleList);
	        setParent(null);
    	} catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    private void reSetMaximums(ArrayList<ModuleReport> reportList, ArrayList<IBundleCoverage> coverageList) {

    	for (IBundleCoverage coverageCov : coverageList) {
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
    	for (ModuleReport report:  reportList) {
   		 report.maxClazz = maxClazz;
   		 report.maxBranch = maxBranch;
   		 report.maxMethod = maxMethod;
   		 report.maxLine = maxLine;
   		 report.maxComplexity = maxComplexity;
   		 report.maxInstruction = maxInstruction;
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
