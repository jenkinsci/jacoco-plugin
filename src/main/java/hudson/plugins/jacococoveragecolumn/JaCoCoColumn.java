package hudson.plugins.jacococoveragecolumn;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Job;
import hudson.model.Run;
import hudson.plugins.jacoco.JacocoBuildAction;
import hudson.plugins.jacoco.model.Coverage;
import hudson.plugins.jacoco.report.CoverageReport;
import hudson.views.ListViewColumn;

import java.awt.Color;
import java.math.BigDecimal;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * View column that shows the code coverage (line) percentage
 *
 */
public class JaCoCoColumn extends ListViewColumn {

	@DataBoundConstructor
	public JaCoCoColumn() {
	}

	public String getPercent(final Job<?, ?> job) {
		final Run<?, ?> lastSuccessfulBuild = job.getLastSuccessfulBuild();
		final StringBuilder stringBuilder = new StringBuilder();

		if (lastSuccessfulBuild == null) {
			stringBuilder.append("N/A");
		} else if (lastSuccessfulBuild.getAction(JacocoBuildAction.class) == null){
			stringBuilder.append("N/A");
		} else {
			final Double percent = getLinePercent(lastSuccessfulBuild);
			stringBuilder.append(percent);
		}

		return stringBuilder.toString();
	}

	public String getLineColor(final BigDecimal amount) {
		if (amount == null) {
			return null;
		}
		return CoverageRange.valueOf(amount.doubleValue()).getLineHexString();
	}

	public String getFillColor(final BigDecimal amount) {
		if (amount == null) {
			return null;
		}
		final Color c = CoverageRange.fillColorOf(amount.doubleValue());
		return CoverageRange.colorAsHexString(c);
	}

	public BigDecimal getLineCoverage(final Job<?, ?> job) {
		final Run<?, ?> lastSuccessfulBuild = job.getLastSuccessfulBuild();
		return BigDecimal.valueOf(getLinePercent(lastSuccessfulBuild)
				.doubleValue());
	}

	private Double getLinePercent(final Run<?, ?> lastSuccessfulBuild) {
		final Float percentageFloat = getPercentageFloat(lastSuccessfulBuild);
		final double doubleValue = percentageFloat.doubleValue();

		final int decimalPlaces = 2;
		BigDecimal bigDecimal = new BigDecimal(doubleValue);

		// setScale is immutable
		bigDecimal = bigDecimal.setScale(decimalPlaces,
				BigDecimal.ROUND_HALF_UP);
		return bigDecimal.doubleValue();
	}

	private Float getPercentageFloat(final Run<?, ?> lastSuccessfulBuild) {
		if(lastSuccessfulBuild == null) {
			return new Float(0);
		}

		final JacocoBuildAction action = lastSuccessfulBuild
				.getAction(JacocoBuildAction.class);

		if(action == null) {
			return new Float(0);
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

	private static class DescriptorImpl extends Descriptor<ListViewColumn> {
		@Override
		public ListViewColumn newInstance(final StaplerRequest req,
				final JSONObject formData) throws FormException {
			return new JaCoCoColumn();
		}

		@Override
		public String getDisplayName() {
			return "JaCoCo Line Coverage";
		}
	}
}
