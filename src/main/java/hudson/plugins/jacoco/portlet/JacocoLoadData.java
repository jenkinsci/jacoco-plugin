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
package hudson.plugins.jacoco.portlet;

import hudson.model.Job;
import hudson.model.Run;
import hudson.plugins.jacoco.JacocoBuildAction;
import hudson.plugins.jacoco.portlet.bean.JacocoCoverageResultSummary;
import hudson.plugins.jacoco.portlet.utils.Constants;
import hudson.plugins.jacoco.portlet.utils.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.joda.time.LocalDate;

/**
 * Load data of JaCoCo coverage results used by chart or grid.
 */
public final class JacocoLoadData {

  /**
   * Private constructor avoiding this class to be used in a non-static way.
   */
  private JacocoLoadData() {
  }

  /**
   * Get JaCoCo coverage results of all jobs and store into a sorted
   * HashMap by date.
   *
   * @param jobs
   *        jobs of Dashboard view
   * @param daysNumber
   *          number of days
   * @return Map The sorted summaries
   */
  public static Map<LocalDate, JacocoCoverageResultSummary> loadChartDataWithinRange(List<Job<?,?>> jobs, int daysNumber) {

    Map<LocalDate, JacocoCoverageResultSummary> summaries = new HashMap<>();

    // Get the last build (last date) of the all jobs
    LocalDate lastDate = Utils.getLastDate(jobs);

    // No builds
    if (lastDate == null) {
      return null;
    }

    // Get the first date from last build date minus number of days
    LocalDate firstDate = lastDate.minusDays(daysNumber);

    // For each job, get JaCoCo coverage results according with
    // date range (last build date minus number of days)
    for (Job<?,?> job : jobs) {

      Run<?,?> run = job.getLastCompletedBuild();

      if (null != run) {
        LocalDate runDate = new LocalDate(run.getTimestamp());

        while (runDate.isAfter(firstDate)) {

          summarize(summaries, run, runDate, job);

          run = run.getPreviousBuild();
          while (run != null && run.isBuilding()) {
            run = run.getPreviousBuild();
          }

          if (null == run) {
            break;
          }

          runDate = new LocalDate(run.getTimestamp());

        }
      }
    }

    // Sorting by date, ascending order
    return new TreeMap<>(summaries);

  }

  /**
   * Summarize JaCoCo coverage results.
   *
   * @param summaries
   *          a Map of JacocoCoverageResultSummary objects indexed by
   *          dates
   * @param run
   *          the build which will provide information about the
   *          coverage result
   * @param runDate
   *          the date on which the build was performed
   * @param job
   *          job from the DashBoard Portlet view
   */
  private static void summarize(Map<LocalDate, JacocoCoverageResultSummary> summaries, Run<?,?> run, LocalDate runDate, Job<?,?> job) {

    JacocoCoverageResultSummary jacocoCoverageResult = getResult(run);

    // Retrieve JaCoCo information for informed date
    JacocoCoverageResultSummary jacocoCoverageResultSummary = summaries.get(runDate);

    // Consider the last result of each
    // job date (if there are many builds for the same date). If not
    // exists, the JaCoCo coverage data must be added. If exists
    // JaCoCo coverage data for the same date but it belongs to other
    // job, sum the values.
    if (jacocoCoverageResultSummary == null) {
      jacocoCoverageResultSummary = new JacocoCoverageResultSummary();
      jacocoCoverageResultSummary.addCoverageResult(jacocoCoverageResult);
      jacocoCoverageResultSummary.setJob(job);
    } else {

      // Check if exists JaCoCo data for same date and job
      List<JacocoCoverageResultSummary> listResults = jacocoCoverageResultSummary.getJacocoCoverageResults();
      boolean found = false;

      for (JacocoCoverageResultSummary item : listResults) {
        if ((null != item.getJob()) && (null != item.getJob().getName()) && (null != job)) {
          if (item.getJob().getName().equals(job.getName())) {
            found = true;
            break;
          }
        }
      }

      if (!found) {
        jacocoCoverageResultSummary.addCoverageResult(jacocoCoverageResult);
        jacocoCoverageResultSummary.setJob(job);
      }
    }

    summaries.put(runDate, jacocoCoverageResultSummary);
  }

