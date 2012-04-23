package hudson.plugins.jacoco.report;

/**
 * @author Kohsuke Kawaguchi
 */
public final class ClassReport extends AggregatedReport<PackageReport,ClassReport,MethodReport> {

	@Override
	public void setName(String name) {
		super.setName(name.replaceAll("/", "."));
	}

}
