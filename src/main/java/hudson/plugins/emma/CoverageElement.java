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

    void addTo(AbstractReport<?,?> report) throws IOException {

    	Ratio r = null;
    	if(type.equals("class, %")) {
    		r = report.clazz;
        } else if(type.equals("method, %")) {
    		r = report.method;
        } else if(type.equals("block, %")) {
    		r = report.block;
        } else if(type.equals("line, %")) {
    		r = report.line;
        } else {
            throw new IllegalArgumentException("Invalid type: "+type);
        }
    	r.addValue(value);

    }
}
