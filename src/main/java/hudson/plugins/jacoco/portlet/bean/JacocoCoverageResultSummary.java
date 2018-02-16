/*
 *  The MIT License
 *
 *  Copyright 2010 Sony Ericsson Mobile Communications. All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

/*
 * @author Allyn Pierre (Allyn.GreyDeAlmeidaLimaPierre@sonyericsson.com)
 * @author Eduardo Palazzo (Eduardo.Palazzo@sonyericsson.com)
 * @author Mauro Durante (Mauro.DuranteJunior@sonyericsson.com)
 */
package hudson.plugins.jacoco.portlet.bean;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import hudson.model.Job;
import hudson.plugins.jacoco.portlet.utils.Utils;

/**
 * Summary of the Jacoco Coverage result.
 */
public class JacocoCoverageResultSummary {

  /**
   * The related job.
   */
  private Job<?,?> job;

  /**
   * Line coverage percentage.
   */
  private float lineCoverage;

  /**
   * Method coverage percentage.
   */
  private float methodCoverage;

  /**
   * Class coverage percentage.
   */
  private float classCoverage;

  /**
   * Block coverage percentage.
   */
  private float instructionCoverage;

  /**
   * Block coverage percentage.
   */
  private float branchCoverage;

  /**
   * Complexity score (not a percentage).
   */
  private float complexityScore;

  private List<JacocoCoverageResultSummary> coverageResults = new ArrayList<>();

  /**
   * Default Constructor.
   */
  public JacocoCoverageResultSummary() {
  }

  /**
   * Constructor with parameters.
   *
   * @param job
   *          the related Job
   * @param lineCoverage
   *          line coverage percentage
   * @param methodCoverage
   *          method coverage percentage
   * @param classCoverage
   *          class coverage percentage
   * @param branchCoverage
   *          branch coverage percentage
   * @param instructionCoverage 
   *          instruction coverage percentage
   * @param complexityScore 
   *          complexity score (not a percentage)
   */
  public JacocoCoverageResultSummary(Job<?,?> job, float lineCoverage, float methodCoverage,
    float classCoverage, float branchCoverage, float instructionCoverage, float complexityScore) {
    this.job = job;
    this.lineCoverage = lineCoverage;
    this.methodCoverage = methodCoverage;
    this.classCoverage = classCoverage;
    this.branchCoverage = branchCoverage;
    this.instructionCoverage = instructionCoverage;
    this.complexityScore = complexityScore;
  }

  /**
   * Add a coverage result.
   *
   * @param coverageResult
   *          a coverage result
   * @return JacocoCoverageResultSummary summary of the Jacoco coverage
   *         result
   */
  public JacocoCoverageResultSummary addCoverageResult(JacocoCoverageResultSummary coverageResult) {

    this.setLineCoverage(this.getLineCoverage() + coverageResult.getLineCoverage());
    this.setMethodCoverage(this.getMethodCoverage() + coverageResult.getMethodCoverage());
    this.setClassCoverage(this.getClassCoverage() + coverageResult.getClassCoverage());
    this.setBranchCoverage(this.getBranchCoverage() + coverageResult.getBranchCoverage());
    this.setInstructionCoverage(this.getInstructionCoverage() + coverageResult.getInstructionCoverage());
    this.setComplexityScore(this.getComplexityScore() + coverageResult.getComplexityScore());

    getCoverageResults().add(coverageResult);

    return this;
  }

  /**
   * Get list of JacocoCoverageResult objects.
   *
   * @return List a List of JacocoCoverageResult objects
   */
  public List<JacocoCoverageResultSummary> getJacocoCoverageResults() {
    return this.getCoverageResults();
  }

  /**
   * Getter of the total of class coverage.
   *
   * @return float the total of class coverage.
   */
  public float getTotalClassCoverage() {
    if (this.getCoverageResults().size() <= 0) {
      return 0.0f;
    }

    float totalClass = this.getClassCoverage() / this.getCoverageResults().size();
    totalClass = Utils.roundFloat(1, RoundingMode.HALF_EVEN, totalClass);

    return totalClass;
  }