  /**
   * Get the JaCoCo coverage result for a specific run.
   *
   * @param run
   *          a job execution
   * @return JaCoCoCoverageTestResult the coverage result
   */
  public static JacocoCoverageResultSummary getResult(Run<?,?> run) {
    JacocoBuildAction jacocoAction = run.getAction(JacocoBuildAction.class);

    BigDecimal classCoverage = new BigDecimal(0).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
    BigDecimal lineCoverage = new BigDecimal(0).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
    BigDecimal methodCoverage = new BigDecimal(0).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
    BigDecimal branchCoverage = new BigDecimal(0).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
    BigDecimal instructionCoverage = new BigDecimal(0).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
    BigDecimal complexityScore = new BigDecimal(0).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);

    if (jacocoAction != null) {
      if (null != jacocoAction.getClassCoverage()) {
        classCoverage = jacocoAction.getClassCoverage().getPercentageBigDecimal();
      }
      if (null != jacocoAction.getLineCoverage()) {
        lineCoverage = jacocoAction.getLineCoverage().getPercentageBigDecimal();
      }
      if (null != jacocoAction.getMethodCoverage()) {
        methodCoverage = jacocoAction.getMethodCoverage().getPercentageBigDecimal();
      }
      if (null != jacocoAction.getBranchCoverage()) {
        branchCoverage = jacocoAction.getBranchCoverage().getPercentageBigDecimal();
      }
      if (null != jacocoAction.getInstructionCoverage()) {
        instructionCoverage = jacocoAction.getInstructionCoverage().getPercentageBigDecimal();
      }
      if (null != jacocoAction.getComplexityScore()) {
        complexityScore = jacocoAction.getComplexityScore().getPercentageBigDecimal();
      }
    }
    return new JacocoCoverageResultSummary(
        run.getParent(), lineCoverage, methodCoverage, classCoverage,
        branchCoverage, instructionCoverage, complexityScore);
  }

  /**
   * Summarize the last coverage results of all jobs. If a job doesn't
   * include any coverage, add zero.
   *
   * @param jobs
   *          a final Collection of Job objects
   * @return JacocoCoverageResultSummary the result summary
   */
  public static JacocoCoverageResultSummary getResultSummary(final Collection<Job<?,?>> jobs) {
    JacocoCoverageResultSummary summary = new JacocoCoverageResultSummary();

    for (Job<?,?> job : jobs) {

      BigDecimal classCoverage = new BigDecimal(0).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
      BigDecimal lineCoverage = new BigDecimal(0).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
      BigDecimal methodCoverage = new BigDecimal(0).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
      BigDecimal branchCoverage = new BigDecimal(0).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
      BigDecimal instructionCoverage = new BigDecimal(0).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
      BigDecimal complexityScore = new BigDecimal(0).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);

      Run<?,?> run = job.getLastSuccessfulBuild();

      if (run != null) {

        JacocoBuildAction jacocoAction = job.getLastSuccessfulBuild().getAction(JacocoBuildAction.class);

        if (null != jacocoAction) {
          if (null != jacocoAction.getClassCoverage()) {
            classCoverage = jacocoAction.getClassCoverage().getPercentageBigDecimal();
          }
          if (null != jacocoAction.getLineCoverage()) {
            lineCoverage = jacocoAction.getLineCoverage().getPercentageBigDecimal();
          }

          if (null != jacocoAction.getMethodCoverage()) {
            methodCoverage = jacocoAction.getMethodCoverage().getPercentageBigDecimal();
          }

          if (null != jacocoAction.getBranchCoverage()) {
            branchCoverage = jacocoAction.getBranchCoverage().getPercentageBigDecimal();
          }

          if (null != jacocoAction.getInstructionCoverage()) {
            instructionCoverage = jacocoAction.getInstructionCoverage().getPercentageBigDecimal();
          }

          if (null != jacocoAction.getComplexityScore()) {
            complexityScore = jacocoAction.getComplexityScore().getPercentageBigDecimal();
          }
        }
      }
      summary.addCoverageResult(new JacocoCoverageResultSummary(
          job, lineCoverage, methodCoverage, classCoverage, branchCoverage, instructionCoverage, complexityScore));
    }
    return summary;
  }
}
