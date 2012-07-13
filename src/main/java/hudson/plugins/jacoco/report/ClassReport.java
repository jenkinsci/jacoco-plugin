package hudson.plugins.jacoco.report;

import hudson.plugins.jacoco.model.CoverageObject;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Kohsuke Kawaguchi
 */
public final class ClassReport extends AggregatedReport<PackageReport,ClassReport,MethodReport> {

	@Override
	public void setName(String name) {
		super.setName(name.replaceAll("/", "."));
		logger.log(Level.INFO, "ClassReport");
	}
	private static final Logger logger = Logger.getLogger(CoverageObject.class.getName());

}
