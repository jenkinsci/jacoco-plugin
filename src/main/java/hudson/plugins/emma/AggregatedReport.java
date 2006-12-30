package hudson.plugins.emma;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Kohsuke Kawaguchi
 */
public class AggregatedReport<C extends AbstractReport> extends AbstractReport {

    private final Map<String,C> children = new TreeMap<String,C>();

    public void add(C child) {
        children.put(child.getName(),child);
    }
}
