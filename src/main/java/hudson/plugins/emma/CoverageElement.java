package hudson.plugins.emma;

import java.io.IOException;

/**
 * This is a transitive object used during the parsing, but not a part of
 * the final tree built. 
 *
 * @author Kohsuke Kawaguchi
 */
public final class CoverageElement {
    private String type;
    private String value;

    // set by attributes
    public void setType(String type) {
        this.type = type;
    }

    // set by attributes
    public void setValue(String value) {
        this.value = value;
    }

    void addTo(AbstractReport report) throws IOException {
        Ratio r = Ratio.parseValue(value);

        if(type.equals("class, %")) {
            report.clazz = r;
            return;
        }
        if(type.equals("method, %")) {
            report.method = r;
            return;
        }
        if(type.equals("block, %")) {
            report.block = r;
            return;
        }
        if(type.equals("line, %")) {
            report.line = r;
            return;
        }

        throw new IllegalArgumentException("Invalid type: "+type);
    }
}
