package hudson.plugins.jacoco.portlet.bean;

import hudson.model.Job;
import hudson.model.Run;
import hudson.plugins.jacoco.JacocoPublisher;
import hudson.plugins.jacoco.portlet.JacocoLoadData;

import java.math.BigDecimal;

/**
 * This class encapsulates actual delta coverage of current build.
 * It calculates absolute difference between coverage of last successful build and current build
 */
public class JacocoDeltaCoverageResultSummary {

    /**
     * Variables to capture delta coverage of current build
     */
    private BigDecimal instructionCoverage;

    private BigDecimal branchCoverage;

    private BigDecimal complexityCoverage;

    private BigDecimal lineCoverage;

    private BigDecimal methodCoverage;

    private BigDecimal classCoverage;

    private boolean coverageBetterThanPrevious=false;

    public JacocoDeltaCoverageResultSummary() {
    }

    // Used to extract coverage result of current and last successful build and encapsulate delta coverage values
    public static JacocoDeltaCoverageResultSummary build(Run<?,?> run){
        Job<?, ?> parent = run.getParent();
        Run<?,?> lastSuccessfulBuild = parent!=null ? parent.getLastSuccessfulBuild():null;
        JacocoCoverageResultSummary lastBuildCoverage = lastSuccessfulBuild!=null ? JacocoLoadData.getResult(lastSuccessfulBuild):new JacocoCoverageResultSummary();
        JacocoCoverageResultSummary currentBuildCoverage = JacocoLoadData.getResult(run);

        JacocoDeltaCoverageResultSummary jacocoDeltaCoverageResultSummary = new JacocoDeltaCoverageResultSummary();
        jacocoDeltaCoverageResultSummary.instructionCoverage = lastBuildCoverage.getInstructionCoverage().subtract(currentBuildCoverage.getInstructionCoverage()).abs();
        jacocoDeltaCoverageResultSummary.branchCoverage = lastBuildCoverage.getBranchCoverage().subtract(currentBuildCoverage.getBranchCoverage()).abs();
        jacocoDeltaCoverageResultSummary.complexityCoverage = lastBuildCoverage.getComplexityScore().subtract(currentBuildCoverage.getComplexityScore()).abs();
        jacocoDeltaCoverageResultSummary.lineCoverage = lastBuildCoverage.getLineCoverage().subtract(currentBuildCoverage.getLineCoverage()).abs();
        jacocoDeltaCoverageResultSummary.methodCoverage = lastBuildCoverage.getMethodCoverage().subtract(currentBuildCoverage.getMethodCoverage()).abs();
        jacocoDeltaCoverageResultSummary.classCoverage = lastBuildCoverage.getClassCoverage().subtract(currentBuildCoverage.getClassCoverage()).abs();

        if((currentBuildCoverage.getInstructionCoverage().compareTo(lastBuildCoverage.getInstructionCoverage())!= -1)
                && (currentBuildCoverage.getBranchCoverage().compareTo(lastBuildCoverage.getBranchCoverage()) != -1) && (currentBuildCoverage.getComplexityScore().compareTo(lastBuildCoverage.getComplexityScore()) != -1)
                && (currentBuildCoverage.getLineCoverage().compareTo(lastBuildCoverage.getLineCoverage()) != -1) && (currentBuildCoverage.getMethodCoverage().compareTo(lastBuildCoverage.getMethodCoverage()) != -1)
                && (currentBuildCoverage.getClassCoverage().compareTo(lastBuildCoverage.getClassCoverage()) != -1))
            // Since delta coverage is the absolute difference by definition,
            // use this flag to mark if the current coverage is bigger than the coverage of last successful build
            jacocoDeltaCoverageResultSummary.coverageBetterThanPrevious = true;

        return jacocoDeltaCoverageResultSummary;
    }

    public BigDecimal getInstructionCoverage() {
        return instructionCoverage;
    }

    public BigDecimal getBranchCoverage() {
        return branchCoverage;
    }

    public BigDecimal getComplexityCoverage() {
        return complexityCoverage;
    }

    public BigDecimal getLineCoverage() {
        return lineCoverage;
    }

    public BigDecimal getMethodCoverage() {
        return methodCoverage;
    }

    public BigDecimal getClassCoverage() {
        return classCoverage;
    }

    public boolean isCoverageBetterThanPrevious() {
        return coverageBetterThanPrevious;
    }

    public void setInstructionCoverage(BigDecimal instructionCoverage) {
        this.instructionCoverage = instructionCoverage;
    }

    public void setBranchCoverage(BigDecimal branchCoverage) {
        this.branchCoverage = branchCoverage;
    }

    public void setComplexityCoverage(BigDecimal complexityCoverage) {
        this.complexityCoverage = complexityCoverage;
    }

    public void setLineCoverage(BigDecimal lineCoverage) {
        this.lineCoverage = lineCoverage;
    }

    public void setMethodCoverage(BigDecimal methodCoverage) {
        this.methodCoverage = methodCoverage;
    }

    public void setClassCoverage(BigDecimal classCoverage) {
        this.classCoverage = classCoverage;
    }

    public void setCoverageBetterThanPrevious(boolean coverageBetterThanPrevious) {
        this.coverageBetterThanPrevious = coverageBetterThanPrevious;
    }

    @Override
    public String toString() {
        return "JacocoDeltaCoverageResultSummary [" +
                "instructionCoverage=" + instructionCoverage.toString() +
                ", branchCoverage=" + branchCoverage.toString() +
                ", complexityCoverage=" + complexityCoverage.toString() +
                ", lineCoverage=" + lineCoverage.toString() +
                ", methodCoverage=" + methodCoverage.toString() +
                ", classCoverage=" + classCoverage.toString() +
                ", coverageBetterThanPrevious=" + coverageBetterThanPrevious +
                ']';
    }
}
