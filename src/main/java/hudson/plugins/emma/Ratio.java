package hudson.plugins.emma;

import java.io.IOException;
import java.io.Serializable;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * Represents <tt>x/y</tt> where x={@link #numerator} and y={@link #denominator}.
 * 
 * @author Kohsuke Kawaguchi
 */
@ExportedBean
final public class Ratio implements Serializable {
  

    private float numerator = 0;
    private float denominator = 0;
    boolean initialized = false;

    public Ratio(float...f) {
    	if (f.length >=2 ) {
    		initialized = true;
            this.numerator = f[0];
            this.denominator = f[1];
    	}
    }
    
    public float getNumerator() {
        return numerator;
    }

    public float getDenominator() {
        return denominator;
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
    @Exported
    public int getPercentage() {
        return Math.round(getPercentageFloat());
    }

    /**
     * Gets the percentage in float.
     */
    @Exported
    public float getPercentageFloat() {
        return denominator<=0? 0: 100*numerator/denominator;
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
    
    public void addValue(String v) {
        float[] f = parse(v);
        numerator += f[0];
        denominator += f[1];
		initialized = true;
    }
    
    public boolean isInitialized() {
    	return initialized;
    }

    /**
     * Parses the value attribute format of EMMA "52% (52/100)".
     */
    static float[] parse(String v) {
        // if only I could use java.util.Scanner...
        
        // only leave "a/b" in "N% (a/b)"
        int idx = v.indexOf('(');
        v = v.substring(idx+1,v.length()-1);
        idx = v.indexOf('/');
        
        return new float[]{ parseFloat(v.substring(0,idx)), parseFloat(v.substring(idx+1)) };
    }

    static Ratio parseValue(String v) throws IOException {
        return new Ratio(parse(v));
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

}