  /**
   * Getter of the total of block coverage.
   *
   * @return float the total of block coverage.
   */
  public float getTotalBranchCoverage() {
    if (this.getCoverageResults().size() <= 0) {
      return 0.0f;
    } 

    float totalBranch = this.getBranchCoverage() / this.getCoverageResults().size();
    totalBranch = Utils.roundFloat(1, RoundingMode.HALF_EVEN, totalBranch);

    return totalBranch;
  }

  /**
   * Getter of the total of block coverage.
   *
   * @return float the total of block coverage.
   */
  public float getTotalInstructionCoverage() {
    if (this.getCoverageResults().size() <= 0) {
      return 0.0f;
    }

    float totalInstr = this.getInstructionCoverage() / this.getCoverageResults().size();
    totalInstr = Utils.roundFloat(1, RoundingMode.HALF_EVEN, totalInstr);

    return totalInstr;
  }

  /**
   * Getter of the total of block coverage.
   *
   * @return float the total of block coverage.
   */
  public float getTotalComplexityScore() {
    if (this.getCoverageResults().size() <= 0) {
      return 0.0f;
    }

    float totalComplex = this.getComplexityScore() / this.getCoverageResults().size();
    totalComplex = Utils.roundFloat(1, RoundingMode.HALF_EVEN, totalComplex);

    return totalComplex;
  }

  /**
   * Getter of the total of line coverage.
   *
   * @return float the total of line coverage.
   */
  public float getTotalLineCoverage() {
    if (this.getCoverageResults().size() <= 0) {
      return 0.0f;
    } 

    float totalLine = this.getLineCoverage() / this.getCoverageResults().size();
    totalLine = Utils.roundFloat(1, RoundingMode.HALF_EVEN, totalLine);

    return totalLine;
  }

  /**
   * Getter of the total of method coverage.
   *
   * @return float the total of method coverage.
   */
  public float getTotalMethodCoverage() {
    if (this.getCoverageResults().size() <= 0) {
      return 0.0f;
    }

    float totalMethod = this.getMethodCoverage() / this.getCoverageResults().size();
    totalMethod = Utils.roundFloat(1, RoundingMode.HALF_EVEN, totalMethod);

    return totalMethod;
  }

  /**
   * @return Job a job
   */
  public Job<?,?> getJob() {
    return job;
  }

  public float getInstructionCoverage() {
    return instructionCoverage;
  }

  public float getBranchCoverage() {
    return branchCoverage;
  }

  public float getComplexityScore() {
    return complexityScore;
  }

  /**
   * @return the lineCoverage
   */
  public float getLineCoverage() {
    return lineCoverage;
  }

  /**
   * @return the methodCoverage
   */
  public float getMethodCoverage() {
    return methodCoverage;
  }

  /**
   * @return the classCoverage
   */
  public float getClassCoverage() {
    return classCoverage;
  }

  /**
   * @param job
   *          the job to set
   */
  public void setJob(Job<?,?> job) {
    this.job = job;
  }

  public void setInstructionCoverage(float instructionCoverage) {
    this.instructionCoverage = instructionCoverage;
  }

  public void setBranchCoverage(float branchCoverage) {
    this.branchCoverage = branchCoverage;
  }

  public void setComplexityScore(float complexityScore) {
    this.complexityScore = complexityScore;
  }

  /**
   * @param lineCoverage
   *          the lineCoverage to set
   */
  public void setLineCoverage(float lineCoverage) {
    this.lineCoverage = lineCoverage;
  }

  /**
   * @param methodCoverage
   *          the methodCoverage to set
   */
  public void setMethodCoverage(float methodCoverage) {
    this.methodCoverage = methodCoverage;
  }

  /**
   * @param classCoverage
   *          the classCoverage to set
   */
  public void setClassCoverage(float classCoverage) {
    this.classCoverage = classCoverage;
  }

  /**
   * @return a list of coverage results
   */
  public List<JacocoCoverageResultSummary> getCoverageResults() {
    return coverageResults;
  }

  /**
   * @param coverageResults
   *          the list of coverage results to set
   */
  public void setCoverageResults(List<JacocoCoverageResultSummary> coverageResults) {
    this.coverageResults = coverageResults;
  }
}
