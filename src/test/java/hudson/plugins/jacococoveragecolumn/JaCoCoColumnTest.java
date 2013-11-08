package hudson.plugins.jacococoveragecolumn;

import static org.junit.Assert.*;
import hudson.console.ConsoleNote;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.Cause;
import hudson.model.Descriptor.FormException;
import hudson.model.ExternalJob;
import hudson.model.ExternalRun;
import hudson.model.Job;
import hudson.plugins.jacoco.JacocoBuildAction;
import hudson.plugins.jacoco.model.Coverage;
import hudson.plugins.jacoco.model.CoverageElement;
import hudson.plugins.jacoco.model.CoverageElement.Type;
import hudson.search.QuickSilver;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.stapler.export.Exported;

public class JaCoCoColumnTest {

	protected Float percentFloat;
	private JaCoCoColumn jacocoColumn;

	@Before
	public void setUp() {
		jacocoColumn = new JaCoCoColumn();
	}

	@Test
	public void testGetPercentWithNoLastSuccessfulBuild() {
		ServletContext context = EasyMock.createNiceMock(ServletContext.class);
		
		EasyMock.replay(context);

		final Job<?, ?> mockJob = new ExternalJob("externaljob") {
			@Override
			protected void reload() {
			}
		};
		assertFalse(jacocoColumn.hasCoverage(mockJob));
		assertEquals("N/A", jacocoColumn.getPercent(mockJob));
		assertEquals(new BigDecimal("0.0"), jacocoColumn.getLineCoverage(mockJob));
		
		EasyMock.verify(context);
	}

	@Test
	public void testGetPercentWithLastSuccessfulBuild() {
		ServletContext context = EasyMock.createNiceMock(ServletContext.class);
		
		EasyMock.replay(context);

		final Job<?, ?> mockJob = new ExternalJobExtension("externaljob");
		assertFalse(jacocoColumn.hasCoverage(mockJob));
		assertEquals("N/A", jacocoColumn.getPercent(mockJob));
		assertEquals(new BigDecimal("0.0"), jacocoColumn.getLineCoverage(mockJob));

		EasyMock.verify(context);
	}

