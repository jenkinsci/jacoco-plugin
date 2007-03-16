package hudson.plugins.emma;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;
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

    public Map<String,CHILD> getChildren() {
        return children;
    }

    protected void setParent(PARENT p) {
        super.setParent(p);
        for (CHILD c : children.values())
            c.setParent((SELF)this);
    }

    public CHILD getDynamic(String token, StaplerRequest req, StaplerResponse rsp ) throws IOException {
        return getChildren().get(token);
    }
    
    @Override
    public void setFailed() {
        super.setFailed();

        if (getParent() != null)
            getParent().setFailed();
    }
}
