package hudson.plugins.jacoco.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;

import hudson.FilePath;


public class ModuleInfo {
		private FilePath srcDir;
		private FilePath classDir;
		private FilePath execFile;
		private FilePath generatedHTMLsDir;
		
		private ExecutionDataStore executionDataStore;
		private SessionInfoStore sessionInfoStore;
		
		private IBundleCoverage bundleCoverage;
		
		public IBundleCoverage getBundleCoverage() {
			return bundleCoverage;
		}
		public void setBundleCoverage(IBundleCoverage bundleCoverage) {
			this.bundleCoverage = bundleCoverage;
		}
		public FilePath getGeneratedHTMLsDir() {
			return generatedHTMLsDir;
		}
		public void setGeneratedHTMLsDir(FilePath generatedHTMLsDir) {
			new File(generatedHTMLsDir.getRemote());
			this.generatedHTMLsDir = generatedHTMLsDir;
		}
		public FilePath getSrcDir() {
			return srcDir;
		}
		public void setSrcDir(FilePath srcDir) {
			this.srcDir = srcDir;
		}
		public FilePath getClassDir() {
			return classDir;
		}
		public void setClassDir(FilePath classDir) {
			this.classDir = classDir;
		}
		public FilePath getExecFile() {
			return execFile;
		}
		public void setExecFile(FilePath execFile) {
			this.execFile = execFile;
		}
		private void loadExecutionData() throws IOException {
	    	ExecutionDataStore executionDataStore;
	    	SessionInfoStore sessionInfoStore;
	    	File executionDataFile = new File(execFile.getRemote());
			final FileInputStream fis = new FileInputStream(executionDataFile);
			final ExecutionDataReader executionDataReader = new ExecutionDataReader(
					fis);
			executionDataStore = new ExecutionDataStore();
			sessionInfoStore = new SessionInfoStore();

			executionDataReader.setExecutionDataVisitor(executionDataStore);
			executionDataReader.setSessionInfoVisitor(sessionInfoStore);

			while (executionDataReader.read()) {
			}

			fis.close();
		}
	    private IBundleCoverage analyzeStructure() throws IOException {
			final CoverageBuilder coverageBuilder = new CoverageBuilder();
			final Analyzer analyzer = new Analyzer(executionDataStore,
					coverageBuilder);

			analyzer.analyzeAll(new File(classDir.getRemote()));

			return coverageBuilder.getBundle("ModuleReport");
		}
	    public IBundleCoverage create() throws IOException {

			// Read the jacoco.exec file. Multiple data stores could be merged
			// at this point
			loadExecutionData();

			// Run the structure analyzer on a single class folder to build up
			// the coverage model. The process would be similar if your classes
			// were in a jar file. Typically you would create a bundle for each
			// class folder and each jar you want in your report. If you have
			// more than one bundle you will need to add a grouping node to your
			// report
			final IBundleCoverage bundleCoverageFinal = analyzeStructure();
			this.bundleCoverage = bundleCoverageFinal;
			return bundleCoverageFinal;
		}
}
