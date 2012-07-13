package hudson.plugins.jacoco.report;

import hudson.plugins.jacoco.model.CoverageObject;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Kohsuke Kawaguchi
 */
public final class SourceFileReport extends
		AggregatedReport {
	
	@Override
    public void setName(String name) {
        super.setName(name.replaceAll("/", "."));
    	logger.log(Level.INFO, "SourceFileReport");
    }
    	private static final Logger logger = Logger.getLogger(CoverageObject.class.getName());
    
   // @Override
    /*public void add(ClassReport child) {
    	String newChildName = child.getName().replaceAll(this.getName() + ".", ""); 
    	child.setName(newChildName);
        getChildren().put(child.getName(), child);
        this.hasClassCoverage();
    }*/

}
