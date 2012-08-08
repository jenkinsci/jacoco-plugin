package hudson.plugins.jacoco.model;

import hudson.FilePath;
import hudson.model.BuildListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;


public class ModuleInfo {
	 
		private String title;
		private BuildListener listener;
		private FilePath srcDir;
		private FilePath classDir;
		private FilePath execFile;
		private FilePath generatedHTMLsDir;
		
		private ExecutionDataStore executionDataStore;
		private SessionInfoStore sessionInfoStore;
		
		private IBundleCoverage bundleCoverage;
		
		public ModuleInfo(BuildListener listener) {
			this.listener = listener;
		}
		public IBundleCoverage getBundleCoverage() {
			return bundleCoverage;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public void setBundleCoverage(IBundleCoverage bundleCoverage) {
			this.bundleCoverage = bundleCoverage;
		}
		public FilePath getGeneratedHTMLsDir() {
			return generatedHTMLsDir;
		}
		public void setGeneratedHTMLsDir(FilePath generatedHTMLsDir) {
			//new File(generatedHTMLsDir.getRemote());
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
			final PrintStream logger = listener.getLogger();
			logger.println("Loading execution data..");
	    	File executionDataFile = new File(execFile.getRemote());
	    	logger.println("executionDataFile: " + executionDataFile.getAbsolutePath());
	    	logger.println("title: " + title);
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
	    	final PrintStream logger = listener.getLogger();
			logger.println("Analyze structure");
			File classDirectory = new File(classDir.getRemote());
			logger.println("classdir :" + classDirectory.getAbsolutePath());
			final CoverageBuilder coverageBuilder = new CoverageBuilder();
			final Analyzer analyzer = new Analyzer(executionDataStore,
					coverageBuilder);
			
			analyzer.analyzeAll(classDirectory);
			
			return coverageBuilder.getBundle(title);
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
			//final IBundleCoverage bundleCoverageFinal ;
			this.bundleCoverage = analyzeStructure();
			return this.bundleCoverage;
		}
}
