package hudson.plugins.jacoco.report;

import java.io.File;
import java.io.Writer;

import org.jacoco.core.analysis.IClassCoverage;

/**
 * @author Kohsuke Kawaguchi
 */
public final class ClassReport extends AggregatedReport<PackageReport,ClassReport,MethodReport> {

    private String sourceFilePath;
    private IClassCoverage classCov;

    @Override
	public void setName(String name) {
		super.setName(name.replaceAll("/", "."));
		//logger.log(Level.INFO, "ClassReport");
	}
	@Override
	public void add(MethodReport child) {
    	String newChildName = child.getName();
    	child.setName(newChildName);
        getChildren().put(child.getName(), child);
    }

    public void setSrcFileInfo(IClassCoverage classCov, String sourceFilePath) {
   		this.sourceFilePath = sourceFilePath;
   		this.classCov = classCov;
   	}

    /**
     * Read the source Java file for this class.
     * @return the source Java file for this class.
     */
    public File getSourceFilePath() {
        return new File(sourceFilePath);
    }

    public void printHighlightedSrcFile(Writer output) {
        new SourceAnnotator(getSourceFilePath()).printHighlightedSrcFile(classCov,output);
   	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ":"
				+ " instruction=" + instruction
				+ " branch=" + branch
				+ " complexity=" + complexity
				+ " line=" + line
				+ " method=" + method;
	}
}
