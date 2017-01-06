package hudson.plugins.jacoco.report;

import hudson.model.BuildListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.report.FileMultiReportOutput;
import org.jacoco.report.IReportGroupVisitor;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.ISourceFileLocator;
import org.jacoco.report.MultiReportVisitor;
import org.jacoco.report.csv.CSVFormatter;
import org.jacoco.report.html.HTMLFormatter;
import org.jacoco.report.xml.XMLFormatter;

public class ReportFactory {

	// not sure a factory is the best way to go, but we are still in the exploration phase
	// and it's ok for now :)

	/**
	 * Location of the workspace.
	 */
	private final File workspaceDir;
	
	/**
	 * Output directory for the reports. Note that this parameter is only
	 * relevant if the goal is run from the command line or from the default
	 * build lifecycle. If the goal is run indirectly as part of a site
	 * generation, the output directory configured in the Maven Site Plugin is
	 * used instead.
	 * 
	 * @parameter default-value="${project.reporting.outputDirectory}/jacoco"
	 */
	private File outputDirectory;

	/**
	 * Encoding of the generated reports.
	 * 
	 * @parameter expression="${project.reporting.outputEncoding}"
	 *            default-value="UTF-8"
	 */
	private String outputEncoding;

	/**
	 * Encoding of the source files.
	 * 
	 * @parameter expression="${project.build.sourceEncoding}"
	 *            default-value="UTF-8"
	 */
	private String sourceEncoding;

	/**
	 * File with execution data.
	 * 
	 * @parameter default-value="${project.build.directory}/jacoco.exec"
	 */
	private File dataFile;

	/**
	 * A list of class files to include in instrumentation/analysis/reports. May
	 * use wildcard characters (* and ?). When not specified - everything will
	 * be included.
	 * 
	 * @parameter
	 */
	//private List<String> includes;

	/**
	 * A list of class files to exclude from instrumentation/analysis/reports.
	 * May use wildcard characters (* and ?).
	 * 
	 * @parameter
	 */
	//private List<String> excludes;

	/**
	 * Flag used to suppress execution.
	 * 
	 * @parameter expression="${jacoco.skip}" default-value="false"
	 */
	//private boolean skip;

	/**
	 * Maven project.
	 * 
	 * @parameter expression="${project}"
	 * @readonly
	 */
	//private MavenProject project;

	private SessionInfoStore sessionInfoStore;

	private ExecutionDataStore executionDataStore;
	
	private BuildListener listener;

	public ReportFactory(File workspaceDir, BuildListener listener) {
		if (workspaceDir == null) {
			throw new NullPointerException("Null workspace not allowed");
		}
		this.workspaceDir = workspaceDir;
		this.listener = listener;
	}

	protected void executeReport(Locale locale) throws IOException {
		try (PrintStream logger = listener.getLogger()) {
			try {
				logger.println("Executing loadExecutionData..");
				loadExecutionData();
			} catch (final IOException e) {
				logger.println("NO EXEC FILE!");
				//logger.log(Level.WARNING, "NO EXEC FILE!");
				// TODO is there a better exception type for jenkins plugins to throw?
				throw new RuntimeException(
						"Unable to read execution data file " + dataFile + ": "
								+ e.getMessage(), e);
			}
			logger.println("Executing createVisitor()");
			final IReportVisitor visitor = createVisitor();
			logger.println("Executing visitInfo");
			visitor.visitInfo(sessionInfoStore.getInfos(),
					executionDataStore.getContents());
			logger.println("Executing createReport(visitor)");
			createReport(visitor);
			logger.println("Executing visitEnd()..");
			visitor.visitEnd();
		}
	}

	private void loadExecutionData() throws IOException {
		try (PrintStream logger = listener.getLogger()) {
			logger.println("Executing sessionInfoStore..");
			sessionInfoStore = new SessionInfoStore();
			logger.println("Executing executionDataStore..");
			executionDataStore = new ExecutionDataStore();
			FileInputStream in = null;
			try {
				logger.println("Executing newFileInputStream..");
				in = new FileInputStream(dataFile);
				logger.println("Executing ExecutionDataReader..");
				final ExecutionDataReader reader = new ExecutionDataReader(in);
				reader.setSessionInfoVisitor(sessionInfoStore);
				reader.setExecutionDataVisitor(executionDataStore);
				reader.read();
			} finally {
				if (in != null) {
					in.close();
				}
			}
		}
	}

	private void createReport(final IReportGroupVisitor visitor)
			throws IOException {
		final IBundleCoverage bundle = createBundle();
		final SourceFileCollection locator = new SourceFileCollection(
				getCompileSourceRoots(), sourceEncoding);
		checkForMissingDebugInformation(bundle);
		visitor.visitBundle(bundle, locator);
	}

	private void checkForMissingDebugInformation(final ICoverageNode node) {
		if (node.getClassCounter().getTotalCount() > 0
				&& node.getLineCounter().getTotalCount() == 0) {
			// TODO log as a jenkins warning
			System.out.println(
					"To enable source code annotation class files have to be compiled with debug information.");
		}
	}

