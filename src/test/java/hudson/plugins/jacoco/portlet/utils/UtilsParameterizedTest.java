package hudson.plugins.jacoco.portlet.utils;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static junit.framework.Assert.assertEquals;

/**
 * Test {@link hudson.plugins.jacoco.portlet.utils.Utils}
 * through HudsonTestCase extension.
 *
 * @author Mauro Durante Junior &lt;Mauro.Durantejunior@sonyericsson.com&gt;
 * @author Eduardo Palazzo &lt;Eduardo.Palazzo@sonyericsson.com&gt;
 */
@RunWith(value = Parameterized.class)
public class UtilsParameterizedTest {

  /**
   * The value input into the system.
   */
  private String inputValue;

  /**
   * The expected returned value.
   */
  private int expected;

  /**
   * The object containing the input and output parameters.
   * @return Collection&lt;Object[]> a Collection containing pairs of
   * input values and the respective expected value
   * returned by the method under test.
   */
  @Parameters
  public static Collection<Object[]> data() {
    final int expectedValue = 10;
    final int aValidValue = 1;
    Object[][] data = new Object[][] {
      {null, expectedValue},
      {"", expectedValue},
      {"0", expectedValue},
      {"a", expectedValue},
      {"-1", expectedValue},
      {"1", aValidValue}, };
    return Arrays.asList(data);
  }

  /**
   * Constructor to be used in this parameterized test.
   * @param inputValue the value input into the system
   * @param expected the expected returned value
   */
  public UtilsParameterizedTest(String inputValue, int expected) {
    this.inputValue = inputValue;
    this.expected = expected;
  }

  /**
   * This method tests validateChartAttributes() behavior.
   * Tests {@link hudson.plugins.jacoco.portlet.utils.Utils#validateChartAttributes(java.lang.String, int) }.
   */
  @Test
  public void testValidateCharAttributes() {
    assertEquals(expected, Utils.validateChartAttributes(inputValue, expected));
  }
}
