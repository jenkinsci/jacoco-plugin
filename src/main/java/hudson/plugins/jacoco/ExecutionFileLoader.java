package hudson.plugins.jacoco;

import hudson.FilePath;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.tools.ant.util.FileUtils;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;


public class ExecutionFileLoader implements Serializable {
	 
		private String name;
		private FilePath srcDir;
		private FilePath classDir;
		private FilePath execFile;
		private FilePath generatedHTMLsDir;
		private String title;
		
		private ExecutionDataStore executionDataStore;
		private SessionInfoStore sessionInfoStore;
		
		private IBundleCoverage bundleCoverage;
		
		private ArrayList<FilePath> execFiles; 
		
		public ExecutionFileLoader() {
			execFiles=new ArrayList<FilePath>();
		}
		
		public void addExecFile(FilePath execFile) {
			execFiles.add(execFile);
		}
		
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
			
			executionDataStore = new ExecutionDataStore();
			sessionInfoStore = new SessionInfoStore();
			
			for (final Iterator<FilePath> i = execFiles.iterator(); i.hasNext();) {
				InputStream isc = null;
				try {
					File executionDataFile = new File(i.next().getRemote());
					final FileInputStream fis = new FileInputStream(executionDataFile);
	                final ExecutionDataReader reader = new ExecutionDataReader(fis);
	                reader.setSessionInfoVisitor(sessionInfoStore);
	                reader.setExecutionDataVisitor(executionDataStore);
	                reader.read();
	                isc = fis;
	            } catch (final IOException e) {
	                e.printStackTrace();
	            } finally {
	                FileUtils.close(isc);
	            }
	        }
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
