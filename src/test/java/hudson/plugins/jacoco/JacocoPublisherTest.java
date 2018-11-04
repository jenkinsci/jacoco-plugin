package hudson.plugins.jacoco;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.niceMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.internal.analysis.ClassCoverageImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.Job;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.jacoco.JacocoPublisher.DescriptorImpl;
import hudson.plugins.jacoco.report.ClassReport;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;

@RunWith(PowerMockRunner.class)
@PrepareForTest(JacocoPublisher.class)
public class JacocoPublisherTest extends AbstractJacocoTestBase {
    private final TaskListener taskListener = niceMock(TaskListener.class);
    private final Launcher launcher = niceMock(Launcher.class);
	private StringBuilder logContent;

    @Before
    public void setUp() {
		logContent = new StringBuilder();
		expect(taskListener.getLogger()).andReturn(new PrintStream(System.out) {
													   @Override
													   public void print(String s) {
														   super.print(s);
														   logContent.append(s);
													   }
												   }
		).anyTimes();
	}

    @SuppressWarnings("deprecation")
	@Test
	public void testConstruct() {
		JacocoPublisher publisher = new JacocoPublisher(null, null, null, null, null, false,
				null, null, null, null,
				null, null, null, null,
				null, null, null, null,
				false, null, null, null, null, null, null, false);
		assertNotNull(publisher.toString());
	}

	@Test
	public void testGetterSetter() {
		JacocoPublisher publisher = new JacocoPublisher();
		publisher.setChangeBuildStatus(true);
		assertTrue(publisher.getChangeBuildStatus());
		assertTrue(publisher.isChangeBuildStatus());

		publisher.setClassPattern("pattern");
		assertEquals("pattern", publisher.getClassPattern());

		publisher.setExclusionPattern("excl");
		assertEquals("excl", publisher.getExclusionPattern());

		publisher.setExecPattern("exec");
		assertEquals("exec", publisher.getExecPattern());

		publisher.setInclusionPattern("incl");
		assertEquals("incl", publisher.getInclusionPattern());

		publisher.setSourcePattern("source");
		assertEquals("source", publisher.getSourcePattern());

		publisher.setSourceExclusionPattern("sourceExclusion");
		assertEquals("sourceExclusion", publisher.getSourceExclusionPattern());

		publisher.setSourceInclusionPattern("sourceInclusion");
		assertEquals("sourceInclusion", publisher.getSourceInclusionPattern());

		publisher.setMaximumBranchCoverage("maxB");
		assertEquals("maxB", publisher.getMaximumBranchCoverage());

		publisher.setMaximumClassCoverage("maxCl");
		assertEquals("maxCl", publisher.getMaximumClassCoverage());

		publisher.setMaximumComplexityCoverage("maxCo");
		assertEquals("maxCo", publisher.getMaximumComplexityCoverage());

		publisher.setMaximumInstructionCoverage("maxI");
		assertEquals("maxI", publisher.getMaximumInstructionCoverage());

		publisher.setMaximumLineCoverage("maxL");
		assertEquals("maxL", publisher.getMaximumLineCoverage());

		publisher.setMaximumMethodCoverage("minM");
		assertEquals("minM", publisher.getMaximumMethodCoverage());

		publisher.setMinimumBranchCoverage("minB");
		assertEquals("minB", publisher.getMinimumBranchCoverage());

		publisher.setMinimumClassCoverage("minCl");
		assertEquals("minCl", publisher.getMinimumClassCoverage());

		publisher.setMinimumComplexityCoverage("minCo");
		assertEquals("minCo", publisher.getMinimumComplexityCoverage());

		publisher.setMinimumInstructionCoverage("minI");
		assertEquals("minI", publisher.getMinimumInstructionCoverage());

		publisher.setMinimumLineCoverage("minL");
		assertEquals("minL", publisher.getMinimumLineCoverage());

		publisher.setMinimumMethodCoverage("minM");
		assertEquals("minM", publisher.getMinimumMethodCoverage());

		assertNotNull(publisher.toString());

		assertEquals(BuildStepMonitor.NONE, publisher.getRequiredMonitorService());

		BuildStepDescriptor<Publisher> descriptor = new DescriptorImpl();
		assertNotNull(descriptor);
		assertNotNull(descriptor.getDisplayName());
		assertTrue(descriptor.isApplicable(null));
	}

