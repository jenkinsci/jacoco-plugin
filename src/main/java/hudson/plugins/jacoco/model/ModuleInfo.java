package hudson.plugins.jacoco.model;

import hudson.FilePath;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;

@Deprecated
public class ModuleInfo {

		private String name;
		private FilePath srcDir;
		private FilePath classDir;
		private FilePath execFile;
		private FilePath generatedHTMLsDir;
		//private String title;

		private ExecutionDataStore executionDataStore;
		private SessionInfoStore sessionInfoStore;

		private IBundleCoverage bundleCoverage;

		public IBundleCoverage getBundleCoverage() {
			return bundleCoverage;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public void setBundleCoverage(IBundleCoverage bundleCoverage) {
			this.bundleCoverage = bundleCoverage;
		}
		public FilePath getGeneratedHTMLsDir() {
			return generatedHTMLsDir;
		}
		public void setGeneratedHTMLsDir(FilePath generatedHTMLsDir) {
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
			File classDirectory = new File(classDir.getRemote());
			final CoverageBuilder coverageBuilder = new CoverageBuilder();
			final Analyzer analyzer = new Analyzer(executionDataStore,
					coverageBuilder);

			analyzer.analyzeAll(classDirectory);

			return coverageBuilder.getBundle(name);
		}
	    public IBundleCoverage loadBundleCoverage() throws IOException {
			loadExecutionData();
			this.bundleCoverage = analyzeStructure();
			return this.bundleCoverage;
		}
}