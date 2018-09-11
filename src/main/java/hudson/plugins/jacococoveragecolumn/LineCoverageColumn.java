package hudson.plugins.jacococoveragecolumn;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Run;
import hudson.plugins.jacoco.JacocoBuildAction;
import hudson.plugins.jacoco.model.Coverage;
import hudson.views.ListViewColumnDescriptor;
import hudson.views.ListViewColumn;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;

/**
 * View column that shows the code line coverage percentage
 *
 */
public class LineCoverageColumn extends AbstractJaCoCoCoverageColumn {

	@DataBoundConstructor
	public LineCoverageColumn() {
	}

	@Override
	protected Float getPercentageFloat(final Run<?, ?> lastSuccessfulBuild) {
		if(lastSuccessfulBuild == null) {
			return 0f;
		}

		final JacocoBuildAction action = lastSuccessfulBuild
				.getAction(JacocoBuildAction.class);

		if(action == null) {
			return 0f;
		}

		final Coverage ratio = action.getLineCoverage();
		return ratio.getPercentageFloat();
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
										  @Nonnull final JSONObject formData) {
			return new LineCoverageColumn();
		}

		@Override
		public boolean shownByDefault() {
			return false;
		}

		@Nonnull
		@Override
		public String getDisplayName() {
			return "JaCoCo Line Coverage";
		}
	}
}
