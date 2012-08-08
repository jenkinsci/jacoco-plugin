package hudson.plugins.jacoco.report;

import hudson.plugins.jacoco.model.CoverageObject;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICoverageNode;

/**
 * @author Kohsuke Kawaguchi
 * @author David Carver
 */
public final class PackageReport extends AggregatedReport<CoverageReport,PackageReport,ClassReport> {

    @Override
    public void setName(String name) {
        super.setName(name.replaceAll("/", "."));
    }
    
    @Override
    public void add(ClassReport child) {
    	String newChildName = child.getName().replaceAll(this.getName() + ".", ""); 
    	child.setName(newChildName);
    	//child.buildURL = this.getParent().getBuild().getUrl();
        this.getChildren().put(child.getName(), child);
        logger.log(Level.INFO, "PackageReport");
    }
    public  void reSetMaximumsClass(ArrayList<ClassReport> reportList,
    		ArrayList<IClassCoverage> coverageList) {
    	 int maxClazz = 1;
    	 int maxMethod=1;
    	 int maxLine=1;
    	 int maxComplexity=1;
    	 int maxInstruction=1;
    	 int maxBranch=1;
    	 
    	 for (ICoverageNode coverageCov: coverageList) {
    		 if (maxClazz < coverageCov.getClassCounter().getCoveredCount()) {
    			 maxClazz = coverageCov.getClassCounter().getCoveredCount();
    		 } 
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
    	 for (ClassReport report:  reportList) {
    		 report.maxClazz = maxClazz;
    		 report.maxBranch = maxBranch;
    		 report.maxMethod = maxMethod;
    		 report.maxLine = maxLine;
    		 report.maxComplexity = maxComplexity;
    		 report.maxInstruction = maxInstruction;
    	 }
    }
    private static final Logger logger = Logger.getLogger(CoverageObject.class.getName());
    
}
