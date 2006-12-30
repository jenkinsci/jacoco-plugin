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

    public String toString() {
        return numerator+"/"+denominator;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ratio ratio = (Ratio) o;

        if (Float.compare(ratio.denominator, denominator) != 0) return false;
        if (Float.compare(ratio.numerator, numerator) != 0) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = numerator != +0.0f ? Float.floatToIntBits(numerator) : 0;
        result = 31 * result + denominator != +0.0f ? Float.floatToIntBits(denominator) : 0;
        return result;
    }

    private static final long serialVersionUID = 1L;
}