	@Test
	public void testGetPercentWithBuildAndAction() {
		ServletContext context = EasyMock.createNiceMock(ServletContext.class);
		
		EasyMock.replay(context);

		final Job<?, ?> mockJob = new ExternalJob("externaljob") {
			@Override
			protected void reload() {
			}

			@Override
			@Exported
			@QuickSilver
			public ExternalRun getLastSuccessfulBuild() {
				try {
					ExternalRun newBuild = newBuild();
					newBuild.getActions().add(new JacocoBuildAction(null, null, null, null, new BuildListener() {
						private static final long serialVersionUID = 1L;

						public void hyperlink(String url, String text) throws IOException {
						}
						
						public PrintStream getLogger() {
							return null;
						}
						
						public PrintWriter fatalError(String format, Object... args) {
							return null;
						}
						
						public PrintWriter fatalError(String msg) {
							return null;
						}
						
						public PrintWriter error(String format, Object... args) {
							return null;
						}
						
						public PrintWriter error(String msg) {
							return null;
						}
						
						public void annotate(@SuppressWarnings("rawtypes") ConsoleNote ann) throws IOException {
							
						}
						
						public void started(List<Cause> causes) {
						}
						
						public void finished(Result result) {
						}
					}, null, null));
					assertEquals(1, newBuild.getActions().size());
					return newBuild;
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}

			@Override
			protected synchronized void saveNextBuildNumber() throws IOException {
			}
		};
		assertTrue(jacocoColumn.hasCoverage(mockJob));
		assertEquals("0.0", jacocoColumn.getPercent(mockJob));
		assertEquals(new BigDecimal("0.0"), jacocoColumn.getLineCoverage(mockJob));
		
		EasyMock.verify(context);
	}
	
	@Test
	public void testGetLineColorWithNull() throws Exception {
		assertNull(jacocoColumn.getLineColor(null, null));
	}

	@Test
	public void testGetLineColor() throws Exception {
		final BuildListener listener = EasyMock.createNiceMock(BuildListener.class);
		EasyMock.replay(listener);
		
		// without job we cannot check for NA
		assertEquals(CoverageRange.PERFECT.getLineHexString(), jacocoColumn.getLineColor(null, BigDecimal.valueOf(100)));
		
		// with job, we detect that it has a build, but no JaCoCoBuildAction => NA
		Job<?, ?> mockJob = new ExternalJobExtension("externaljob");
		assertEquals(CoverageRange.NA.getLineHexString(), jacocoColumn.getLineColor(mockJob, BigDecimal.valueOf(100)));
		
		// with job and build and JaCoCoBuildAction we detect correct coverage again
		mockJob = new ExternalJobExtensionWithBuildAction("externaljob", listener);
		assertEquals(CoverageRange.PERFECT.getLineHexString(), jacocoColumn.getLineColor(mockJob, BigDecimal.valueOf(100)));

		// finally with job, but no build => NA again
		mockJob = new ExternalJobExtensionWithNoLastBuild("externaljob");
		assertEquals(CoverageRange.NA.getLineHexString(), jacocoColumn.getLineColor(mockJob, BigDecimal.valueOf(100)));
		
		EasyMock.verify(listener);
	}

	@Test
	public void testGetFillColorWithNull() throws Exception {
		assertNull(jacocoColumn.getFillColor(null, null));
	}

	@Test
	public void testGetFillColor100() throws Exception {
		final BuildListener listener = EasyMock.createNiceMock(BuildListener.class);
		EasyMock.replay(listener);
		
		// without job we cannot check for NA
		assertEquals(CoverageRange.PERFECT.getFillHexString(), jacocoColumn.getFillColor(null, BigDecimal.valueOf(100)));

		// with job, we detect that it has a build, but no JaCoCoBuildAction => NA
		Job<?, ?> mockJob = new ExternalJobExtension("externaljob");
		assertEquals(CoverageRange.NA.getFillHexString(), jacocoColumn.getFillColor(mockJob, BigDecimal.valueOf(100)));

		// with job and build and JaCoCoBuildAction we detect correct coverage again
		mockJob = new ExternalJobExtensionWithBuildAction("externaljob", listener);
		assertEquals(CoverageRange.PERFECT.getFillHexString(), jacocoColumn.getFillColor(mockJob, BigDecimal.valueOf(100)));

		// finally with job, but no build => NA again
		mockJob = new ExternalJobExtensionWithNoLastBuild("externaljob");
		assertEquals(CoverageRange.NA.getFillHexString(), jacocoColumn.getFillColor(mockJob, BigDecimal.valueOf(100)));
		
		EasyMock.verify(listener);
	}

	@Test
	public void testDescriptor() throws FormException {
		assertNotNull(jacocoColumn.getDescriptor());
		assertNotNull(jacocoColumn.getDescriptor().newInstance(null, null));
		assertNotNull(jacocoColumn.getDescriptor().getDisplayName());
	}

	private final class ExternalJobExtensionWithNoLastBuild extends ExternalJobExtension {

		private ExternalJobExtensionWithNoLastBuild(String name) {
			super(name);
		}

		@Override
		public ExternalRun getLastSuccessfulBuild() {
			return null;
		}
	}

	private final class ExternalJobExtensionWithBuildAction extends ExternalJobExtension {

		private final BuildListener listener;

		private ExternalJobExtensionWithBuildAction(String name, BuildListener listener) {
			super(name);
			this.listener = listener;
		}

		@Override
		@Exported
		@QuickSilver
		public ExternalRun getLastSuccessfulBuild() {
			try {
				ExternalRun run = newBuild();
				Map<Type, Coverage> map = Collections.<CoverageElement.Type, Coverage>emptyMap();
				run.addAction(new JacocoBuildAction(null, null, map, null, listener, null, null));
				return run;
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	private class ExternalJobExtension extends ExternalJob {

		private ExternalJobExtension(String name) {
			super(name);
		}

		@Override
		protected void reload() {
		}

		@Override
		@Exported
		@QuickSilver
		public ExternalRun getLastSuccessfulBuild() {
			try {
				return newBuild();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		protected synchronized void saveNextBuildNumber() throws IOException {
		}
	}
}
