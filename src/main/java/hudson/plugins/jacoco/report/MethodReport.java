package hudson.plugins.jacoco.report;

import hudson.plugins.jacoco.model.Coverage;
import hudson.plugins.jacoco.model.CoverageElement;
import hudson.plugins.jacoco.model.CoverageObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.IMethodCoverage;

import com.google.common.io.Files;

/**
 * @author Kohsuke Kawaguchi
 * @author David Carver
 * @author Ognjen Bubalo
 */
//AggregatedReport<PackageReport,ClassReport,MethodReport>  -  AbstractReport<ClassReport,MethodReport>
public final class MethodReport extends AggregatedReport<ClassReport,MethodReport, SourceFileReport> {
	
	public String desc;
	
	public String lineNo;
	
	private IMethodCoverage methodCov;
	
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public String getDesc(String desc) {
		return this.desc;
	}
	
	@Override
	public String printFourCoverageColumns() {
        StringBuilder buf = new StringBuilder();
        instruction.setType(CoverageElement.Type.INSTRUCTION);
        complexity.setType(CoverageElement.Type.COMPLEXITY);
        branch.setType(CoverageElement.Type.BRANCH);
        line.setType(CoverageElement.Type.LINE);
        method.setType(CoverageElement.Type.METHOD);
		printRatioCell(isFailed(), this.instruction, buf);
		printRatioCell(isFailed(), this.branch, buf);
		printRatioCell(isFailed(), this.complexity, buf);
		printRatioCell(isFailed(), this.line, buf);
        printRatioCell(isFailed(), this.method, buf);
        //logger.log(Level.INFO, "Printing Ratio cells within MethodReport.");
		return buf.toString();
	}
	
	@Override
	public void add(SourceFileReport child) {
    	String newChildName = child.getName().replaceAll(this.getName() + ".", ""); 
    	child.setName(newChildName);
        getChildren().put(child.getName(), child);
        this.hasClassCoverage();
        //logger.log(Level.INFO, "SourceFileReport");
    }
	
	
	
	private static final Logger logger = Logger.getLogger(CoverageObject.class.getName());

	public void setSrcFileInfo(IMethodCoverage methodCov) {
		this.methodCov = methodCov;
	}
	
    public String printHighlightedSrcFile() {
        return new SourceAnnotator(getParent().getSourceFilePath()).printHighlightedSrcFile(methodCov);
   	}
}
