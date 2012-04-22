package hudson.plugins.jacoco.report;

/**
 * @author Kohsuke Kawaguchi
 */
public final class PackageReport extends AggregatedReport<CoverageReport,PackageReport,SourceFileReport> {

    @Override
    public void setName(String name) {
        super.setName(name.replaceAll("/", "."));
    }
    
}
