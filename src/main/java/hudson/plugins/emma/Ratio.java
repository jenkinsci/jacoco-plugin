package hudson.plugins.emma;

import java.io.IOException;
import java.io.Serializable;

/**
 * Represents <tt>x/y</tt> where x={@link #numerator} and y={@link #denominator}.
 * 
 * @author Kohsuke Kawaguchi
 */
final public class Ratio implements Serializable {
    public final float numerator;
    public final float denominator;

    private Ratio(float numerator, float denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    /**
     * Gets "x/y" representation.
     */
    public String toString() {
        return print(numerator)+"/"+print(denominator);
    }

    private String print(float f) {
        int i = (int) f;
        if(i==f)
            return String.valueOf(i);
        else
            return String.valueOf(f);
    }

    /**
     * Gets the percentage in integer.
     */
    public int getPercentage() {
        return Math.round(getPercentageFloat());
    }

    /**
     * Gets the percentage in float.
     */
    public float getPercentageFloat() {
        return 100*numerator/denominator;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ratio ratio = (Ratio) o;

        return Float.compare(ratio.denominator, denominator)==0
            && Float.compare(ratio.numerator, numerator)==0;

    }

    public int hashCode() {
        int result;
        result = numerator != +0.0f ? Float.floatToIntBits(numerator) : 0;
        result = 31 * result + denominator != +0.0f ? Float.floatToIntBits(denominator) : 0;
        return result;
    }

    /**
     * Parses the value attribute format of EMMA "52% (52/100)".
     */
    static Ratio parseValue(String v) throws IOException {
        // if only I could use java.util.Scanner...

        // only leave "a/b" in "N% (a/b)"
        int idx = v.indexOf('(');
        v = v.substring(idx+1,v.length()-1);

        idx = v.indexOf('/');

        return Ratio.create(
           parseFloat(v.substring(0,idx)),
           parseFloat(v.substring(idx+1)));
    }

     /**
      * Parses the float value stored in a string. Uses simple heuristics to
      * handle comma or dot as a decimal point.
      */
     private static float parseFloat(String v) {
         int idx = v.indexOf(',');
         if (idx >= 0) {
             v = v.substring(0, idx) + "." + v.substring(idx+1);
         }
         return Float.parseFloat(v);
     }

    private static final long serialVersionUID = 1L;

//
// fly-weight patterns for common Ratio instances (x/y) where x<y
// and x,y are integers.
//
    private static final Ratio[] COMMON_INSTANCES = new Ratio[256];

    /**
     * Creates a new instance of {@link Ratio}.
     */
    public static Ratio create(float x, float y) {
        int xx= (int) x;
        int yy= (int) y;

        if(xx==x && yy==y) {
            int idx = yy * (yy + 1) / 2 + xx;
            if(0<=idx && idx<COMMON_INSTANCES.length) {
                Ratio r = COMMON_INSTANCES[idx];
                if(r==null)
                    COMMON_INSTANCES[idx] = r = new Ratio(x,y);
                return r;
            }
        }

        return new Ratio(x,y);
    }
}
