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
package hudson.plugins.emma.portlet.bean;

import hudson.model.Job;
import hudson.plugins.emma.portlet.utils.Utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Summary of the EMMA Coverage result.
 */
public class EmmaCoverageResultSummary {

  /**
   * The related job.
   */
  private Job job;

  /**
   * Block coverage percentage.
   */
  private float blockCoverage;

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

  private List<EmmaCoverageResultSummary> coverageResults = new ArrayList<EmmaCoverageResultSummary>();

  /**
   * Default Constructor.
   */
  public EmmaCoverageResultSummary() {
  }

  /**
   * Constructor with parameters.
   *
   * @param job
   *          the related Job
   * @param blockCoverage
   *          block coverage percentage
   * @param lineCoverage
   *          line coverage percentage
   * @param methodCoverage
   *          method coverage percentage
   * @param classCoverage
   *          coverage percentage
   */
  public EmmaCoverageResultSummary(Job job, float blockCoverage, float lineCoverage, float methodCoverage,
    float classCoverage) {
    this.job = job;
    this.blockCoverage = blockCoverage;
    this.lineCoverage = lineCoverage;
    this.methodCoverage = methodCoverage;
    this.classCoverage = classCoverage;
  }

  /**
   * Add a coverage result.
   *
   * @param coverageResult
   *          a coverage result
   * @return EmmaCoverageResultSummary summary of the EMMA coverage
   *         result
   */
  public EmmaCoverageResultSummary addCoverageResult(EmmaCoverageResultSummary coverageResult) {

    this.setBlockCoverage(this.getBlockCoverage() + coverageResult.getBlockCoverage());
    this.setLineCoverage(this.getLineCoverage() + coverageResult.getLineCoverage());
    this.setMethodCoverage(this.getMethodCoverage() + coverageResult.getMethodCoverage());
    this.setClassCoverage(this.getClassCoverage() + coverageResult.getClassCoverage());

    getCoverageResults().add(coverageResult);

    return this;
  }

  /**
   * Get list of EmmaCoverageResult objects.
   *
   * @return List a List of EmmaCoverageResult objects
   */
  public List<EmmaCoverageResultSummary> getEmmaCoverageResults() {
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
    } else {
      float totalClass = this.getClassCoverage() / this.getCoverageResults().size();
      totalClass = Utils.roundFLoat(1, BigDecimal.ROUND_HALF_EVEN, totalClass);
      return totalClass;
    }
  }

  /**
   * Getter of the total of block coverage.
   *
   * @return float the total of block coverage.
   */
  public float getTotalBlockCoverage() {
    if (this.getCoverageResults().size() <= 0) {
      return 0.0f;
    } else {
      float totalBlock = this.getBlockCoverage() / this.getCoverageResults().size();
      totalBlock = Utils.roundFLoat(1, BigDecimal.ROUND_HALF_EVEN, totalBlock);
      return totalBlock;
    }
  }

  /**
   * Getter of the total of line coverage.
   *
   * @return float the total of line coverage.
   */
  public float getTotalLineCoverage() {
    if (this.getCoverageResults().size() <= 0) {
      return 0.0f;
    } else {
      float totalLine = this.getLineCoverage() / this.getCoverageResults().size();
      totalLine = Utils.roundFLoat(1, BigDecimal.ROUND_HALF_EVEN, totalLine);
      return totalLine;
    }
  }

  /**
   * Getter of the total of method coverage.
   *
   * @return float the total of method coverage.
   */
  public float getTotalMethodCoverage() {
    if (this.getCoverageResults().size() <= 0) {
      return 0.0f;
    } else {
      float totalMethod = this.getMethodCoverage() / this.getCoverageResults().size();
      totalMethod = Utils.roundFLoat(1, BigDecimal.ROUND_HALF_EVEN, totalMethod);
      return totalMethod;
    }
  }

  /**
   * @return Job a job
   */
  public Job getJob() {
    return job;
  }

  /**
   * @return the blockCoverage
   */
  public float getBlockCoverage() {
    return blockCoverage;
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
  public void setJob(Job job) {
    this.job = job;
  }

  /**
   * @param blockCoverage
   *          the blockCoverage to set
   */
  public void setBlockCoverage(float blockCoverage) {
    this.blockCoverage = blockCoverage;
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
  public List<EmmaCoverageResultSummary> getCoverageResults() {
    return coverageResults;
  }

  /**
   * @param coverageResults
   *          the list of coverage results to set
   */
  public void setCoverageResults(List<EmmaCoverageResultSummary> coverageResults) {
    this.coverageResults = coverageResults;
  }
}
