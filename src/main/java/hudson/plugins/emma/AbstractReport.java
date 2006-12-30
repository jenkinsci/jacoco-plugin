package hudson.plugins.emma;

import hudson.model.ModelObject;

import java.io.IOException;

/**
 * Base class of all the coverage report.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class AbstractReport<
    PARENT extends AggregatedReport<?,PARENT,?>,
    SELF> implements ModelObject {

    private String name;

    /*package*/ Ratio clazz,method,block,line;

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
        return name;
    }

    public Ratio getClassCoverage() {
        return clazz;
    }

    public Ratio getMethodCoverage() {
        return method;
    }

    public Ratio getBlockCoverage() {
        return block;
    }

    public Ratio getLineCoverage() {
        return line;
    }

    /**
     * Called at the last stage of the tree construction,
     * to set the back pointer.
     */
    protected void setParent(PARENT p) {
        this.parent = p;
    }

    /**
     * Gets the back pointer to the parent coverage object.
     */
    public PARENT getParent() {
        return parent;
    }

    /**
     * Gets the corresponding coverage report object in the previous
     * run that has the record.
     *
     * @return
     *      null if no earlier record was found.
     */
    public SELF getPreviousResult() {
        PARENT p = parent;
        while(true) {
            p = p.getPreviousResult();
            if(p==null)
                return null;
            SELF prev = (SELF)parent.getChildren().get(name);
            if(prev!=null)
                return prev;
        }
    }
}
