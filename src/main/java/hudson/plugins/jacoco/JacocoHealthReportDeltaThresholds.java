package hudson.plugins.jacoco;

import hudson.plugins.jacoco.portlet.utils.Constants;

import java.math.BigDecimal;

/**
 * Created by Aditi Rajawat on 2/9/17.
 * This class encapsulates delta thresholds configured by the user.
 * The threshold values are interpreted as percentages
 */
public class JacocoHealthReportDeltaThresholds {

    /**
     * Variables to hold delta threshold values for different types of percentage coverages
     */
    private BigDecimal deltaInstruction;
    private BigDecimal deltaBranch;
    private BigDecimal deltaComplexity;
    private BigDecimal deltaLine;
    private BigDecimal deltaMethod;
    private BigDecimal deltaClass;

    public JacocoHealthReportDeltaThresholds() {
    }

    // Constructor used for bounding user-configured string threshold values to corresponding big decimal thresholds
    public JacocoHealthReportDeltaThresholds(String deltaInstruction, String deltaBranch, String deltaComplexity, String deltaLine, String deltaMethod, String deltaClass){
        this.deltaInstruction = deltaInstruction!=null ? new BigDecimal(deltaInstruction).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP) : new BigDecimal("0").setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
        this.deltaBranch = deltaBranch!=null ? new BigDecimal(deltaBranch).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP) : new BigDecimal("0").setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
        this.deltaComplexity = deltaComplexity!=null ? new BigDecimal(deltaComplexity).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP) : new BigDecimal("0").setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
        this.deltaLine = deltaLine!=null ? new BigDecimal(deltaLine).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP) : new BigDecimal("0").setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
        this.deltaMethod = deltaMethod!=null ? new BigDecimal(deltaMethod).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP) : new BigDecimal("0").setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
        this.deltaClass = deltaClass!=null ? new BigDecimal(deltaClass).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP): new BigDecimal("0").setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
        this.ensureValid(); // Validate threshold values while creating new object to encapsulate these
    }

    // Used to apply [0,100] range over threshold values
    // 0 is set if threshold is smaller than 0 and 100 is set if threshold is bigger than 100, else remains unchanged
    private BigDecimal applyRange(BigDecimal min, BigDecimal value, BigDecimal max){
        if(value.compareTo(min) == -1)
            return min;
        else if(value.compareTo(max) == 1)
            return max;
        else
            return value;

    }

    // Ensure if the threshold values are within [0, 100] percentage range
    public void ensureValid(){
        BigDecimal min = new BigDecimal(0).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
        BigDecimal max = new BigDecimal(100).setScale(Constants.COVERAGE_PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);

        this.deltaInstruction = applyRange(min, this.deltaInstruction, max);
        this.deltaBranch = applyRange(min, this.deltaBranch, max);
        this.deltaComplexity = applyRange(min, this.deltaComplexity, max);
        this.deltaLine = applyRange(min, this.deltaLine, max);
        this.deltaMethod = applyRange(min, this.deltaMethod, max);
        this.deltaClass = applyRange(min, this.deltaClass, max);
    }

    public BigDecimal getDeltaInstruction() {
        return deltaInstruction;
    }

    public void setDeltaInstruction(BigDecimal deltaInstruction) {
        this.deltaInstruction = deltaInstruction;
    }

    public BigDecimal getDeltaBranch() {
        return deltaBranch;
    }

    public void setDeltaBranch(BigDecimal deltaBranch) {
        this.deltaBranch = deltaBranch;
    }

    public BigDecimal getDeltaComplexity() {
        return deltaComplexity;
    }

    public void setDeltaComplexity(BigDecimal deltaComplexity) {
        this.deltaComplexity = deltaComplexity;
    }

    public BigDecimal getDeltaLine() {
        return deltaLine;
    }

    public void setDeltaLine(BigDecimal deltaLine) {
        this.deltaLine = deltaLine;
    }

    public BigDecimal getDeltaMethod() {
        return deltaMethod;
    }

    public void setDeltaMethod(BigDecimal deltaMethod) {
        this.deltaMethod = deltaMethod;
    }

    public BigDecimal getDeltaClass() {
        return deltaClass;
    }

    public void setDeltaClass(BigDecimal deltaClass) {
        this.deltaClass = deltaClass;
    }

    @Override
    public String toString() {
        return "JacocoHealthReportDeltaThresholds [" +
                "deltaInstruction=" + deltaInstruction.toString() +
                ", deltaBranch=" + deltaBranch.toString() +
                ", deltaComplexity=" + deltaComplexity.toString() +
                ", deltaLine=" + deltaLine.toString() +
                ", deltaMethod=" + deltaMethod.toString() +
                ", deltaClass=" + deltaClass.toString() +
                ']';
    }
}
