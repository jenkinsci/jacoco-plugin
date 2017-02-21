package hudson.plugins.jacococoveragecolumn;

import hudson.model.BuildListener;
import hudson.model.Descriptor.FormException;
import hudson.model.Job;
import hudson.model.Run;
import hudson.plugins.jacoco.JacocoBuildAction;
import hudson.plugins.jacoco.model.Coverage;
import hudson.plugins.jacoco.model.CoverageElement.Type;
import hudson.search.QuickSilver;
import hudson.util.StreamTaskListener;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.stapler.export.Exported;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;

import static org.junit.Assert.*;

public class JaCoCoColumnTest {
	private JaCoCoColumn jacocoColumn;

	//@Override
    @Before
	public void setUp() throws Exception {
		jacocoColumn = new JaCoCoColumn();
		
		//super.setUp();
	}

	@Test
	public void testGetPercentWithNoLastSuccessfulBuild() {
		ServletContext context = EasyMock.createNiceMock(ServletContext.class);
		
		EasyMock.replay(context);

		final Job<?, ?> mockJob = new ExternalJobExtensionWithNoLastBuild("externaljob");
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

		final Job<?, ?> mockJob = new MyJob("externaljob") {
			@Override
			@Exported
			@QuickSilver
			public MyRun getLastSuccessfulBuild() {
				try {
				    MyRun newBuild = newBuild();
					newBuild.addAction(new JacocoBuildAction(null, null, StreamTaskListener.fromStdout(), null, null));
					assertEquals(1, newBuild.getAllActions().size());
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
		assertEquals("100.0", jacocoColumn.getPercent(mockJob));
		assertEquals(new BigDecimal("100.0"), jacocoColumn.getLineCoverage(mockJob));

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

        public ExternalJobExtensionWithNoLastBuild(String name) {
            super(name);
        }

		@Override
		public MyRun getLastSuccessfulBuild() {
			return null;
		}
	}

	private final class ExternalJobExtensionWithBuildAction extends ExternalJobExtension {

		private final BuildListener listener;

        public ExternalJobExtensionWithBuildAction(String name, BuildListener listener) {
            super(name);
			this.listener = listener;
		}

		@Override
		@Exported
		@QuickSilver
		public MyRun getLastSuccessfulBuild() {
			try {
				MyRun run = newBuild();
				Map<Type, Coverage> map = Collections.emptyMap();
				run.addAction(new JacocoBuildAction(map, null, listener, null, null));
				return run;
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	private class ExternalJobExtension extends MyJob {

	    public ExternalJobExtension(String name) {
	        super(name);
	    }

        @Override
        @Exported
        @QuickSilver
        public MyRun getLastSuccessfulBuild() {
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
	
	private class MyJob extends Job<MyJob,MyRun> {

        public MyJob(String name) {
            super(null, name);
        }

        @Override
        public boolean isBuildable() {
            return false;
        }

        @Override
        protected SortedMap<Integer, MyRun> _getRuns() {
            return null;
        }

        @Override
        protected void removeRun(MyRun run) {
        }

        protected synchronized MyRun newBuild() throws IOException {
            return new MyRun(this);
        }
	}
	
	private class MyRun extends Run<MyJob,MyRun> {

        public MyRun(MyJob job) throws IOException {
            super(job);
        }
	}
}
