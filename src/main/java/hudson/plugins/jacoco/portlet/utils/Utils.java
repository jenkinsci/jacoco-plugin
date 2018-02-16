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
package hudson.plugins.jacoco.portlet.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.List;

import hudson.model.Job;
import hudson.model.Result;
import hudson.model.Run;

/**
 * Defines common methods that are used for the whole project.
 */
public final class Utils {

  /**
   * Private constructor: class contains only static methods.
   */
  private Utils() {
  }

  /**
   * Validate chart attributes returning a valid value to the object.
   *
   * @param attribute
   *          the attribute: width, height, number of days
   * @param defaultValue
   *          default value for the attribute
   * @return int attribute valid value
   */
  public static int validateChartAttributes(String attribute, int defaultValue) {

    // If user fills the attributes with negative, empty or not number
    // values, Hudson will not show an invalid message, it will assume
    // default values

    if (attribute != null) {
      if (attribute.equals("") || attribute.equals("0")) {
        return defaultValue;
      }

      // Check if attribute value is a number
      try {
        int validAttributeValue = Integer.parseInt(attribute);
        // Attribute value is a number - check if it is negative
        if (validAttributeValue < 0) {
          return defaultValue;
        }
        return validAttributeValue;
      } catch (NumberFormatException e) {
        return defaultValue;
      }
    }

    return defaultValue;
  }

  /**
   * For the given list of jobs, this will search all jobs and return
   * the last run date of all.
   *
   * @param jobs
   *          a list of jobs from the DashBoard Portlet view
   * @return LocalDate the last date of all jobs that belogs to
   *         Dashboard View.
   */
  public static Calendar getLastDate(List<Job<?,?>> jobs) {
    Calendar lastDate = null;
    for (Job<?,?> job : jobs) {
      Run<?,?> lastRun = job.getLastCompletedBuild();
      if (lastRun != null) {
          Calendar date = lastRun.getTimestamp();
        if (lastDate == null) {
          lastDate = date;
        }
        if (date.after(lastDate)) {
          lastDate = date;
        }
      }
    }
    return lastDate;
  }

  /**
   * Method for rounding float values according to the requested mode.
   *
   * @param scale
   *          the rounding scale
   * @param roundingMode
   *          the rounding direction @see java.math.RoundingMode
   * @param value
   *          the value to be rounded
   * @return the rounded value
   */
  public static float roundFloat(int scale, RoundingMode roundingMode, float value) {
    BigDecimal bigDecimal = new BigDecimal(value);
    bigDecimal = bigDecimal.setScale(scale, roundingMode);
    return bigDecimal.floatValue();
  }


  public static int nthOccurrence(String str, char c, int n) {
	    int pos = str.indexOf(c, 0);
	    while (n-- > 0 && pos != -1)
	        pos = str.indexOf(c, pos+1);
	    return pos;
  }

  /** Logical AND operation of Jenkins build results:
   * Success AND Success = Success
   * Unstable AND Unstable = Unstable
   * Failure AND Failure = Failure
   * X AND Failure = Failure, Failure AND X = Failure, X = Success/Unstable/Failure
   * Y AND Unstable = Unstable, Unstable AND Y = Unstable, Y = Success/Unstable
   * @param op1 first result 
   * @param op2 second result
   * @return Logical AND operation of {@code op1 AND op2}
   */
  public static Result applyLogicalAnd(Result op1, Result op2){

    if(op1.toString().equals("FAILURE") || op2.toString().equals("FAILURE"))
      return Result.FAILURE;

    if(op1.toString().equals("UNSTABLE") || op2.toString().equals("UNSTABLE"))
      return Result.UNSTABLE;

    if(op1.toString().equals("SUCCESS") && op2.toString().equals("SUCCESS"))
      return Result.SUCCESS;

    return Result.FAILURE;

  }

}
