package hudson.plugins.jacoco.model;

import java.io.Serializable;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * Represents <tt>x/y</tt> where x={@link #missed} and y={@link #covered}.
 * 
 * @author Kohsuke Kawaguchi
 * @author Jonathan Fuerth
 */
@ExportedBean
final public class Coverage implements Serializable {

    private int missed = 0;
    private int covered = 0;
    private CoverageElement.Type type;
    boolean initialized = false;

    public Coverage(int missed, int covered) {
        this.missed = missed;
        this.covered = covered;
        this.initialized = true;
    }
    
    public Coverage() {
    }

    @Exported
    public int getMissed() {
        return missed;
    }

    @Exported
    public int getCovered() {
        return covered;
    }
    
    @Exported
    public int getTotal() {
        return missed + covered;
    }

    /**
     * Gets "missed/covered (%)" representation.
     */
    @Override
    public String toString() {
        return missed + "/" + covered;
    }

    /**
     * Gets the percentage as an integer between 0 and 100.
     * @return the coverage percentage as a rounded integer between 0 and 100.
     * @see #getPercentageFloat()
     */
    @Exported
    public int getPercentage() {
        return Math.round(getPercentageFloat());
    }

    /**
     * Gets the percentage as a float between 0f and 100f.
     * @return the coverage percentage as a float between 0f and 100f.
     *      returns 100f if no coverage data was recorded at all, i.e. covered and missed are zero
     * @see #getPercentage()
     */
    @Exported
    public float getPercentageFloat() {
        float numerator = covered;
        float denominator = missed + covered;

        // there are two cases that we cannot distinguish easily
        // a) covered and missing are zero because no code was covered
        // b) covered and missing are zero because there is no code to cover in this class, e.g. interfaces

        // we use b) here for now, see JENKINS-56123 for more discussion on this
        return denominator <= 0 ? 100 : 100 * (numerator / denominator);
    }

    public CoverageElement.Type getType() {
        return type;
    }

    public void setType(CoverageElement.Type type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coverage ratio = (Coverage) o;

        return ratio.covered == covered
            && ratio.missed == missed;

    }

    @Override
    public int hashCode() {
        int result;
        result = missed;
        result = 31 * result + covered;
        return result;
    }

    /**
     * Adds the given missed and covered values to the ones already
     * contained in this ratio.
     * 
     * @param missed The amount to add to the missed.
     * @param covered The amount to add to the covered.
     */
    public void accumulate(int missed, int covered) {
      this.missed = missed;
      this.covered = covered;
      initialized = true;
    }
    public void accumulatePP(int missed, int covered) {
        this.missed += missed;
        this.covered += covered;
        initialized = true;
      }

    public boolean isInitialized() {
        return initialized;
    }

    private static final long serialVersionUID = 1L;

}
