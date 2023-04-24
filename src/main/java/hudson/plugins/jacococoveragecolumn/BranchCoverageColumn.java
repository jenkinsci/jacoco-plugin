package hudson.plugins.jacococoveragecolumn;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Run;
import hudson.views.ListViewColumn;
import hudson.views.ListViewColumnDescriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * View column that shows the code branch coverage percentage
 *
 */
public class BranchCoverageColumn extends AbstractJaCoCoCoverageColumn {

	@DataBoundConstructor
	public BranchCoverageColumn() {
	}

	@Override
	protected Float getPercentageFloat(final Run<?, ?> lastSuccessfulBuild) {
		return getPercentageFloat(lastSuccessfulBuild,
				(a) -> a.getBranchCoverage().getPercentageFloat());
	}

	@Extension
	public static final Descriptor<ListViewColumn> DESCRIPTOR = new DescriptorImpl();

	@Override
	public Descriptor<ListViewColumn> getDescriptor() {
		return DESCRIPTOR;
	}

	private static class DescriptorImpl extends ListViewColumnDescriptor {
		@Override
		public ListViewColumn newInstance(final StaplerRequest req,
										  @NonNull final JSONObject formData) {
			return new BranchCoverageColumn();
		}
		
		@Override
		public boolean shownByDefault() {
			return false;
		}

		@NonNull
		@Override
		public String getDisplayName() {
			return "JaCoCo Branch Coverage";
		}
	}
}
