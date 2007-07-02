package hudson.plugins.emma;

import java.io.Serializable;

/**
 * Holds the configuration details for {@link hudson.model.HealthReport} generation
 *
 * @author Stephen Connolly
 * @since 1.7
 */
public class EmmaHealthReportThresholds implements Serializable {
    private int minClass;
    private int maxClass;
    private int minMethod;
    private int maxMethod;
    private int minBlock;
    private int maxBlock;
    private int minLine;
    private int maxLine;

    public EmmaHealthReportThresholds() {
    }

    public EmmaHealthReportThresholds(int minClass, int maxClass, int minMethod, int maxMethod, int minBlock, int maxBlock, int minLine, int maxLine) {
        this.minClass = minClass;
        this.maxClass = maxClass;
        this.minMethod = minMethod;
        this.maxMethod = maxMethod;
        this.minBlock = minBlock;
        this.maxBlock = maxBlock;
        this.minLine = minLine;
        this.maxLine = maxLine;
        ensureValid();
    }

    private int applyRange(int min , int value, int max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public void ensureValid() {
        maxClass = applyRange(0, maxClass, 100);
        minClass = applyRange(0, minClass, maxClass);
        maxMethod = applyRange(0, maxMethod, 100);
        minMethod = applyRange(0, minMethod, maxMethod);
        maxBlock = applyRange(0, maxBlock, 100);
        minBlock = applyRange(0, minBlock, maxBlock);
        maxLine = applyRange(0, maxLine, 100);
        minLine = applyRange(0, minLine, maxLine);
    }

    public int getMinClass() {
        return minClass;
    }

    public void setMinClass(int minClass) {
        this.minClass = minClass;
    }

    public int getMaxClass() {
        return maxClass;
    }

    public void setMaxClass(int maxClass) {
        this.maxClass = maxClass;
    }

    public int getMinMethod() {
        return minMethod;
    }

    public void setMinMethod(int minMethod) {
        this.minMethod = minMethod;
    }

    public int getMaxMethod() {
        return maxMethod;
    }

    public void setMaxMethod(int maxMethod) {
        this.maxMethod = maxMethod;
    }

    public int getMinBlock() {
        return minBlock;
    }

    public void setMinBlock(int minBlock) {
        this.minBlock = minBlock;
    }

    public int getMaxBlock() {
        return maxBlock;
    }

    public void setMaxBlock(int maxBlock) {
        this.maxBlock = maxBlock;
    }

    public int getMinLine() {
        return minLine;
    }

    public void setMinLine(int minLine) {
        this.minLine = minLine;
    }

    public int getMaxLine() {
        return maxLine;
    }

    public void setMaxLine(int maxLine) {
        this.maxLine = maxLine;
    }
}
