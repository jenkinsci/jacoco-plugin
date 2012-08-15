package hudson.plugins.jacoco.report;

import hudson.plugins.jacoco.model.CoverageObject;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.IMethodCoverage;

/**
 * @author Kohsuke Kawaguchi
 */
public final class ClassReport extends AggregatedReport<PackageReport,ClassReport,MethodReport> {

	public String buildURL;
	@Override
	public void setName(String name) {
		super.setName(name.replaceAll("/", "."));
		logger.log(Level.INFO, "ClassReport");
	}
	@Override
	public void add(MethodReport child) {
    	String newChildName = child.getName();//child.getName().replaceAll(this.getName() + ".", ""); 
    	child.setName(newChildName);
    	//child.setSourceFilePath(buildURL+"/"+this.getName().substring(this.getName().lastIndexOf(".")+1)+".html");
        getChildren().put(child.getName(), child);
    }
	
	public void reSetMaximums(ArrayList<MethodReport> reportList,
    		ArrayList<IMethodCoverage> coverageList) {
    	 int maxMethod=1;
    	 int maxLine=1;
    	 int maxComplexity=1;
    	 int maxInstruction=1;
    	 int maxBranch=1;
    	 
    	 for (IMethodCoverage coverageCov: coverageList) {
    		
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
    	 }
    	 for (MethodReport report:  reportList) {
    		 report.setMaxBranch(maxBranch);
    		 report.setMaxMethod(maxMethod);
    		 report.setMaxLine(maxLine);
    		 report.setMaxComplexity(maxComplexity);
    		 report.setMaxInstruction(maxInstruction);
    	 }
    }
	
	private static final Logger logger = Logger.getLogger(ClassReport.class.getName());

}
