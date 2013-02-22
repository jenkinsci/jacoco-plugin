package hudson.plugins.jacoco.report;

import hudson.plugins.jacoco.model.CoverageObject;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICoverageNode;

/**
 * @author Kohsuke Kawaguchi
 * @author David Carver
 */
public final class PackageReport extends AggregatedReport<CoverageReport,PackageReport,ClassReport> {

    /**
     * Give the default no-name package a non-empty name.
     */
    @Override
    public String getName() {
        String n = super.getName();
        return n.length() == 0 ? "(default)" : n;
    }

    @Override
    public void setName(String name) {
        super.setName(name.replaceAll("/", "."));
    }
    
    @Override
    public void add(ClassReport child) {
    	String newChildName = child.getName().replaceAll(this.getName() + ".", ""); 
    	child.setName(newChildName);
        this.getChildren().put(child.getName(), child);
        //logger.log(Level.INFO, "PackageReport");
    }
    
    private static final Logger logger = Logger.getLogger(CoverageObject.class.getName());
    
}
