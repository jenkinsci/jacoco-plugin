package hudson.plugins.jacoco.report;

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
        printRatioCell(isFailed(), method, buf);
        printRatioCell(isFailed(), line, buf);
        printRatioCell(isFailed(), complexity, buf);
        printRatioCell(isFailed(), instruction, buf);
        printRatioCell(isFailed(), branch, buf);
        return buf.toString();
	}
	
}
