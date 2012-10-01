package hudson.plugins.jacoco.report;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jacoco.core.analysis.IMethodCoverage;

/**
 * @author Kohsuke Kawaguchi
 */
public final class ClassReport extends AggregatedReport<PackageReport,ClassReport,MethodReport> {

	public String buildURL;
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

	@Override
	public String toString() {
		return getClass().getSimpleName() + ":"
				+ " instruction=" + instruction
				+ " branch=" + branch
				+ " complexity=" + complexity
				+ " line=" + line
				+ " method=" + method;
	}
	private static final Logger logger = Logger.getLogger(ClassReport.class.getName());

}
