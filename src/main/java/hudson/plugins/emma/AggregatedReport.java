package hudson.plugins.emma;

import java.util.Map;
import java.util.TreeMap;

/**
 * Reports that have children.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class AggregatedReport<
    PARENT extends AggregatedReport<?,PARENT,?>,
    SELF extends AggregatedReport<PARENT,SELF,CHILD>,
    CHILD extends AbstractReport<SELF,CHILD>> extends AbstractReport<PARENT,SELF> {

    private final Map<String, CHILD> children = new TreeMap<String, CHILD>();

    public void add(CHILD child) {
        children.put(child.getName(),child);
    }

    public Map<String, CHILD> getChildren() {
        return children;
    }

    protected void setParent(PARENT p) {
        super.setParent(p);
        for (CHILD c : children.values())
            c.setParent((SELF)this);
    }
}
