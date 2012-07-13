package hudson.plugins.jacoco.report;

import hudson.plugins.jacoco.model.CoverageObject;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Kohsuke Kawaguchi
 * @author David Carver
 */
public final class MethodReport extends AbstractReport<ClassReport,MethodReport> {
	
	public String desc;
	
	public String lineNo;
	
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
		printRatioCell(isFailed(), instruction, buf);
		printRatioCell(isFailed(), branch, buf);
		printRatioCell(isFailed(), complexity, buf);
		printRatioCell(isFailed(), line, buf);
        printRatioCell(isFailed(), method, buf);
        logger.log(Level.INFO, "Printing Ratio cells within MethodReport.");
		return buf.toString();
	}
	private static final Logger logger = Logger.getLogger(CoverageObject.class.getName());
	
}
