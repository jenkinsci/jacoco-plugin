package hudson.plugins.jacoco;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import hudson.plugins.jacoco.report.ClassReport;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.internal.analysis.ClassCoverageImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.niceMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JacocoPublisherTest extends AbstractJacocoTestBase {
    private final TaskListener taskListener = niceMock(TaskListener.class);
    private final Launcher launcher = niceMock(Launcher.class);

    @Before
    public void setUp() {
        expect(taskListener.getLogger()).andReturn(System.out).anyTimes();
    }

    @SuppressWarnings("deprecation")
	@Test
	public void testConstruct() {
		JacocoPublisher publisher = new JacocoPublisher(null, null, null, null, null, false,
				null, null, null, null,
				null, null, null, null,
				null, null, null, null,
				false);
		assertNotNull(publisher.toString());
	}

	@Test
	public void testGetterSetter() throws Exception {
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

		BuildStepDescriptor<Publisher> descriptor = publisher.getDescriptor();
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

		assertEquals("input${key}input", publisher.resolveFilePaths(run, listener, "input${key}input", Collections.singletonMap("key", "value")));

		EasyMock.verify(run, listener);
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

		assertEquals("input${key}input", publisher.resolveFilePaths(build, listener, "input${key}input"));

		EasyMock.verify(build, listener);
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
		expectLastCall().andAnswer(new IAnswer<Void>() {
			@Override
			public Void answer() throws Throwable {
				buildAction.set((JacocoBuildAction) getCurrentArguments()[0]);
				buildAction.get().onAttached(run);

				return null;
			}
		});

        File dir = File.createTempFile("JaCoCoPublisherTest", ".tst");
        assertTrue(dir.delete());
        assertTrue(dir.mkdirs());
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

        // verify
		verify(taskListener, run);
	}

	@Test
	public void testCheckResult() throws Exception {
		TaskListener listener = TaskListener.NULL;
		JacocoBuildAction action = new JacocoBuildAction(null, new JacocoHealthReportThresholds(), listener, null, null);

		ICoverageNode covReport = new ClassCoverageImpl("name", 1, false);

		action.setCoverage(new ClassReport(), covReport);

		assertEquals(Result.SUCCESS, JacocoPublisher.checkResult(action));
	}

	@Test
	public void testSkipCopyOfSrcFilesTrue() throws IOException, InterruptedException{

		final Run run = mock(Run.class);
		expect(run.getEnvironment(taskListener)).andReturn(new EnvVars()).anyTimes();
		expect(run.getResult()).andReturn(Result.SUCCESS).anyTimes();
		expect(run.getParent()).andReturn(null).anyTimes();

		// create a test build directory
		File rootDir = File.createTempFile("BuildTest", ".tst");
		assertTrue(rootDir.delete());
		assertTrue(rootDir.mkdirs());
		FilePath root = new FilePath(rootDir);

		expect(run.getRootDir()).andReturn(rootDir).anyTimes();

		Action action = anyObject();
		run.addAction(action);
		final AtomicReference<JacocoBuildAction> buildAction = new AtomicReference<>();
		expectLastCall().andAnswer(new IAnswer<Void>() {
			@Override
			public Void answer() throws Throwable {
				buildAction.set((JacocoBuildAction) getCurrentArguments()[0]);
				buildAction.get().onAttached(run);

				return null;
			}
		});

		replay(taskListener, run);

		// create a test workspace of Jenkins job
		File wksp = File.createTempFile("workspace", ".tst");
		assertTrue(wksp.delete());
		assertTrue(wksp.mkdir());
		wksp.deleteOnExit();
		FilePath workspace = new FilePath(wksp);

		// create class and source files directory inside the test workspace
		File d1 = new File(workspace.child("classes").getRemote());
		assertTrue(d1.mkdir());
		d1.deleteOnExit();
		File testClass = File.createTempFile("Test", ".class", d1);
		assertTrue(testClass.delete());
		assertTrue(testClass.mkdir());
		testClass.deleteOnExit();

		File d2 = new File(workspace.child("java").getRemote());
		assertTrue(d2.mkdir());
		d2.deleteOnExit();
		File testSrc = File.createTempFile("Test", ".java", d2);
		assertTrue(testSrc.delete());
		assertTrue(testSrc.mkdir());
		testSrc.deleteOnExit();

		// set skip copy of src files as true
		JacocoPublisher publisher = new JacocoPublisher("**/**.exec", "**/classes", "**/java", null, null, true, null, null
				, null, null, null, null, null, null
				, null, null, null, null, false);
		publisher.perform(run, workspace, launcher, taskListener);

		// verify if jacoco/sources doesn't exists
		File jacocoSrc = new File(rootDir, "jacoco/sources");
		Assert.assertFalse(jacocoSrc.exists() && jacocoSrc.isDirectory());

		verify(taskListener, run);
	}

	@Test
	public void testSkipCopyOfSrcFilesFalse() throws IOException, InterruptedException{

		final Run run = mock(Run.class);
		expect(run.getEnvironment(taskListener)).andReturn(new EnvVars()).anyTimes();
		expect(run.getResult()).andReturn(Result.SUCCESS).anyTimes();
		expect(run.getParent()).andReturn(null).anyTimes();

		// create a test build directory
		File rootDir = File.createTempFile("BuildTest", ".tst");
		assertTrue(rootDir.delete());
		assertTrue(rootDir.mkdirs());
		FilePath root = new FilePath(rootDir);

		expect(run.getRootDir()).andReturn(rootDir).anyTimes();

		Action action = anyObject();
		run.addAction(action);
		final AtomicReference<JacocoBuildAction> buildAction = new AtomicReference<>();
		expectLastCall().andAnswer(new IAnswer<Void>() {
			@Override
			public Void answer() throws Throwable {
				buildAction.set((JacocoBuildAction) getCurrentArguments()[0]);
				buildAction.get().onAttached(run);

				return null;
			}
		});

		replay(taskListener, run);

		// create a test workspace of Jenkins job
		File wksp = File.createTempFile("workspace", ".tst");
		assertTrue(wksp.delete());
		assertTrue(wksp.mkdir());
		wksp.deleteOnExit();
		FilePath workspace = new FilePath(wksp);

		// create class and source files directory inside the test workspace
		File d1 = new File(workspace.child("classes").getRemote());
		assertTrue(d1.mkdir());
		d1.deleteOnExit();
		File testClass = File.createTempFile("Test", ".class", d1);
		assertTrue(testClass.delete());
		assertTrue(testClass.mkdir());
		testClass.deleteOnExit();

		File d2 = new File(workspace.child("java").getRemote());
		assertTrue(d2.mkdir());
		d2.deleteOnExit();
		File testSrc = File.createTempFile("Test", ".java", d2);
		assertTrue(testSrc.delete());
		assertTrue(testSrc.mkdir());
		testSrc.deleteOnExit();

		// set skip copy of src files as false
		JacocoPublisher publisher = new JacocoPublisher("**/**.exec", "**/classes", "**/java", null, null, false, null, null
				, null, null, null, null, null, null
				, null, null, null, null, false);
		publisher.perform(run, workspace, launcher, taskListener);

		// verify if jacoco/sources exists
		File jacocoSrc = new File(rootDir, "jacoco/sources");
		assertTrue(jacocoSrc.exists() && jacocoSrc.isDirectory());
		verify(taskListener, run);
	}
}