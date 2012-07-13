package hudson.plugins.jacoco.report;

import hudson.plugins.jacoco.model.CoverageObject;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Kohsuke Kawaguchi
 * @author David Carver
 */
public final class PackageReport extends AggregatedReport<CoverageReport,PackageReport,ClassReport> {

    @Override
    public void setName(String name) {
        super.setName(name.replaceAll("/", "."));
    }
    
    @Override
    public void add(ClassReport child) {
    	String newChildName = child.getName().replaceAll(this.getName() + ".", ""); 
    	child.setName(newChildName);
        getChildren().put(child.getName(), child);
        this.hasClassCoverage();
        logger.log(Level.INFO, "PackageReport");
    }
    private static final Logger logger = Logger.getLogger(CoverageObject.class.getName());
    
}
