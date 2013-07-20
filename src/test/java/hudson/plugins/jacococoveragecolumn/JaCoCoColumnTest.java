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
import hudson.search.QuickSilver;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

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
		assertEquals("N/A", jacocoColumn.getPercent(mockJob));
		assertEquals(new BigDecimal("0.0"), jacocoColumn.getLineCoverage(mockJob));

		EasyMock.verify(context);
	}

	@Test
	public void testGetPercentWithLastSuccessfulBuild() {
		ServletContext context = EasyMock.createNiceMock(ServletContext.class);
		
		EasyMock.replay(context);

		final Job<?, ?> mockJob = new ExternalJobExtension("externaljob");
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
		assertEquals(CoverageRange.NA.getLineHexString(), jacocoColumn.getLineColor(null, BigDecimal.valueOf(100)));
		
		Job<?, ?> mockJob = new ExternalJobExtension("externaljob");
		assertEquals(CoverageRange.NA.getLineHexString(), jacocoColumn.getLineColor(mockJob, BigDecimal.valueOf(100)));

		mockJob = new ExternalJobExtension("externaljob") {
			@Override
			public ExternalRun getLastSuccessfulBuild() {
				return null;
			}
		};
		assertEquals(CoverageRange.NA.getLineHexString(), jacocoColumn.getLineColor(mockJob, BigDecimal.valueOf(100)));
	}

	@Test
	public void testGetFillColorWithNull() throws Exception {
		assertNull(jacocoColumn.getFillColor(null, null));
	}

	@Test
	public void testGetFillColor100() throws Exception {
		assertEquals(CoverageRange.PERFECT.getFillHexString(), jacocoColumn.getFillColor(null, BigDecimal.valueOf(100)));

		Job<?, ?> mockJob = new ExternalJobExtension("externaljob");
		assertEquals(CoverageRange.NA.getFillHexString(), jacocoColumn.getFillColor(mockJob, BigDecimal.valueOf(100)));

		mockJob = new ExternalJobExtension("externaljob") {
			@Override
			public ExternalRun getLastSuccessfulBuild() {
				return null;
			}
		};
		assertEquals(CoverageRange.NA.getFillHexString(), jacocoColumn.getFillColor(mockJob, BigDecimal.valueOf(100)));
	}

	@Test
	public void testDescriptor() throws FormException {
		assertNotNull(jacocoColumn.getDescriptor());
		assertNotNull(jacocoColumn.getDescriptor().newInstance(null, null));
		assertNotNull(jacocoColumn.getDescriptor().getDisplayName());
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
