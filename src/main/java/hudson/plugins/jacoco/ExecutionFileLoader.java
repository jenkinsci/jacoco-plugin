package hudson.plugins.jacoco;

import hudson.FilePath;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.plexus.util.FileUtils;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.maven.FileFilter;


public class ExecutionFileLoader implements Serializable {
    private final static String[] STARSTAR = {"**"};
    private final static String[] ITEM_ZERO = {"{0}"};
    
		private String name;
		private FilePath srcDir;
		private FilePath classDir;
		private FilePath generatedHTMLsDir;
		private String[] includes;
		private String[] excludes;
		
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
		private void loadExecutionData() throws IOException {
			
			executionDataStore = new ExecutionDataStore();
			sessionInfoStore = new SessionInfoStore();
			
			for (FilePath filePath : execFiles) {
				File executionDataFile = new File(filePath.getRemote());
				try {
					final FileInputStream fis = new FileInputStream(executionDataFile);
					try {
	                    final ExecutionDataReader reader = new ExecutionDataReader(fis);
	                    reader.setSessionInfoVisitor(sessionInfoStore);
	                    reader.setExecutionDataVisitor(executionDataStore);
	                    reader.read();
					} finally {
					    fis.close();
					}
	            } catch (final IOException e) {
	            	System.out.println("While reading execution data-file: " + executionDataFile);
	                e.printStackTrace();
	            }
	        }
		}
	    private IBundleCoverage analyzeStructure() throws IOException {
	    	
			File classDirectory = new File(classDir.getRemote());
			final CoverageBuilder coverageBuilder = new CoverageBuilder();
			final Analyzer analyzer = new Analyzer(executionDataStore,
					coverageBuilder);
			
			if (includes==null) {
				includes = STARSTAR;
			} else if (includes.length == 0) {
				includes = STARSTAR;
			} else if ((includes.length == 1) && ("".equals(includes[0]))) {
				includes = STARSTAR;
			} 
			if (excludes==null) {
				excludes = ITEM_ZERO;
			}  else if (excludes.length==0) {
				excludes = ITEM_ZERO;
			}

			final FileFilter fileFilter = new FileFilter(Arrays.asList(includes), Arrays.asList(excludes));
			try {
				@SuppressWarnings("unchecked")
				final List<File> filesToAnalyze = FileUtils.getFiles(classDirectory, fileFilter.getIncludes(), fileFilter.getExcludes());
				for (final File file : filesToAnalyze) {
					analyzer.analyzeAll(file);
				}
			} catch (final Exception e) {
				System.out.println("While reading class directory: " + classDirectory);
				e.printStackTrace();
			}
			return coverageBuilder.getBundle(name);
		}
	    public IBundleCoverage loadBundleCoverage() throws IOException {
			loadExecutionData();
			this.bundleCoverage = analyzeStructure();
			return this.bundleCoverage;
		}

		public void setIncludes(String[] includes) {
			this.includes = includes;
		}

		public void setExcludes(String[] excludes) {
			this.excludes = excludes;
		}
}