	@Test
	public void testSaveCoverageReports() throws Exception {
		File tempDir = File.createTempFile("coverage", ".tst");
		assertTrue(tempDir.delete());
		try {
			JacocoPublisher.saveCoverageReports(new FilePath(tempDir), new FilePath(tempDir));
		} finally {
			FileUtils.deleteDirectory(tempDir);
		}
	}

	@Test
	public void testResolveFilePathsNoReplace() throws Exception {
		JacocoPublisher publisher = new JacocoPublisher();
		Run<?, ?> run = mock(Run.class);
		TaskListener listener = mock(TaskListener.class);

		expect(run.getEnvironment(listener)).andReturn(new EnvVars());

		EasyMock.replay(run, listener);

		assertEquals("input", publisher.resolveFilePaths(run, listener, "input", Collections.singletonMap("key", "value")));

		EasyMock.verify(run, listener);
	}

	@Test
	public void testResolveFilePathsReplace() throws Exception {
		JacocoPublisher publisher = new JacocoPublisher();
		Run<?, ?> run = mock(Run.class);
		TaskListener listener = mock(TaskListener.class);

		expect(run.getEnvironment(listener)).andReturn(new EnvVars());

		EasyMock.replay(run, listener);

		assertEquals("inputvalueinput", publisher.resolveFilePaths(run, listener, "input${key}input", Collections.singletonMap("key", "value")));

		EasyMock.verify(run, listener);
	}

