package hudson.plugins.jacoco.report;

/**
 * @author Kohsuke Kawaguchi
 * @author David Carver
 */
public final class MethodReport extends AbstractReport<ClassReport,MethodReport> {
	
	public String desc;
	
	public String line;
	
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
		this.line = line;
	}
	
	public String getLine() {
		return line;
	}
	
}
