package hudson.plugins.emma;

import hudson.model.ModelObject;

import java.io.IOException;

/**
 * Base class of all the coverage report.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class AbstractReport<SELF> implements ModelObject {

    private String name;

    /*package*/ Ratio clazz,method,block,line;

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
}
