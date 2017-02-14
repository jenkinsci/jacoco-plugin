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

/**
 * @author Allyn Pierre (Allyn.GreyDeAlmeidaLimaPierre@sonyericsson.com)
 * @author Eduardo Palazzo (Eduardo.Palazzo@sonyericsson.com)
 * @author Mauro Durante (Mauro.DuranteJunior@sonyericsson.com)
 */
package hudson.plugins.jacoco.portlet.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import hudson.model.Job;
import hudson.plugins.jacoco.portlet.utils.Constants;
import hudson.plugins.jacoco.portlet.utils.Utils;

/**
 * Summary of the Jacoco Coverage result.
 * Modified by Aditi Rajawat for build-over-build feature
 * Changed coverage type from float to Big Decimal to enable delta coverage calculation upto scale of 6
 */
public class JacocoCoverageResultSummary {

  /**
   * The related job.
   */
  private Job<?,?> job;

  /**
   * Line coverage percentage.
   */
  private BigDecimal lineCoverage;

  /**
   * Method coverage percentage.
   */
  private BigDecimal methodCoverage;

  /**
   * Class coverage percentage.
   */
  private BigDecimal classCoverage;

  /**
   * Block coverage percentage.
   */
  private BigDecimal instructionCoverage;

  /**
   * Block coverage percentage.
   */
  private BigDecimal branchCoverage;

  /**
   * Complexity score (not a percentage).
   */
  private BigDecimal complexityScore;

  private List<JacocoCoverageResultSummary> coverageResults = new ArrayList<JacocoCoverageResultSummary>();

  /**
   * Default Constructor.
   */
  public JacocoCoverageResultSummary() {
    this.lineCoverage = new BigDecimal(0).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
    this.methodCoverage = new BigDecimal(0).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
    this.classCoverage = new BigDecimal(0).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
    this.instructionCoverage = new BigDecimal(0).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
    this.branchCoverage = new BigDecimal(0).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
    this.complexityScore = new BigDecimal(0).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
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
   *          coverage percentage
   */
  public JacocoCoverageResultSummary(Job<?,?> job, BigDecimal lineCoverage, BigDecimal methodCoverage,
                                     BigDecimal classCoverage, BigDecimal branchCoverage, BigDecimal instructionCoverage, BigDecimal complexityScore) {
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

    this.lineCoverage = this.lineCoverage.add(coverageResult.getLineCoverage());
    this.methodCoverage = this.methodCoverage.add(coverageResult.getMethodCoverage());
    this.classCoverage = this.classCoverage.add(coverageResult.getClassCoverage());
    this.branchCoverage = this.branchCoverage.add(coverageResult.getBranchCoverage());
    this.instructionCoverage = this.instructionCoverage.add(coverageResult.getInstructionCoverage());
    this.complexityScore = this.complexityScore.add(coverageResult.getComplexityScore());

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

    float totalClass = this.getClassCoverage().floatValue() / this.getCoverageResults().size();
    totalClass = Utils.roundFLoat(1, BigDecimal.ROUND_HALF_EVEN, totalClass);
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
    
    float totalBranch = this.getBranchCoverage().floatValue() / this.getCoverageResults().size();
    totalBranch = Utils.roundFLoat(1, BigDecimal.ROUND_HALF_EVEN, totalBranch);
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
     
    float totalInstr = this.getInstructionCoverage().floatValue() / this.getCoverageResults().size();
    totalInstr = Utils.roundFLoat(1, BigDecimal.ROUND_HALF_EVEN, totalInstr);
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
     
    float totalComplex = this.getComplexityScore().floatValue() / this.getCoverageResults().size();
    totalComplex = Utils.roundFLoat(1, BigDecimal.ROUND_HALF_EVEN, totalComplex);
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
      
    float totalLine = this.getLineCoverage().floatValue() / this.getCoverageResults().size();
    totalLine = Utils.roundFLoat(1, BigDecimal.ROUND_HALF_EVEN, totalLine);
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
    
    float totalMethod = this.getMethodCoverage().floatValue() / this.getCoverageResults().size();
    totalMethod = Utils.roundFLoat(1, BigDecimal.ROUND_HALF_EVEN, totalMethod);
    return totalMethod;
  }

  /**
   * @return Job a job
   */
  public Job<?,?> getJob() {
    return job;
  }

  public BigDecimal getInstructionCoverage() {
    return instructionCoverage;
  }

  public BigDecimal getBranchCoverage() {
    return branchCoverage;
  }

  public BigDecimal getComplexityScore() {
    return complexityScore;
  }

  /**
   * @return the lineCoverage
   */
  public BigDecimal getLineCoverage() {
    return lineCoverage;
  }

  /**
   * @return the methodCoverage
   */
  public BigDecimal getMethodCoverage() {
    return methodCoverage;
  }

  /**
   * @return the classCoverage
   */
  public BigDecimal getClassCoverage() {
    return classCoverage;
  }

  /**
   * @param job
   *          the job to set
   */
  public void setJob(Job<?,?> job) {
    this.job = job;
  }

  public void setInstructionCoverage(BigDecimal instructionCoverage) {
    this.instructionCoverage = instructionCoverage;
  }

  public void setBranchCoverage(BigDecimal branchCoverage) {
    this.branchCoverage = branchCoverage;
  }

  public void setComplexityScore(BigDecimal complexityScore) {
    this.complexityScore = complexityScore;
  }

  /**
   * @param lineCoverage
   *          the lineCoverage to set
   */
  public void setLineCoverage(BigDecimal lineCoverage) {
    this.lineCoverage = lineCoverage;
  }

  /**
   * @param methodCoverage
   *          the methodCoverage to set
   */
  public void setMethodCoverage(BigDecimal methodCoverage) {
    this.methodCoverage = methodCoverage;
  }

  /**
   * @param classCoverage
   *          the classCoverage to set
   */
  public void setClassCoverage(BigDecimal classCoverage) {
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
