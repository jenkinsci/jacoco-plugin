package hudson.plugins.jacoco.portlet.utils;

import hudson.model.Result;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * Created by Aditi Rajawat on 2/10/17.
 */
public class UtilsComparisonTest {

    // Test if boolean results for big decimal comparison utility method are correct
    @Test
    public void isEqualOrLessThanTest(){
        BigDecimal first = new BigDecimal(10.09090).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
        BigDecimal second = new BigDecimal(10.099).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
        BigDecimal third =  new BigDecimal(10.099).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
        Assert.assertTrue(Utils.isEqualOrLessThan(first, second));
        Assert.assertTrue(Utils.isEqualOrLessThan(second, third));
        Assert.assertFalse(Utils.isEqualOrLessThan(second, first));
    }

    // Test if logical AND operation on Result data type works as expected
    @Test
    public void logicalAndOfResultsTest(){
        Result result_1 = Result.SUCCESS;
        Result result_2 = Result.UNSTABLE;
        Result result_3 = Result.FAILURE;
        Assert.assertEquals("Success AND Success = Success", "SUCCESS", Utils.applyLogicalAnd(result_1, result_1).toString());
        Assert.assertEquals("Unstable AND Unstable = Unstable", "UNSTABLE", Utils.applyLogicalAnd(result_2, result_2).toString());
        Assert.assertEquals("Unstable AND Success = Unstable", "UNSTABLE", Utils.applyLogicalAnd(result_2, result_1).toString());
        Assert.assertEquals("Success AND Unstable = Unstable", "UNSTABLE", Utils.applyLogicalAnd(result_1, result_2).toString());
        Assert.assertEquals("Failure AND Success = Failure", "FAILURE", Utils.applyLogicalAnd(result_3, result_1).toString());
        Assert.assertEquals("Success AND Failure = Failure", "FAILURE", Utils.applyLogicalAnd(result_1, result_3).toString());
        Assert.assertEquals("Failure AND Unstable = Failure", "FAILURE", Utils.applyLogicalAnd(result_3, result_2).toString());
        Assert.assertEquals("Unstable AND Failure = Failure", "FAILURE", Utils.applyLogicalAnd(result_2, result_3).toString());
        Assert.assertEquals("Failure AND Failure = Failure", "FAILURE", Utils.applyLogicalAnd(result_3, result_3).toString());
    }
}
