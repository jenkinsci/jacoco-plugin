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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

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
    
    /**
     * Loads the exec files using JaCoCo API. Creates the reporting objects and the report tree.
     * 
     * @param action
     * @param reports
     * @throws IOException
     */
    public CoverageReport(JacocoBuildAction action, ArrayList<ModuleInfo> reports ) throws IOException {
    	this(action);
    	try {
	        for (ModuleInfo moduleInfo: reports) {
	        	Coverage tempCov = new Coverage();
	            tempCov.accumulatePP(moduleInfo.getBundleCoverage().getBranchCounter().getMissedCount(), moduleInfo.getBundleCoverage().getBranchCounter().getCoveredCount());
	            this.branch.accumulatePP(tempCov.getMissed(), tempCov.getCovered());
	            if (maxBranch < moduleInfo.getBundleCoverage().getBranchCounter().getCoveredCount()) {
	    			maxBranch = moduleInfo.getBundleCoverage().getBranchCounter().getCoveredCount();
	    		}
	            tempCov = new Coverage();
	            tempCov.accumulatePP(moduleInfo.getBundleCoverage().getLineCounter().getMissedCount(), moduleInfo.getBundleCoverage().getLineCounter().getCoveredCount());
	        	this.line.accumulatePP(tempCov.getMissed(), tempCov.getCovered());
	        	if (maxLine <  moduleInfo.getBundleCoverage().getLineCounter().getCoveredCount()) {
	    			maxLine =  moduleInfo.getBundleCoverage().getLineCounter().getCoveredCount();
	    		}
	        	tempCov = new Coverage();
	        	tempCov.accumulatePP(moduleInfo.getBundleCoverage().getComplexityCounter().getMissedCount(), moduleInfo.getBundleCoverage().getComplexityCounter().getCoveredCount());
	        	this.complexity.accumulatePP(tempCov.getMissed(), tempCov.getCovered());
	        	if (maxComplexity < moduleInfo.getBundleCoverage().getComplexityCounter().getCoveredCount()) {
	    			maxComplexity = moduleInfo.getBundleCoverage().getComplexityCounter().getCoveredCount();
	    		}
	        	tempCov = new Coverage();
	        	tempCov.accumulatePP(moduleInfo.getBundleCoverage().getClassCounter().getMissedCount(), moduleInfo.getBundleCoverage().getClassCounter().getCoveredCount());
	        	this.clazz.accumulatePP(tempCov.getMissed(), tempCov.getCovered());
	        	if (maxClazz < moduleInfo.getBundleCoverage().getClassCounter().getCoveredCount()) {
	    			maxClazz = moduleInfo.getBundleCoverage().getClassCounter().getCoveredCount();
	    		}
	        	tempCov = new Coverage();
	        	tempCov.accumulatePP(moduleInfo.getBundleCoverage().getInstructionCounter().getMissedCount(), moduleInfo.getBundleCoverage().getInstructionCounter().getCoveredCount());
	        	this.instruction.accumulatePP(tempCov.getMissed(), tempCov.getCovered());
	        	if (maxInstruction < moduleInfo.getBundleCoverage().getInstructionCounter().getCoveredCount()) {
	    			maxInstruction = moduleInfo.getBundleCoverage().getInstructionCounter().getCoveredCount();
	    		}
	        	tempCov = new Coverage();
	        	tempCov.accumulatePP(moduleInfo.getBundleCoverage().getMethodCounter().getMissedCount(), moduleInfo.getBundleCoverage().getMethodCounter().getCoveredCount());
	        	this.method.accumulatePP(tempCov.getMissed(), tempCov.getCovered());
	        	if (maxMethod < moduleInfo.getBundleCoverage().getMethodCounter().getCoveredCount()) {
	    			maxMethod = moduleInfo.getBundleCoverage().getMethodCounter().getCoveredCount();
	    		}
	        }
	        
	        
	        ArrayList<IBundleCoverage> moduleList = new ArrayList<IBundleCoverage>();
			//ArrayList<CoverageObject> moduleReportList = new ArrayList<CoverageObject>();
			int i=0;
	        for (ModuleInfo moduleInfo: reports) {
	          
	        	  ModuleReport moduleReport = new ModuleReport();
	        	  action.logger.println("[JaCoCo plugin] Loading module: " + moduleInfo.getName());
	        	  moduleReport.setName(moduleInfo.getName());
	        	  moduleReport.setParent(this);
	        	  if (moduleInfo.getBundleCoverage() !=null ) {
	        		  moduleList.add(moduleInfo.getBundleCoverage());
	        		  this.setCoverage(moduleReport, moduleInfo.getBundleCoverage());
	        		  
	        		  
	        		  ArrayList<IPackageCoverage> packageList = new ArrayList<IPackageCoverage>(moduleInfo.getBundleCoverage().getPackages());
	        		  for (IPackageCoverage packageCov: packageList) {
	        			  PackageReport packageReport = new PackageReport();
	        			  packageReport.setName(packageCov.getName());
	        			  packageReport.setParent(moduleReport);
	        			  moduleReport.setCoverage(packageReport, packageCov);
	        			  
	        			  
	        			  ArrayList<IClassCoverage> classList = new ArrayList<IClassCoverage>(packageCov.getClasses());
	        			  for (IClassCoverage classCov: classList) {
	        				  ClassReport classReport = new ClassReport();
	        				  classReport.setName(classCov.getName());
	        				  classReport.setParent(packageReport);
	            			  packageReport.setCoverage(classReport, classCov);
	            			  
	            			 
	            			  ArrayList<IMethodCoverage> methodList = new ArrayList<IMethodCoverage>(classCov.getMethods());
	            			  for (IMethodCoverage methodCov: methodList) {
	            				  MethodReport methodReport = new MethodReport();
	            				  methodReport.setName(methodCov.getName());
	            				  methodReport.setParent(classReport);
	            				  classReport.setCoverage(methodReport, methodCov);
	            				  methodReport.setSrcFileInfo(methodCov, moduleInfo.getSrcDir()+ "/" + packageCov.getName() + "/"+ classCov.getSourceFileName());
	            				
	                			  classReport.add(methodReport);
	            			  }
	            			  
	            			  
	            			  packageReport.add(classReport);
	        			  }
	        			  
	        			  
	        			  moduleReport.add(packageReport);
	        		  }
	        	  }
	        	  this.add(moduleReport);
	         
	        }
	        setParent(null);
    	} catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public  < ReportType extends AggregatedReport > void setCoverage( ReportType reportToSet, ICoverageNode covReport) {
		Coverage tempCov = new Coverage();
		tempCov.accumulatePP(covReport.getClassCounter().getMissedCount(), covReport.getClassCounter().getCoveredCount());
		reportToSet.clazz = tempCov;
		if (this.maxClazz < tempCov.getCovered()) {
			this.maxClazz = tempCov.getCovered();
		}

		tempCov = new Coverage();
		tempCov.accumulatePP(covReport.getBranchCounter().getMissedCount(), covReport.getBranchCounter().getCoveredCount());
		reportToSet.branch = tempCov;
		if (this.maxBranch < tempCov.getCovered()) {
			this.maxBranch = tempCov.getCovered();
		}

		tempCov = new Coverage();
		tempCov.accumulatePP(covReport.getLineCounter().getMissedCount(), covReport.getLineCounter().getCoveredCount());
		reportToSet.line = tempCov;
		if (this.maxLine < tempCov.getCovered()) {
			this.maxLine = tempCov.getCovered();
		}
		
		tempCov = new Coverage();
		tempCov.accumulatePP(covReport.getInstructionCounter().getMissedCount(), covReport.getInstructionCounter().getCoveredCount());
		reportToSet.instruction = tempCov;
		if (this.maxInstruction < tempCov.getCovered()) {
			this.maxInstruction = tempCov.getCovered();
		}

		tempCov = new Coverage();
		tempCov.accumulatePP(covReport.getMethodCounter().getMissedCount(), covReport.getMethodCounter().getCoveredCount());
		reportToSet.method = tempCov;
		if (this.maxMethod < tempCov.getCovered()) {
			this.maxMethod = tempCov.getCovered();
		}

		tempCov = new Coverage();
		tempCov.accumulatePP(covReport.getComplexityCounter().getMissedCount(), covReport.getComplexityCounter().getCoveredCount());
		reportToSet.complexity = tempCov;
		if (this.maxComplexity < tempCov.getCovered()) {
			this.maxComplexity = tempCov.getCovered();
		}

	}
    
    static NumberFormat dataFormat = new DecimalFormat("000.00", new DecimalFormatSymbols(Locale.US));
	static NumberFormat percentFormat = new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.US));
	static NumberFormat intFormat = new DecimalFormat("0", new DecimalFormatSymbols(Locale.US));
    @Override
    protected void printRatioTable(Coverage ratio, StringBuilder buf){
		String percent = percentFormat.format(ratio.getPercentageFloat());
		String numerator = intFormat.format(ratio.getMissed());
		String denominator = intFormat.format(ratio.getCovered());
		
		buf.append("<table class='percentgraph' cellpadding='0px' cellspacing='0px'><tr class='percentgraph'>")
		.append("<td width='64px' class='data'>").append(ratio.getPercentage()).append("%</td>")
		.append("<td class='percentgraph'>")
		.append("<div class='percentgraph' style='width: ").append(100).append("px;'>").append("<div class='redbar' style='width: ").append(0 == ratio.getCovered() ? 100 :  ((float)ratio.getMissed()/(float)ratio.getCovered())*100).append("px;'>")
		.append("<span class='text'>").append("M:"+numerator).append(" ").append("C: "+ denominator)
		.append("</span></div></div></td></tr></table>") ;
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
