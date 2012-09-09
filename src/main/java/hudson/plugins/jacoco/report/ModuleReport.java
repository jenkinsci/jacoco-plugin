package hudson.plugins.jacoco.report;

import hudson.plugins.jacoco.model.CoverageObject;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.IPackageCoverage;

/**
 * @author Kohsuke Kawaguchi
 * @author David Carver
 */
public final class ModuleReport extends AggregatedReport<CoverageReport,ModuleReport,PackageReport>{

    @Override
    public void setName(String name) {
        super.setName(name.replaceAll("/", "."));
    }
    
    @Override
    public void add(PackageReport child) {
        this.getChildren().put(child.getName(), child);
        logger.log(Level.INFO, "ModuleReport");
    }

    private static final Logger logger = Logger.getLogger(CoverageObject.class.getName());
    
}
