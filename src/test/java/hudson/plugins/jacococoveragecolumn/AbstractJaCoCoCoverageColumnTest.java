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
import net.sf.json.JSONObject;
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

public class AbstractJaCoCoCoverageColumnTest {

	private AbstractJaCoCoCoverageColumn abstractJaCoCoCoverageColumn;

    @Before
	public void setUp() {
		abstractJaCoCoCoverageColumn = new AbstractJaCoCoCoverageColumn() {
			@Override
			protected Float getPercentageFloat(Run<?, ?> build) {
				return getPercentageFloat(build, (a) -> 66.666f);
			}
		};
	}

	@Test
	public void testGetPercentWithNoLastSuccessfulBuild() {
		ServletContext context = EasyMock.createNiceMock(ServletContext.class);
		
		EasyMock.replay(context);

		final Job<?, ?> mockJob = new ExternalJobExtensionWithNoLastBuild("externaljob");
		assertFalse(abstractJaCoCoCoverageColumn.hasCoverage(mockJob));
		assertEquals("N/A", abstractJaCoCoCoverageColumn.getPercent(mockJob));
		assertEquals(new BigDecimal("0.0"), abstractJaCoCoCoverageColumn.getCoverage(mockJob));
		
		EasyMock.verify(context);
	}

	@Test
	public void testGetPercentWithLastSuccessfulBuild() {
		ServletContext context = EasyMock.createNiceMock(ServletContext.class);
		
		EasyMock.replay(context);

		final Job<?, ?> mockJob = new ExternalJobExtension("externaljob");
		assertFalse(abstractJaCoCoCoverageColumn.hasCoverage(mockJob));
		assertEquals("N/A", abstractJaCoCoCoverageColumn.getPercent(mockJob));
		assertEquals(new BigDecimal("0.0"), abstractJaCoCoCoverageColumn.getCoverage(mockJob));

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
			protected synchronized void saveNextBuildNumber() {
			}
		};
		assertTrue(abstractJaCoCoCoverageColumn.hasCoverage(mockJob));
		assertEquals("66.67", abstractJaCoCoCoverageColumn.getPercent(mockJob));
		assertEquals(new BigDecimal("66.67"), abstractJaCoCoCoverageColumn.getCoverage(mockJob));

		EasyMock.verify(context);
	}

	@Test
	public void testGetColorWithNull() {
		assertNull(abstractJaCoCoCoverageColumn.getColor(null, null));
	}

	@Test
	public void testGeColor() {
		final BuildListener listener = EasyMock.createNiceMock(BuildListener.class);
		EasyMock.replay(listener);
		
		// without job we cannot check for NA
		assertEquals(CoverageRange.PERFECT.getLineHexString(), abstractJaCoCoCoverageColumn
				.getColor(null, BigDecimal.valueOf(100)));
		
		// with job, we detect that it has a build, but no JaCoCoBuildAction => NA
		Job<?, ?> mockJob = new ExternalJobExtension("externaljob");
		assertEquals(CoverageRange.NA.getLineHexString(), abstractJaCoCoCoverageColumn
				.getColor(mockJob, BigDecimal.valueOf(100)));
		
		// with job and build and JaCoCoBuildAction we detect correct coverage again
		mockJob = new ExternalJobExtensionWithBuildAction("externaljob", listener);
		assertEquals(CoverageRange.PERFECT.getLineHexString(), abstractJaCoCoCoverageColumn
				.getColor(mockJob, BigDecimal.valueOf(100)));

		// finally with job, but no build => NA again
		mockJob = new ExternalJobExtensionWithNoLastBuild("externaljob");
		assertEquals(CoverageRange.NA.getLineHexString(), abstractJaCoCoCoverageColumn
				.getColor(mockJob, BigDecimal.valueOf(100)));
		
		EasyMock.verify(listener);
	}

	@Test
	public void testGetFillColorWithNull() {
		assertNull(abstractJaCoCoCoverageColumn.getFillColor(null, null));
	}

	@Test
	public void testGetFillColor100() {
		final BuildListener listener = EasyMock.createNiceMock(BuildListener.class);
		EasyMock.replay(listener);
		
		// without job we cannot check for NA
		assertEquals(CoverageRange.PERFECT.getFillHexString(), abstractJaCoCoCoverageColumn
				.getFillColor(null, BigDecimal.valueOf(100)));

		// with job, we detect that it has a build, but no JaCoCoBuildAction => NA
		Job<?, ?> mockJob = new ExternalJobExtension("externaljob");
		assertEquals(CoverageRange.NA.getFillHexString(), abstractJaCoCoCoverageColumn
				.getFillColor(mockJob, BigDecimal.valueOf(100)));

		// with job and build and JaCoCoBuildAction we detect correct coverage again
		mockJob = new ExternalJobExtensionWithBuildAction("externaljob", listener);
		assertEquals(CoverageRange.PERFECT.getFillHexString(), abstractJaCoCoCoverageColumn
				.getFillColor(mockJob, BigDecimal.valueOf(100)));

		// finally with job, but no build => NA again
		mockJob = new ExternalJobExtensionWithNoLastBuild("externaljob");
		assertEquals(CoverageRange.NA.getFillHexString(), abstractJaCoCoCoverageColumn
				.getFillColor(mockJob, BigDecimal.valueOf(100)));
		
		EasyMock.verify(listener);
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
		protected synchronized void saveNextBuildNumber() {
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
