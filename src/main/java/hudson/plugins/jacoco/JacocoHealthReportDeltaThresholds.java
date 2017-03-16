package hudson.plugins.jacoco;

import hudson.plugins.jacoco.portlet.utils.Constants;

import java.math.BigDecimal;

/**
 * This class encapsulates delta thresholds configured by the user.
 * The threshold values are interpreted as percentages
 */
public class JacocoHealthReportDeltaThresholds {

    /**
     * Variables to hold delta threshold values for different types of percentage coverages
     */
    private float deltaInstruction;
    private float deltaBranch;
    private float deltaComplexity;
    private float deltaLine;
    private float deltaMethod;
    private float deltaClass;

    public JacocoHealthReportDeltaThresholds() {
    }

    // Constructor used for bounding user-configured string threshold values to corresponding big decimal thresholds
    public JacocoHealthReportDeltaThresholds(String deltaInstruction, String deltaBranch, String deltaComplexity, String deltaLine, String deltaMethod, String deltaClass){
        this.deltaInstruction = deltaInstruction!=null ? Float.valueOf(deltaInstruction) : 0f;
        this.deltaBranch = deltaBranch!=null ? Float.valueOf(deltaBranch) : 0f;
        this.deltaComplexity = deltaComplexity!=null ? Float.valueOf(deltaComplexity) : 0f;
        this.deltaLine = deltaLine!=null ? Float.valueOf(deltaLine) : 0f;
        this.deltaMethod = deltaMethod!=null ? Float.valueOf(deltaMethod) : 0f;
        this.deltaClass = deltaClass!=null ? Float.valueOf(deltaClass): 0f;
        this.ensureValid(); // Validate threshold values while creating new object to encapsulate these
    }

    // Used to apply [0,100] range over threshold values
    // 0 is set if threshold is smaller than 0 and 100 is set if threshold is bigger than 100, else remains unchanged
    private float applyRange(float min, float value, float max){
        if(value < min)
            return min;
        else if(value > max)
            return max;
        else
            return value;

    }

    // Ensure if the threshold values are within [0, 100] percentage range
    public void ensureValid(){
        float min = 0f;
        float max = 100f;

        this.deltaInstruction = applyRange(min, this.deltaInstruction, max);
        this.deltaBranch = applyRange(min, this.deltaBranch, max);
        this.deltaComplexity = applyRange(min, this.deltaComplexity, max);
        this.deltaLine = applyRange(min, this.deltaLine, max);
        this.deltaMethod = applyRange(min, this.deltaMethod, max);
        this.deltaClass = applyRange(min, this.deltaClass, max);
    }

    public float getDeltaInstruction() {
        return deltaInstruction;
    }

    public void setDeltaInstruction(float deltaInstruction) {
        this.deltaInstruction = deltaInstruction;
    }

    public float getDeltaBranch() {
        return deltaBranch;
    }

    public void setDeltaBranch(float deltaBranch) {
        this.deltaBranch = deltaBranch;
    }

    public float getDeltaComplexity() {
        return deltaComplexity;
    }

    public void setDeltaComplexity(float deltaComplexity) {
        this.deltaComplexity = deltaComplexity;
    }

    public float getDeltaLine() {
        return deltaLine;
    }

    public void setDeltaLine(float deltaLine) {
        this.deltaLine = deltaLine;
    }

    public float getDeltaMethod() {
        return deltaMethod;
    }

    public void setDeltaMethod(float deltaMethod) {
        this.deltaMethod = deltaMethod;
    }

    public float getDeltaClass() {
        return deltaClass;
    }

    public void setDeltaClass(float deltaClass) {
        this.deltaClass = deltaClass;
    }

    @Override
    public String toString() {
        return "JacocoHealthReportDeltaThresholds [" +
                "deltaInstruction=" + deltaInstruction +
                ", deltaBranch=" + deltaBranch +
                ", deltaComplexity=" + deltaComplexity +
                ", deltaLine=" + deltaLine +
                ", deltaMethod=" + deltaMethod +
                ", deltaClass=" + deltaClass +
                ']';
    }
}
