package hudson.plugins.jacoco.report;

import hudson.plugins.jacoco.model.CoverageElement;

import java.io.Writer;

import org.jacoco.core.analysis.IMethodCoverage;

/**
 * @author Kohsuke Kawaguchi
 * @author David Carver
 * @author Ognjen Bubalo
 */
//AggregatedReport<PackageReport,ClassReport,MethodReport>  -  AbstractReport<ClassReport,MethodReport>
public final class MethodReport extends AggregatedReport<ClassReport,MethodReport, SourceFileReport> {
	
	private IMethodCoverage methodCov;
	
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
        //logger.log(Level.INFO, "SourceFileReport");
    }

    @Override
    public boolean hasClassCoverage() {
        return false;
    }
	
	public void setSrcFileInfo(IMethodCoverage methodCov) {
		this.methodCov = methodCov;
	}
	
    public void printHighlightedSrcFile(Writer output) {
        new SourceAnnotator(getParent().getSourceFilePath()).printHighlightedSrcFile(methodCov,output);
   	}
}
