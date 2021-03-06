package hudson.plugins.jacoco.portlet.bean;

import hudson.model.Job;
import hudson.model.Run;
import hudson.plugins.jacoco.portlet.JacocoLoadData;

/**
 * This class encapsulates actual delta coverage of current build.
 * It calculates absolute difference between coverage of last successful build and current build
 */
public class JacocoDeltaCoverageResultSummary {

    /**
     * Variables to capture delta coverage of current build
     */
    private float instructionCoverage;

    private float branchCoverage;

    private float complexityCoverage;

    private float lineCoverage;

    private float methodCoverage;

    private float classCoverage;

    public JacocoDeltaCoverageResultSummary() {
    }

    // Used to extract coverage result of current and last successful build and encapsulate delta coverage values
    public static JacocoDeltaCoverageResultSummary build(Run<?,?> run){
        Run<?,?> lastSuccessfulBuild = run.getParent().getLastSuccessfulBuild();
        JacocoCoverageResultSummary lastBuildCoverage = lastSuccessfulBuild!=null ? JacocoLoadData.getResult(lastSuccessfulBuild):new JacocoCoverageResultSummary();
        JacocoCoverageResultSummary currentBuildCoverage = JacocoLoadData.getResult(run);

        JacocoDeltaCoverageResultSummary jacocoDeltaCoverageResultSummary = new JacocoDeltaCoverageResultSummary();
        jacocoDeltaCoverageResultSummary.instructionCoverage = currentBuildCoverage.getInstructionCoverage() - lastBuildCoverage.getInstructionCoverage();
        jacocoDeltaCoverageResultSummary.branchCoverage = currentBuildCoverage.getBranchCoverage() - lastBuildCoverage.getBranchCoverage();
        jacocoDeltaCoverageResultSummary.complexityCoverage = currentBuildCoverage.getComplexityScore() - lastBuildCoverage.getComplexityScore();
        jacocoDeltaCoverageResultSummary.lineCoverage = currentBuildCoverage.getLineCoverage() - lastBuildCoverage.getLineCoverage();
        jacocoDeltaCoverageResultSummary.methodCoverage = currentBuildCoverage.getMethodCoverage() - lastBuildCoverage.getMethodCoverage();
        jacocoDeltaCoverageResultSummary.classCoverage = currentBuildCoverage.getClassCoverage() - lastBuildCoverage.getClassCoverage();

        return jacocoDeltaCoverageResultSummary;
    }

    public float getInstructionCoverage() {
        return instructionCoverage;
    }

    public float getBranchCoverage() {
        return branchCoverage;
    }

    public float getComplexityCoverage() {
        return complexityCoverage;
    }

    public float getLineCoverage() {
        return lineCoverage;
    }

    public float getMethodCoverage() {
        return methodCoverage;
    }

    public float getClassCoverage() {
        return classCoverage;
    }

    public void setInstructionCoverage(float instructionCoverage) {
        this.instructionCoverage = instructionCoverage;
    }

    public void setBranchCoverage(float branchCoverage) {
        this.branchCoverage = branchCoverage;
    }

    public void setComplexityCoverage(float complexityCoverage) {
        this.complexityCoverage = complexityCoverage;
    }

    public void setLineCoverage(float lineCoverage) {
        this.lineCoverage = lineCoverage;
    }

    public void setMethodCoverage(float methodCoverage) {
        this.methodCoverage = methodCoverage;
    }

    public void setClassCoverage(float classCoverage) {
        this.classCoverage = classCoverage;
    }

    @Override
    public String toString() {
        return "JacocoDeltaCoverageResultSummary [" +
                "instructionCoverage=" + instructionCoverage +
                ", branchCoverage=" + branchCoverage +
                ", complexityCoverage=" + complexityCoverage +
                ", lineCoverage=" + lineCoverage +
                ", methodCoverage=" + methodCoverage +
                ", classCoverage=" + classCoverage +
                ']';
    }
}