	private IBundleCoverage createBundle() throws IOException {
		final CoverageBuilder builder = new CoverageBuilder();
		final Analyzer analyzer = new Analyzer(executionDataStore, builder);
		final File classesDir = new File(workspaceDir, "\\target\\classes"); // TODO: only works in maven default setup. class file location must come from user preference

		List<File> filesToAnalyze = getFilesToAnalyze(classesDir);

		for (File file : filesToAnalyze) {
			analyzer.analyzeAll(file);
		}

		return builder.getBundle("projectName"); // FIXME: need to know how to determine bundle name
	}

	// closing is done in end-handling of the visitor
	@SuppressWarnings("resource")
    private IReportVisitor createVisitor() throws IOException {
		final List<IReportVisitor> visitors = new ArrayList<>();

		outputDirectory.mkdirs();

		final XMLFormatter xmlFormatter = new XMLFormatter();
		xmlFormatter.setOutputEncoding(outputEncoding);
		visitors.add(xmlFormatter.createVisitor(new FileOutputStream(new File(
				outputDirectory, "jacoco.xml"))));

		final CSVFormatter formatter = new CSVFormatter();
		formatter.setOutputEncoding(outputEncoding);
		visitors.add(formatter.createVisitor(new FileOutputStream(new File(
				outputDirectory, "jacoco.csv"))));

		final HTMLFormatter htmlFormatter = new HTMLFormatter();
		// formatter.setFooterText(footer);
		htmlFormatter.setOutputEncoding(outputEncoding);
		// formatter.setLocale(locale);
		visitors.add(htmlFormatter.createVisitor(new FileMultiReportOutput(
				outputDirectory)));

		return new MultiReportVisitor(visitors);
	}

	private static class SourceFileCollection implements ISourceFileLocator {

		private final List<File> sourceRoots;
		private final String encoding;

		public SourceFileCollection(final List<File> sourceRoots,
				final String encoding) {
			if (sourceRoots == null) throw new NullPointerException();
			if (encoding == null) throw new NullPointerException();
			this.sourceRoots = sourceRoots;
			this.encoding = encoding;
		}

		public Reader getSourceFile(final String packageName,
				final String fileName) throws IOException {
			final String r;
			if (packageName.length() > 0) {
				r = packageName + '/' + fileName;
			} else {
				r = fileName;
			}
			for (final File sourceRoot : sourceRoots) {
				final File file = new File(sourceRoot, r);
				if (file.exists() && file.isFile()) {
					return new InputStreamReader(new FileInputStream(file),
							encoding);
				}
			}
			return null;
		}

		public int getTabWidth() {
			return 4;
		}
	}

	private List<File> getCompileSourceRoots() {
		final List<File> result = new ArrayList<>();
		// TODO user preference
		result.add(new File(workspaceDir, "\\src\\main\\java"));
		result.add(new File(workspaceDir, "\\src\\test\\java"));
		return result;
	}

	protected List<File> getFilesToAnalyze(File rootDir) throws IOException {
		// this will come from jenkins functionality (it supports ant-style search patterns)
		
		// TODO use jenkins file pattern matching (ant-style, like "**/classes") saves time :), right
		return Collections.singletonList(rootDir);
	}

	public void createReport() throws IOException {
		try (PrintStream logger = listener.getLogger()) {
			this.dataFile = new File(workspaceDir, "\\target\\jacoco.exec").getAbsoluteFile();
			logger.println("Execfile should be here: " + workspaceDir + "\\target\\jacoco.exec");
			this.outputDirectory = new File(workspaceDir, "\\target\\jenkins-jacoco").getAbsoluteFile(); // this is not permanent; we will not be creating the report just like this
			this.outputEncoding = "UTF-8";
			this.sourceEncoding = "UTF-8"; // TODO user preference (UTF-8 is actually often wrong, since javac default is platform default encoding)
			logger.println("Executing executeReport..");
			this.executeReport(Locale.ENGLISH);
		}
	}

	public static void main(String... args) throws IOException {
		ReportFactory rf = new ReportFactory(new File("."), null);
		rf.dataFile = new File("target/jacoco.exec");
		rf.outputDirectory = new File("target/jenkins-jacoco"); // this is not permanent; we will not be creating the report just like this
		rf.outputEncoding = "UTF-8";
		rf.sourceEncoding = "UTF-8"; // TODO user preference (UTF-8 is actually often wrong, since javac default is platform default encoding)
		rf.executeReport(Locale.ENGLISH);
		
		// we did it! it shows no coverage because the .exec file is empty, probably
		// let's run a build with coverage reporting, maybe it wont work, because
		
		//there is some problem with maven execution here from eclipse, I am using cmd line, but we can try
		// ok, thanks for the warning. maybe I will notice the problem
	}
	//private static final Logger logger = Logger.getLogger(JacocoBuildAction.class.getName());
}
