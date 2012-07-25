package hudson.plugins.jacoco.report;

import hudson.plugins.jacoco.model.CoverageObject;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Kohsuke Kawaguchi
 */
public final class ClassReport extends AggregatedReport<PackageReport,ClassReport,MethodReport> {

	public String buildURL;
	@Override
	public void setName(String name) {
		super.setName(name.replaceAll("/", "."));
		logger.log(Level.INFO, "ClassReport");
	}
	@Override
	public void add(MethodReport child) {
    	String newChildName = child.getName();//child.getName().replaceAll(this.getName() + ".", ""); 
    	child.setName(newChildName);
    	child.setSourceFilePath(buildURL+"/"+this.getName().substring(this.getName().lastIndexOf(".")+1)+".java");
        getChildren().put(child.getName(), child);
        this.hasClassCoverage();
        logger.log(Level.INFO, "ClassReport");
    }
	private static final Logger logger = Logger.getLogger(ClassReport.class.getName());

}
