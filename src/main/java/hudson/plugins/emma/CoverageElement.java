package hudson.plugins.emma;

import java.io.IOException;

/**
 * This is a transitive object used during the parsing, but not a part of
 * the final tree built. 
 *
 * @author Kohsuke Kawaguchi
 */
public final class CoverageElement {

    /**
     * Enumeration of coverage types that appear in a JaCoCo report.
     * 
     * @author Jonathan Fuerth <jfuerth@gmail.com>
     */
    private enum Type {
      INSTRUCTION {
        @Override
        public Ratio getAssociatedRatio(AbstractReport<?, ?> from) {
          return from.instruction;
        }
      },
      BRANCH {
        @Override
        public Ratio getAssociatedRatio(AbstractReport<?, ?> from) {
          return from.branch;
        }
      },
      LINE {
        @Override
        public Ratio getAssociatedRatio(AbstractReport<?, ?> from) {
          return from.line;
        }
      },
      COMPLEXITY {
        @Override
        public Ratio getAssociatedRatio(AbstractReport<?, ?> from) {
          return from.complexity;
        }
      },
      METHOD {
        @Override
        public Ratio getAssociatedRatio(AbstractReport<?, ?> from) {
          return from.method;
        }
      },
      CLASS {
        @Override
        public Ratio getAssociatedRatio(AbstractReport<?, ?> from) {
          return from.clazz;
        }
      };
      
      /**
       * Returns the ratio object on the given report that tracks this type of coverage.
       * 
       * @param from The report to return the appropriate Ratio object from. Not null.
       */
      public abstract Ratio getAssociatedRatio(AbstractReport<?,?> from);
    }

    private Type type;
    private int missed;
    private int covered;

    // set by attributes
    public void setType(String type) {
        this.type = Type.valueOf(type);
    }

    // set by attributes
    public void setMissed(int missed) {
      this.missed = missed;
    }

    // set by attributes
    public void setCovered(int covered) {
      this.covered = covered;
    }

    void addTo(AbstractReport<?,?> report) throws IOException {
        type.getAssociatedRatio(report).accumulate(covered, (missed + covered));
    }
}