	@Test
	public void testResolveFilePathsException() throws Exception {
		JacocoPublisher publisher = new JacocoPublisher();
		Run<?, ?> run = mock(Run.class);
		TaskListener listener = mock(TaskListener.class);

		expect(run.getEnvironment(listener)).andThrow(new IllegalStateException("TestException"));
		expect(listener.getLogger()).andReturn(System.out);

		EasyMock.replay(run, listener);

		//noinspection CatchMayIgnoreException
		try {
			publisher.resolveFilePaths(run, listener, "input${key}input", Collections.singletonMap("key", "value"));
			fail("Should catch exception here");
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().startsWith("Failed to resolve parameters"));
		}
	}

	@Test
	public void testResolveFilePathsAbstractBuildNoReplace() throws Exception {
		JacocoPublisher publisher = new JacocoPublisher();
		AbstractBuild<?, ?> build = mock(AbstractBuild.class);
		TaskListener listener = mock(TaskListener.class);

		expect(build.getEnvironment(listener)).andReturn(new EnvVars());
		expect(build.getBuildVariables()).andReturn(Collections.singletonMap("key", "value"));

		EasyMock.replay(build, listener);

		assertEquals("input", publisher.resolveFilePaths(build, listener, "input"));

		EasyMock.verify(build, listener);
	}

	@Test
	public void testResolveFilePathsAbstractBuildReplace() throws Exception {
		JacocoPublisher publisher = new JacocoPublisher();
		AbstractBuild<?, ?> build = mock(AbstractBuild.class);
		TaskListener listener = mock(TaskListener.class);

		expect(build.getEnvironment(listener)).andReturn(new EnvVars());
		expect(build.getBuildVariables()).andReturn(Collections.singletonMap("key", "value"));

		EasyMock.replay(build, listener);

		assertEquals("inputvalueinput", publisher.resolveFilePaths(build, listener, "input${key}input"));

		EasyMock.verify(build, listener);
	}

	@Test
	public void testResolveFilePathsAbstractBuildException() throws Exception {
		JacocoPublisher publisher = new JacocoPublisher();
		AbstractBuild<?, ?> build = mock(AbstractBuild.class);
		TaskListener listener = mock(TaskListener.class);

		expect(build.getEnvironment(listener)).andThrow(new IllegalStateException("TestException"));
		expect(listener.getLogger()).andReturn(System.out);

		EasyMock.replay(build, listener);

		//noinspection CatchMayIgnoreException
		try {
			publisher.resolveFilePaths(build, listener, "input${key}input");
			fail();
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().startsWith("Failed to resolve parameters"));
		}
	}

	@Test
	public void testLocateReports() throws Exception {
		// Create a temporary workspace in the system
		File w = File.createTempFile("workspace", ".test");
		assertTrue(w.delete());
		assertTrue(w.mkdir());
		w.deleteOnExit();
		FilePath workspace = new FilePath(w);

		// Create 4 files in the workspace
		File f1 = File.createTempFile("jacoco", ".xml", w);
		f1.deleteOnExit();
		File f2 = File.createTempFile("anyname", ".xml", w);
		f2.deleteOnExit();
		File f3 = File.createTempFile("jacoco", ".xml", w);
		f3.deleteOnExit();
		File f4 = File.createTempFile("anyname", ".xml", w);
		f4.deleteOnExit();


		// Create a folder and move there 2 files
		File d1 = new File(workspace.child("subdir").getRemote());
		assertTrue(d1.mkdir());
		d1.deleteOnExit();

		File f5 = new File(workspace.child(d1.getName()).child(f3.getName()).getRemote());
		File f6 = new File(workspace.child(d1.getName()).child(f4.getName()).getRemote());
		assertTrue(f3.renameTo(f5));
		assertTrue(f4.renameTo(f6));
		f5.deleteOnExit();
		f6.deleteOnExit();
		
		/*
		// Look for files in the entire workspace recursively without providing 
		// the includes parameter
		FilePath[] reports = JacocoPublisher.locateCoverageReports(workspace, "**e/jacoco*.xml");
		assertEquals(2 , reports.length);

		// Generate a includes string and look for files 
		String includes = f1.getName() + "; " + f2.getName() + "; " + d1.getName();
		reports = JacocoPublisher.locateCoverageReports(workspace, includes);
		assertEquals(3, reports.length);

		// Save files in local workspace
		FilePath local = workspace.child("coverage_localfolder");
		JacocoPublisher.saveCoverageReports(local, reports);
		assertEquals(3, local.list().size());
		
		local.deleteRecursive();
		 */

	}

	@Test
	public void testPerformWithDefaultSettings() throws IOException, InterruptedException {
		// expect
		final Run run = mock(Run.class);
		expect(run.getResult()).andReturn(Result.SUCCESS).anyTimes();
		expect(run.getEnvironment(taskListener)).andReturn(new EnvVars()).anyTimes();
		expect(run.getParent()).andReturn(null).anyTimes();
		Action action = anyObject();
		run.addAction(action);
		final AtomicReference<JacocoBuildAction> buildAction = new AtomicReference<>();
		expectLastCall().andAnswer((IAnswer<Void>) () -> {
			buildAction.set((JacocoBuildAction) getCurrentArguments()[0]);
			buildAction.get().onAttached(run);

			return null;
		});

        File dir = File.createTempFile("JaCoCoPublisherTest", ".tst");
        assertTrue(dir.delete());
        assertTrue(dir.mkdirs());

        try {
			assertTrue(new File(dir, "jacoco/classes").mkdirs());
			FilePath filePath = new FilePath(dir);

			expect(run.getRootDir()).andReturn(dir).anyTimes();

			replay(taskListener, run);

			// execute
			JacocoPublisher publisher = new JacocoPublisher();
			publisher.perform(run, filePath, launcher, taskListener);

			assertNotNull(buildAction.get());
			assertEquals(Result.SUCCESS, JacocoPublisher.checkResult(buildAction.get()));

			assertNotNull(run.toString());
		} finally {
        	FileUtils.deleteDirectory(dir);
		}

		// verify
		verify(taskListener, run);
	}

	@Test
	public void testCheckResult() {
		TaskListener listener = TaskListener.NULL;
		JacocoBuildAction action = new JacocoBuildAction(null, new JacocoHealthReportThresholds(), listener, null, null);

		ICoverageNode covReport = new ClassCoverageImpl("name", 1, false);

		action.setCoverage(new ClassReport(), covReport);

		assertEquals(Result.SUCCESS, JacocoPublisher.checkResult(action));
	}

	@Test
	public void testSkipCopyOfSrcFilesTrue() throws IOException, InterruptedException{
		final Run run = new RunBuilder().taskListener(taskListener).build();
		FilePath workspace = new WorkspaceBuilder()
				.file("classes/Test.class")
				.file("src/main/java/Test.java")
				.build();

		JacocoPublisher publisher = new JacocoPublisher();
		publisher.setSkipCopyOfSrcFiles(true);
		publisher.perform(run, workspace, launcher, taskListener);

		// verify if jacoco/sources doesn't exists
		File jacocoSrc = new File(run.getRootDir(), "jacoco/sources");
		Assert.assertFalse(jacocoSrc.exists() && jacocoSrc.isDirectory());

		verify(taskListener, run);

		// clean up afterwards
		FileUtils.deleteDirectory(run.getRootDir());
		FileUtils.deleteDirectory(new File(workspace.getRemote()));
	}

	@Test
	public void testSkipCopyOfSrcFilesFalse() throws IOException, InterruptedException{
		final Run run = new RunBuilder().taskListener(taskListener).build();
		FilePath workspace = new WorkspaceBuilder()
				.file("classes/Test.class")
				.file("src/main/java/Test.java")
				.build();

		JacocoPublisher publisher = new JacocoPublisher();
		publisher.setSkipCopyOfSrcFiles(false);
		publisher.perform(run, workspace, launcher, taskListener);

		// verify if jacoco/sources exists
		File jacocoSrc = new File(run.getRootDir(), "jacoco/sources");
		assertTrue(jacocoSrc.exists() && jacocoSrc.isDirectory());
		verify(taskListener, run);

		// clean up afterwards
		FileUtils.deleteDirectory(run.getRootDir());
		FileUtils.deleteDirectory(new File(workspace.getRemote()));
	}

	@Test
	public void testCopyClassAndSource() throws IOException, InterruptedException {
		final Run run = new RunBuilder().taskListener(taskListener).build();
		FilePath workspace = new WorkspaceBuilder()
				.file("classes/Test.class")
				.file("classes/Test.jar")
				.file("classes/sub/Test2.class")
				.file("src/main/java/Test.java")
				.file("src/main/java/generated/bean.java")
				.file("src/main/java/Test.groovy")
				.file("src/main/java/test.png")
				.build();

		JacocoPublisher publisher = new JacocoPublisher();
		publisher.setClassPattern("**/classes");
		publisher.setSourceInclusionPattern("**/*.java,**/*.groovy");
		publisher.setSourceExclusionPattern("generated/**/*");
		publisher.perform(run, workspace, launcher, taskListener);

		assertTrue(new File(run.getRootDir(), "jacoco/classes/Test.class").exists());
		assertFalse(new File(run.getRootDir(), "jacoco/classes/Test.jar").exists());
		assertTrue(new File(run.getRootDir(), "jacoco/classes/sub/Test2.class").exists());
		assertFalse(new File(run.getRootDir(), "jacoco/classes/Test2.class").exists());
		assertTrue(new File(run.getRootDir(), "jacoco/sources/Test.java").exists());
		assertTrue(new File(run.getRootDir(), "jacoco/sources/Test.groovy").exists());
		assertFalse(new File(run.getRootDir(), "jacoco/sources/generated/bean.java").exists());
		assertFalse(new File(run.getRootDir(), "jacoco/sources/test.png").exists());
		verify(taskListener, run);

		// clean up afterwards
		FileUtils.deleteDirectory(run.getRootDir());
		FileUtils.deleteDirectory(new File(workspace.getRemote()));
	}

	@Test
	public void testCopyClass_Wrong() throws IOException, InterruptedException {
		final Run run = new RunBuilder().taskListener(taskListener).build();
		FilePath workspace = new WorkspaceBuilder()
				.file("classes/Test.class")
				.file("classes/Test.jar")
				.file("classes/sub/Test2.class")
				.build();

		JacocoPublisher publisher = new JacocoPublisher();
		publisher.setClassPattern("**/classes/");
		publisher.perform(run, workspace, launcher, taskListener);

		assertTrue(new File(run.getRootDir(), "jacoco/classes/Test.class").exists());
		assertFalse(new File(run.getRootDir(), "jacoco/classes/Test.jar").exists());
		assertTrue(new File(run.getRootDir(), "jacoco/classes/sub/Test2.class").exists());
		assertTrue(new File(run.getRootDir(), "jacoco/classes/Test2.class").exists()); // will be copied accidentally

		assertTrue(logContent.toString().contains("WARNING: You are using directory patterns with trailing /, /* or /**"));
		assertTrue(logContent.toString().replace("\\","/").contains("tst/classes 2 files"));
		assertTrue(logContent.toString().replace("\\","/").contains("tst/classes/sub 1 files"));
		verify(taskListener, run);

		// clean up afterwards
		FileUtils.deleteDirectory(run.getRootDir());
		FileUtils.deleteDirectory(new File(workspace.getRemote()));
	}

	// Test perform with build over build feature turned ON
	@Test
	public void testPerformWithBuildOverBuild() throws IOException, InterruptedException {

		// expect
		final Run run = PowerMock.createNiceMock(Run.class);
		final Job job = PowerMock.createNiceMock(Job.class);

		expect(run.getResult()).andReturn(Result.SUCCESS).anyTimes();
		expect(run.getEnvironment(taskListener)).andReturn(new EnvVars()).anyTimes();
		Action action = anyObject();
		run.addAction(action);
		final AtomicReference<JacocoBuildAction> buildAction = new AtomicReference<>();
		expectLastCall().andAnswer((IAnswer<Void>) () -> {
			buildAction.set((JacocoBuildAction) getCurrentArguments()[0]);
			buildAction.get().onAttached(run);

			return null;
		});

		File dir = File.createTempFile("JaCoCoPublisherTest", ".tst");
		assertTrue(dir.delete());
		assertTrue(dir.mkdirs());
		assertTrue(new File(dir, "jacoco/classes").mkdirs());
		FilePath filePath = new FilePath(dir);

		try {
			expect(run.getRootDir()).andReturn(dir).anyTimes();
			expect(run.getParent()).andReturn(job).anyTimes();
			expect(job.getLastSuccessfulBuild()).andReturn(run).anyTimes();

			PowerMock.replay(taskListener, run, job);

			// execute
			//noinspection deprecation
			JacocoPublisher publisher = new JacocoPublisher("**/**.exec", "**/classes", "**/src/main/java", "", "", false, "0", "0"
					, "0", "0", "0", "0", "0", "0"
					, "0", "0", "0", "0", false,
					"10.564", "5.65", "9.995", "11.4529", "9.346", "5.237", true);
			publisher.perform(run, filePath, launcher, taskListener);

			assertNotNull(buildAction.get());
			assertEquals("Build over build result", "SUCCESS",
					publisher.checkBuildOverBuildResult(run, taskListener.getLogger()).toString());
		} finally {
			FileUtils.deleteDirectory(dir);
		}

		// verify
		PowerMock.verify(taskListener, run, job);
	}
}
