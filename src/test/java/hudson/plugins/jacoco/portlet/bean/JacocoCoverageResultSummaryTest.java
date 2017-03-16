package hudson.plugins.jacoco.portlet.bean;

import hudson.model.Job;
import hudson.model.Run;
import org.junit.Test;

import java.util.Collections;
import java.util.SortedMap;

import static org.junit.Assert.*;

public class JacocoCoverageResultSummaryTest {
    @Test
    public void testCoverageSetterGetter() throws Exception {
        JacocoCoverageResultSummary summary = new JacocoCoverageResultSummary();
        summary.setBranchCoverage(23.4f);
        summary.setClassCoverage(23.5f);
        summary.setComplexityScore(23.6f);
        summary.setInstructionCoverage(23.7f);
        summary.setLineCoverage(23.8f);
        summary.setMethodCoverage(23.9f);

        assertEquals(23.4f, summary.getBranchCoverage(), 0.01);
        assertEquals(23.5f, summary.getClassCoverage(), 0.01);
        assertEquals(23.6f, summary.getComplexityScore(), 0.01);
        assertEquals(23.7f, summary.getInstructionCoverage(), 0.01);
        assertEquals(23.8f, summary.getLineCoverage(), 0.01);
        assertEquals(23.9f, summary.getMethodCoverage(), 0.01);

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
        JacocoCoverageResultSummary summary = new JacocoCoverageResultSummary(null, 23.4f, 23.5f,
                23.6f, 23.7f, 23.8f, 23.9f);

        assertEquals(23.7f, summary.getBranchCoverage(), 0.01);
        assertEquals(23.6f, summary.getClassCoverage(), 0.01);
        assertEquals(23.9f, summary.getComplexityScore(), 0.01);
        assertEquals(23.8f, summary.getInstructionCoverage(), 0.01);
        assertEquals(23.4f, summary.getLineCoverage(), 0.01);
        assertEquals(23.5f, summary.getMethodCoverage(), 0.01);

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
        JacocoCoverageResultSummary orig = new JacocoCoverageResultSummary(null, 23.4f, 23.5f,
                23.6f, 23.7f, 23.8f, 23.9f);

        JacocoCoverageResultSummary summary = new JacocoCoverageResultSummary();

        assertEquals(summary, summary.addCoverageResult(orig));

        assertEquals(23.7f, summary.getBranchCoverage(), 0.01);
        assertEquals(23.6f, summary.getClassCoverage(), 0.01);
        assertEquals(23.9f, summary.getComplexityScore(), 0.01);
        assertEquals(23.8f, summary.getInstructionCoverage(), 0.01);
        assertEquals(23.4f, summary.getLineCoverage(), 0.01);
        assertEquals(23.5f, summary.getMethodCoverage(), 0.01);

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
        JacocoCoverageResultSummary orig = new JacocoCoverageResultSummary(null, 23.4f, 23.5f,
                23.6f, 23.7f, 23.8f, 23.9f);

        JacocoCoverageResultSummary summary = new JacocoCoverageResultSummary();

        summary.setCoverageResults(Collections.singletonList(orig));

        assertEquals(0f, summary.getBranchCoverage(), 0.01);
        assertEquals(0f, summary.getClassCoverage(), 0.01);
        assertEquals(0f, summary.getComplexityScore(), 0.01);
        assertEquals(0f, summary.getInstructionCoverage(), 0.01);
        assertEquals(0f, summary.getLineCoverage(), 0.01);
        assertEquals(0f, summary.getMethodCoverage(), 0.01);

        assertEquals(0f, summary.getTotalBranchCoverage(), 0.01);
        assertEquals(0f, summary.getTotalClassCoverage(), 0.01);
        assertEquals(0f, summary.getTotalComplexityScore(), 0.01);
        assertEquals(0f, summary.getTotalInstructionCoverage(), 0.01);
        assertEquals(0f, summary.getTotalLineCoverage(), 0.01);
        assertEquals(0f, summary.getTotalMethodCoverage(), 0.01);

        assertFalse(summary.getJacocoCoverageResults().isEmpty());
    }
}