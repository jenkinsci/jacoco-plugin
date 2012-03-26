package hudson.plugins.jacoco;

/**
 * @author Kohsuke Kawaguchi
 */
public final class ClassReport extends AggregatedReport<SourceFileReport,ClassReport,MethodReport> {

	@Override
	public void setName(String name) {
		super.setName(name.replaceAll("/", "."));
	}

}
