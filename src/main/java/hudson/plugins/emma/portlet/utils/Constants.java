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
package hudson.plugins.emma.portlet.utils;

/**
 * Defines the variables with pre-defined values.
 */
public final class Constants {
  /**
   * Private constructor: class contains only static methods.
   */
  private Constants() {
  }

  /**
   * Default width of the Graph.
   */
  public static final int DEFAULT_WIDTH = 500;

  /**
   * Default height of the Graph.
   */
  public static final int DEFAULT_HEIGHT = 250;

  /**
   * Default number of days of the Graph.
   */
  public static final int DEFAULT_DAYS_NUMBER = 30;

  /**
   * Number of milliseconds in a day.
   */
  public static final long MILLISECONDS_IN_A_DAY = 24L * 60 * 60 * 1000;

  /**
   * Spaces around the graph - top.
   */
  public static final double TOP_INSET = 5.0;

  /**
   * Spaces around the graph - left.
   */
  public static final double LEFT_INSET = 0.0;

  /**
   * Spaces around the graph - bottom.
   */
  public static final double BOTTOM_INSET = 5.0;

  /**
   * Spaces around the graph - right.
   */
  public static final double RIGHT_INSET = 0.0;

  /**
   * The gap between columns of the chart.
   */
  public static final double COLUMNS_GAP = 0.2;

  /**
   * Lower margin.
   */
  public static final double LOWER_MARGIN = 0.0;

  /**
   * Upper margin.
   */
  public static final double UPPER_MARGIN = 0.0;

  /**
   * The default foreground alpha transparency.
   */
  public static final float FOREGROUND_ALPHA = 0.8f;

  /**
   * The chart axis label.
   */
  public static final String AXIS_LABEL = "Days";

  /**
   * The chart axis label value.
   */
  public static final String AXIS_LABEL_VALUE = "Coverage(%)";

  /**
   * The chart upper bound value.
   */
  public static final int UPPER_BOUND = 100;

  /**
   * The chart lower bound value.
   */
  public static final int LOWER_BOUND = 0;

  /**
   * The chart line thickness value.
   */
  public static final float LINE_THICKNESS = 3.5f;

  /**
   * The chart default margin value.
   */
  public static final double DEFAULT_MARGIN = 0.0;
}
