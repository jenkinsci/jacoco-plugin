package hudson.plugins.jacoco.report;

import hudson.plugins.jacoco.model.CoverageObject;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Kohsuke Kawaguchi
 */
public final class SourceFileReport extends AbstractReport<MethodReport,SourceFileReport> {
	
	@Override
    public void setName(String name) {
        super.setName(name.replaceAll("/", "."));
    	//logger.log(Level.INFO, "SourceFileReport");
    }
    	private static final Logger logger = Logger.getLogger(SourceFileReport.class.getName());
    

}	
