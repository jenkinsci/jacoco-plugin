package hudson.plugins.emma;

import java.io.Serializable;

/**
 * Represents <tt>x/y</tt> where x={@link #numerator} and y={@link #denominator}.
 * 
 * @author Kohsuke Kawaguchi
 */
final class Ratio implements Serializable {
    public final float numerator;
    public final float denominator;

    public Ratio(float numerator, float denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    private static final long serialVersionUID = 1L;
}
