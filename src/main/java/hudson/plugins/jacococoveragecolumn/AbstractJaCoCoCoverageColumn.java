package hudson.plugins.jacococoveragecolumn;

import hudson.model.Job;
import hudson.model.Run;
import hudson.plugins.jacoco.JacocoBuildAction;
import hudson.plugins.jacoco.model.Coverage;
import hudson.views.ListViewColumn;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;

/**
 * Abstract view column to show the code coverage percentage
 *
 */
public abstract class AbstractJaCoCoCoverageColumn extends ListViewColumn {

	protected abstract Float getPercentageFloat(final Run<?, ?> build);

	protected Float getPercentageFloat(final Run<?, ?> lastSuccessfulBuild,
									   Function<JacocoBuildAction, Float> percentageFunction) {
		if(lastSuccessfulBuild == null) {
			return 0f;
		}

		final JacocoBuildAction action = lastSuccessfulBuild
				.getAction(JacocoBuildAction.class);

		if(action == null) {
			return 0f;
		}

		return percentageFunction.apply(action);
	}


	public boolean hasCoverage(final Job<?, ?> job) {
		final Run<?, ?> lastSuccessfulBuild = job.getLastSuccessfulBuild();
		return lastSuccessfulBuild != null &&
				lastSuccessfulBuild.getAction(JacocoBuildAction.class) != null;
	}

	public String getPercent(final Job<?, ?> job) {
		final StringBuilder stringBuilder = new StringBuilder();

		if (!hasCoverage(job)) {
			stringBuilder.append("N/A");
		} else {
			final Run<?, ?> lastSuccessfulBuild = job.getLastSuccessfulBuild();
			final Double percent = getPercent(lastSuccessfulBuild);
			stringBuilder.append(percent);
		}

		return stringBuilder.toString();
	}

	public String getColor(final Job<?, ?> job, final BigDecimal amount) {
		if (amount == null) {
			return null;
		}

		if(job != null && !hasCoverage(job)) {
			return CoverageRange.NA.getLineHexString();
		}

		return CoverageRange.valueOf(amount.doubleValue()).getLineHexString();
	}

	public String getFillColor(final Job<?, ?> job, final BigDecimal amount) {
		if (amount == null) {
			return null;
		}

		if(job != null && !hasCoverage(job)) {
			return CoverageRange.NA.getFillHexString();
		}

		final Color c = CoverageRange.fillColorOf(amount.doubleValue());
		return CoverageRange.colorAsHexString(c);
	}

	public BigDecimal getCoverage(final Job<?, ?> job) {
		final Run<?, ?> lastSuccessfulBuild = job.getLastSuccessfulBuild();
		return BigDecimal.valueOf(getPercent(lastSuccessfulBuild));
	}

	private Double getPercent(final Run<?, ?> lastSuccessfulBuild) {
		final Float percentageFloat = getPercentageFloat(lastSuccessfulBuild);
		final double doubleValue = percentageFloat.doubleValue();

		final int decimalPlaces = 2;
		BigDecimal bigDecimal = new BigDecimal(doubleValue);

		// setScale is immutable
		bigDecimal = bigDecimal.setScale(decimalPlaces,
				RoundingMode.HALF_UP);
		return bigDecimal.doubleValue();
	}

}
