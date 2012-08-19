package hudson.plugins.jacoco;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.plugins.jacoco.model.CoverageObject;

import org.kohsuke.stapler.DataBoundConstructor;


/**
 * Represents one row on the configuration page.
 * 
 * @author Jonathan Fuerth
 * @author Ognjen Bubalo
 */
public class ConfigRow extends AbstractDescribableImpl<ConfigRow> {

	private String moduleName;
	private String srcDir;
	private String classDir;
	private String execFile;

	@DataBoundConstructor
	public ConfigRow(String moduleName, String srcDir, String classDir, String execFile) {
		super();
		this.moduleName = moduleName;
		this.srcDir = srcDir;
		this.classDir = classDir;
		this.execFile = execFile;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getSrcDir() {
		return srcDir;
	}

	public void setSrcDir(String srcDir) {
		this.srcDir = srcDir;
	}

	public String getClassDir() {
		return classDir;
	}

	public void setClassDir(String classDir) {
		this.classDir = classDir;
	}

	public String getExecFile() {
		return execFile;
	}

	public void setExecFile(String execFile) {
		this.execFile = execFile;
	}

    @Override
	public String toString() {
		return "ConfigRow [moduleName=" +moduleName+ "srcDir=" + srcDir + ", classDir=" + classDir
				+ ", execFile=" + execFile + "]";
	}


	@Extension
    public static class DescriptorImpl extends Descriptor<ConfigRow> {
        public String getDisplayName() { return ""; }
    }

}
