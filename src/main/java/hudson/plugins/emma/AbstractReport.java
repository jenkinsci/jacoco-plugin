package hudson.plugins.emma;

import java.io.IOException;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class AbstractReport {

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
}
