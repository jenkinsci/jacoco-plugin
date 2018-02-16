package hudson.plugins.jacoco.report;

import hudson.model.ModelObject;
import hudson.model.Run;
import hudson.plugins.jacoco.model.CoverageElement;
import hudson.plugins.jacoco.model.CoverageObject;

import java.io.IOException;

/**
 * Base class of the coverage report tree,
 * which maintains the details of the coverage report.
 *
 * @author Kohsuke Kawaguchi
 * @param <PARENT> Parent type
 * @param <SELF> Self type
 */
public abstract class AbstractReport<PARENT extends AggregatedReport<?,PARENT,?>,
    SELF extends CoverageObject<SELF>> extends CoverageObject<SELF> implements ModelObject {

    private String name;

    private PARENT parent;

    public void addCoverage(CoverageElement cv) throws IOException {
        cv.addTo(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return getName();
    }

    /**
     * Called at the last stage of the tree construction,
     * to set the back pointer.
     * @param p parent
     */
    protected void setParent(PARENT p) {
        this.parent = p;
    }

    /**
     * Gets the back pointer to the parent coverage object.
     */
    @Override
    public PARENT getParent() {
        return parent;
    }

    @Override
    public SELF getPreviousResult() {
        PARENT p = parent;
        while(true) {
            p = p.getPreviousResult();
            if(p==null)
                return null;
            SELF prev = (SELF)p.getChildren().get(name);
            if(prev!=null)
                return prev;
        }
    }

    @Override
    public Run<?,?> getBuild() {
    	return parent.getBuild();
    }
    
}
