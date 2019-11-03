package hudson.plugins.jacoco;

import hudson.plugins.jacoco.model.Coverage;
import hudson.plugins.jacoco.model.CoverageElement;
import org.junit.Test;

import static org.junit.Assert.*;

public class JacocoHealthReportThresholdsTest {

    @Test
    public void ensureValidWithAllZero() {
        JacocoHealthReportThresholds th = new JacocoHealthReportThresholds(
                0, 0,
                0, 0,
                0, 0,
                0, 0,
                0, 0,
                0 ,0);
        assertEquals(0, th.getMinClass());
        assertEquals(0, th.getMaxClass());
        assertEquals(0, th.getMinMethod());
        assertEquals(0, th.getMaxMethod());
        assertEquals(0, th.getMinLine());
        assertEquals(0, th.getMaxLine());
        assertEquals(0, th.getMinBranch());
        assertEquals(0, th.getMaxBranch());
        assertEquals(0, th.getMinInstruction());
        assertEquals(0, th.getMaxInstruction());
        assertEquals(0, th.getMinComplexity());
        assertEquals(0, th.getMaxComplexity());

        assertNotNull(th.toString());
    }

    @Test
    public void ensureValidWithMaxZero() {
        JacocoHealthReportThresholds th = new JacocoHealthReportThresholds(
                1, 0,
                2, 0,
                3, 0,
                4, 0,
                5, 0,
                6 ,0);

        // currently all zero because "min <= max" is enforced
        assertEquals(0, th.getMinClass());
        assertEquals(0, th.getMaxClass());
        assertEquals(0, th.getMinMethod());
        assertEquals(0, th.getMaxMethod());
        assertEquals(0, th.getMinLine());
        assertEquals(0, th.getMaxLine());
        assertEquals(0, th.getMinBranch());
        assertEquals(0, th.getMaxBranch());
        assertEquals(0, th.getMinInstruction());
        assertEquals(0, th.getMaxInstruction());
        assertEquals(0, th.getMinComplexity());
        assertEquals(0, th.getMaxComplexity());

        assertNotNull(th.toString());
    }

    @Test
    public void ensureValidWithValues() {
        JacocoHealthReportThresholds th = new JacocoHealthReportThresholds(
                1, 2,
                3, 4,
                5, 6,
                7, 8,
                9, 10,
                11 ,12);
        assertEquals(1, th.getMinClass());
        assertEquals(2, th.getMaxClass());
        assertEquals(3, th.getMinMethod());
        assertEquals(4, th.getMaxMethod());
        assertEquals(5, th.getMinLine());
        assertEquals(6, th.getMaxLine());
        assertEquals(7, th.getMinBranch());
        assertEquals(8, th.getMaxBranch());
        assertEquals(9, th.getMinInstruction());
        assertEquals(10, th.getMaxInstruction());
        assertEquals(11, th.getMinComplexity());
        assertEquals(12, th.getMaxComplexity());

        assertNotNull(th.toString());
    }

    @Test
    public void ensureValidValuesTooSmall() {
        JacocoHealthReportThresholds th = new JacocoHealthReportThresholds(
                -1, -2,
                -3, -4,
                -5, -6,
                -7, -8,
                -9, -10,
                -11 ,-12);
        assertEquals(0, th.getMinClass());
        assertEquals(0, th.getMaxClass());
        assertEquals(0, th.getMinMethod());
        assertEquals(0, th.getMaxMethod());
        assertEquals(0, th.getMinLine());
        assertEquals(0, th.getMaxLine());
        assertEquals(0, th.getMinBranch());
        assertEquals(0, th.getMaxBranch());
        assertEquals(0, th.getMinInstruction());
        assertEquals(0, th.getMaxInstruction());
        assertEquals(0, th.getMinComplexity());
        assertEquals(0, th.getMaxComplexity());

        assertNotNull(th.toString());
    }

    @Test
    public void ensureValidValuesTooLarge() {
        JacocoHealthReportThresholds th = new JacocoHealthReportThresholds(
                101, 102,
                103, 104,
                105, 106,
                107, 108,
                109, 110,
                111 ,112);
        assertEquals(100, th.getMinClass());
        assertEquals(100, th.getMaxClass());
        assertEquals(100, th.getMinMethod());
        assertEquals(100, th.getMaxMethod());
        assertEquals(100, th.getMinLine());
        assertEquals(100, th.getMaxLine());
        assertEquals(100, th.getMinBranch());
        assertEquals(100, th.getMaxBranch());
        assertEquals(100, th.getMinInstruction());
        assertEquals(100, th.getMaxInstruction());
        assertEquals(100, th.getMinComplexity());
        assertEquals(100, th.getMaxComplexity());

        assertNotNull(th.toString());
    }

    @Test
    public void ensureValidWithMinValuesTooHigh() {
        JacocoHealthReportThresholds th = new JacocoHealthReportThresholds(
                21, 2,
                23, 4,
                25, 6,
                27, 8,
                29, 10,
                211 ,12);
        assertEquals(2, th.getMinClass());
        assertEquals(2, th.getMaxClass());
        assertEquals(4, th.getMinMethod());
        assertEquals(4, th.getMaxMethod());
        assertEquals(6, th.getMinLine());
        assertEquals(6, th.getMaxLine());
        assertEquals(8, th.getMinBranch());
        assertEquals(8, th.getMaxBranch());
        assertEquals(10, th.getMinInstruction());
        assertEquals(10, th.getMaxInstruction());
        assertEquals(12, th.getMinComplexity());
        assertEquals(12, th.getMaxComplexity());

        assertNotNull(th.toString());
    }

