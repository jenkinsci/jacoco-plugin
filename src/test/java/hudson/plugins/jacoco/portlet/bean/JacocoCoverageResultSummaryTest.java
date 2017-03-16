package hudson.plugins.jacoco.portlet.bean;

import hudson.model.Job;
import hudson.model.Run;
import hudson.plugins.jacoco.portlet.utils.Constants;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.SortedMap;

import static org.junit.Assert.*;

public class JacocoCoverageResultSummaryTest {
    @Test
    public void testCoverageSetterGetter() throws Exception {
        JacocoCoverageResultSummary summary = new JacocoCoverageResultSummary();
        summary.setBranchCoverage(new BigDecimal(23.4f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP));
        summary.setClassCoverage(new BigDecimal(23.5f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP));
        summary.setComplexityScore(new BigDecimal(23.6f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP));
        summary.setInstructionCoverage(new BigDecimal(23.7f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP));
        summary.setLineCoverage(new BigDecimal(23.8f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP));
        summary.setMethodCoverage(new BigDecimal(23.9f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP));

        assertEquals(new BigDecimal(23.4f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), summary.getBranchCoverage());
        assertEquals(new BigDecimal(23.5f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), summary.getClassCoverage());
        assertEquals(new BigDecimal(23.6f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), summary.getComplexityScore());
        assertEquals(new BigDecimal(23.7f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), summary.getInstructionCoverage());
        assertEquals(new BigDecimal(23.8f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), summary.getLineCoverage());
        assertEquals(new BigDecimal(23.9f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), summary.getMethodCoverage());

        assertEquals(0.0f, summary.getTotalBranchCoverage(), 0.01);
        assertEquals(0.0f, summary.getTotalClassCoverage(), 0.01);
        assertEquals(0.0f, summary.getTotalComplexityScore(), 0.01);
        assertEquals(0.0f, summary.getTotalInstructionCoverage(), 0.01);
        assertEquals(0.0f, summary.getTotalLineCoverage(), 0.01);
        assertEquals(0.0f, summary.getTotalMethodCoverage(), 0.01);

        assertTrue(summary.getJacocoCoverageResults().isEmpty());
        assertNull(summary.getJob());
        //noinspection unchecked
        Job job = new Job(null, "job") {
            @Override
            public boolean isBuildable() {
                return false;
            }

            @Override
            protected SortedMap<Integer, ? extends Run> _getRuns() {
                return null;
            }

            @Override
            protected void removeRun(Run run) {

            }
        };
        summary.setJob(job);
        assertNotNull(summary.getJob());
    }

    @Test
    public void constructor() throws Exception {
        JacocoCoverageResultSummary summary = new JacocoCoverageResultSummary(null,
                new BigDecimal(23.4f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), new BigDecimal(23.5f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP),
                new BigDecimal(23.6f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), new BigDecimal(23.7f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP),
                new BigDecimal(23.8f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), new BigDecimal(23.9f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP));

        assertEquals(new BigDecimal(23.7f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), summary.getBranchCoverage());
        assertEquals(new BigDecimal(23.6f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), summary.getClassCoverage());
        assertEquals(new BigDecimal(23.9f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), summary.getComplexityScore());
        assertEquals(new BigDecimal(23.8f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), summary.getInstructionCoverage());
        assertEquals(new BigDecimal(23.4f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), summary.getLineCoverage());
        assertEquals(new BigDecimal(23.5f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), summary.getMethodCoverage());

        assertEquals(0.0f, summary.getTotalBranchCoverage(), 0.01);
        assertEquals(0.0f, summary.getTotalClassCoverage(), 0.01);
        assertEquals(0.0f, summary.getTotalComplexityScore(), 0.01);
        assertEquals(0.0f, summary.getTotalInstructionCoverage(), 0.01);
        assertEquals(0.0f, summary.getTotalLineCoverage(), 0.01);
        assertEquals(0.0f, summary.getTotalMethodCoverage(), 0.01);

        assertTrue(summary.getJacocoCoverageResults().isEmpty());
    }

    @Test
    public void addCoverageResults() throws Exception {
        JacocoCoverageResultSummary orig = new JacocoCoverageResultSummary(null,
                new BigDecimal(23.4f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), new BigDecimal(23.5f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP),
                new BigDecimal(23.6f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), new BigDecimal(23.7f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP),
                new BigDecimal(23.8f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), new BigDecimal(23.9f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP));

        JacocoCoverageResultSummary summary = new JacocoCoverageResultSummary();

        assertEquals(summary, summary.addCoverageResult(orig));

        assertEquals(new BigDecimal(23.7f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), summary.getBranchCoverage());
        assertEquals(new BigDecimal(23.6f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), summary.getClassCoverage());
        assertEquals(new BigDecimal(23.9f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), summary.getComplexityScore());
        assertEquals(new BigDecimal(23.8f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), summary.getInstructionCoverage());
        assertEquals(new BigDecimal(23.4f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), summary.getLineCoverage());
        assertEquals(new BigDecimal(23.5f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), summary.getMethodCoverage());

        assertEquals(23.7f, summary.getTotalBranchCoverage(), 0.01);
        assertEquals(23.6f, summary.getTotalClassCoverage(), 0.01);
        assertEquals(23.9f, summary.getTotalComplexityScore(), 0.01);
        assertEquals(23.8f, summary.getTotalInstructionCoverage(), 0.01);
        assertEquals(23.4f, summary.getTotalLineCoverage(), 0.01);
        assertEquals(23.5f, summary.getTotalMethodCoverage(), 0.01);

        assertFalse(summary.getJacocoCoverageResults().isEmpty());
    }

    @Test
    public void setCoverageResults() throws Exception {
        JacocoCoverageResultSummary orig = new JacocoCoverageResultSummary(null, new BigDecimal(23.4f), new BigDecimal(23.5f),
                new BigDecimal(23.6f), new BigDecimal(23.7f), new BigDecimal(23.8f), new BigDecimal(23.9f));

        JacocoCoverageResultSummary summary = new JacocoCoverageResultSummary();

        summary.setCoverageResults(Collections.singletonList(orig));

        assertEquals(new BigDecimal(0f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), summary.getBranchCoverage());
        assertEquals(new BigDecimal(0f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), summary.getClassCoverage());
        assertEquals(new BigDecimal(0f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), summary.getComplexityScore());
        assertEquals(new BigDecimal(0f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), summary.getInstructionCoverage());
        assertEquals(new BigDecimal(0f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), summary.getLineCoverage());
        assertEquals(new BigDecimal(0f).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP), summary.getMethodCoverage());

        assertEquals(0f, summary.getTotalBranchCoverage(), 0.01);
        assertEquals(0f, summary.getTotalClassCoverage(), 0.01);
        assertEquals(0f, summary.getTotalComplexityScore(), 0.01);
        assertEquals(0f, summary.getTotalInstructionCoverage(), 0.01);
        assertEquals(0f, summary.getTotalLineCoverage(), 0.01);
        assertEquals(0f, summary.getTotalMethodCoverage(), 0.01);

        assertFalse(summary.getJacocoCoverageResults().isEmpty());
    }
}