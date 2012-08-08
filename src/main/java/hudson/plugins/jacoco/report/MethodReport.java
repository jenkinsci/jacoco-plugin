package hudson.plugins.jacoco.report;

import hudson.plugins.jacoco.model.Coverage;
import hudson.plugins.jacoco.model.CoverageElement;
import hudson.plugins.jacoco.model.CoverageObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jacoco.core.analysis.IMethodCoverage;

/**
 * @author Kohsuke Kawaguchi
 * @author David Carver
 */
//AggregatedReport<PackageReport,ClassReport,MethodReport>  -  AbstractReport<ClassReport,MethodReport>
public final class MethodReport extends AggregatedReport<ClassReport,MethodReport, SourceFileReport> {
	
	public String desc;
	
	public String lineNo;
	
	public String sourceFilePath;
	
	public String getSourceFilePath() {
		return sourceFilePath;
	}
	
	public void setSourceFilePath(String sourceFilePath) {
		this.sourceFilePath = sourceFilePath;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public String getDesc(String desc) {
		return this.desc;
	}
	
	@Override
	public String getDisplayName() {
		return super.getDisplayName();
	}
	
	public void setLine(String line) {
		this.lineNo = line;
	}
	
	public String getLine() {
		return lineNo;
	}
	@Override
	public String printFourCoverageColumns() {
        StringBuilder buf = new StringBuilder();
        instruction.setType(CoverageElement.Type.INSTRUCTION);
        complexity.setType(CoverageElement.Type.COMPLEXITY);
        branch.setType(CoverageElement.Type.BRANCH);
        line.setType(CoverageElement.Type.LINE);
        method.setType(CoverageElement.Type.METHOD);
		printRatioCell(isFailed(), this.instruction, buf);
		printRatioCell(isFailed(), this.branch, buf);
		printRatioCell(isFailed(), this.complexity, buf);
		printRatioCell(isFailed(), this.line, buf);
        printRatioCell(isFailed(), this.method, buf);
        logger.log(Level.INFO, "Printing Ratio cells within MethodReport.");
		return buf.toString();
	}
	
	
	@Override
	public void add(SourceFileReport child) {
    	String newChildName = child.getName().replaceAll(this.getName() + ".", ""); 
    	child.setName(newChildName);
        getChildren().put(child.getName(), child);
        this.hasClassCoverage();
        logger.log(Level.INFO, "SourceFileReport");
    }

	public  void setCoverage(IMethodCoverage covReport) {
  	  Coverage tempCov = new Coverage();
		  tempCov.accumulate(covReport.getBranchCounter().getMissedCount(), covReport.getBranchCounter().getCoveredCount());
		  this.branch = tempCov;
		  
		  tempCov = new Coverage();
		  tempCov.accumulate(covReport.getLineCounter().getMissedCount(), covReport.getLineCounter().getCoveredCount());
		  this.line = tempCov;
		  
		  tempCov = new Coverage();
		  tempCov.accumulate(covReport.getInstructionCounter().getMissedCount(), covReport.getInstructionCounter().getCoveredCount());
		  this.instruction = tempCov;
		  
		  tempCov = new Coverage();
		  tempCov.accumulate(covReport.getMethodCounter().getMissedCount(), covReport.getMethodCounter().getCoveredCount());
		  this.method = tempCov;
		  
		  tempCov = new Coverage();
		  tempCov.accumulate(covReport.getComplexityCounter().getMissedCount(), covReport.getComplexityCounter().getCoveredCount());
		  this.complexity = tempCov;
  }
	private static final Logger logger = Logger.getLogger(CoverageObject.class.getName());
	
}