    @Test
    public void testSetters() {
        JacocoHealthReportThresholds th = new JacocoHealthReportThresholds(
                0, 0,
                0, 0,
                0, 0,
                0, 0,
                0, 0,
                0 ,0);

        th.setMinClass(1);
        th.setMaxClass(2);
        th.setMinMethod(3);
        th.setMaxMethod(4);
        th.setMinLine(5);
        th.setMaxLine(6);
        th.setMinBranch(7);
        th.setMaxBranch(8);
        th.setMinInstruction(9);
        th.setMaxInstruction(10);
        th.setMinComplexity(11);
        th.setMaxComplexity(12);

        assertEquals(1, th.getMinClass());
        assertEquals(2, th.getMaxClass());
        assertEquals(3, th.getMinMethod());
        assertEquals(4, th.getMaxMethod());
        assertEquals(5, th.getMinLine());
        assertEquals(6, th.getMaxLine());
        assertEquals(7, th.getMinBranch());
        assertEquals(8, th.getMaxBranch());
        assertEquals(9, th.getMinInstruction());
        assertEquals(10, th.getMaxInstruction());
        assertEquals(11, th.getMinComplexity());
        assertEquals(12, th.getMaxComplexity());
    }

    @Test
    public void testGetResultByTypeAndRatioBetween() {
        JacocoHealthReportThresholds th = new JacocoHealthReportThresholds(
                1, 2,
                1, 2,
                1, 2,
                1, 2,
                1, 2,
                1, 2);

        Coverage ratio = new Coverage(99, 1);
        ratio.setType(CoverageElement.Type.CLASS);
        assertEquals(JacocoHealthReportThresholds.RESULT.BETWEENMINMAX, th.getResultByTypeAndRatio(ratio));
        ratio.setType(CoverageElement.Type.METHOD);
        assertEquals(JacocoHealthReportThresholds.RESULT.BETWEENMINMAX, th.getResultByTypeAndRatio(ratio));
        ratio.setType(CoverageElement.Type.LINE);
        assertEquals(JacocoHealthReportThresholds.RESULT.BETWEENMINMAX, th.getResultByTypeAndRatio(ratio));
        ratio.setType(CoverageElement.Type.BRANCH);
        assertEquals(JacocoHealthReportThresholds.RESULT.BETWEENMINMAX, th.getResultByTypeAndRatio(ratio));
        ratio.setType(CoverageElement.Type.INSTRUCTION);
        assertEquals(JacocoHealthReportThresholds.RESULT.BETWEENMINMAX, th.getResultByTypeAndRatio(ratio));
        ratio.setType(CoverageElement.Type.COMPLEXITY);
        assertEquals(JacocoHealthReportThresholds.RESULT.BETWEENMINMAX, th.getResultByTypeAndRatio(ratio));
    }

    @Test
    public void testGetResultByTypeAndRatioBelow() {
        JacocoHealthReportThresholds th = new JacocoHealthReportThresholds(
                1, 2,
                1, 2,
                1, 2,
                1, 2,
                1, 2,
                1, 2);

        Coverage ratio = new Coverage(100, 0);
        ratio.setType(CoverageElement.Type.CLASS);
        assertEquals(JacocoHealthReportThresholds.RESULT.BELOWMINIMUM, th.getResultByTypeAndRatio(ratio));
        ratio.setType(CoverageElement.Type.METHOD);
        assertEquals(JacocoHealthReportThresholds.RESULT.BELOWMINIMUM, th.getResultByTypeAndRatio(ratio));
        ratio.setType(CoverageElement.Type.LINE);
        assertEquals(JacocoHealthReportThresholds.RESULT.BELOWMINIMUM, th.getResultByTypeAndRatio(ratio));
        ratio.setType(CoverageElement.Type.BRANCH);
        assertEquals(JacocoHealthReportThresholds.RESULT.BELOWMINIMUM, th.getResultByTypeAndRatio(ratio));
        ratio.setType(CoverageElement.Type.INSTRUCTION);
        assertEquals(JacocoHealthReportThresholds.RESULT.BELOWMINIMUM, th.getResultByTypeAndRatio(ratio));
        ratio.setType(CoverageElement.Type.COMPLEXITY);
        assertEquals(JacocoHealthReportThresholds.RESULT.BELOWMINIMUM, th.getResultByTypeAndRatio(ratio));
    }

    @Test
    public void testGetResultByTypeAndRatioAbove() {
        JacocoHealthReportThresholds th = new JacocoHealthReportThresholds(
                1, 2,
                1, 2,
                1, 2,
                1, 2,
                1, 2,
                1, 2);

        Coverage ratio = new Coverage(0, 100);
        ratio.setType(CoverageElement.Type.CLASS);
        assertEquals(JacocoHealthReportThresholds.RESULT.ABOVEMAXIMUM, th.getResultByTypeAndRatio(ratio));
        ratio.setType(CoverageElement.Type.METHOD);
        assertEquals(JacocoHealthReportThresholds.RESULT.ABOVEMAXIMUM, th.getResultByTypeAndRatio(ratio));
        ratio.setType(CoverageElement.Type.LINE);
        assertEquals(JacocoHealthReportThresholds.RESULT.ABOVEMAXIMUM, th.getResultByTypeAndRatio(ratio));
        ratio.setType(CoverageElement.Type.BRANCH);
        assertEquals(JacocoHealthReportThresholds.RESULT.ABOVEMAXIMUM, th.getResultByTypeAndRatio(ratio));
        ratio.setType(CoverageElement.Type.INSTRUCTION);
        assertEquals(JacocoHealthReportThresholds.RESULT.ABOVEMAXIMUM, th.getResultByTypeAndRatio(ratio));
        ratio.setType(CoverageElement.Type.COMPLEXITY);
        assertEquals(JacocoHealthReportThresholds.RESULT.ABOVEMAXIMUM, th.getResultByTypeAndRatio(ratio));
    }
}
