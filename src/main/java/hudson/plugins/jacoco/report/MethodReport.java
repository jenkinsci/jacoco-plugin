package hudson.plugins.jacoco.report;

import hudson.plugins.jacoco.model.CoverageObject;

import java.util.logging.Level;
import java.util.logging.Logger;

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
	//NOT NEEDED
	/*@Override
	public String printFourCoverageColumns() {
        StringBuilder buf = new StringBuilder();
		printRatioCell(isFailed(), instruction, buf);
		printRatioCell(isFailed(), branch, buf);
		printRatioCell(isFailed(), complexity, buf);
		printRatioCell(isFailed(), line, buf);
        printRatioCell(isFailed(), method, buf);
        logger.log(Level.INFO, "Printing Ratio cells within MethodReport.");
		return buf.toString();
	}*/
	@Override
	public void add(SourceFileReport child) {
    	String newChildName = child.getName().replaceAll(this.getName() + ".", ""); 
    	child.setName(newChildName);
        getChildren().put(child.getName(), child);
        this.hasClassCoverage();
        logger.log(Level.INFO, "SourceFileReport");
    }
	private static final Logger logger = Logger.getLogger(CoverageObject.class.getName());
	
}
